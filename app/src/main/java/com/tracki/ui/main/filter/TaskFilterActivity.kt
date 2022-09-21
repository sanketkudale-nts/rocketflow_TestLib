package com.tracki.ui.main.filter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.GetManualLocationRequest
import com.tracki.data.model.request.RegionRequest
import com.tracki.data.model.request.SaveFilterData
import com.tracki.data.model.response.config.GetUserManualLocationData
import com.tracki.data.model.response.config.LocData
import com.tracki.data.model.response.config.WorkFlowCategories
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityTaskFilterBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_task_filter.*
import javax.inject.Inject


class TaskFilterActivity : BaseActivity<ActivityTaskFilterBinding, TaskFilterViewModel>(),
    TaskFilterNavigator {


    private var allowGeography: Boolean = false

    @Inject
    lateinit var mTaskFilterViewModel: TaskFilterViewModel
    private var regionId: String? = null
    private var hubId: String? = null
    private var stateId: String? = null
    private var cityId: String? = null
    private var regionIdPrev: String? = null
    private var hubIdPrev: String? = null
    private var stateIdPrev: String? = null
    private var cityIdPrev: String? = null
    private var categoryId: String? = null
    private var hubList: ArrayList<LocData>? = ArrayList()
    private lateinit var mActivityTaskFilterBinding: ActivityTaskFilterBinding

    private var from: String? = null

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityTaskFilterBinding = viewDataBinding
        mTaskFilterViewModel.navigator = this
        setToolbar(mActivityTaskFilterBinding.toolbar, "Geography Filter")
        if (intent != null) {
//            if (intent.hasExtra("regionId")) {
//                regionIdPrev = intent.getStringExtra("regionId")
//            }
//            if (intent.hasExtra("hubIdStr")) {
//                hubIdPrev = intent.getStringExtra("hubIdStr")
//            }
//            if (intent.hasExtra("stateId")) {
//                stateIdPrev = intent.getStringExtra("stateId")
//            }
//            if (intent.hasExtra("cityId")) {
//                cityIdPrev = intent.getStringExtra("cityId")
//            }
            if (intent.hasExtra("from")) {
                from = intent.getStringExtra("from")
                if (from.equals(AppConstants.ATTENDANCE)) {
                    allowGeography = true
                }
            }
            if (intent.hasExtra("categoryId")) {
                categoryId = intent.getStringExtra("categoryId")
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
                if ((preferencesHelper.filterMap != null)) {
                    if (preferencesHelper.filterMap.containsKey(categoryId)) {
                        var saveFilterData = preferencesHelper.filterMap[categoryId]
                        if (saveFilterData != null) {
                            regionIdPrev = saveFilterData!!.regionId
                            hubIdPrev = saveFilterData.hubId
                            stateIdPrev = saveFilterData.stateId
                            cityIdPrev = saveFilterData.cityId
                        }

                    }
                }
            }

        }
        showLoading()
        var regionRequest = RegionRequest(userGeoReq = allowGeography)
        mTaskFilterViewModel.getRegionList(httpManager, true, regionRequest)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter_clear_geo_location, menu)
        return true
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_task_filter
    }

    override fun getViewModel(): TaskFilterViewModel {
        return mTaskFilterViewModel
    }

    override fun handleRegionListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        // hideLoading()
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
//                        if (list.size == 1) {
//                            tvLabelSelectStartArea.visibility = View.GONE
//                            spnCategoryStartArea.visibility = View.GONE
//                            regionId = list[0].id
//                            CommonUtils.showLogMessage("e", "regionId", regionId);
//                            var getUserManualLocationData = GetManualLocationRequest()
//                            getUserManualLocationData.regionId = regionId
//                            spnCategoryStartState.adapter = null
//                            stateId = null
//                            showLoading()
//                            mTaskFilterViewModel.getStateList(httpManager, getUserManualLocationData, true)
//                        } else {
                        spnCategoryStartArea.visibility = View.VISIBLE
                        tvLabelSelectStartArea.visibility = View.VISIBLE
                        spnCategoryStartArea!!.adapter = arrayAdapter
                        if (regionIdPrev != null) {
                            var locData = LocData();
                            locData.id = regionIdPrev
                            var selectedPosition = list.indexOf(locData)
                            if (selectedPosition != -1) {
                                spnCategoryStartArea.setSelection(selectedPosition)
                            }
                        }


                        spnCategoryStartArea!!.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItem = parent.getItemAtPosition(position).toString()
                                    regionId = list[position].id
                                    CommonUtils.showLogMessage("e", "regionId", regionId);
                                    var getUserManualLocationData = GetManualLocationRequest()
                                    getUserManualLocationData.regionId = regionId
                                    getUserManualLocationData.userGeoReq = allowGeography
                                    spnCategoryStartState.adapter = null
                                    stateId = null
                                    showLoading()
                                    mTaskFilterViewModel.getStateList(
                                        httpManager,
                                        getUserManualLocationData,
                                        true
                                    )

                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {

                                }
                            }
                        // }


                    }


                } else {
                }

            }
        } else {
            stateId = null
            hideLoading()
        }

    }

    override fun handleStateListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        //  hideLoading()
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
//                        if (list.size == 1) {
//                            spnCategoryStartState.visibility = View.GONE
//                            tvLabelSelectStartState.visibility = View.GONE
//                            stateId = list[0].id
//                            CommonUtils.showLogMessage("e", "stateId", stateId);
//                            var getUserManualLocationData = GetManualLocationRequest()
//                            getUserManualLocationData.regionId = regionId
//                            getUserManualLocationData.stateId = stateId
//                            getUserManualLocationData.userGeoReq=allowGeography
//                            spnCategoryStartCity.adapter = null
//                            cityId = null
//                            showLoading()
//                            mTaskFilterViewModel.getCityList(httpManager, getUserManualLocationData, true)
//
//                        } else {
                        spnCategoryStartState.visibility = View.VISIBLE
                        tvLabelSelectStartState.visibility = View.VISIBLE
                        spnCategoryStartState!!.adapter = arrayAdapter
                        if (stateIdPrev != null) {
                            var locData = LocData();
                            locData.id = stateIdPrev
                            var selectedPosition = list.indexOf(locData)
                            if (selectedPosition != -1) {
                                spnCategoryStartState.setSelection(selectedPosition)
                            }
                        }
                        spnCategoryStartState!!.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItem = parent.getItemAtPosition(position).toString()
                                    stateId = list[position].id
                                    CommonUtils.showLogMessage("e", "stateId", stateId);
                                    var getUserManualLocationData = GetManualLocationRequest()
                                    getUserManualLocationData.regionId = regionId
                                    getUserManualLocationData.stateId = stateId
                                    getUserManualLocationData.userGeoReq = allowGeography
                                    spnCategoryStartCity.adapter = null
                                    cityId = null
                                    showLoading()
                                    mTaskFilterViewModel.getCityList(
                                        httpManager,
                                        getUserManualLocationData,
                                        true
                                    )

                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {

                                }
                            }
                        //    }

                    }


                } else {
                }

            }
        } else {
            cityId = null
            hideLoading()
        }

    }

    override fun handleCityListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        // hideLoading()
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
//                        if (list.size == 1) {
//                            spnCategoryStartCity.visibility = View.GONE
//                            tvLabelSelectStartCity.visibility = View.GONE
//                            cityId = list[0].id
//                            CommonUtils.showLogMessage("e", "cityId", cityId);
//                            var getUserManualLocationData = GetManualLocationRequest()
//                            getUserManualLocationData.regionId = regionId
//                            getUserManualLocationData.stateId = stateId
//                            getUserManualLocationData.cityId = cityId
//                            spnCategoryStartHub.adapter = null
//                            hubId = null
//                            showLoading()
//                            mTaskFilterViewModel.getHubList(httpManager, getUserManualLocationData, true)
//                        } else {
                        spnCategoryStartCity.visibility = View.VISIBLE
                        tvLabelSelectStartCity.visibility = View.VISIBLE
                        spnCategoryStartCity!!.adapter = arrayAdapter
                        if (cityIdPrev != null) {
                            var locData = LocData();
                            locData.id = cityIdPrev
                            var selectedPosition = list.indexOf(locData)
                            if (selectedPosition != -1) {
                                spnCategoryStartCity.setSelection(selectedPosition)
                            }
                        }
                        spnCategoryStartCity!!.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItem = parent.getItemAtPosition(position).toString()
                                    cityId = list[position].id
                                    CommonUtils.showLogMessage("e", "cityId", cityId);
                                    var getUserManualLocationData = GetManualLocationRequest()
                                    getUserManualLocationData.regionId = regionId
                                    getUserManualLocationData.stateId = stateId
                                    getUserManualLocationData.cityId = cityId
                                    getUserManualLocationData.userGeoReq = allowGeography
                                    spnCategoryStartHub.adapter = null
                                    hubId = null
                                    showLoading()
                                    mTaskFilterViewModel.getHubList(
                                        httpManager,
                                        getUserManualLocationData,
                                        true
                                    )

                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {

                                }
                            }
                        //  }

                    }


                } else {
                }

            }
        } else {
            hubId = null
            hideLoading()
            spnCategoryStartCity.visibility = View.GONE
            tvLabelSelectStartHubs.visibility = View.GONE
            spnCategoryStartHub.visibility = View.GONE
            tvLabelSelectStartCity.visibility = View.GONE
            rlHubLocation.visibility = View.GONE
        }

    }

    override fun handleHubListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            tvHubName.text = ""
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
//                        if (list.size == 1) {
//                            spnCategoryStartHub.visibility = View.GONE
//                            tvLabelSelectStartHubs.visibility = View.GONE
//                            rlHubLocation.visibility = View.GONE
//                            hubId = list[0].id
//                            tvHubName.text = list[0].name
//                            CommonUtils.showLogMessage("e", "hubId", hubId);
//                        } else {
//                            spnCategoryStartHub.visibility = View.VISIBLE
//                            tvLabelSelectStartHubs.visibility = View.VISIBLE
//                            spnCategoryStartHub!!.adapter = arrayAdapter
//                            spnCategoryStartHub!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                                    val selectedItem = parent.getItemAtPosition(position).toString()
//                                    hubId = list[position].id
//                                    CommonUtils.showLogMessage("e", "hubId", hubId);
//
//
//                                }
//
//                                override fun onNothingSelected(parent: AdapterView<*>) {
//
//                                }
//                            }
                        if (hubIdPrev != null) {
                            val regex = ", "
                            val mutableList = hubIdPrev!!.split(regex).toMutableList()
                            for (i in mutableList) {
                                for (dataInList in list) {
                                    if (dataInList.id == i)
                                        dataInList.isSelected = true
                                }
                            }

                            var savedList = list.filter { it.isSelected }
                            val commaSeperatedId = savedList.joinToString { it -> "${it.id}" }
                            val commaSeperatedName = savedList.joinToString { it -> "${it.name}" }
                            hubId = commaSeperatedId
                            tvHubName.text = commaSeperatedName
                        }
                        tvLabelSelectStartHubs.visibility = View.VISIBLE
                        rlHubLocation.visibility = View.VISIBLE

                        rlHubLocation.setOnClickListener {
                            openSelectMultipleSelect(list)
                        }

                        // }

                    }


                } else {
                }

            }
        } else {
            spnCategoryStartHub.visibility = View.GONE
            tvLabelSelectStartHubs.visibility = View.GONE
            rlHubLocation.visibility = View.GONE
        }

    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear -> {
                clearFilter()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun submitFilter() {
        val returnIntent = Intent()
        returnIntent.putExtra("regionId", regionId)
        returnIntent.putExtra("hubId", hubId)
        returnIntent.putExtra("stateId", stateId)
        returnIntent.putExtra("cityId", cityId)
        CommonUtils.showLogMessage("e", "regionId", regionId)
        CommonUtils.showLogMessage("e", "hubId", hubId)
        CommonUtils.showLogMessage("e", "stateId", stateId)
        CommonUtils.showLogMessage("e", "cityId", cityId)
        if (from.equals(AppConstants.TASK)) {
            var saveFilterConfig = SaveFilterData()
            saveFilterConfig.cityId = cityId
            saveFilterConfig.hubId = hubId
            saveFilterConfig.stateId = stateId
            saveFilterConfig.regionId = regionId
            if (preferencesHelper.filterMap != null) {
                if (preferencesHelper.filterMap.containsKey(categoryId)) {
                    preferencesHelper.filterMap.remove(categoryId)
                    var map = HashMap<String, SaveFilterData>()
                    map[categoryId!!] = saveFilterConfig
//                preferencesHelper.filterMap[categoryId] = saveFilterConfig
                    var previousmap = preferencesHelper.filterMap
                    previousmap.putAll(map)
                    preferencesHelper.saveFilterMap(previousmap)

                } else {
                    var map = HashMap<String, SaveFilterData>()
                    map[categoryId!!] = saveFilterConfig
                    var previousmap = preferencesHelper.filterMap
                    previousmap.putAll(map)
                    preferencesHelper.saveFilterMap(previousmap)
                }
            } else {
                var map = HashMap<String, SaveFilterData>()
                map[categoryId!!] = saveFilterConfig
                preferencesHelper.saveFilterMap(map)
            }
        }
        if (regionId == null && hubId == null && stateId == null && regionId == null) {
            setResult(Activity.RESULT_CANCELED, returnIntent)
        } else {
            setResult(Activity.RESULT_OK, returnIntent)
        }

        finish()
    }

    override fun clearFilter() {
        preferencesHelper.saveFilterMap(null)
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, TaskFilterActivity::class.java)
        }
    }

    private fun openSelectMultipleSelect(list: ArrayList<LocData>) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_multiselect_drop_down)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        val rVMultiSelect = dialog.findViewById<RecyclerView>(R.id.rVMultiSelect)
        val buttonSubmit = dialog.findViewById<Button>(R.id.btnDone)
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val etSearch = dialog.findViewById<EditText>(R.id.etSearch)
        var adapter = MultipleSelectDropDownAdapter(this)
        adapter.addItems(list)
        rVMultiSelect.adapter = adapter
        dialog.window!!.attributes = lp
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length >= 2) {
                    rVMultiSelect.removeAllViewsInLayout()
                    adapter!!.filter!!.filter(charSequence)
                } else
                    adapter!!.filter!!.filter("")
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        buttonSubmit.setOnClickListener {
            var list = adapter.getAllList().filter { it.isSelected }
            val commaSeperatedId = list.joinToString { it -> "${it.id}" }
            val commaSeperatedName = list.joinToString { it -> "${it.name}" }
            hubId = commaSeperatedId
            tvHubName.text = commaSeperatedName
            CommonUtils.showLogMessage("e", "commastring", commaSeperatedId)
            CommonUtils.showLogMessage("e", "commastring", commaSeperatedName)
            dialog.dismiss()
        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        if (!dialog.isShowing)
            dialog.show()
    }
}