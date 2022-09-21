package com.rocketflyer.rocketflow.ui.newcreatetask

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.AppPreferencesHelper
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.BaseResponse
import com.tracki.data.model.request.*
import com.tracki.data.model.request.Contact
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityNewCreateTaskBinding
import com.tracki.ui.addcustomer.AddCustomerActivity
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.custom.CircleTransform
import com.tracki.ui.custom.ExecutorThread
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.dynamicform.DynamicFormActivity
import com.tracki.ui.dynamicform.dynamicfragment.FormSubmitListener
import com.tracki.ui.newdynamicform.NewDynamicFormFragment
import com.tracki.ui.userlisting.UserListingActivity
import com.tracki.utils.*
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.network.TrackThatCallback
import com.trackthat.lib.models.ErrorResponse
import com.trackthat.lib.models.SuccessResponse
import com.trackthat.lib.models.TrackthatLocation
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_new_create_task.*
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.collections.ArrayList


open class NewCreateTaskActivity :
    BaseActivity<ActivityNewCreateTaskBinding, NewCreateTaskViewModel>(),
    NewCreateTaskNavigator, FormSubmitListener, HasSupportFragmentInjector,
    BaseAutocompleteAdapter.OnItemSelectedAUtoComplete {
    init {
        System.loadLibrary("keys")
    }

    external fun getGoogleMapKey(): String?
    private var snackBar: Snackbar? = null
    private lateinit var selectedUserId: String
    private lateinit var selectedUsername: String
    private var allowGeography: Boolean = false
    private var currentLocation: GeoCoordinates? = null
    private var categoryId: String? = null
    private var parentTaskId: String? = null
    private var parentReffId: String? = null

    @Inject
    lateinit var mCreateTaskViewModel: NewCreateTaskViewModel

    @Inject
    lateinit var mGetSuggetionViewModel: GetUserSuggestionListViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    var lookUps: List<LookUps>? = null
    lateinit var view: View
    private lateinit var startLatLng: LatLng
    private var endLatLng: LatLng? = null
    lateinit var api: Api
    private lateinit var mActivityCreateTaskBinding: ActivityNewCreateTaskBinding
    private var startTime = 0L
    private var endTime = 0L

    private var startYear = 0
    private var startMonth = 0
    private var startDay = 0
    private var startHour = 0
    private var startMinute = 0

    private var endYear = 0
    private var endMonth = 0
    private var endDay = 0
    private var endHour = 0
    private var endMinute = 0
    private var spnCategory: Spinner? = null
    private var buddyName: String? = null
    private var selItem: String = ""//hard coded by previous developer
    private var dynamicFormToDisplay: String? = null
    private var allowedFields: String? = null
    private var allowedFieldFirst: Boolean? = null
    var requestedBy: String? = null
    var requestUserType: List<String>? = null
    var assigneeLabel: String? = null
    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_new_create_task
    override fun getViewModel() = mCreateTaskViewModel
    private var buddyId: String? = null
    private var fleetId: String? = null
    private var regionId: String? = null
    private var regionIdEnd: String? = null
    private var stateId: String? = null
    private var stateIdEnd: String? = null
    private var cityId: String? = null
    private var cityIdEnd: String? = null
    private var hubId: String? = null
    private var hubIdEnd: String? = null
    private lateinit var task: Task
    private var taskId = ""
    private var spinnerVal: String? = null
    private var createTaskRequest: CreateTaskRequest? = null
    private var mCategoryId: MutableLiveData<String> = MutableLiveData()
    private var mainMap: HashMap<String, ArrayList<FormData>>? = null
    private var dynamicFormsNew: DynamicFormsNew? = null
    private var isEditable: Boolean = true
    private var dynamicFragment: NewDynamicFormFragment? = null
    private var formList = ArrayList<String>()
    var mainData: ArrayList<FormData>? = null
    var titleText: TextView? = null
    var percentageText: TextView? = null
    var currentStatusText: TextView? = null
    var progressBar: ProgressBar? = null
    var rlProgress: RelativeLayout? = null
    var rlSubmittingData: RelativeLayout? = null

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    private var mDynamicHandler: DynamicHandler? = null
    private var handlerThread: ExecutorThread? = null

    enum class FIELD {
        START_LOCATION, END_LOCATION, SELECT_BUDDY, SELECT_CLIENT, START_TIME, END_TIME, POINT_OF_CONTACT, TASK_NAME,
        TASK_TYPE, DESCRIPTION, SELECT_FLEET, SELECT_CITY, TASK_ID, REFERENCE_ID, GOOGLE_OR_MANUAL_SOURCE, GOOGLE_OR_MANUAL_DESTINATION, SYSTEM_LOCATION,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,FIELD6,FIELD7,FIELD8,FIELD9,FIELD10,FIELD11,FIELD12,FIELD13,FIELD14,FIELD15
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityCreateTaskBinding = viewDataBinding
        mCreateTaskViewModel.navigator = this
        handlerThread = ExecutorThread()
        setUp()
        getCurrentLocation()
    }


    override fun onAssignNowClicked() {
//        Toast.makeText(this, "click", Toast.LENGTH_LONG).show()

    }

    override fun selectDateTime(v: View) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMin = c.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year

            CommonUtils.openTimePicker(this, mHour, mMin) { _, hourOfDay, minute ->
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minute)
                val formattedTime = DateTimeUtil.getFormattedTime(c.time)
                Log.e(TAG, "${c.time}")
                val cal = Calendar.getInstance()
                when (v.id) {
                    R.id.edStartDateTime -> {
                        startDay = dayOfMonth
                        startMonth = monthOfYear
                        startYear = year
                        startHour = hourOfDay
                        startMinute = minute
                        cal.set(Calendar.YEAR, startYear)
                        cal.set(Calendar.MONTH, startMonth)
                        cal.set(Calendar.DAY_OF_MONTH, startDay)
                        cal.set(Calendar.HOUR_OF_DAY, startHour)
                        cal.set(Calendar.MINUTE, startMinute)
                        cal.set(Calendar.SECOND, 0)
                        startTime = cal.timeInMillis

                        edStartDateTime.setText("$selectedDate | $formattedTime")
                    }
                    R.id.edEndDateTime -> {
                        endDay = dayOfMonth
                        endMonth = monthOfYear
                        endYear = year
                        endHour = hourOfDay
                        endMinute = minute

                        cal.set(Calendar.YEAR, endYear)
                        cal.set(Calendar.MONTH, endMonth)
                        cal.set(Calendar.DAY_OF_MONTH, endDay)
                        cal.set(Calendar.HOUR_OF_DAY, endHour)
                        cal.set(Calendar.MINUTE, endMinute)
                        cal.set(Calendar.SECOND, 0)
                        endTime = cal.timeInMillis
                        edEndDateTime.setText("$selectedDate | $formattedTime")
                    }
                }
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    override fun openMainActivity(taskId: String) {
        val intent = Intent()
        intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_ID, buddyId)
        intent.putExtra(AppConstants.Extra.EXTRA_FLEET_ID, fleetId)
        intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    override fun onResume() {
        super.onResume();
        hideLoading()

        //Restore state here
    }

    /**
     * Method used to get the current location from the sdk.
     */
    fun getCurrentLocation() {
        TrackThat.getCurrentLocation(object : TrackThatCallback() {
            override fun onSuccess(successResponse: SuccessResponse) {
                val loc = successResponse.responseObject as TrackthatLocation
                currentLocation = GeoCoordinates()
                currentLocation!!.latitude = loc.latitude
                currentLocation!!.longitude = loc.longitude
                currentLatLng = LatLng(loc.latitude, loc.longitude)
                startLatLng = currentLatLng
                var placeName = CommonUtils.getAddress(this@NewCreateTaskActivity, currentLatLng)
                edEnterStartLocation.setText(placeName)
                edEnterEndLocation.setText(placeName)

            }

            override fun onError(errorResponse: ErrorResponse) {
                currentLocation = null
            }
        })
    }

    override fun openPlaceAutoComplete(view: View) {
        this.view = view
        try {
            val fields: List<Place.Field> =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent: Intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.e(NewCreateTaskActivity.TAG, e.message)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e(NewCreateTaskActivity.TAG, e.message)
        }
    }

    override fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            finish()
        }
    }

    override fun pointOfContactClicked() {

    }

    override fun checkBuddyResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val buddyListResponse =
                Gson().fromJson(result.toString(), BuddyListResponse::class.java)
            val list = buddyListResponse.buddies
            if (list != null && list.size > 0) {
                var buddyList: MutableList<String?> = ArrayList()
                for (data in list) {
                    buddyList.add(data.name!!)
                }

//
                var arrayAdapter = object :
                    ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, buddyList) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getView(position, convertView, parent)
                        val externalFont = Typeface.createFromAsset(
                            parent.context.assets,
                            "fonts/campton_book.ttf"
                        )
                        (v as TextView).typeface = externalFont
                        return v
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getDropDownView(position, convertView, parent)
                        val externalFont = Typeface.createFromAsset(
                            parent.context.assets,
                            "fonts/campton_book.ttf"
                        )
                        (v as TextView).typeface = externalFont
                        return v
                    }
                }
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnBuddy!!.adapter = arrayAdapter
                spnBuddy!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        buddyId = list[position].buddyId
                        CommonUtils.showLogMessage("e", "buddyId", buddyId);
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }


            } else {
            }
        }
    }

    override fun checkFleetResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val buddyListResponse =
                Gson().fromJson(result.toString(), FleetListResponse::class.java)
            val list = buddyListResponse.fleets
            if (list != null && list.size > 0) {
                var fleetList: MutableList<String?> = ArrayList()
                for (data in list) {
                    data.fleetName?.let {
                        fleetList.add(data.fleetName!!)
                    }

                }

//                var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fleetList);
//                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                var arrayAdapter = object :
                    ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, fleetList) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getView(position, convertView, parent)
                        val externalFont = Typeface.createFromAsset(
                            parent.context.assets,
                            "fonts/campton_book.ttf"
                        )
                        (v as TextView).typeface = externalFont
                        return v
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getDropDownView(position, convertView, parent)
                        val externalFont = Typeface.createFromAsset(
                            parent.context.assets,
                            "fonts/campton_book.ttf"
                        )
                        (v as TextView).typeface = externalFont
                        //v.setBackgroundColor(Color.GREEN);
                        return v
                    }
                }
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnFleet!!.adapter = arrayAdapter
                spnFleet!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        fleetId = list[position].fleetId
                        CommonUtils.showLogMessage("e", "fleetId", fleetId);
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

            } else {

            }
        }
    }


    fun getLabelName(categoryId: String?): String? {
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper.workFlowCategoriesList
        var lebel: String? = null

        for (i in workFlowCategoriesList) {
            if (i.categoryId != null)
                if (i.categoryId == categoryId) {
                    if (i.name != null)
                        lebel = i.name
                }
        }
        return lebel
    }

    fun getDynamicFormId(categoryId: String?): String? {
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper.workFlowCategoriesList
        var id: String? = null

        for (i in workFlowCategoriesList) {
            if (i.categoryId != null)
                if (i.categoryId == categoryId) {
                    if (i.dynamicFormId != null)
                        id = i.dynamicFormId
                }
        }
        return id
    }

    fun getAllowedFields(categoryId: String?): List<Field> {
        var allowedFieldsList: List<Field>? = ArrayList()
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper.workFlowCategoriesList
        if (!workFlowCategoriesList.isNullOrEmpty()) {
            for (i in workFlowCategoriesList) {
                if (i.categoryId != null && categoryId != null)
                    if (i.categoryId == categoryId) {
                        if (i.name != null)
                            selItem = i.name!!
                        if (i.channelConfig != null && i.channelConfig!!.fields != null && i.channelConfig!!.fields!!.isNotEmpty())
                            allowedFieldsList = i.channelConfig!!.fields
                        if (i.channelConfig != null && i.channelConfig!!.channelSetting != null && i.channelConfig!!.channelSetting!!.allowedFieldFirst != null)
                            this.allowedFieldFirst =
                                i.channelConfig!!.channelSetting!!.allowedFieldFirst
                        if (i.channelConfig != null && i.channelConfig!!.channelSetting != null && i.channelConfig!!.channelSetting!!.creationConfig != null && i.channelConfig!!.channelSetting!!.creationConfig!!.requestedBy != null)
                            this.requestedBy =
                                i.channelConfig!!.channelSetting!!.creationConfig!!.requestedBy
                        else
                            this.requestedBy = null
                        if (i.channelConfig != null && i.channelConfig!!.channelSetting != null && i.channelConfig!!.channelSetting!!.creationConfig != null && i.channelConfig!!.channelSetting!!.creationConfig!!.requestUserType != null)
                            this.requestUserType =
                                i.channelConfig!!.channelSetting!!.creationConfig!!.requestUserType
                        else
                            this.requestUserType = null
                        if (i.channelConfig != null && i.channelConfig!!.channelSetting != null && i.channelConfig!!.channelSetting!!.creationConfig != null && i.channelConfig!!.channelSetting!!.creationConfig!!.assigneeLabel != null)
                            this.assigneeLabel =
                                i.channelConfig!!.channelSetting!!.creationConfig!!.assigneeLabel
                        else
                            this.assigneeLabel = null
                    }
            }
        }


        return allowedFieldsList!!
    }


    @SuppressLint("SetTextI18n")
    private fun setUp() {

        Places.initialize(this@NewCreateTaskActivity, getGoogleMapKey()!!)
        if (intent.hasExtra(AppConstants.Extra.EXTRA_PAREN_TASK_ID)) {
            parentTaskId = intent.getStringExtra(AppConstants.Extra.EXTRA_PAREN_TASK_ID)
            CommonUtils.showLogMessage("e", "parentTaskId", parentTaskId)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID)) {
            parentReffId = intent.getStringExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID)
        }
        enableStartLocation.setOnToggledListener{ toggleableView, isChecked ->
            if (isChecked) {
                tvLableManualStartLocation.text = "Enter Location"
                edEnterManualStartLocation.visibility = View.GONE
                edEnterStartLocation.visibility = View.VISIBLE
            } else {
                tvLableManualStartLocation.text = "Enter Manual Location"
                edEnterManualStartLocation.visibility = View.VISIBLE
                edEnterStartLocation.visibility = View.GONE

            }
        }
        /*enableStartLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tvLableManualStartLocation.text = "Enter Location"
                edEnterManualStartLocation.visibility = View.GONE
                edEnterStartLocation.visibility = View.VISIBLE
            } else {
                tvLableManualStartLocation.text = "Enter Manual Location"
                edEnterManualStartLocation.visibility = View.VISIBLE
                edEnterStartLocation.visibility = View.GONE

            }
        }*/
        /*enableEndLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tvLableManualEndLocation.text = "Enter Location"
                edEnterManualEndLocation.visibility = View.GONE
                edEnterEndLocation.visibility = View.VISIBLE
            } else {
                tvLableManualEndLocation.text = "Enter Manual Location"
                edEnterManualEndLocation.visibility = View.VISIBLE
                edEnterEndLocation.visibility = View.GONE

            }
        }*/
        enableEndLocation.setOnToggledListener{ toggleableView, isChecked ->
            if (isChecked) {
                tvLableManualEndLocation.text = "Enter Location"
                edEnterManualEndLocation.visibility = View.GONE
                edEnterEndLocation.visibility = View.VISIBLE
            } else {
                tvLableManualEndLocation.text = "Enter Manual Location"
                edEnterManualEndLocation.visibility = View.VISIBLE
                edEnterEndLocation.visibility = View.GONE

            }
        }
        var viewProgress = mActivityCreateTaskBinding.viewProgress
        titleText = viewProgress.findViewById<TextView>(R.id.tvTitle)
        currentStatusText = viewProgress!!.findViewById<TextView>(R.id.currentStatusText)
        percentageText = viewProgress!!.findViewById<TextView>(R.id.tvPercentage)
        progressBar = viewProgress!!.findViewById<ProgressBar>(R.id.pb_loading)
        rlSubmittingData = viewProgress!!.findViewById<RelativeLayout>(R.id.rlSubmittingData)
        rlProgress = viewProgress!!.findViewById<RelativeLayout>(R.id.rlProgress)
        enableStartLocation.isOn = false
        enableEndLocation.isOn = false
        btnCLick.setOnClickListener {
            if (dynamicFormsNew != null) {
                if (dynamicFragment != null) {
                    dynamicFragment!!.onclickMainButton()
                } else {
                    var dynamicActionConfig = DynamicActionConfig()
                    dynamicActionConfig.action = Type.DISPOSE
                    onProcessClick(ArrayList(), dynamicActionConfig, null, null)
                }
            } else {
                var dynamicActionConfig = DynamicActionConfig()
                dynamicActionConfig.action = Type.DISPOSE
                onProcessClick(ArrayList(), dynamicActionConfig, null, null)
            }

        }
        mActivityCreateTaskBinding.edReference.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
            override fun beforeTextChanged(
                arg0: CharSequence?, arg1: Int, arg2: Int,
                arg3: Int
            ) {
            }

            override fun afterTextChanged(et: Editable) {
                var s: String = et.toString()
                if (s != s.toUpperCase()) {
                    s = s.toUpperCase()
                    mActivityCreateTaskBinding.edReference.setText(s)
                    mActivityCreateTaskBinding.edReference.setSelection(mActivityCreateTaskBinding.edReference.length()) //fix reverse texting
                }
            }
        })
        spnCategory = mActivityCreateTaskBinding.spnCategory
        requestCurrentLocation(locationCallback)
        var categories: MutableList<String?> = ArrayList();
        var list: MutableList<WorkFlowCategories> = ArrayList();
        var mainList = preferencesHelper.workFlowCategoriesList
        val channelConfigMap = preferencesHelper.workFlowCategoriesListChannel
        for (i in mainList) {
            var categoryId = i.categoryId
            if (categoryId != null && channelConfigMap != null && channelConfigMap.containsKey(
                    categoryId
                )
            ) {
                val channelConfig: ChannelConfig = channelConfigMap.get(categoryId)!!
                if (channelConfig.channelSetting != null) {
                    val channelSetting = channelConfig.channelSetting
                    if (channelSetting!!.allowCreation != null && channelSetting!!.allowCreation!!) {
                        if (channelSetting!!.creationMode === CreationMode.DIRECT) {
                            list.add(i)
                            categories!!.add(i.name!!)
                        }

                    }
                }
            }


        }


        mCategoryId.observe(this, androidx.lifecycle.Observer { categoryId ->
            if (categoryId != null) {
                var llallowedFields: List<Field>? = getAllowedFields(categoryId)
                var jsonConverter = JSONConverter<List<Field>>()
                var data = jsonConverter.objectToJson(llallowedFields!!)
                CommonUtils.showLogMessage("e", "allowed field", data.toString())
                if (requestedBy != null && requestedBy.equals("OTHERS")) {
                    cardViewClientList.visibility = View.VISIBLE
                    performSearchWidgetTask()
                } else {
                    cardViewClientList.visibility = View.GONE
                }
                if (!llallowedFields.isNullOrEmpty()) {
                    var START_LOCATION = Field(field = FIELD.START_LOCATION.name)
                    if (llallowedFields.contains(START_LOCATION)) {
                        var position = llallowedFields.indexOf(START_LOCATION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilStartLocation.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilStartLocation.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLebelStartLocation.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilStartLocation.visibility = View.GONE

                    }
                    var END_LOCATION = Field(field = FIELD.END_LOCATION.name)
                    if (llallowedFields.contains(END_LOCATION)) {
                        var position = llallowedFields.indexOf(END_LOCATION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilEndLocation.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilEndLocation.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelEndLocation.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilEndLocation.visibility = View.GONE
                    }
                    var GOOGLE_OR_MANUAL_SOURCE = Field(field = FIELD.GOOGLE_OR_MANUAL_SOURCE.name)
                    var SYSTEM_LOCATION = Field(field = FIELD.SYSTEM_LOCATION.name)
                    if (llallowedFields.contains(GOOGLE_OR_MANUAL_SOURCE)) {
                        getCurrentLocation()
                        var position = llallowedFields.indexOf(GOOGLE_OR_MANUAL_SOURCE)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.cardManualStartLocation.visibility =
                                View.VISIBLE
                            mCreateTaskViewModel.getRegionList(
                                httpManager,
                                true,
                                RegionRequest(userGeoReq = allowGeography)
                            )
                        } else {
                            mActivityCreateTaskBinding.cardManualStartLocation.visibility =
                                View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelManualStartLocationName.text =
                                llallowedFields[position].label
                    } else if (llallowedFields.contains(SYSTEM_LOCATION)) {
                        getCurrentLocation()
                        var position = llallowedFields.indexOf(SYSTEM_LOCATION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.cardManualStartLocation.visibility =
                                View.VISIBLE
                            mActivityCreateTaskBinding.llToggleEnableStartLoc.visibility = View.GONE
                            mActivityCreateTaskBinding.tvLableManualStartLocation.visibility =
                                View.GONE
                            mActivityCreateTaskBinding.edEnterManualStartLocation.visibility =
                                View.GONE
                            mActivityCreateTaskBinding.edEnterStartLocation.visibility = View.GONE
                            mCreateTaskViewModel.getRegionList(
                                httpManager,
                                true,
                                RegionRequest(userGeoReq = allowGeography)
                            )
                        } else {
                            mActivityCreateTaskBinding.cardManualStartLocation.visibility =
                                View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelManualStartLocationName.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.cardManualStartLocation.visibility = View.GONE
                    }
                    var GOOGLE_OR_MANUAL_DESTINATION =
                        Field(field = FIELD.GOOGLE_OR_MANUAL_DESTINATION.name)
                    if (llallowedFields.contains(GOOGLE_OR_MANUAL_DESTINATION)) {
                        var position = llallowedFields.indexOf(GOOGLE_OR_MANUAL_DESTINATION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.cardManualEndLocation.visibility =
                                View.VISIBLE
                            mCreateTaskViewModel.getRegionList(
                                httpManager,
                                false,
                                RegionRequest(userGeoReq = allowGeography)
                            )
                        } else {
                            mActivityCreateTaskBinding.cardManualEndLocation.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelManualEndLocationName.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.cardManualEndLocation.visibility = View.GONE
                    }
                    var SELECT_BUDDY = Field(field = FIELD.SELECT_BUDDY.name)
                    if (llallowedFields.contains(SELECT_BUDDY)) {
                        var position = llallowedFields.indexOf(SELECT_BUDDY)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llbuddy.visibility = View.VISIBLE
                            mCreateTaskViewModel.checkBuddy(httpManager)

                        } else {
                            mActivityCreateTaskBinding.llbuddy.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelBuddy.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llbuddy.visibility = View.GONE
                    }
                    var SELECT_CLIENT = Field(field = FIELD.SELECT_CLIENT.name)
                    if (llallowedFields.contains(SELECT_CLIENT)) {
                        var position = llallowedFields.indexOf(SELECT_CLIENT)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llClient.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.llClient.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelClient.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llClient.visibility = View.GONE
                    }
                    var START_TIME = Field(field = FIELD.START_TIME.name)
                    if (llallowedFields.contains(START_TIME)) {
                        var position = llallowedFields.indexOf(START_TIME)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val c = Calendar.getInstance()
                            startHour = c.get(Calendar.HOUR_OF_DAY)
                            startMinute = c.get(Calendar.MINUTE)
                            val startTimew = DateTimeUtil.getFormattedTime(c.time)
                            startDay = c.get(Calendar.DAY_OF_MONTH)
                            startMonth = c.get(Calendar.MONTH) + 1
                            startYear = c.get(Calendar.YEAR)
                            startTime = c.timeInMillis
                            val startDate = "$startDay-$startMonth-$startYear"
                            //set current time into the field
                            edStartDateTime.setText("$startDate | $startTimew")
                            edStartDateTime.setSelection(edStartDateTime.text.length)
                            mActivityCreateTaskBinding.tilStartDateTime.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilStartDateTime.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLebelStartDateTime.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilStartDateTime.visibility = View.GONE
                    }
                    var END_TIME = Field(field = FIELD.END_TIME.name)
                    if (llallowedFields.contains(END_TIME)) {
                        var position = llallowedFields.indexOf(END_TIME)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val cal = Calendar.getInstance()
                            cal.time = Date()
                            cal.add(Calendar.HOUR_OF_DAY, 1)
                            endYear = cal.get(Calendar.YEAR)
                            endMonth = cal.get(Calendar.MONTH) + 1
                            endDay = cal.get(Calendar.DAY_OF_MONTH)
                            endHour = cal.get(Calendar.HOUR_OF_DAY)
                            endMinute = cal.get(Calendar.MINUTE)
                            endTime = cal.timeInMillis
                            val endTimew = DateTimeUtil.getFormattedTime(cal.time)
                            val endDate = "$endDay-$endMonth-$endYear"

                            //set current time into the field
                            edEndDateTime.setText("$endDate | $endTimew")
                            edEndDateTime.setSelection(edEndDateTime.text.length)
                            mActivityCreateTaskBinding.tilEndDateTime.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilEndDateTime.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelEndDateTime.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilEndDateTime.visibility = View.GONE
                    }
                    var POINT_OF_CONTACT = Field(field = FIELD.POINT_OF_CONTACT.name)
                    if (llallowedFields.contains(POINT_OF_CONTACT)) {
                        var position = llallowedFields.indexOf(POINT_OF_CONTACT)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llPOC.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.llPOC.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelPOC.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llPOC.visibility = View.GONE
                    }
                    var TASK_NAME = Field(field = FIELD.TASK_NAME.name)
                    if (llallowedFields.contains(TASK_NAME)) {
                        var position = llallowedFields.indexOf(TASK_NAME)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilTaskName.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilTaskName.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelTaskName.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilTaskName.visibility = View.GONE
                    }
                    var DESCRIPTION = Field(field = FIELD.DESCRIPTION.name)
                    if (llallowedFields.contains(DESCRIPTION)) {

                        var position = llallowedFields.indexOf(DESCRIPTION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.edDescription.visibility = View.VISIBLE
                            mActivityCreateTaskBinding.tvDescription.visibility = View.VISIBLE
                            mActivityCreateTaskBinding.lldescription.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.edDescription.visibility = View.GONE
                            mActivityCreateTaskBinding.tvDescription.visibility = View.GONE
                            mActivityCreateTaskBinding.lldescription.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvDescription.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.edDescription.visibility = View.GONE
                        mActivityCreateTaskBinding.tvDescription.visibility = View.GONE
                        mActivityCreateTaskBinding.lldescription.visibility = View.GONE
                    }
                    var SELECT_FLEET = Field(field = FIELD.SELECT_FLEET.name)
                    if (llallowedFields.contains(SELECT_FLEET)) {
                        var position = llallowedFields.indexOf(SELECT_FLEET)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llfleet.visibility = View.VISIBLE
                            mCreateTaskViewModel.checkFleet(httpManager)
                        } else {
                            mActivityCreateTaskBinding.llfleet.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelFleet.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llfleet.visibility = View.GONE
                    }
                    var SELECT_CITY = Field(field = FIELD.SELECT_CITY.name)
                    if (llallowedFields.contains(SELECT_CITY)) {
                        var position = llallowedFields.indexOf(SELECT_CITY)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llcity.visibility = View.VISIBLE
                        } else {
                            mActivityCreateTaskBinding.llcity.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelCity.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llcity.visibility = View.GONE
                    }
                    var TASK_ID = Field(field = FIELD.TASK_ID.name)
                    if (llallowedFields.contains(TASK_ID)) {
                        var position = llallowedFields.indexOf(TASK_ID)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilTaskId.visibility = View.VISIBLE
                        } else {
                            mActivityCreateTaskBinding.tilTaskId.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelTaskId.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilTaskId.visibility = View.GONE
                    }
                    var REFERENCE_ID = Field(field = FIELD.REFERENCE_ID.name)
                    if (llallowedFields.contains(REFERENCE_ID)) {


                        var position = llallowedFields.indexOf(REFERENCE_ID)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilReference.visibility = View.VISIBLE
                            if (parentReffId != null) {
                                mActivityCreateTaskBinding.edReference.setText(parentReffId)
                                mActivityCreateTaskBinding.edReference.isEnabled = false
                            } else {
                                if (intent.hasExtra("reffId")) {
                                    var reffId = intent.getStringExtra("reffId")
                                    mActivityCreateTaskBinding.edReference.setText(reffId)
                                    mActivityCreateTaskBinding.edReference.isEnabled = true
                                }
                            }
                        } else {
                            mActivityCreateTaskBinding.tilReference.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelReference.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilReference.visibility = View.GONE
                    }


                }

                if (getDynamicFormId(categoryId) != null) {
                    dynamicFragment = NewDynamicFormFragment.getInstance(
                        getDynamicFormId(categoryId),
                        categoryId,
                        isEditable,
                        ArrayList()
                    )
                    replaceFragment(dynamicFragment!!, getDynamicFormId(categoryId))
                }
                dynamicFormsNew = CommonUtils.getFormByFormId(getDynamicFormId(categoryId))
                if (dynamicFormsNew != null && dynamicFormsNew!!.fields != null && dynamicFormsNew!!.fields!!.isNotEmpty()) {
                    try {
                        var formData: FormData? =
                            dynamicFormsNew!!.fields!!.filter { s -> s.type == DataType.BUTTON }
                                .filter { data -> data.actionConfig?.action == Type.DISPOSE || data.actionConfig?.action == Type.FORM || data.actionConfig?.action == Type.API }
                                .single()
                        formData?.let {
                            it.value?.let {
                                btnCLick.text = formData.value
                            }
                        }
                    } catch (e: java.lang.Exception) {

                    }

                } else {
                    btnCLick.text = "Create"
                }
            }

        })

//        var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        var arrayAdapter =
            object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, categories) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    val externalFont =
                        Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                    (v as TextView).typeface = externalFont
                    return v
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val v = super.getDropDownView(position, convertView, parent)
                    val externalFont =
                        Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                    (v as TextView).typeface = externalFont
                    //v.setBackgroundColor(Color.GREEN);
                    return v
                }
            }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCategory!!.adapter = arrayAdapter
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            var categoryMap: Map<String, String>? = null
            val str: String = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)!!
            categoryMap =
                Gson().fromJson(str, object : TypeToken<HashMap<String?, String?>?>() {}.type)
            if (categoryMap != null && categoryMap!!.containsKey("categoryId")) {
                categoryId = categoryMap!!.get("categoryId")
                if (categoryId != null) {
                    for (i in list.indices) {
                        if (list[i].categoryId == categoryId) {
                            selItem = list[i].name!!
                            spnCategory!!.setSelection(i)
                            break
                        }
                    }
                }
            }
        }
        if (intent.hasExtra("from")) {
            if (intent.getStringExtra("from")!!
                    .equals("taskListing") || intent.getStringExtra("from")!!.equals("dashBoard")
            ) {
                cardViewCategoryId.visibility = View.GONE
                val listCategory = preferencesHelper.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData = listCategory[position]
                            allowGeography = myCatData.allowGeography
                        }
                    }
                }
                mCategoryId.value = categoryId
                setToolbar(mActivityCreateTaskBinding.toolbar, "Create " + getLabelName(categoryId))

            }
        } else {
            cardViewCategoryId.visibility = View.VISIBLE
            setToolbar(mActivityCreateTaskBinding.toolbar, getString(R.string.create_task))

        }

        // mCategoryId.value = "0"
        spnCategory!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // val selectedItem = parent.getItemAtPosition(position).toString()
                categoryId = list[position].categoryId
                selItem = list[position].name!!
                mCategoryId.value = categoryId
                val listCategory = preferencesHelper.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData = listCategory[position]
                            allowGeography = myCatData.allowGeography
                        }
                    }
                }
                CommonUtils.showLogMessage("e", "categoryId", categoryId);


            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        val taskActions: Array<String?>
        lookUps = TrackiApplication.getLookups()
        if (lookUps != null && lookUps?.isNotEmpty()!!) {
            taskActions = Array(lookUps!!.size) { null }
//            taskActions[0] = "Select Task Type"
            for (i in lookUps?.indices!!) {
                taskActions[i] = lookUps!![i].value!!
            }
            Log.e(NewCreateTaskActivity.TAG, "array items---->> $taskActions")
            try {
                val aa = ArrayAdapter(this, R.layout.item_simple_spinner, taskActions)
                aa.setDropDownViewResource(R.layout.item_simple_spinner_dropdown)
                //spinnerTaskAction.adapter = aa
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            // spinnerTaskAction.visibility = View.GONE
        }

        val taskName = "Task created at ${
            DateTimeUtil.getFormattedTime(
                System.currentTimeMillis(),
                DateTimeUtil.DATE_TIME_FORMAT_2
            )
        }"
        edTaskName.setText(taskName)
        edTaskName.setSelection(edTaskName.text.length)



        if (intent != null) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_EDIT_TASK)) {
                task = intent.getSerializableExtra(AppConstants.Extra.EXTRA_EDIT_TASK) as Task

                val contact = task.contact
                if (contact != null) {
                    if (contact.name != null) {
                        edContactName.setText(contact.name)
                    }
                    if (contact.mobile != null) {
                        edMobileNumber.setText(contact.mobile)
                    }
                }
                edTaskName.setText(task.taskName)
                edTaskName.setSelection(task.taskName?.length!!)

                val calendar = Calendar.getInstance()
                startTime = task.startTime
                calendar.timeInMillis = startTime

                startDay = calendar.get(Calendar.DAY_OF_MONTH)
                startMonth = calendar.get(Calendar.MONTH)
                startYear = calendar.get(Calendar.YEAR)
                startHour = calendar.get(Calendar.HOUR_OF_DAY)
                startMinute = calendar.get(Calendar.MINUTE)

                val selectedDate = "$startDay-${startMonth + 1}-$startYear"
                val formattedTime = DateTimeUtil.getFormattedTime(calendar.time!!)
                edStartDateTime.setText("$selectedDate | $formattedTime")
                edStartDateTime.setSelection("$selectedDate | $formattedTime".length)

                val cala = Calendar.getInstance()
                if (task.endTime != 0L) {
                    endTime = task.endTime
                    cala.timeInMillis = endTime
                }
                endDay = cala.get(Calendar.DAY_OF_MONTH)
                endMonth = cala.get(Calendar.MONTH)
                endYear = cala.get(Calendar.YEAR)
                endHour = cala.get(Calendar.HOUR_OF_DAY)
                endMinute = cala.get(Calendar.MINUTE)

                val selectedEndDate = "$endDay-${endMonth + 1}-$endYear"
                val formattedEndTime = DateTimeUtil.getFormattedTime(cala.time!!)
                edEndDateTime.setText("$selectedEndDate | $formattedEndTime")
                edEndDateTime.setSelection("$selectedEndDate | $formattedEndTime".length)

                edStartLocation.setText(task.source?.address)
                edStartLocation.setSelection(task.source?.address?.length!!)
                startLatLng =
                    LatLng(task.source?.location?.latitude!!, task.source?.location?.longitude!!)
                if (task.destination != null && task.destination?.location != null) {
                    endLatLng = LatLng(
                        task.destination?.location?.latitude!!,
                        task.destination?.location?.longitude!!
                    )
                    edEndLocation.setText(task.destination?.address)
                    edEndLocation.setSelection(task.destination?.address?.length!!)
                }
                if (task.description != null) {
                    edDescription.setText(task.description)
                    edDescription.setSelection(task.description?.length!!)
                }

                buddyId = task.buddyDetail?.buddyId
                fleetId = task.fleetId
                buddyName = task.buddyDetail?.name
                taskId = task.taskId!!

            }
            if (intent.hasExtra(AppConstants.Extra.SELECTED_BUDDY_LIST_EXTRA) &&
                intent.getSerializableExtra(AppConstants.Extra.SELECTED_BUDDY_LIST_EXTRA) != null
            ) {
                @Suppress("UNCHECKED_CAST")
                val list =
                    intent.getSerializableExtra(AppConstants.Extra.SELECTED_BUDDY_LIST_EXTRA) as ArrayList<Buddy>
                if (list.size > 0) {
                    buddyId = list[0].buddyId!!
                    buddyName = list[0].name!!
                }
            }
            if (intent.hasExtra(AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA) &&
                intent.getSerializableExtra(AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA) != null
            ) {
                @Suppress("UNCHECKED_CAST")
                val list =
                    intent.getSerializableExtra(AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA) as ArrayList<Fleet>
                if (list.size > 0) {
                    fleetId = list[0].fleetId!!
                }
            }
        }
    }

    private fun performSearchWidgetTask() {
        if (assigneeLabel != null) {
            tvTitleSelectUser.text = assigneeLabel
            //tvLabelUser.text="Selected $assigneeLabel : "
            rlSelectedUser.visibility = View.GONE

        }
        mActivityCreateTaskBinding.etUser.setText("")
        selectedUsername = ""
        selectedUserId = ""
        mActivityCreateTaskBinding.etUser.threshold = 2
        var suggestionAdapter = BaseAutocompleteAdapter(this)
        suggestionAdapter.setViewModel(mGetSuggetionViewModel)
        suggestionAdapter.setHttpManager(httpManager)
        if (requestUserType != null)
            suggestionAdapter.setUserType(requestUserType!!)

        if (TrackiApplication.getApiMap()[ApiType.GET_SEARCH_USER] != null) {
            val api = TrackiApplication.getApiMap()[ApiType.GET_SEARCH_USER]
            suggestionAdapter.setApi(api)
        }
        mActivityCreateTaskBinding.etUser.setAdapter(suggestionAdapter)
        mActivityCreateTaskBinding.etUser.setLoadingIndicator(
            findViewById<View>(R.id.pb_loading_indicator) as ProgressBar
        )
//
    }

    override fun userSelected(dataUser: ClientData) {
        var name = ""
        if (dataUser.firstName != null && dataUser.lastName != null) {
            name = dataUser.firstName + " " + dataUser.lastName
        } else if (dataUser.firstName != null) {
            name = dataUser.firstName
        } else if (dataUser.lastName != null) {
            name = dataUser.lastName
        }
        selectedUsername = name
        if (dataUser.userId != null) {
            selectedUserId = dataUser.userId
            CommonUtils.showLogMessage("e", "selectedUserId", dataUser.userId)
        }

        mActivityCreateTaskBinding.etUser.setText("")
        var ivUser = rlSelectedUser.findViewById<ImageView>(R.id.ivUser)
        var tvName = rlSelectedUser.findViewById<TextView>(R.id.tvName)
        var tvMobile = rlSelectedUser.findViewById<TextView>(R.id.tvMobile)
        tvName.setText(name)
        if (dataUser.mobile != null)
            tvMobile.setText(dataUser.mobile)
        else
            tvMobile.setText("")
        if (dataUser.profileImg != null && dataUser.profileImg != "") {
            GlideApp.with(this)
                .asBitmap()
                .load(dataUser.profileImg)
                .apply(
                    RequestOptions()
                        .transform(CircleTransform())
                        .placeholder(R.drawable.ic_my_profile)
                )
                .error(R.drawable.ic_my_profile)
                .into(ivUser)
        } else {
            ivUser.setImageResource(R.drawable.ic_my_profile)
        }
        rlSelectedUser.visibility = View.VISIBLE
    }

    override fun handleUserNotFound() {
        openDialogUserNotFound()
    }
    private fun openDialogUserNotFound() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.dialog_user_not_found)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)

        val imageView = dialog.findViewById<View>(R.id.iv_cancel) as ImageView
        val add = dialog.findViewById<View>(R.id.btnPositive) as Button
        val noThanks = dialog.findViewById<View>(R.id.btnNegative) as Button

        dialog.window!!.attributes = lp
        imageView.setOnClickListener { dialog.dismiss() }
        noThanks.setOnClickListener { dialog.dismiss() }
        add.setOnClickListener { dialog.dismiss()
            addUserScreen()
        }
        if (!dialog.isShowing) dialog.show()
    }
    fun addUserScreen(){
        var intent = Intent(this, AddCustomerActivity::class.java)
        intent.putExtra("from", AppConstants.EMPLOYEES)
        intent.putExtra("action", "add")
        if(!requestUserType.isNullOrEmpty())
        intent.putStringArrayListExtra(AppConstants.REQUESTED_USER_TYPES,
            requestUserType as java.util.ArrayList<String>?
        )
        startActivity(intent)
    }

    /**
     * Method used to add fragment and show it to user.
     *
     * @param fragment fragment that needs to be added/replaced.
     */
    private fun replaceFragment(fragment: Fragment, fragmentName: String?) {
        val formName = CommonUtils.getFormByFormId(fragmentName)
        //  CommonUtils.showLogMessage("e", "formid", fragmentName);
        if (formName?.name != null) {
            mActivityCreateTaskBinding?.toolbar?.title = formName.name
        } else {
            mActivityCreateTaskBinding?.toolbar?.title = ""
        }

        val manager = supportFragmentManager

        val ft = manager.beginTransaction()
        if (allowedFieldFirst != null && !allowedFieldFirst!!) {
            CommonUtils.showLogMessage("e", "allowedFieldFirst", "false");
            container.visibility = View.GONE
            ft.replace(R.id.container_second, fragment, fragmentName)
            container_second.visibility = View.VISIBLE
//            val params = container_second.layoutParams as MarginLayoutParams
//            var marginData=-1*CommonUtils.dpToPixel(this,70)
//            params.setMargins(0, 0, 0, marginData)
//            container_second.layoutParams = params

            if (formName == null) {
                container_second.visibility = View.GONE
            }


        } else {
            CommonUtils.showLogMessage("e", "allowedFieldFirst", "true");
            container_second.visibility = View.GONE
            ft.replace(R.id.container, fragment, fragmentName)
            container.visibility = View.VISIBLE
            if (formName == null) {
                container.visibility = View.GONE
            }
        }
        ft.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.e(TAG, "Place: " + place.name + ", " + place.id)
                    if (view.id == R.id.edStartLocation) {
                        startLatLng = place.latLng!!
                        mActivityCreateTaskBinding.edStartLocation.setText(place.name)
                    } else if (view.id == R.id.edEndLocation) {
                        endLatLng = place.latLng!!
                        mActivityCreateTaskBinding.edEndLocation.setText(place.name)
                    } else if (view.id == R.id.edEnterStartLocation) {
                        startLatLng = place.latLng!!
                        mActivityCreateTaskBinding.edEnterStartLocation.setText(place.name)
                    } else if (view.id == R.id.edEnterEndLocation) {
                        endLatLng = place.latLng!!
                        mActivityCreateTaskBinding.edEnterEndLocation.setText(place.name)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i(TAG, status.statusMessage)
                    TrackiToast.Message.showShort(this, status.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> // The user canceled the operation.
                    TrackiToast.Message.showShort(this, "operation cancelled.")
            }
        }


    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<BaseResponse> = JSONConverter()
            var response: BaseResponse =
                jsonConverter.jsonToObject(result.toString(), BaseResponse::class.java)
            if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.PREF_KEY_IS_FORM_DATA_MAP);
            }
            if (response.taskId != null)
                openMainActivity(response.taskId!!)
        }
    }


    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult == null) {
                return
            }
            for (location in locationResult.locations) {
                if (location != null) {
                    removeLocationUpdates(this)
                    startLatLng = LatLng(location.latitude, location.longitude)
                    endLatLng = LatLng(location.latitude, location.longitude)
                    var stringAddress =
                        CommonUtils.getAddress(this@NewCreateTaskActivity, startLatLng)
                    stringAddress?.let {
                        mActivityCreateTaskBinding.edStartLocation.setText(it)
                        mActivityCreateTaskBinding.edEnterStartLocation.setText(it)
                        mActivityCreateTaskBinding.edEnterEndLocation.setText(it)
                        mActivityCreateTaskBinding.edEndLocation.setText(it)
                    }


//
                }
            }
        }
    }


    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 23487
        private val TAG = NewCreateTaskActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, NewCreateTaskActivity::class.java)
    }

    override fun onProcessClick(
        list: ArrayList<FormData>,
        dynamicActionConfig: DynamicActionConfig?,
        currentFormId: String?,
        dfdid: String?
    ) {


        var startAddress = ""
        var endAddress = ""
        if (cardManualStartLocation.visibility == View.GONE) {
            startAddress = edStartLocation.text.toString().trim()
        } else {
            if (edEnterManualStartLocation.visibility == View.VISIBLE)
                startAddress = edEnterManualStartLocation.text.toString().trim()
            else {
                startAddress = edEnterStartLocation.text.toString().trim()
            }
        }
        if (cardManualEndLocation.visibility == View.GONE) {
            endAddress = edEndLocation.text.toString().trim()
        } else {
            if (edEnterManualEndLocation.visibility == View.VISIBLE)
                endAddress = edEnterManualEndLocation.text.toString().trim()
            else {
                endAddress = edEnterEndLocation.text.toString().trim()
            }
        }


//        val buddy = spnBuddy.selectedItem.toString().trim()
//        val client = spnClient.selectedItem.toString().trim()
        val startDateTime = edStartDateTime.text.toString().trim()
        val endDateTime = edEndDateTime.text.toString().trim()
        val pocName = edContactName.text.toString().trim()
        val pocMobile = edMobileNumber.text.toString().trim()
        val taskName = edTaskName.text.toString().trim()
        val referenceId = edReference.text.toString().trim()
        taskId = edTaskId.text.toString().trim()
        val description = edDescription.text.toString().trim()
//        val fleet = spnFleet.selectedItem.toString().trim()
        var GOOGLE_OR_MANUAL_SOURCE=CommonUtils.getTaskAllowedField(categoryId,"GOOGLE_OR_MANUAL_SOURCE",preferencesHelper)
        var GOOGLE_OR_MANUAL_DESTINATION=CommonUtils.getTaskAllowedField(categoryId,"GOOGLE_OR_MANUAL_DESTINATION",preferencesHelper)
        var SYSTEM_LOCATION=CommonUtils.getTaskAllowedField(categoryId,"SYSTEM_LOCATION",preferencesHelper)
//        val city = spnCity.selectedItem.toString().trim()

        if (GOOGLE_OR_MANUAL_SOURCE != null && GOOGLE_OR_MANUAL_SOURCE.visible && GOOGLE_OR_MANUAL_SOURCE.required && regionId == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_SOURCE!!.errMsg.isNullOrBlank()) "Please Select Start Location Region" else GOOGLE_OR_MANUAL_SOURCE!!.errMsg
            )

        } else if (GOOGLE_OR_MANUAL_SOURCE != null && GOOGLE_OR_MANUAL_SOURCE.visible && GOOGLE_OR_MANUAL_SOURCE.required && stateId == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_SOURCE!!.errMsg.isNullOrBlank()) "Please Select Start Location State" else GOOGLE_OR_MANUAL_SOURCE!!.errMsg
            )

        }else if (GOOGLE_OR_MANUAL_SOURCE != null && GOOGLE_OR_MANUAL_SOURCE.visible && GOOGLE_OR_MANUAL_SOURCE.required && cityId == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_SOURCE!!.errMsg.isNullOrBlank()) "Please Select Start Location City" else GOOGLE_OR_MANUAL_SOURCE!!.errMsg
            )

        }
        else if (GOOGLE_OR_MANUAL_SOURCE != null && GOOGLE_OR_MANUAL_SOURCE.visible && GOOGLE_OR_MANUAL_SOURCE.required && hubId == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_SOURCE!!.errMsg.isNullOrBlank()) "Please Select Start Location Hub" else GOOGLE_OR_MANUAL_SOURCE!!.errMsg
            )

        }


        else if (SYSTEM_LOCATION != null && SYSTEM_LOCATION.visible && SYSTEM_LOCATION.required && regionId == null) {
            TrackiToast.Message.showShort(
                this,
                if (SYSTEM_LOCATION!!.errMsg.isNullOrBlank()) "Please Select Start Location Region" else SYSTEM_LOCATION!!.errMsg
            )

        } else if (SYSTEM_LOCATION != null && SYSTEM_LOCATION.visible && SYSTEM_LOCATION.required && stateId == null) {
            TrackiToast.Message.showShort(
                this,
                if (SYSTEM_LOCATION!!.errMsg.isNullOrBlank()) "Please Select Start Location State" else SYSTEM_LOCATION!!.errMsg
            )

        }else if (SYSTEM_LOCATION != null && SYSTEM_LOCATION.visible && SYSTEM_LOCATION.required && cityId == null) {
            TrackiToast.Message.showShort(
                this,
                if (SYSTEM_LOCATION!!.errMsg.isNullOrBlank()) "Please Select Start Location City" else SYSTEM_LOCATION!!.errMsg
            )

        }
        else if (SYSTEM_LOCATION != null && SYSTEM_LOCATION.visible && SYSTEM_LOCATION.required && hubId == null) {
            TrackiToast.Message.showShort(
                this,
                if (SYSTEM_LOCATION!!.errMsg.isNullOrBlank()) "Please Select Start Location Hub" else SYSTEM_LOCATION!!.errMsg
            )

        }

        else if (GOOGLE_OR_MANUAL_DESTINATION != null && GOOGLE_OR_MANUAL_DESTINATION.visible && GOOGLE_OR_MANUAL_DESTINATION.required && regionIdEnd == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_DESTINATION!!.errMsg.isNullOrBlank()) "Please Select End Location Region" else GOOGLE_OR_MANUAL_DESTINATION!!.errMsg
            )

        }
        else if (GOOGLE_OR_MANUAL_DESTINATION != null && GOOGLE_OR_MANUAL_DESTINATION.visible && GOOGLE_OR_MANUAL_DESTINATION.required && stateIdEnd == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_DESTINATION!!.errMsg.isNullOrBlank()) "Please Select End Location State" else GOOGLE_OR_MANUAL_DESTINATION!!.errMsg
            )

        }
        else if (GOOGLE_OR_MANUAL_DESTINATION != null && GOOGLE_OR_MANUAL_DESTINATION.visible && GOOGLE_OR_MANUAL_DESTINATION.required && cityIdEnd == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_DESTINATION!!.errMsg.isNullOrBlank()) "Please Select End Location City" else GOOGLE_OR_MANUAL_DESTINATION!!.errMsg
            )

        }
        else if (GOOGLE_OR_MANUAL_DESTINATION != null && GOOGLE_OR_MANUAL_DESTINATION.visible && GOOGLE_OR_MANUAL_DESTINATION.required && hubIdEnd == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_DESTINATION!!.errMsg.isNullOrBlank()) "Please Select End Location Hub" else GOOGLE_OR_MANUAL_DESTINATION!!.errMsg
            )

        }
       /* if (cardManualStartLocation.visibility == View.VISIBLE && regionId == null) {
            TrackiToast.Message.showShort(this, "Please Select Start Location Region")

        } else if (cardManualStartLocation.visibility == View.VISIBLE && stateId == null) {
            TrackiToast.Message.showShort(this, "Please Select Start Location State")
        } else if (cardManualStartLocation.visibility == View.VISIBLE && cityId == null) {
            TrackiToast.Message.showShort(this, "Please Select Start Location City")

        } else if (cardManualStartLocation.visibility == View.VISIBLE && hubId == null) {
            TrackiToast.Message.showShort(this, "Please Select Start Location Hub")

        }*//* else if (cardManualStartLocation.visibility == View.VISIBLE && startAddress.isEmpty()) {
            TrackiToast.Message.showShort(this,"Start location field cannot be empty")
        }*/ /*else if (cardManualEndLocation.visibility == View.VISIBLE && regionIdEnd == null) {
            TrackiToast.Message.showShort(this, "Please Select End Location  Region")
        } else if (cardManualEndLocation.visibility == View.VISIBLE && stateIdEnd == null) {
            TrackiToast.Message.showShort(this, "Please Select End Location State")
        } else if (cardManualEndLocation.visibility == View.VISIBLE && cityIdEnd == null) {
            TrackiToast.Message.showShort(this, "Please Select End Location City")

        } else if (cardManualEndLocation.visibility == View.VISIBLE && hubIdEnd == null) {
            TrackiToast.Message.showShort(this, "Please Select End Location Hub")
        }*/ /*else if (cardManualEndLocation.visibility == View.VISIBLE && endAddress.isEmpty()) {
            TrackiToast.Message.showShort(this,"End Location field cannot be empty")
        } else if (tilStartLocation.visibility == View.VISIBLE && startAddress.isEmpty()) {
            TrackiToast.Message.showShort(this,"Start location field cannot be empty")
        } */ else if (tilTaskId.visibility == View.VISIBLE && taskId.isEmpty()) {
            TrackiToast.Message.showShort(this, "Task Id field cannot be empty")
        }/* else if (tilEndLocation.visibility == View.VISIBLE && endAddress.isEmpty()) {
            TrackiToast.Message.showShort(this,"End Location field cannot be empty")
        } */ else if (llbuddy.visibility == View.VISIBLE && spnBuddy.selectedItem == null) {
            TrackiToast.Message.showShort(this, "Please Select Your Buddy")
        } else if (llbuddy.visibility == View.VISIBLE && spnBuddy.selectedItem.toString().trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please Select Your Buddy")
        } else if (llClient.visibility == View.VISIBLE && spnClient.selectedItem == null) {
            TrackiToast.Message.showShort(this, "Please Select Client")
        } else if (llClient.visibility == View.VISIBLE && spnClient.selectedItem.toString().trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please Select Client")
        }else if (cardViewClientList.visibility == View.VISIBLE && !::selectedUserId.isInitialized) {
            TrackiToast.Message.showShort(this, "Please Select Requested By")
        } else if (cardViewClientList.visibility == View.VISIBLE && selectedUserId.toString().trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please Select Requested By")
        }  else if (tilStartDateTime.visibility == View.VISIBLE && startDateTime.isEmpty()) {
            TrackiToast.Message.showShort(this, "Start Date & Time field cannot be empty")
        }/* else if ((tilStartDateTime.visibility == View.VISIBLE && tilEndDateTime.visibility == View.VISIBLE) && (startYear == endYear && startMonth == endMonth && startDay == endDay) || (startYear == endYear && startMonth == endMonth && endHour < startHour)) {
            Toast.makeText(this, getString(R.string.end_time_cannot_be_less_than_start_time), Toast.LENGTH_SHORT).show()

        } else if ((tilStartDateTime.visibility == View.VISIBLE && tilEndDateTime.visibility == View.VISIBLE) && (startYear == endYear && startMonth == endMonth && startDay == endDay) || (startYear == endYear && startMonth == endMonth && endHour == startHour && endMinute <= startMinute)) {
            Toast.makeText(this, getString(R.string.end_time_cannot_be_less_than_start_time), Toast.LENGTH_SHORT).show()

        } else if ((tilEndDateTime.visibility == View.VISIBLE && tilStartDateTime.visibility == View.VISIBLE) && (endYear <= startYear && endMonth <= startMonth && endDay < startDay)) {
            Toast.makeText(this, getString(R.string.no_past_date), Toast.LENGTH_SHORT).show()

        }*/ else if (tilEndDateTime.visibility == View.VISIBLE && endDateTime.isEmpty()) {
            TrackiToast.Message.showShort(this, "End Date & Time field cannot be empty")
        } else if ((tilStartDateTime.visibility == View.VISIBLE && tilEndDateTime.visibility == View.VISIBLE) && startTime > endTime) {
            TrackiToast.Message.showShort(
                this,
                getString(R.string.end_time_cannot_be_less_than_start_time)
            )
        } else if (llPOC.visibility == View.VISIBLE && pocName.isEmpty()) {
            TrackiToast.Message.showShort(this, "Contact Person Name field cannot be empty")
        } else if (llPOC.visibility == View.VISIBLE && pocMobile.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                "Contact Person mobile number field cannot be empty"
            )
        } else if (llPOC.visibility == View.VISIBLE && pocMobile.length < 10) {
            TrackiToast.Message.showShort(this, "Mobile Number cannot be less than 10 digit")
        } else if (llPOC.visibility == View.VISIBLE && pocMobile.length < 10) {
            TrackiToast.Message.showShort(this, "Mobile Number cannot be less than 10 digit")
        } else if (tilTaskName.visibility == View.VISIBLE && taskName.isEmpty()) {
            TrackiToast.Message.showShort(this, "Task Name cannot be empty")
        } else if (tilReference.visibility == View.VISIBLE && referenceId.isEmpty()) {
            TrackiToast.Message.showShort(this, "Reference Id cannot be empty")
        } else if (lldescription.visibility == View.VISIBLE && description.isEmpty()) {
            TrackiToast.Message.showShort(this, "Description cannot be empty")
        } else if (llfleet.visibility == View.VISIBLE && spnFleet.selectedItem == null) {
            TrackiToast.Message.showShort(this, "Please select your fleet")
        } else if (llfleet.visibility == View.VISIBLE && spnFleet.selectedItem.toString().trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please select your fleet")
        } else if (llcity.visibility == View.VISIBLE && spnCity.selectedItem == null) {
            TrackiToast.Message.showShort(this, "Please select your city")
        } else if (llcity.visibility == View.VISIBLE && spnCity.selectedItem.toString().trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please select your city")
        } else {
            val startLoc = GeoCoordinates()
            if (this::startLatLng.isInitialized) {
                startLoc.latitude = startLatLng.latitude
                startLoc.longitude = startLatLng.longitude
            }

            var source: com.tracki.data.model.response.config.Place? =
                com.tracki.data.model.response.config.Place()
            var isCategoryContainsStartLocation=CommonUtils.isCategoryContainsFiled(FIELD.START_LOCATION.name,categoryId,preferencesHelper)
            var isCategoryContainsEndLocation=CommonUtils.isCategoryContainsFiled(FIELD.END_LOCATION.name,categoryId,preferencesHelper)
            if (tilStartLocation.visibility == View.VISIBLE || cardManualStartLocation.visibility == View.VISIBLE||isCategoryContainsStartLocation) {
                if (llToggleEnableStartLoc.visibility == View.VISIBLE||isCategoryContainsStartLocation)
                    source?.address = startAddress
                source!!.location = startLoc
            }
            if (cardManualStartLocation.visibility == View.VISIBLE) {
                source!!.regionId = regionId
                source!!.stateId = stateId
                source!!.cityId = cityId
                source!!.hubId = hubId
                source!!.manualLocation = !enableStartLocation.isOn
                if (source!!.manualLocation || llToggleEnableStartLoc.visibility == View.GONE)
                    source!!.location = null

            }

            var destination: com.tracki.data.model.response.config.Place? =
                com.tracki.data.model.response.config.Place()

            if (endLatLng != null && endLatLng?.latitude != 0.0 && endLatLng?.longitude != 0.0) {
                val endLoc = GeoCoordinates()
                endLoc.latitude = endLatLng?.latitude!!
                endLoc.longitude = endLatLng?.longitude!!
                destination!!.address = endAddress
                destination!!.location = endLoc
                if (cardManualEndLocation.visibility == View.VISIBLE) {
                    destination!!.regionId = regionIdEnd
                    destination!!.hubId = hubIdEnd
                    destination!!.stateId = stateIdEnd
                    destination!!.cityId = cityIdEnd
                    destination!!.manualLocation = !enableEndLocation.isOn
                    if (destination!!.manualLocation)
                        destination.location = null

                }

            } else {
                val endLoc = GeoCoordinates()
                endLoc.latitude = 0.0
                endLoc.longitude = 0.0
                destination!!.address = endAddress
                destination!!.location = endLoc
                if (cardManualEndLocation.visibility == View.VISIBLE) {
                    destination!!.regionId = regionIdEnd
                    destination!!.hubId = hubIdEnd
                    destination!!.stateId = stateIdEnd
                    destination!!.cityId = cityIdEnd
                    destination!!.manualLocation = !enableEndLocation.isOn
                    if (destination!!.manualLocation)
                        destination.location = null

                }

            }

            buddyId = if (buddyId != null) buddyId!! else ""
            fleetId = if (fleetId != null) fleetId!! else ""
            var name = preferencesHelper.userDetail?.name
            if (name == null) {
                name = ""
            }
            if (mActivityCreateTaskBinding.tilEndDateTime.visibility == View.GONE) {
                endTime = 0
            }
            if (mActivityCreateTaskBinding.tilEndLocation.visibility == View.GONE&&!isCategoryContainsEndLocation && cardManualEndLocation.visibility == View.GONE) {
                destination = null
            }
            if (tilStartLocation.visibility == View.GONE&&!isCategoryContainsStartLocation && cardManualStartLocation.visibility == View.GONE) {
                source = null
            }


            createTaskRequest = CreateTaskRequest(
                false,
                buddyId!!,
                fleetId!!,
                name,
                description,
                destination,
                source,
                startTime,
                taskName,
                taskId,
                endTime,
                Contact(pocName, pocMobile),
                spinnerVal
            )
            if (intent.hasExtra("directMapping")) {
                createTaskRequest!!.directMapping = intent.getBooleanExtra("directMapping", false)
            }
            if (intent.hasExtra("invIds")) {
                createTaskRequest!!.invIds = intent.getStringArrayListExtra("invIds")
            }
            if (intent.hasExtra("from")) {
                if (intent.getStringExtra("from").equals("taskListing")) {
                    if (parentReffId != null) {
                        createTaskRequest!!.referenceId = parentReffId

                    } else {
                        createTaskRequest!!.referenceId = referenceId
                    }
                    if (parentTaskId != null) {
                        createTaskRequest!!.parentTaskId = parentTaskId
                    }

                } else if (intent.getStringExtra("from").equals("dashBoard")) {
                    createTaskRequest!!.referenceId = referenceId
                }
            } else {
                createTaskRequest!!.referenceId = referenceId
            }
            if (::selectedUserId.isInitialized && selectedUserId.isNotEmpty())
                createTaskRequest!!.requestBy = selectedUserId
            if (categoryId != null)
                createTaskRequest!!.categoryId = categoryId
            hideKeyboard()
            //showLoading()
            var json = JSONConverter<CreateTaskRequest>().objectToJson(createTaskRequest)
            CommonUtils.showLogMessage("e", "jsondata", json)

            var bundle = Bundle();
            bundle.putSerializable("CREATE_TASK_REQUEST", createTaskRequest);
            Log.e(TAG, "config type----> ${dynamicActionConfig?.action} ")
            if (mainMap == null) {
                mainMap = HashMap()
            }
            //add form data after validation into the map
            if (list.isNotEmpty()) {
                mainMap?.set(currentFormId!!, list)
                var jsonConverter = JSONConverter<HashMap<String, ArrayList<FormData>>>()
                var data = jsonConverter.objectToJson(mainMap!!)
                CommonUtils.showLogMessage("e", "allowed field", data.toString())


//                if (mainMap == null) {
//                    mainMap = HashMap()
//                }
//                //add form data after validation into the map
//                mainMap?.set(currentFormId!!, list)
            }
            var jsonConverter = JSONConverter<ArrayList<FormData>>();
            Log.e(DynamicFormActivity.TAG, jsonConverter.objectToJson(list))
            // replace a new fragment to the container
            if (dynamicActionConfig?.action == Type.FORM) {
                showLoading()
                //make the back button visible if user click on next form
                replaceFragment(
                    NewDynamicFormFragment.getInstance(
                        dynamicActionConfig.target!!,
                        taskId!!,
                        isEditable,
                        ArrayList()
                    ), dynamicActionConfig.target!!
                )
                hideLoading()
            } else if (dynamicActionConfig?.action == Type.API) {
                mainData = ArrayList()
                //add all the values of map to the hashMap and go to all
                // elements to get the required data
                for ((_, value) in mainMap!!) {
                    mainData?.addAll(value)
                }
                //if form data hashMap is not empty then we get all the image file
                // and upload the data to the server and get the respective urls in string format.
                if (mainData?.isNotEmpty()!!) {
                    val hashMapFileRequest = HashMap<String, ArrayList<File>>()
                    for (i in mainData?.indices!!) {

                        val v = mainData!![i].file
                        if (v != null && v.isNotEmpty()) {
                            //remove last element only if type is camera else
                            // SIGNATURE OR FILE always contains single image
                            if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                //  Log.e("path",v[i].path)
                                v.removeAt(0)
                            }
                            if (mainData!![i].type == DataType.CAMERA && v.size>0&&v[v.size-1].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                //  Log.e("path",v[i].path)
                                v.removeAt(v.size-1)
                            }
                            if (v.isNotEmpty()) {
                                var listIterator = v.listIterator()
                                while (listIterator.hasNext()) {
                                    if (!listIterator.next().exists()) {
                                        listIterator.remove()
                                    }
                                }
                            }
                            if (v.isNotEmpty()) {
                                val key = mainData!![i].name!!
                                hashMapFileRequest[key] = v
                            }

                        }
                        Log.e(
                            "NewCreateTaskActivity", mainData!![i].name + "<------->"
                                    + mainData!![i].enteredValue
                        )
                    }
                    var jsonConverter = JSONConverter<HashMap<String, ArrayList<File>>>();
                    Log.e(DynamicFormActivity.TAG, jsonConverter.objectToJson(hashMapFileRequest))
                    Log.e(DynamicFormActivity.TAG, "Size =>" + hashMapFileRequest.size)
                    if (hashMapFileRequest.isNotEmpty()) {


//                        val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
//                        mCreateTaskViewModel.uploadFileList(hashMapFileRequest, httpManager, api, true)
                        if (NetworkUtils.isNetworkConnected(this@NewCreateTaskActivity)) {
                            if (NetworkUtils.isConnectedFast(this@NewCreateTaskActivity)) {
                                mActivityCreateTaskBinding.btnCLick.visibility = View.GONE
                                mActivityCreateTaskBinding.viewProgress.visibility = View.VISIBLE
                                CommonUtils.makeScreenDisable(this)
                                fileUploadCounter = 0
                                nestedScrollView.postDelayed(
                                    { nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                                    200
                                )
                                count = 0
                                val thread = HandlerThread("workkker")
                                thread.start()
                                //start a handler
                                mDynamicHandler = DynamicHandler(thread.looper, dynamicActionConfig)
//                        start a thread other than main
                                handlerThread?.setRequestParams(
                                    mDynamicHandler!!,
                                    hashMapFileRequest,
                                    httpManager,
                                    null
                                )
                                //start thread
                                handlerThread?.start()
                            } else {
                                CommonUtils.showSnakbarForNetworkSettings(
                                    this@NewCreateTaskActivity,
                                    nestedScrollView,
                                    AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE
                                )
                            }

                        } else {
                            // TrackiToast.Message.showShort(this, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
                        }
                    } else {
                        finalApiHit()
                    }
                }
            } else if (dynamicActionConfig?.action == Type.DISPOSE) {

                mainData = ArrayList()
                //add all the values of map to the hashMap and go to all
                // elements to get the required data
                for ((_, value) in mainMap!!) {
                    mainData?.addAll(value)
                }
                //if form data hashMap is not empty then we get all the image file
                // and upload the data to the server and get the respective urls in string format.
                if (mainData != null && mainData!!.isNotEmpty()!!) {
                    val hashMapFileRequest = HashMap<String, ArrayList<File>>()
                    for (i in mainData?.indices!!) {

                        val v = mainData!![i].file
                        if (v != null && v.isNotEmpty()) {
                            //remove last element only if type is camera else
                            // SIGNATURE OR FILE always contains single image
                            if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                // Log.e("path",v[i].path)

                                v.removeAt(0)
                            }
                            if (mainData!![i].type == DataType.CAMERA &&v.size>0&& v[v.size-1].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                //  Log.e("path",v[i].path)
                                v.removeAt(v.size-1)
                            }
                            if (v.isNotEmpty()) {
                                var listIterator = v.listIterator()
                                while (listIterator.hasNext()) {
                                    if (!listIterator.next().exists()) {
                                        listIterator.remove();
                                    }
                                }
                            }



                            if (v.isNotEmpty()) {
                                val key = mainData!![i].name!!
                                hashMapFileRequest[key] = v
                            }
                        }
                        Log.e(
                            "NewCreateTaskActivity", mainData!![i].name + "<------->"
                                    + mainData!![i].enteredValue
                        )
                    }
                    var jsonConverter = JSONConverter<HashMap<String, ArrayList<File>>>();
                    Log.e(DynamicFormActivity.TAG, jsonConverter.objectToJson(hashMapFileRequest))
                    Log.e(DynamicFormActivity.TAG, "Size =>" + hashMapFileRequest.size)
                    if (hashMapFileRequest.isNotEmpty()) {


//                        val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
//                        mCreateTaskViewModel.uploadFileList(hashMapFileRequest, httpManager, api, false)
                        //uplo
                        if (NetworkUtils.isNetworkConnected(this@NewCreateTaskActivity)) {

                            if (NetworkUtils.isConnectedFast(this@NewCreateTaskActivity)) {
                                count = 0
                                Log.e(TAG, "worker thread open")
                                // showLoading()
                                mActivityCreateTaskBinding.btnCLick.visibility = View.GONE
                                mActivityCreateTaskBinding.viewProgress.visibility = View.VISIBLE
                                CommonUtils.makeScreenDisable(this)
                                nestedScrollView.postDelayed(
                                    { nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                                    200
                                )
                                fileUploadCounter = 0
                                val thread = HandlerThread("workkker")
                                thread.start()
                                //start a handler
                                mDynamicHandler = DynamicHandler(thread.looper, dynamicActionConfig)
//                        start a thread other than main
                                handlerThread?.setRequestParams(
                                    mDynamicHandler!!,
                                    hashMapFileRequest,
                                    httpManager,
                                    null
                                )
                                //start thread
                                handlerThread?.start()
                            } else {
                                CommonUtils.showSnakbarForNetworkSettings(
                                    this@NewCreateTaskActivity,
                                    nestedScrollView,
                                    AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE
                                )
                            }


                        } else {
                            // TrackiToast.Message.showShort(this, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
                        }

                    } else {
                        showLoading()
                        perFormCreateTask()
                    }
                } else {
                    showLoading()
                    perFormCreateTask()
                }

            }


        }
    }

    private fun perFormCreateTask() {
        if (dynamicFormsNew != null) {
            var dynamicFormMainData = CommonUtils.createFormData(
                mainData, selItem, taskId!!, dynamicFormsNew!!.formId,
                dynamicFormsNew!!.version
            )
            Log.e(TAG, "Final Dynamic Form Data-----> $dynamicFormMainData")

            var taskData = dynamicFormMainData!!.taskData
            createTaskRequest!!.taskData = taskData
            createTaskRequest!!.dfId = getDynamicFormId(categoryId)
        }
        if (categoryId != null)
            createTaskRequest!!.categoryId = categoryId


        var jsonConverter = JSONConverter<CreateTaskRequest>()
        var json = jsonConverter.objectToJson(createTaskRequest)
        CommonUtils.showLogMessage("e", "task data", json)
        if (NetworkUtils.isNetworkConnected(this@NewCreateTaskActivity)) {
            var apiType = ApiType.CREATE_TASK
            var api = TrackiApplication.getApiMap()[apiType]
            mCreateTaskViewModel.createTaskApi(httpManager, createTaskRequest!!, api!!)
        } else {
            hideLoading()
            TrackiToast.Message.showShort(
                this@NewCreateTaskActivity,
                getString(R.string.no_internet)
            )
        }

    }

    private fun finalApiHit() {

        var dynamicFormMainData = CommonUtils.createFormData(
            mainData, selItem, taskId!!, dynamicFormsNew!!.formId,
            dynamicFormsNew!!.version
        )
        Log.e(TAG, "Final Dynamic Form Data-----> $dynamicFormMainData")

        if (NetworkUtils.isNetworkConnected(this@NewCreateTaskActivity)) {
            showLoading()
            val api = TrackiApplication.getApiMap()[ApiType.UPDATE_TASK_DATA]
            mCreateTaskViewModel.uploadTaskData(dynamicFormMainData, httpManager, api)
        } else {
            hideLoading()
            TrackiToast.Message.showShort(
                this@NewCreateTaskActivity,
                getString(R.string.no_internet)
            )
        }
    }

    override fun upLoadFileApiResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@NewCreateTaskActivity)) {
            val fileUrlsResponse = Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
            val fileResponseMap = fileUrlsResponse.filesUrl
            if (mainData != null) {
                for (i in mainData!!.indices) {
                    var formData = mainData!![i]
                    if (fileResponseMap!!.containsKey(formData.name)) {
                        var fileUrlList: java.util.ArrayList<String> = java.util.ArrayList<String>()
                        fileUrlList = fileResponseMap[formData.name]!!
                        val commaSeperatedString = fileUrlList.joinToString { it -> it }
                        formData.enteredValue = commaSeperatedString
                    }

                }
                finalApiHit()
            }
        } else {
            finalApiHit()
        }

    }

    override fun upLoadFileDisposeApiResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this@NewCreateTaskActivity)) {
            val fileUrlsResponse = Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
            val fileResponseMap = fileUrlsResponse.filesUrl
            if (mainData != null) {
                for (i in mainData!!.indices) {
                    var formData = mainData!![i]
                    if (fileResponseMap!!.containsKey(formData.name)) {
                        var fileUrlList: java.util.ArrayList<String> = java.util.ArrayList<String>()
                        fileUrlList = fileResponseMap[formData.name]!!
                        val commaSeperatedString = fileUrlList.joinToString { it -> it }
                        formData.enteredValue = commaSeperatedString
                    }

                }
                perFormCreateTask()
            }
        } else {
            perFormCreateTask()
        }
    }

    override fun handleRegionListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            executive?.data?.let {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        buddyList
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            return v
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    if (isStart) {
                        if (list.size == 1) {
                            tvLabelSelectStartArea.visibility = View.GONE
                            spnCategoryStartArea.visibility = View.GONE
                            cvspnCategoryStartArea.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectStartArea)
//                            CommonUtils.hideViewWithAnimation(spnCategoryStartArea)
                            regionId = list[0].id
                            CommonUtils.showLogMessage("e", "regionId", regionId);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionId
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryStartState.adapter = null
                            stateId = null
                            mCreateTaskViewModel.getStateList(
                                httpManager,
                                getUserManualLocationData,
                                true
                            )
                        } else {
                            spnCategoryStartArea.visibility = View.VISIBLE
                            cvspnCategoryStartArea.visibility = View.VISIBLE
                            tvLabelSelectStartArea.visibility = View.VISIBLE
                            spnCategoryStartArea!!.adapter = arrayAdapter
                            spnCategoryStartArea!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        regionId = list[position].id
                                        CommonUtils.showLogMessage("e", "regionId", regionId);
                                        var getUserManualLocationData = GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionId
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryStartState.adapter = null
                                        stateId = null
                                        mCreateTaskViewModel.getStateList(
                                            httpManager,
                                            getUserManualLocationData,
                                            true
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }


                    } else {
                        if (list.size == 1) {
                            spnCategoryEndArea.visibility = View.GONE
                            cvspnCategoryEndArea.visibility = View.GONE
                            tvLabelSelectEndArea.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryEndArea)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectEndArea)
                            regionIdEnd = list[0].id
                            CommonUtils.showLogMessage("e", "regionIdEnd", regionIdEnd);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionIdEnd
                            spnCategoryEndState.adapter = null
                            stateIdEnd = null
                            mCreateTaskViewModel.getStateList(
                                httpManager,
                                getUserManualLocationData,
                                false
                            )
                        } else {
                            spnCategoryEndArea.visibility = View.VISIBLE
                            cvspnCategoryEndArea.visibility = View.VISIBLE
                            tvLabelSelectEndArea.visibility = View.VISIBLE
                            spnCategoryEndArea!!.adapter = arrayAdapter
                            spnCategoryEndArea!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        regionIdEnd = list[position].id
                                        CommonUtils.showLogMessage("e", "regionIdEnd", regionIdEnd);
                                        var getUserManualLocationData = GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionIdEnd
                                        spnCategoryEndState.adapter = null
                                        stateIdEnd = null
                                        mCreateTaskViewModel.getStateList(
                                            httpManager,
                                            getUserManualLocationData,
                                            false
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    }


                } else {
                }

            }
        } else {
            if (isStart) {
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    cardManualStartLocation.visibility = View.GONE
                }
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    cardManualEndLocation.visibility = View.GONE
                }
            }
        }

    }

    override fun handleStateListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            executive?.data?.let {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        buddyList
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            return v
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    if (isStart) {
                        if (list.size == 1) {
                            spnCategoryStartState.visibility = View.GONE
                            cvspnCategoryStartState.visibility = View.GONE
                            tvLabelSelectStartState.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryStartState)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectStartState)
                            stateId = list[0].id
                            CommonUtils.showLogMessage("e", "stateId", stateId);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionId
                            getUserManualLocationData.stateId = stateId
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryStartCity.adapter = null
                            cityId = null
                            mCreateTaskViewModel.getCityList(
                                httpManager,
                                getUserManualLocationData,
                                true
                            )

                        } else {
                            spnCategoryStartState.visibility = View.VISIBLE
                            cvspnCategoryStartState.visibility = View.VISIBLE
                            tvLabelSelectStartState.visibility = View.VISIBLE
                            spnCategoryStartState!!.adapter = arrayAdapter
                            spnCategoryStartState!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        stateId = list[position].id
                                        CommonUtils.showLogMessage("e", "stateId", stateId);
                                        var getUserManualLocationData = GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionId
                                        getUserManualLocationData.stateId = stateId
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryStartCity.adapter = null
                                        cityId = null
                                        mCreateTaskViewModel.getCityList(
                                            httpManager,
                                            getUserManualLocationData,
                                            true
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    } else {
                        if (list.size == 1) {
                            spnCategoryEndState.visibility = View.GONE
                            cvspnCategoryEndState.visibility = View.GONE
                            tvLabelSelectEndState.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryEndState)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectEndState)
                            stateIdEnd = list[0].id
                            CommonUtils.showLogMessage("e", "stateIdEnd", stateIdEnd);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionIdEnd
                            getUserManualLocationData.stateId = stateIdEnd
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryEndCity.adapter = null
                            cityIdEnd = null
                            mCreateTaskViewModel.getCityList(
                                httpManager,
                                getUserManualLocationData,
                                false
                            )
                        } else {
                            spnCategoryEndState.visibility = View.VISIBLE
                            cvspnCategoryEndState.visibility = View.VISIBLE
                            tvLabelSelectEndState.visibility = View.VISIBLE
                            spnCategoryEndState!!.adapter = arrayAdapter
                            spnCategoryEndState!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        stateIdEnd = list[position].id
                                        CommonUtils.showLogMessage("e", "stateIdEnd", stateIdEnd);
                                        var getUserManualLocationData = GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionIdEnd
                                        getUserManualLocationData.stateId = stateIdEnd
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryEndCity.adapter = null
                                        cityIdEnd = null
                                        mCreateTaskViewModel.getCityList(
                                            httpManager,
                                            getUserManualLocationData,
                                            false
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    }


                } else {
                }

            }
        } else {
            if (isStart) {
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    // cardManualStartLocation.visibility = View.GONE
                }
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    // cardManualEndLocation.visibility = View.GONE
                }
            }
        }

    }

    override fun handleCityListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            executive?.data?.let {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        buddyList
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            return v
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    if (isStart) {
                        if (list.size == 1) {
                            spnCategoryStartCity.visibility = View.GONE
                            cvspnCategoryStartCity.visibility = View.GONE
                            tvLabelSelectStartCity.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryStartCity)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectStartCity)
                            cityId = list[0].id
                            CommonUtils.showLogMessage("e", "cityId", cityId);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionId
                            getUserManualLocationData.stateId = stateId
                            getUserManualLocationData.cityId = cityId
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryStartHub.adapter = null
                            hubId = null
                            mCreateTaskViewModel.getHubList(
                                httpManager,
                                getUserManualLocationData,
                                true
                            )
                        } else {
                            spnCategoryStartCity.visibility = View.VISIBLE
                            cvspnCategoryStartCity.visibility = View.VISIBLE
                            tvLabelSelectStartCity.visibility = View.VISIBLE
                            spnCategoryStartCity!!.adapter = arrayAdapter
                            spnCategoryStartCity!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        cityId = list[position].id
                                        CommonUtils.showLogMessage("e", "cityId", cityId);
                                        var getUserManualLocationData = GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionId
                                        getUserManualLocationData.stateId = stateId
                                        getUserManualLocationData.cityId = cityId
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryStartHub.adapter = null
                                        hubId = null
                                        mCreateTaskViewModel.getHubList(
                                            httpManager,
                                            getUserManualLocationData,
                                            true
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    } else {
                        if (list.size == 1) {
                            spnCategoryEndCity.visibility = View.GONE
                            cvspnCategoryEndCity.visibility = View.GONE
                            tvLabelSelectEndCity.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryEndCity)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectEndCity)
                            cityIdEnd = list[0].id
                            CommonUtils.showLogMessage("e", "cityIdEnd", cityIdEnd);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionIdEnd
                            getUserManualLocationData.stateId = stateIdEnd
                            getUserManualLocationData.cityId = cityIdEnd
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryEndHub.adapter = null
                            hubIdEnd = null
                            mCreateTaskViewModel.getHubList(
                                httpManager,
                                getUserManualLocationData,
                                false
                            )

                        } else {
                            spnCategoryEndCity.visibility = View.VISIBLE
                            cvspnCategoryEndCity.visibility = View.VISIBLE
                            tvLabelSelectEndCity.visibility = View.VISIBLE
                            spnCategoryEndCity!!.adapter = arrayAdapter
                            spnCategoryEndCity!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        cityIdEnd = list[position].id
                                        CommonUtils.showLogMessage("e", "cityIdEnd", cityIdEnd);
                                        var getUserManualLocationData = GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionIdEnd
                                        getUserManualLocationData.stateId = stateIdEnd
                                        getUserManualLocationData.cityId = cityIdEnd
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryEndHub.adapter = null
                                        hubIdEnd = null
                                        mCreateTaskViewModel.getHubList(
                                            httpManager,
                                            getUserManualLocationData,
                                            false
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    }


                } else {
                }

            }
        } else {
            if (isStart) {
//                CommonUtils.hideViewWithAnimation(spnCategoryStartCity)
//                CommonUtils.hideViewWithAnimation(tvLabelSelectStartHubs)
//                CommonUtils.hideViewWithAnimation(spnCategoryStartHub)
//                CommonUtils.hideViewWithAnimation(tvLabelSelectStartCity)
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1
                    && spnCategoryStartState.adapter != null && spnCategoryStartState.adapter.count > 1
                ) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    //  cardManualStartLocation.visibility=View.GONE
                }

                spnCategoryStartCity.visibility = View.GONE
                cvspnCategoryStartCity.visibility = View.GONE
                tvLabelSelectStartHubs.visibility = View.GONE
                spnCategoryStartHub.visibility = View.GONE
                cvspnCategoryStartHub.visibility = View.GONE
                tvLabelSelectStartCity.visibility = View.GONE
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                    && spnCategoryEndState.adapter != null && spnCategoryEndState.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                  //  cardManualEndLocation.visibility = View.GONE
                }
                spnCategoryEndCity.visibility = View.GONE
                cvspnCategoryEndCity.visibility = View.GONE
                tvLabelSelectEndHubs.visibility = View.GONE
                spnCategoryEndHub.visibility = View.GONE
                cvspnCategoryEndHub.visibility = View.GONE
                tvLabelSelectEndCity.visibility = View.GONE
//                CommonUtils.hideViewWithAnimation(spnCategoryEndCity)
//                CommonUtils.hideViewWithAnimation(tvLabelSelectEndHubs)
//                CommonUtils.hideViewWithAnimation(spnCategoryEndHub)
//                CommonUtils.hideViewWithAnimation(tvLabelSelectEndCity)
            }
        }//spnCategoryStartState


    }

    override fun handleHubListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            executive?.data?.let {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        buddyList
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            return v
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    if (isStart) {
                        if (list.size == 1) {
                            spnCategoryStartHub.visibility = View.GONE
                            cvspnCategoryStartHub.visibility = View.GONE
                            tvLabelSelectStartHubs.visibility = View.GONE
                            hubId = list[0].id
                            //cardManualStartLocation.visibility = View.GONE
                            CommonUtils.showLogMessage("e", "hubId", hubId);
                        } else {
                            spnCategoryStartHub.visibility = View.VISIBLE
                            cvspnCategoryStartHub.visibility = View.VISIBLE
                            tvLabelSelectStartHubs.visibility = View.VISIBLE
                            spnCategoryStartHub!!.adapter = arrayAdapter
                            spnCategoryStartHub!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        hubId = list[position].id
                                        CommonUtils.showLogMessage("e", "hubId", hubId);


                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    } else {
                        if (list.size == 1) {
                            spnCategoryEndHub.visibility = View.GONE
                            cvspnCategoryEndHub.visibility = View.GONE
                            tvLabelSelectEndHubs.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryEndHub)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectEndHubs)
//                            CommonUtils.hideViewWithAnimation(cardManualEndLocation)
                            hubIdEnd = list[0].id
                            CommonUtils.showLogMessage("e", "hubIdEnd", hubIdEnd);
                        } else {
                            spnCategoryEndHub.visibility = View.VISIBLE
                            cvspnCategoryEndHub.visibility = View.VISIBLE
                            tvLabelSelectEndHubs.visibility = View.VISIBLE
                            cardManualEndLocation.visibility = View.VISIBLE
                            spnCategoryEndHub!!.adapter = arrayAdapter
                            spnCategoryEndHub!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        hubIdEnd = list[position].id
                                        CommonUtils.showLogMessage("e", "hubIdEnd", hubIdEnd);


                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    }


                } else {
                }
            }
            if (isStart) {
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1
                ) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    // cardManualStartLocation.visibility = View.GONE
                }
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    // cardManualEndLocation.visibility = View.GONE
                }

            }

        } else {
            if (isStart) {
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1

                ) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    //   cardManualStartLocation.visibility = View.GONE
                }
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    // cardManualEndLocation.visibility = View.GONE
                }

            }

        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    var count = 0
    var fileUploadCounter = 0

    inner class DynamicHandler(looper: Looper, var actionConfig: DynamicActionConfig) :
        Handler(looper) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                2 -> {
                    var obj = message.obj as HandlerObject
                    fileUploadCounter += obj.chunkSize
                    var progressUploadText = "${fileUploadCounter}/${obj.totalSize}"
                    var percentage = ((fileUploadCounter * 100 / obj.totalSize))
                    Log.e(TAG, "progressUploadText=> " + progressUploadText)
                    Log.e(TAG, "percentage=> " + percentage.toString())
                    runOnUiThread {
                        progressBar!!.progress = percentage
                        percentageText!!.text = "$percentage %"
                        currentStatusText!!.text = progressUploadText

                    }


                }
                /*For Success */0 -> {

                if (CommonUtils.stringListHashMap.isNotEmpty()) {

                    //get hashMap from adapter and match the name with key of maps
                    // if found then replace entered value with url of image
                    runOnUiThread {
                        rlProgress!!.visibility = View.GONE
                        rlSubmittingData!!.visibility = View.VISIBLE

                    }

                    if (mainData?.isNotEmpty()!!) {
                        val finalMap = java.util.HashMap<String, String>()
                        for (i in mainData?.indices!!) {
                            try {
                                if (mainData!![i].type != DataType.BUTTON) {
                                    if (CommonUtils.stringListHashMap?.containsKey(mainData!![i].name)!!) {
                                        //Log.e("Upload Form List--->", mainData!![i].name!!)
                                        mainData!![i].enteredValue =
                                            CommonUtils.getCommaSeparatedList(CommonUtils.stringListHashMap[mainData!![i].name])
                                    }
                                    finalMap[mainData!![i].name!!] = mainData!![i].enteredValue!!
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        //assign empty object to map again

                        CommonUtils.stringListHashMap = ConcurrentHashMap()
                        if (actionConfig?.action == Type.API)
                            finalApiHit()
                        else if (actionConfig?.action == Type.DISPOSE) {
                            perFormCreateTask()
                        }
                    }
                } else {
                    Log.e(TAG, "Map is empty...Try Again")
                    handlerThread?.interrupt()
                    hideLoading()
                    CommonUtils.stringListHashMap = ConcurrentHashMap()
                    if (actionConfig?.action == Type.API)
                        finalApiHit()
                    else if (actionConfig?.action == Type.DISPOSE) {
                        perFormCreateTask()
                    }
                }
            }
                /*For Error*/1 -> {
                if (count == 0) {
                    runOnUiThread {
                        mActivityCreateTaskBinding.btnCLick.visibility = View.VISIBLE
                        mActivityCreateTaskBinding.viewProgress.visibility = View.GONE
                        CommonUtils.makeScreenClickable(this@NewCreateTaskActivity)
                    }
                    count++

                    fileUploadCounter = 0
                    // This is where you do your work in the UI thread.
                    // Your worker tells you in the message what to do.
/*
                    if (CommonUtils.errorString != null) {
                        showDialogCancel(
                            CommonUtils.errorString
                        ) { dialog: DialogInterface?, which: Int ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    finish()
                                    CommonUtils.errorString=null
                                }
                                DialogInterface.BUTTON_NEGATIVE ->
                                {
                                    CommonUtils.errorString=null
                                    finish()
                                }// proceed with logic by disabling the related features or quit the app.

                            }
                        }

                    }
*/
                    TrackiToast.Message.showShort(this@NewCreateTaskActivity,AppConstants.UNABLE_TO_PROCESS_REQUEST)
                    //after getting error form the thread we interrupt the thread
                    handlerThread?.interrupt()
                    hideLoading()
                }
            }
            }
        }
    }


    override fun networkAvailable() {
        if (snackBar != null)
            snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            mActivityCreateTaskBinding.coordinatorLayout,
            getString(R.string.please_check_your_internet_connection)
        )
    }


}
