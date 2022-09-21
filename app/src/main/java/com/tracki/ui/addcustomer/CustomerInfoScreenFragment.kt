package com.tracki.ui.addcustomer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.*
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
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
import com.tracki.data.model.request.*
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.LayoutFragmentCustomerInfoBinding
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.ui.custom.CircleTransform
import com.tracki.ui.custom.ExecutorThread
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.dynamicform.dynamicfragment.FormSubmitListener
import com.tracki.ui.newdynamicform.NewDynamicFormFragment
import com.tracki.ui.products.AddProductActivity
import com.tracki.utils.*
import com.tracki.utils.TrackiToast.Message.showShort
import com.tracki.utils.image_utility.Compressor
import com.tracki.utils.image_utility.ImagePicker
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.network.TrackThatCallback
import com.trackthat.lib.models.ErrorResponse
import com.trackthat.lib.models.SuccessResponse
import com.trackthat.lib.models.TrackthatLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_create_task.*
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.collections.ArrayList

class CustomerInfoScreenFragment :
    BaseFragment<LayoutFragmentCustomerInfoBinding, AddUserViewModel>(), AddUserNavigator,
    View.OnClickListener,
    FormSubmitListener {
    init {
        System.loadLibrary("keys")
    }

    external fun getGoogleMapKey(): String?

    private lateinit var addEmployeeRequest: AddEmployeeRequest
    private var updateData: UserData? = null
    private var from: String? = null
    private var userData: UserData? = null
    private var action: String? = null
    private var userId: String? = null
    private var dojTime: Long = 0
    private var dojMinute: Int = 0
    private var dojHour: Int = 0
    private var dojYear: Int = 0
    private var dojMonth: Int = 0
    private var dojDay: Int = 0
    private var dobTime: Long = 0L
    private var dobMinute: Int = 0
    private var dobHour: Int = 0
    private var dobYear: Int = 0
    private var dobMonth: Int = 0
    private var dobDay: Int = 0
    private var placeName: String? = null
    private lateinit var startLatLng: LatLng
    private var roleId: String? = null
    private var actualImage: File? = null
    private var compressedImage: File? = null
    lateinit var mAddUserViewModel: AddUserViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper


    @Inject
    lateinit var httpManager: HttpManager

    private var categoryMap: Map<String, String>? = null
    private var currentLocation: GeoCoordinates? = null
    private var isUpdate: Boolean = false
    private val REQUEST_READ_STORAGE = 3
    private val PICK_IMAGE_FILE_ID = 235
    private var profileImg: String? = null

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: LayoutFragmentCustomerInfoBinding

    private var dynamicFragment: NewDynamicFormFragment? = null
    private var dynamicFormsNew: DynamicFormsNew? = null

    private var mDynamicHandler: DynamicHandler? = null
    private var handlerThread: ExecutorThread? = null

    var titleText: TextView? = null
    var percentageText: TextView? = null
    var currentStatusText: TextView? = null
    var progressBar: ProgressBar? = null
    var rlProgress: RelativeLayout? = null
    var rlSubmittingData: RelativeLayout? = null
    var requestUserType: ArrayList<String>? = null
    var mainData: ArrayList<FormData>? = null
    private var snackBar: Snackbar? = null
    private var mainMap: HashMap<String, ArrayList<FormData>>? = null
    private var dfId: String? = null
    private var dfdId: String? = null
    private var isEditable: Boolean = true


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAddUserViewModel.navigator = this
        if (requireArguments().getString("action") != null) {
            action = requireArguments().getString("action")
        }
        handlerThread = ExecutorThread()
        if (action == "view") {
            setHasOptionsMenu(true)
        } else {
            setHasOptionsMenu(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user_edit_options, menu);
        CommonUtils.applyFontToMenu(menu, baseActivity!!)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                ///Delete user
                onDeleteUser(userData!!)

            }
            R.id.action_edit -> {
                onEditUser()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_fragment_customer_info
    }

    override fun getViewModel(): AddUserViewModel {
        mAddUserViewModel =
            ViewModelProviders.of(this, mViewModelFactory).get(AddUserViewModel::class.java)
        return mAddUserViewModel
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding = viewDataBinding
        uiCompomnent()
        //  binding.btnCLick.setOnClickListener(this)
        binding.ivAddPic.setOnClickListener(this)
        //  binding.ivDeletePic.setOnClickListener(this)
        Places.initialize(baseActivity, getGoogleMapKey()!!)
        mAddUserViewModel.navigator = this
        Log.e("Input outer type: ","${binding.edPassword.inputType}")
        if (arguments != null) {

            if (requireArguments().getString(AppConstants.Extra.EXTRA_CATEGORIES) != null) {
                var str: String =
                    requireArguments().getString(AppConstants.Extra.EXTRA_CATEGORIES, null)
                categoryMap = Gson().fromJson<Map<String, String>>(
                    str,
                    object : TypeToken<HashMap<String?, String?>?>() {}.type
                )
                if (categoryMap != null && categoryMap!!.containsKey("RoleId"))
                    roleId = categoryMap!!.get("RoleId")
            }
        }
        var purpuse = "Add"
        if (action == "view") {
            binding.btnCLick.visibility = View.GONE
            binding.ivAddPic.visibility = View.GONE
            disableAllEditText()
        } else {
            binding.btnCLick.visibility = View.VISIBLE
            binding.ivAddPic.visibility = View.VISIBLE
        }
        userData = requireArguments().getParcelable<UserData>("userData")
        var listRolls = ArrayList<RoleConfigData>()
        var allRoleList = preferencesHelper.roleConfigDataList

        if (requireArguments().containsKey(AppConstants.REQUESTED_USER_TYPES)) {
            requestUserType =
                requireArguments().getStringArrayList(AppConstants.REQUESTED_USER_TYPES)
        }
        if (!requestUserType.isNullOrEmpty()) {

            for (roleId in requestUserType!!) {
                var search = RoleConfigData(roleId = roleId)
                if (allRoleList.contains(search)) {
                    var index = allRoleList.indexOf(search)
                    if (index != -1) {
                        listRolls.add(allRoleList[index])
                    }
                }
            }


        } else {
            listRolls.addAll(allRoleList)
        }
        if (userData != null && userData!!.roleId != null && userData!!.roleId!!.isNotEmpty()) {
            roleId = userData!!.roleId!!
        }
        if (requireArguments().getString("from") != null) {
            from = requireArguments().getString("from")
            if (from.equals(AppConstants.EMPLOYEES)) {
                if (!listRolls.isNullOrEmpty()) {
                    populateSpinner(listRolls as ArrayList<RoleConfigData>)

                }
            } else if (from.equals(AppConstants.CUSTOMERS)) {
                binding.llRole.visibility = View.GONE
            } else if (from.equals(AppConstants.OWNER)) {
                binding.llRole.visibility = View.GONE
            }
            binding.viewModel = mAddUserViewModel
            if (roleId != null && baseActivity != null)
                binding.viewModel!!.performShowHideView(baseActivity!!, preferencesHelper, roleId!!)
            else {
                if (!listRolls.isNullOrEmpty()) {
                    roleId = listRolls[0].roleId
                    dfId = listRolls[0].dfId
                    if (listener != null) {
                        listener!!.onUpdateHeader(listRolls[0].roleName)
                    }
                    if (baseActivity != null)
                        binding.viewModel!!.performShowHideView(
                            baseActivity!!,
                            preferencesHelper,
                            roleId!!
                        )
                }
            }
            if (userData == null) {
                getCurrentLocation()
            }
            if (action == "view") {
                binding.tvLabelRoll.text = "Role"
            }

            binding.edPassword.setOnTouchListener(object : View.OnTouchListener {
                @SuppressLint("NewApi")
                override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                    val DRAWABLE_LEFT = 0
                    val DRAWABLE_TOP = 1
                    val DRAWABLE_RIGHT = 2
                    val DRAWABLE_BOTTOM = 3

                    if (event!!.action == MotionEvent.ACTION_UP) {

                        if (event.rawX >= (binding.edPassword.right) - binding.edPassword.compoundDrawables
                                    [DRAWABLE_RIGHT].bounds.width()
                        ) {
                            Log.e("Input type: ","${binding.edPassword.inputType}")
                            when(binding.edPassword.inputType){
                                129,128 -> {
                                    binding.edPassword.setCompoundDrawablesWithIntrinsicBounds(
                                        null, null,
                                        context!!.getDrawable(R.drawable.ic_invisible_pass), null
                                    )
                                    binding.edPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                    binding.edPassword.setSelection(binding.edPassword.length())
                                    Log.e("Input type (1): ","${binding.edPassword.inputType}")
                                }
                                144 -> {
                                    binding.edPassword.setCompoundDrawablesWithIntrinsicBounds(
                                        null, null,
                                        context!!.getDrawable(R.drawable.ic_visible_pass), null
                                    )
                                    binding.edPassword.inputType =
                                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                    binding.edPassword.setSelection(binding.edPassword.length())
                                    Log.e("Input type(2): ","${binding.edPassword.inputType}")
                                }
                            }
                        }
                    }
                    return false
                }
            })
        }

        if (userData != null) {
            isUpdate = true
            purpuse = "Update"
            binding.btnCLick.text = purpuse

            if (!roleId.isNullOrEmpty()) {
                var roleConfigData = RoleConfigData(roleId = roleId)
                if (listRolls.contains(roleConfigData)) {
                    var index = listRolls.indexOf(roleConfigData)
                    if (index != -1) {
                        dfId = listRolls[index].dfId

                    }
                }

            }
            if (userData!!.userId != null && userData!!.userId!!.isNotEmpty()) {
                userId = userData!!.userId!!
                showLoading()
                mAddUserViewModel.getUserDetail(httpManager, userData!!)
            }
        }

        binding.btnCLick.setOnClickListener {
            if (dynamicFormsNew != null && dynamicFormsNew!!.fields != null && dynamicFormsNew!!.fields!!.isNotEmpty()) {
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


    }

    private fun uiCompomnent() {
        var viewProgress = binding.viewProgress
        titleText = viewProgress.findViewById<TextView>(R.id.tvTitle)
        currentStatusText = viewProgress!!.findViewById<TextView>(R.id.currentStatusText)
        percentageText = viewProgress!!.findViewById<TextView>(R.id.tvPercentage)
        progressBar = viewProgress!!.findViewById<ProgressBar>(R.id.pb_loading)
        rlSubmittingData = viewProgress!!.findViewById<RelativeLayout>(R.id.rlSubmittingData)
        rlProgress = viewProgress!!.findViewById<RelativeLayout>(R.id.rlProgress)
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
                    baseActivity!!.runOnUiThread {
                        progressBar!!.progress = percentage
                        percentageText!!.text = "$percentage %"
                        currentStatusText!!.text = progressUploadText

                    }


                }
                /*For Success */0 -> {

                if (CommonUtils.stringListHashMap.isNotEmpty()) {

                    //get hashMap from adapter and match the name with key of maps
                    // if found then replace entered value with url of image
                    baseActivity!!.runOnUiThread {
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
                        if (actionConfig?.action == Type.DISPOSE) {
                            perFormCreateTask()
                        }
                    }
                } else {
                    Log.e(TAG, "Map is empty...Try Again")
                    handlerThread?.interrupt()
                    hideLoading()
                    CommonUtils.stringListHashMap = ConcurrentHashMap()
                    if (actionConfig?.action == Type.DISPOSE) {
                        perFormCreateTask()
                    }
                }
            }
                /*For Error*/1 -> {
                if (count == 0) {
                    baseActivity!!.runOnUiThread {
                        binding.btnCLick.visibility = View.VISIBLE
                        binding.viewProgress.visibility = View.GONE
                        CommonUtils.makeScreenClickable(baseActivity!!)
                    }
                    count++

                    fileUploadCounter = 0

                    TrackiToast.Message.showShort(
                        baseActivity!!,
                        AppConstants.UNABLE_TO_PROCESS_REQUEST
                    )
                    //after getting error form the thread we interrupt the thread
                    handlerThread?.interrupt()
                    hideLoading()
                }
            }
            }
        }
    }

    private fun showDynamicForm(
        dfId: String?, formadata: ArrayList<DynamicFormData>,
    ) {
        if (dfId == null) {
            dynamicFormsNew = null
            return
        }

        dynamicFragment = NewDynamicFormFragment.getInstance(
            dfId,
            roleId!!,
            isEditable,
            formadata
        )
        dynamicFragment!!.setListener(this)
        replaceFragment(dynamicFragment!!, dfId)
        dynamicFormsNew = CommonUtils.getFormByFormId(dfId)
        if (dynamicFormsNew != null && dynamicFormsNew!!.fields != null && dynamicFormsNew!!.fields!!.isNotEmpty()) {
            try {
                var formData: FormData? =
                    dynamicFormsNew!!.fields!!.filter { s -> s.type == DataType.BUTTON }
                        .filter { data -> data.actionConfig?.action == Type.DISPOSE || data.actionConfig?.action == Type.FORM || data.actionConfig?.action == Type.API }
                        .single()
                if (formData != null) {
                    formData.value?.let {
                        binding.btnCLick.text = formData.value
                    }

                } else {
//                    binding.btnCLick.setOnClickListener {
//                        var dfg=DynamicActionConfig()
//                        dfg.action= Type.DISPOSE
//                        onProcessClick(ArrayList(), dfg,null,null)
//                    }
                }
            } catch (e: java.lang.Exception) {
                binding.btnCLick.setOnClickListener {
                    var dfg = DynamicActionConfig()
                    dfg.action = Type.DISPOSE
                    onProcessClick(ArrayList(), dfg, null, null)
                }
            }

        } else {
            binding.btnCLick.text = "Update"
        }


    }


    private fun replaceFragment(fragment: Fragment, dfId: String?) {
        val formName = CommonUtils.getFormByFormId(dfId)
        val manager = childFragmentManager
        val ft = manager.beginTransaction()
        ft.replace(R.id.container, fragment, dfId)
        binding.container.visibility = View.VISIBLE
        if (formName == null) {
            binding.container.visibility = View.GONE
        }
        ft.commit()
    }


    private fun populateSpinner(listRolls: ArrayList<RoleConfigData>) {
        var roleName: MutableList<String?> = ArrayList()
        for (data in listRolls) {
            roleName.add(data.roleName)
        }

        var arrayAdapter = object :
            ArrayAdapter<String?>(baseActivity!!, android.R.layout.simple_spinner_item, roleName) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val externalFont =
                    Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                (v as TextView).typeface = externalFont
                (v as TextView).setTextColor(
                    ContextCompat.getColor(
                        baseActivity!!,
                        R.color.dropdown_color
                    )
                )
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
        binding.spnRolles!!.adapter = arrayAdapter
        var roleConfigData = RoleConfigData(roleId = roleId)
        if (listRolls.contains(roleConfigData)) {
            var index = listRolls.indexOf(roleConfigData)
            if (index != -1) {
                listRolls[index].isSelected = true
                binding.spnRolles.setSelection(index)

            }
        }
        if (listRolls.isNotEmpty() && listRolls.size == 1) {
            roleId = listRolls[0].roleId
            dfId = listRolls[0].dfId
            showFieldAndForm(roleId, dfId, ArrayList())
            binding.llRole.visibility = View.GONE
            if (listener != null) {
                listener!!.onUpdateHeader(listRolls[0].roleName)
            }
        } else {
            binding.llRole.visibility = View.VISIBLE
        }
        // mCategoryId.value = "0"
        binding.spnRolles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                roleId = listRolls[position].roleId
                dfId = listRolls[position].dfId
                if (listener != null) {
                    listener!!.onUpdateHeader(listRolls[position].roleName)
                }
                showFieldAndForm(roleId, dfId, ArrayList())

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    fun showFieldAndForm(roleId: String?, dfId: String?, dfData: ArrayList<DynamicFormData>) {
        if (baseActivity != null)
            binding.viewModel!!.performShowHideView(baseActivity, preferencesHelper, roleId!!)
        if (baseActivity != null && !dfId.isNullOrEmpty()) {
            showDynamicForm(dfId, dfData)
        } else {
            binding.container.visibility = View.GONE
        }
    }

    fun getCurrentLocation() {
        TrackThat.getCurrentLocation(object : TrackThatCallback() {
            override fun onSuccess(successResponse: SuccessResponse) {
                val loc = successResponse.responseObject as TrackthatLocation
                currentLocation = GeoCoordinates()
                currentLocation!!.latitude = loc.latitude
                currentLocation!!.longitude = loc.longitude
                startLatLng = LatLng(loc.latitude, loc.longitude)
                if (baseActivity != null)
                    placeName = CommonUtils.getAddress(baseActivity, startLatLng)
                binding.edLocation.setText(placeName)
            }

            override fun onError(errorResponse: ErrorResponse) {
                currentLocation = null
            }
        })
    }

    private fun openDialogShowImage(url: String) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_show_image_big)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
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
        val imageView = dialog.findViewById<View>(R.id.ivImages) as ImageView

        Glide.with(requireContext())
            .asBitmap()
            .load(url)
            .error(R.drawable.ic_picture)
            .placeholder(R.drawable.ic_picture)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {
                    imageView.setImageBitmap(resource)
                }
            })
        /*  Glide.with(context!!)
                  .load(url)
                  .error(R.drawable.ic_picture)
                  .placeholder(R.drawable.ic_picture)
                  .into(imageView)*/
        dialog.window!!.attributes = lp
        // imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }


    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 23487
        private val TAG = CustomerInfoScreenFragment::class.java.simpleName
        private var listener: OnDataSubmitListener? = null
        fun newInstance(
            FROM: String?,
            categores: String?,
            userData: UserData?,
            action: String?,
            requestUserType: java.util.ArrayList<String>? = null,
            context: OnDataSubmitListener
        ): CustomerInfoScreenFragment? {
            val args = Bundle()
            if (userData != null) {
                args.putParcelable("userData", userData)
            }
            listener = context
            if (!requestUserType.isNullOrEmpty()) {
                args.putStringArrayList(AppConstants.REQUESTED_USER_TYPES, requestUserType)
            }
            args.putString(AppConstants.Extra.EXTRA_CATEGORIES, categores)
            args.putString(AppConstants.Extra.FROM, FROM)
            args.putString("action", action)
            val fragment = CustomerInfoScreenFragment()
            fragment.arguments = args
            return fragment
        }

    }


    override fun handleAddUserResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
            if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.PREF_KEY_IS_FORM_DATA_MAP);
            }
            if (listener != null)
                if (!isUpdate) {
                    listener!!.onSuccess()
                } else {
                    val jsonConverter: JSONConverter<UpdateUserResponse> = JSONConverter()
                    var response: UpdateUserResponse = jsonConverter.jsonToObject(
                        result.toString(),
                        UpdateUserResponse::class.java
                    ) as UpdateUserResponse
                    if (response.data != null) {
                        var updateData = response.data
                        listener!!.onUpdateDataSucess(updateData)
                    } else {
                        listener!!.onUpdateDataSucess(updateData)
                    }
                }
        } else {
            //listener!!.onCancel()
        }
    }

    override fun handleUserDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
            val jsonConverter: JSONConverter<UpdateResponse> = JSONConverter()
            var response: UpdateResponse = jsonConverter.jsonToObject(
                result.toString(),
                UpdateResponse::class.java
            ) as UpdateResponse
            if (response.data != null) {
                if (action.equals("view")) {
                    var data = response.data
                    if (data!!.firstName == null || data.firstName!!.isEmpty()) {
                        binding.cardViewFirstName.visibility = View.GONE
                    }
                    if (data.middleName == null || data.middleName!!.isEmpty()) {
                        binding.cardViewMiddleName.visibility = View.GONE
                    }
                    if (data.lastName == null || data.lastName!!.isEmpty()) {
                        binding.cardViewLastName.visibility = View.GONE
                    }
                    if (data.mobile == null || data.mobile!!.isEmpty()) {
                        binding.cardViewMobile.visibility = View.GONE
                    }
                    if (data.email == null || data.email!!.isEmpty()) {
                        binding.cardViewEmail.visibility = View.GONE
                    }
                    if (data.fatherName == null || data.fatherName!!.isEmpty()) {
                        binding.cardViewFatherName.visibility = View.GONE
                    }
                    if (data.motherName == null || data.motherName!!.isEmpty()) {
                        binding.cardViewMotherName.visibility = View.GONE
                    }
                    if (data.dateOfBirth == null || data.dateOfBirth!!.isEmpty()) {
                        binding.cardViewDob.visibility = View.GONE
                    }
                    if (data.dateOfJoining == null || data.dateOfJoining!!.isEmpty()) {
                        binding.cardViewDoj.visibility = View.GONE
                    }
                    if (data.personId == null || data.personId!!.isEmpty()) {
                        binding.cardViewPersonId.visibility = View.GONE
                    }
                    if (data.password == null || data.password!!.isEmpty()) {
                        binding.cardViewPassword.visibility = View.GONE
                    }
                    if (data.addressInfo == null) {
                        binding.cvLocation.visibility = View.GONE
                    }
                    binding.request = response.data

                    if (!data.dfData.isNullOrEmpty()) {
                        showDynamicForm(dfId, data!!.dfData!!)
                    } else {
                        showDynamicForm(dfId, ArrayList())
                    }

                } else {
                    var data = response.data
                    binding.request = data
                    if (!data?.dfData.isNullOrEmpty()) {
                        showDynamicForm(dfId, data!!.dfData!!)
                    } else {
                        showDynamicForm(dfId, ArrayList())
                    }
                }

                if (response.data!!.profileImg != null && response.data!!.profileImg!!.isNotEmpty()) {
                    profileImg = response.data!!.profileImg!!
//                    binding.ivAddPic.visibility=View.GONE
//                    binding.ivDeletePic.visibility=View.VISIBLE
                    binding.ivPicUser.visibility = View.VISIBLE
                    GlideApp.with(this)
                        .load(profileImg)
                        .apply(
                            RequestOptions()
                                .transform(CircleTransform())
                                .placeholder(R.drawable.ic_user_edit_pic)
                        )
                        .into(binding.ivPicUser)
                    binding.ivPicUser.setOnClickListener {
                        openDialogShowImage(profileImg!!)
                    }
                }
                if (currentLocation == null)
                    currentLocation = GeoCoordinates()
                if (response.data!!.addressInfo != null) {
                    var addressInfo = response.data!!.addressInfo
                    if (addressInfo != null && addressInfo.location != null) {
                        var location = addressInfo.location
                        if (location != null && location.location != null) {
                            var locationX = location.location
                            if (locationX != null) {
                                currentLocation!!.latitude = locationX.latitude!!
                                currentLocation!!.longitude = locationX.longitude!!
                                startLatLng = LatLng(locationX.latitude!!, locationX.longitude!!)
                                placeName = CommonUtils.getAddress(baseActivity!!, startLatLng)
                                binding.edLocation.setText(placeName)
                            }
                        }
                    }
                }

            }

        } else {
        }
    }

    override fun openPlaceAutoComplete(view: View) {
        try {
            val fields: List<Place.Field> =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent: Intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(baseActivity!!)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.e(TAG, e.message)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e(TAG, e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            hideKeyboard()
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.e(TAG, "Place: " + place.name + ", " + place.id)
                    startLatLng = place.latLng!!
                    if (place.name != null) {
                        placeName = place.name
                        binding.edLocation.setText(placeName)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i(TAG, status.statusMessage)
                    TrackiToast.Message.showShort(baseActivity!!, status.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> // The user canceled the operation.
                    TrackiToast.Message.showShort(baseActivity!!, "operation cancelled.")
            }
        }
        if (requestCode == PICK_IMAGE_FILE_ID) {
            actualImage = ImagePicker.getImageFileToUpload(baseActivity!!, resultCode, data)
            compressImage()
        }


    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
            if (listener != null)
                listener!!.onSuccess()

        } else {
            if (listener != null)
                listener!!.onCancel()

        }
    }

    override fun selectDateTime(v: View) {
        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMin = c.get(Calendar.MINUTE)

        CommonUtils.openDatePicker(
            context, mYear, mMonth, mDay,
            0, 0
        ) { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
            val cal = Calendar.getInstance()
            cal[Calendar.YEAR] = year
            cal[Calendar.MONTH] = monthOfYear
            cal[Calendar.DAY_OF_MONTH] = dayOfMonth
            cal[Calendar.HOUR_OF_DAY] = mHour
            cal[Calendar.MINUTE] = mMin
            cal[Calendar.SECOND] = 0
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth
            when (v.id) {
                R.id.edDob -> {
                    dobDay = dayOfMonth
                    dobMonth = monthOfYear
                    dobYear = year
                    dobHour = mHour
                    dobMinute = mMin
                    cal.set(Calendar.YEAR, dobYear)
                    cal.set(Calendar.MONTH, dobMonth)
                    cal.set(Calendar.DAY_OF_MONTH, dobDay)
                    cal.set(Calendar.HOUR_OF_DAY, dobHour)
                    cal.set(Calendar.MINUTE, dobMinute)
                    cal.set(Calendar.SECOND, 0)
                    dobTime = cal.timeInMillis
                    var dobStr = DateTimeUtil.getParsedDate2(dobTime)
                    binding.edDob.setText(dobStr)
                }
                R.id.edDoj -> {
                    dojDay = dayOfMonth
                    dojMonth = monthOfYear
                    dojYear = year
                    dojHour = mHour
                    dojMinute = mMin

                    cal.set(Calendar.YEAR, dojYear)
                    cal.set(Calendar.MONTH, dojMonth)
                    cal.set(Calendar.DAY_OF_MONTH, dojDay)
                    cal.set(Calendar.HOUR_OF_DAY, dojHour)
                    cal.set(Calendar.MINUTE, dojMinute)
                    cal.set(Calendar.SECOND, 0)
                    dojTime = cal.timeInMillis
                    var dojStr = DateTimeUtil.getParsedDate2(dojTime)
                    binding.edDoj.setText(dojStr)
                }

            }

/*        val datePickerDialog = DatePickerDialog(baseActivity!!, { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year

            CommonUtils.openTimePicker(baseActivity!!, mHour, mMin) { _, hourOfDay, minute ->
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minute)
                val formattedTime = DateTimeUtil.getFormattedTime(c.time)
                Log.e(TAG, "${c.time}")
                val cal = Calendar.getInstance()
                when (v.id) {
                    R.id.edDob -> {
                        dobDay = dayOfMonth
                        dobMonth = monthOfYear
                        dobYear = year
                        dobHour = hourOfDay
                        dobMinute = minute
                        cal.set(Calendar.YEAR, dobYear)
                        cal.set(Calendar.MONTH, dobMonth)
                        cal.set(Calendar.DAY_OF_MONTH, dobDay)
                        cal.set(Calendar.HOUR_OF_DAY, dobHour)
                        cal.set(Calendar.MINUTE, dobMinute)
                        cal.set(Calendar.SECOND, 0)
                        dobTime = cal.timeInMillis

                        binding.edDob.setText("$selectedDate | $formattedTime")
                    }
                    R.id.edDoj -> {
                        dojDay = dayOfMonth
                        dojMonth = monthOfYear
                        dojYear = year
                        dojHour = hourOfDay
                        dojMinute = minute

                        cal.set(Calendar.YEAR, dojYear)
                        cal.set(Calendar.MONTH, dojMonth)
                        cal.set(Calendar.DAY_OF_MONTH, dojDay)
                        cal.set(Calendar.HOUR_OF_DAY, dojHour)
                        cal.set(Calendar.MINUTE, dojMinute)
                        cal.set(Calendar.SECOND, 0)
                        dojTime = cal.timeInMillis
                        binding.edDob.setText("$selectedDate | $formattedTime")
                    }
                }
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()*/
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            /*  R.id.ivDeletePic -> {
                  profileImg = null
                  binding.ivAddPic.visibility=View.VISIBLE
                  binding.ivDeletePic.visibility=View.GONE
                  binding.ivPicUser.visibility=View.GONE
              }*/
            R.id.ivAddPic -> {
                onPickImage()
            }
            R.id.btnCLick -> {

            }
        }
    }


    fun onPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (baseActivity!!.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && baseActivity!!.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                baseActivity!!.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            ) {
                proceedToImagePicking()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), REQUEST_READ_STORAGE
                )
            }
        } else {
            proceedToImagePicking()
        }
    }

    private fun proceedToImagePicking() {
        val chooseImageIntent = ImagePicker.getPickImageIntent(baseActivity!!)
        startActivityForResult(chooseImageIntent, PICK_IMAGE_FILE_ID)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    proceedToImagePicking()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @SuppressLint("CheckResult")
    private fun compressImage() {
        if (actualImage == null) {
            showShort(baseActivity!!, "Please choose an image!")
        } else {

            // Compress image in main thread
            //compressedImage = new Compressor(this).compressToFile(actualImage);
            //setCompressedImage();

            // Compress image to bitmap in main thread
            //compressedImageView.setImageBitmap(new Compressor(this).compressToBitmap(actualImage));

            // Compress image using RxJava in background thread
            Compressor(baseActivity!!)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file: File ->
                    compressedImage = file
                    showLoading()
                    val updateFileRequest =
                        UpdateFileRequest(compressedImage!!, FileType.USER_PROFILE, "")
                    val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE_AGAINEST_ENTITY]
                    mAddUserViewModel.uploadProfilePic(updateFileRequest, httpManager, api)
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    showShort(baseActivity!!, throwable.message)
                }
        }
    }


    override fun handleProfilePicResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
            val profileResponse = Gson().fromJson(result.toString(), ProfileResponse::class.java)
            if (profileResponse != null) {
                val imageUrl = profileResponse.imageUrl
                profileImg = imageUrl
//                binding.ivAddPic.visibility=View.GONE
//                binding.ivDeletePic.visibility=View.VISIBLE
//                binding.ivPicUser.visibility=View.VISIBLE
                GlideApp.with(this)
                    .load(profileImg)
                    .apply(
                        RequestOptions()
                            .transform(CircleTransform())
                            .placeholder(R.drawable.ic_user_edit_pic)
                    )
                    .into(binding.ivPicUser)

            }
        }
    }

    interface OnDataSubmitListener {
        fun onSuccess()
        fun onCancel()
        fun onUpdateDataSucess(userData: UserData?)
        fun onUpdateHeader(type: String?)
    }

    private fun onEditUser() {
        var intent = Intent(baseActivity, AddCustomerActivity::class.java)
        intent.putExtra("userData", userData)
        intent.putExtra("from", from)
        intent.putExtra("action", "edit")
        //    intent.putExtra(property, AppConstants.Extra.EXTRA_CATEGORIES)
        startActivity(intent)
    }

    fun onDeleteUser(data: UserData) {
        val dialog = DoubleButtonDialog(baseActivity,
            true,
            null,
            getString(R.string.cancel_buddy_tracking_request),
            getString(R.string.yes),
            getString(R.string.no),
            object : OnClickListener {
                override fun onClickCancel() {}
                override fun onClick() {
                    if (data.userId != null && data.userId!!.isNotEmpty()) {
                        showLoading()
                        mAddUserViewModel.deleteUser(httpManager, data.userId)
                    }
                }
            })
        dialog.show()

    }


    fun returnName(data: UserData): String {
        var name = ""
        if (data.firstName != null && data.middleName != null && data.lastName != null) {
            name = data.firstName + " " + data.middleName + " " + data.lastName
        } else if (data.firstName != null && data.lastName != null) {
            name = data.firstName + " " + data.lastName
        } else if (data.firstName != null && data.middleName != null) {
            name = data.firstName + " " + data.middleName
        } else if (data.firstName != null) {
            name = data.firstName!!
        }
        return name
    }

    override fun handleDeleteResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
            if (listener != null)
                listener!!.onSuccess()

        } else {
            if (listener != null)
                listener!!.onCancel()

        }
    }

    private fun enableEditText(editText: EditText) {
        editText.isFocusableInTouchMode = true
        editText.isFocusable = true
        editText.isEnabled = true
    }

    private fun disableEditText(editText: EditText) {
        editText.isEnabled = false
        editText.isFocusable = false
        editText.isFocusableInTouchMode = false
    }

    private fun disableAllEditText() {
        disableEditText(binding.edFirstName)
        disableEditText(binding.edMiddleName)
        disableEditText(binding.edLastName)
        disableEditText(binding.edMobile)
        disableEditText(binding.edEmail)
        disableEditText(binding.edFather)
        disableEditText(binding.edMother)
        disableEditText(binding.edPassword)
        disableEditText(binding.edPersonId)
        disableEditText(binding.edLocation)
        disableEditText(binding.edDob)
        disableEditText(binding.edDoj)
        binding.spnRolles.isEnabled = false
    }

    override fun onProcessClick(
        list: ArrayList<FormData>,
        dynamicActionConfig: DynamicActionConfig?,
        currentFormId: String?,
        dfdid: String?
    ) {
        var firstName = binding.edFirstName.text.toString().trim()
        var middleName = binding.edMiddleName.text.toString().trim()
        var lastName = binding.edLastName.text.toString().trim()
        var mobile = binding.edMobile.text.toString().trim()
        var email = binding.edEmail.text.toString().trim()
        var password = binding.edPassword.text.toString().trim()
        var dob = binding.edDob.text.toString().trim()
        var doj = binding.edDoj.text.toString().trim()
        var personId = binding.edPersonId.text.toString().trim()
        var location = binding.edLocation.text.toString().trim()
        var fatherName = binding.edFather.text.toString().trim()
        var motherName = binding.edMother.text.toString().trim()
        if (mAddUserViewModel.isProfilePicVisible.get()!! && mAddUserViewModel.isPasswordRequired.get()!! && profileImg == null) {
            TrackiToast.Message.showShort(requireContext(), "Please add profile pic ")
        } else if (mAddUserViewModel.isFirstNameVisible.get()!! && mAddUserViewModel.isFirstNameRequired.get()!! && firstName.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter First Name")
        } else if (mAddUserViewModel.isMiddleNameVisible.get()!! && mAddUserViewModel.isMiddleNameRequired.get()!! && middleName.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Middle Name")
        } else if (mAddUserViewModel.isLastNameVisible.get()!! && mAddUserViewModel.isLastNameRequired.get()!! && lastName.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Last Name")
        } else if (mAddUserViewModel.isMobileVisible.get()!! && mAddUserViewModel.isMobileRequired.get()!! && mobile.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Mobile Number")
        } else if (mAddUserViewModel.isMobileVisible.get()!! && mAddUserViewModel.isMiddleNameRequired.get()!! && !CommonUtils.isMobileValid(
                mobile
            )
        ) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Valid Mobile Number")
        } else if (mAddUserViewModel.isEmailVisible.get()!! && mAddUserViewModel.isEmailRequired.get()!! && email.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Email")
        } else if (mAddUserViewModel.isEmailVisible.get()!! && mAddUserViewModel.isEmailRequired.get()!! && !CommonUtils.isEmailValid(
                email
            )
        ) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Valid Email")
        } else if (mAddUserViewModel.isPasswordVisible.get()!! && mAddUserViewModel.isPasswordRequired.get()!! && password.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Password")
        } else if (mAddUserViewModel.isDobVisible.get()!! && mAddUserViewModel.isDobRequired.get()!! && dob.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Date of Birth")
        } else if (mAddUserViewModel.isDojVisible.get()!! && mAddUserViewModel.isDojRequired.get()!! && doj.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Date of Joining")
        } else if (mAddUserViewModel.isPidVisible.get()!! && mAddUserViewModel.isPidRequired.get()!! && personId.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Person Id")
        } else if (mAddUserViewModel.isAddress.get()!! && mAddUserViewModel.isAddressRequired.get()!! && location.isEmpty()) {
            TrackiToast.Message.showShort(requireContext(), "Please Enter Address")
        } else {
            addEmployeeRequest = AddEmployeeRequest()
            updateData = UserData()
            if (mAddUserViewModel.isFirstNameVisible.get()!! && firstName.isNotEmpty()) {
                addEmployeeRequest.firstName = firstName
                updateData!!.firstName = firstName
            }
            if (mAddUserViewModel.isMiddleNameVisible.get()!! && middleName.isNotEmpty()) {
                addEmployeeRequest.middleName = middleName
                updateData!!.middleName = middleName
            }
            if (mAddUserViewModel.isLastNameVisible.get()!! && lastName.isNotEmpty()) {
                addEmployeeRequest.lastName = lastName
                updateData!!.lastName = lastName
            }
            if (mAddUserViewModel.isMobileVisible.get()!! && mobile.isNotEmpty()) {
                addEmployeeRequest.mobile = mobile
                updateData!!.mobile = mobile
            }
            if (mAddUserViewModel.isEmailVisible.get()!! && email.isNotEmpty()) {
                addEmployeeRequest.email = email
                updateData!!.email = email
            }
            if (mAddUserViewModel.isPasswordVisible.get()!! && password.isNotEmpty()) {
                addEmployeeRequest.password = password
                updateData!!.password = password
            }
            if (mAddUserViewModel.isDobVisible.get()!! && dob.isNotEmpty()) {
                addEmployeeRequest.dateOfBirth = dob
                updateData!!.dateOfBirth = dob
            }
            if (mAddUserViewModel.isDojVisible.get()!! && doj.isNotEmpty()) {
                addEmployeeRequest.dateOfJoining = doj
                updateData!!.dateOfJoining = doj
            }
            if (mAddUserViewModel.isPidVisible.get()!! && personId.isNotEmpty()) {
                addEmployeeRequest.personId = personId
                updateData!!.personId = personId
            }
            if (mAddUserViewModel.isProfilePicVisible.get()!! && profileImg != null) {
                addEmployeeRequest.profileImg = profileImg
                updateData!!.profileImg = profileImg
            }
            if (mAddUserViewModel.isMobileVisible.get()!! && motherName.isNotEmpty()) {
                addEmployeeRequest.motherName = motherName
                updateData!!.motherName = motherName
            }
            if (mAddUserViewModel.isFirstNameVisible.get()!! && fatherName.isNotEmpty()) {
                addEmployeeRequest.fatherName = fatherName
                updateData!!.fatherName = fatherName
            }
            /*if (mAddUserViewModel.isAddress.get()!! && location.isNotEmpty()) {
                var addressInfo = AddressInfo()
                addressInfo.address = location
                var loc = Address()
                loc.address = location
                var locx = LocationX()
                locx.latitude = startLatLng.latitude
                locx.longitude = startLatLng.longitude
                var coordinates = ArrayList<Double>()
                coordinates.add(locx.latitude!!)
                coordinates.add(locx.longitude!!)
                locx.coordinates = coordinates
                loc.location = locx
                addressInfo.location = loc
                addEmployeeRequest.addressInfo = addressInfo
                addEmployeeRequest.address = true

            }*/
            if (roleId != null) {
                addEmployeeRequest.roleId = roleId
                updateData!!.roleId = roleId
            }

            hideKeyboard()
            //showLoading()
            var json = JSONConverter<AddEmployeeRequest>().objectToJson(addEmployeeRequest)
            CommonUtils.showLogMessage("e", "jsondata", json)

            /* var bundle = Bundle();
             bundle.putParcelable("addEmployeeRequest", addEmployeeRequest);
             Log.e(TAG, "config type----> ${dynamicActionConfig?.action} ")*/
            if (mainMap == null) {
                mainMap = HashMap()
            }
            //add form data after validation into the map
            if (list.isNotEmpty()) {
                mainMap?.set(currentFormId!!, list)
                var jsonConverter = JSONConverter<HashMap<String, ArrayList<FormData>>>()
                var data = jsonConverter.objectToJson(mainMap!!)
                CommonUtils.showLogMessage("e", "allowed field", data.toString())

            }
            var jsonConverter = JSONConverter<ArrayList<FormData>>();
            Log.e(TAG, jsonConverter.objectToJson(list))
            if (dynamicActionConfig?.action == Type.DISPOSE) {

                mainData = ArrayList()

                for ((_, value) in mainMap!!) {
                    mainData?.addAll(value)
                }

                if (mainData != null && mainData!!.isNotEmpty()!!) {
                    val hashMapFileRequest = HashMap<String, ArrayList<File>>()
                    for (i in mainData?.indices!!) {

                        val v = mainData!![i].file
                        if (v != null && v.isNotEmpty()) {

                            if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {

                                v.removeAt(0)
                            }
                            if (mainData!![i].type == DataType.CAMERA && v.size > 0 && v[v.size - 1].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                v.removeAt(v.size - 1)
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
                    Log.e(TAG, jsonConverter.objectToJson(hashMapFileRequest))
                    Log.e(TAG, "Size =>" + hashMapFileRequest.size)
                    if (hashMapFileRequest.isNotEmpty()) {


                        if (NetworkUtils.isNetworkConnected(baseActivity!!)) {

                            if (NetworkUtils.isConnectedFast(baseActivity!!)) {
                                count = 0
                                Log.e(TAG, "worker thread open")
                                // showLoading()
                                binding.btnCLick!!.visibility = View.GONE
                                binding.viewProgress.visibility = View.VISIBLE
                                CommonUtils.makeScreenDisable(baseActivity!!)
                                binding.nestedScrollView.postDelayed(
                                    { binding.nestedScrollView.fullScroll(View.FOCUS_DOWN) },
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
                                    baseActivity!!,
                                    binding.nestedScrollView,
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
                mainData, "", "", dynamicFormsNew!!.formId,
                dynamicFormsNew!!.version
            )
            Log.e(TAG, "Final Dynamic Form Data-----> $dynamicFormMainData")

            var taskData = dynamicFormMainData!!.taskData
            if (!taskData.taskData?.isEmpty()!!) {
                (taskData.taskData!! as ArrayList<DynamicFormData>?)!!.also {
                    addEmployeeRequest.dfData = it
                }
            }

        }
        if (!isUpdate) {
            if (TrackiApplication.getApiMap() != null && TrackiApplication.getApiMap()[ApiType.ADD_USER] != null) {
                showLoading()
                var api = TrackiApplication.getApiMap()[ApiType.ADD_USER]
                mAddUserViewModel.addEmployee(httpManager, addEmployeeRequest, api!!)
            }
        } else {
            showLoading()
            if (userId != null && userId!!.isNotEmpty()) {
                addEmployeeRequest.userId = userId
                updateData!!.userId = userId
            }
            if (TrackiApplication.getApiMap() != null && TrackiApplication.getApiMap()[ApiType.UPDATE_USER] != null) {
                var api = TrackiApplication.getApiMap()[ApiType.UPDATE_USER]
                mAddUserViewModel.updateEmployee(httpManager, addEmployeeRequest, api!!)
            }
        }
    }


}