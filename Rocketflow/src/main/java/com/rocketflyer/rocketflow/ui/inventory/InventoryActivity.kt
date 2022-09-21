package com.rocketflyer.rocketflow.ui.inventory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.InventoryRequest
import com.tracki.data.model.request.LinkInventoryRequest
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityInventoryBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.newcreatetask.NewCreateTaskActivity
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.ui.tasklisting.PagingData
import com.tracki.utils.AppConstants
import com.tracki.utils.AppConstants.Extra
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import com.tracki.utils.TrackiToast
import kotlinx.android.synthetic.main.activity_inventory.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class InventoryActivity : BaseActivity<ActivityInventoryBinding, InventoryViewModel>(), InventoryNavigator, View.OnClickListener {
    private var buddyName: String? = null
    private var buddyId: String? = null
    private var fleetId: String? = null
    private var inventoryCategoryId: String? = null
    private var inventoryGroupId: String? = null

    private var categoryId: String? = null
    private var taskId: String? = null
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false

    @Inject
    lateinit var mInventoryViewModel: InventoryViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: InventoryListAdapter

    private var inventoryConfig: InventoryConfig? = null

    private lateinit var mInventoryBinding: ActivityInventoryBinding
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mInventoryBinding.llMain, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mInventoryBinding = viewDataBinding
        mInventoryViewModel.navigator = this
        rvProduct.adapter = adapter
        setToolbar(toolbar, getString(R.string.create_product))
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            var categoryMap: Map<String, String>? = null
            val str: String = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)!!
            categoryMap = Gson().fromJson(str, object : TypeToken<HashMap<String?, String?>?>() {}.type)
            if (categoryMap != null && categoryMap!!.containsKey("categoryId")) {
                categoryId = categoryMap!!.get("categoryId")

                if (categoryId != null) {
                    showLoading()
                    var request = InventoryRequest()
                    request.categoryId = categoryId
                    val pagingData = PagingData()
                    pagingData.datalimit = 10
                    pagingData.pageOffset = (currentPage - 1) * 10
                    pagingData.pageIndex = currentPage
                    request.paginationData = pagingData
                    // request.categoryId = "ddddddd5e9-4495-8bc1-dokkkkd3de79aca05"
                    mInventoryViewModel.checkInventoryData(httpManager, request, true)

                }
            }
        }
        btnSubmit.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnProceed.setOnClickListener(this)
        var layoutManger: LinearLayoutManager = rvProduct!!.layoutManager!! as LinearLayoutManager
        rvProduct.addOnScrollListener(object : PaginationListener(layoutManger) {
            override fun loadMoreItems() {
                this@InventoryActivity.isLoading = true
                currentPage++
                showLoading()
                var request = InventoryRequest()
                request.categoryId = categoryId
                request.inventoryCategoryId = inventoryCategoryId
                request.inventoryGroupId = inventoryGroupId
                val pagingData = PagingData()
                pagingData.datalimit = 10
                pagingData.pageOffset = (currentPage - 1) * 10
                pagingData.pageIndex = currentPage
                request.paginationData = pagingData
                mInventoryViewModel.checkInventoryData(httpManager, request, false)


            }

            override fun isLastPage(): Boolean {
                return this@InventoryActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@InventoryActivity.isLoading
            }
        })


    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_inventory
    }

    override fun getViewModel(): InventoryViewModel {
        return mInventoryViewModel
    }

    override fun checkInventory(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<InventoryResponse> = JSONConverter()
            var response: InventoryResponse = jsonConverter.jsonToObject(result.toString(), InventoryResponse::class.java) as InventoryResponse
            if (response.inventories != null) {
                if (response.inventoriesRequest != null && response.inventoriesRequest!!.paginationData != null) {
                    val list: List<Inventory> = response.inventories!!

                    this@InventoryActivity.isLoading = false
                    adapter.addItems(response.inventories as ArrayList<Inventory>)
                    CommonUtils.showLogMessage("e", "adapter total_count =>",
                            "" + adapter.getItemCount())
                    CommonUtils.showLogMessage("e", "fetch total_count =>",
                            "" + response.inventoriesRequest!!.paginationData!!.dataCount)
                    if (response.inventoriesRequest!!.paginationData!!.dataCount == adapter.getAllList().size) {

                        isLastPage = true
                    }else{
                        isLastPage = false
                    }
                }


            }
            if (response.inventoryConfig != null) {
                inventoryConfig = response.inventoryConfig
            }
            if (response.filterCategoryMapping != null) {
                val list = ArrayList<FilterCategoryData>()
                val filterCategoryData = FilterCategoryData()
                filterCategoryData.id = "-1"
                filterCategoryData.name = "All Category"

                list.add(filterCategoryData)
                val hmIterator: Iterator<*> = response.filterCategoryMapping!!.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val filterCategoryData = FilterCategoryData()
                    filterCategoryData.id = mapElement.key.toString()
                    filterCategoryData.name = mapElement.value.toString()

                    list.add(filterCategoryData)
                }
                if (list.size > 0) {
                    var categoryList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        categoryList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, categoryList) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                            (v as TextView).typeface = externalFont
                            v.textSize = 10f
                            v.gravity = Gravity.CENTER
                            return v
                        }

                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                if (list.size <= 2) {
//                    llCategory.visibility = View.GONE
//                    inventoryCategoryId = list[1].id
//                    CommonUtils.showLogMessage("e", "inventoryCategoryId", inventoryCategoryId);
//                    showLoading()
//                    mInventoryViewModel.getCategoryGroup(httpManager, inventoryCategoryId!!)
//                } else {
                    llCategory.visibility = View.VISIBLE
                    spinnerCategory!!.adapter = arrayAdapter



                    spinnerCategory!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            val selectedItem = parent.getItemAtPosition(position).toString()
                            if (position != 0) {
                                inventoryCategoryId = list[position].id
                                CommonUtils.showLogMessage("e", "inventoryCategoryId", inventoryCategoryId);
                                showLoading()
                                spinnerGroup.adapter = null
                                mInventoryViewModel.getCategoryGroup(httpManager, inventoryCategoryId!!)
                            } else {
                                inventoryCategoryId = null
                                inventoryGroupId = null
                                spinnerGroup.adapter = null
                                val listGroup = ArrayList<FilterCategoryData>()
                                val filterCategoryData = FilterCategoryData()
                                filterCategoryData.id = "-1"
                                filterCategoryData.name = "All Group"

                                listGroup.add(filterCategoryData)
                                var buddyList: MutableList<String?> = java.util.ArrayList()
                                for (data in listGroup) {
                                    buddyList.add(data.name!!)
                                }
                                var arrayAdapter = object : ArrayAdapter<String?>(this@InventoryActivity, android.R.layout.simple_spinner_item, buddyList) {
                                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                                        val v = super.getView(position, convertView, parent)
                                        val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                                        (v as TextView).typeface = externalFont
                                        v.textSize = 10f
                                        v.gravity = Gravity.CENTER
                                        return v
                                    }

                                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                        val v = super.getDropDownView(position, convertView, parent)
                                        val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                                        (v as TextView).typeface = externalFont
                                        //v.setBackgroundColor(Color.GREEN);
                                        return v
                                    }
                                }
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerGroup!!.adapter = arrayAdapter
                            }


                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                    //}


                }

            }
        }


    }

    override fun handleInventoryResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback,error, result, this)){
            val jsonConverter: JSONConverter<InventoryResponse> = JSONConverter()
            var response: InventoryResponse = jsonConverter.jsonToObject(result.toString(), InventoryResponse::class.java) as InventoryResponse
            if (response.inventories != null) {
                if (response.inventories != null) {
                    this@InventoryActivity.isLoading = false
                    adapter.addItems(response.inventories as ArrayList<Inventory>)
                    if (response.inventoriesRequest != null && response.inventoriesRequest!!.paginationData != null) {

                        CommonUtils.showLogMessage("e", "adapter total_count =>",
                                "" + adapter.getItemCount())
                        CommonUtils.showLogMessage("e", "fetch total_count =>",
                                "" + response.inventoriesRequest!!.paginationData!!.dataCount)
                        if (response.inventoriesRequest!!.paginationData!!.dataCount == adapter.getAllList().size) {
                            isLastPage = true
                        }else{
                            isLastPage=false
                        }
                    }

                    // Collections.sort(response.inventories as ArrayList<Inventory>, InventoryComparator())

                }
            }
        }


    }

    override fun handleCategoryGroupResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<CategoryGroupResponse> = JSONConverter()
            var response: CategoryGroupResponse = jsonConverter.jsonToObject(result.toString(), CategoryGroupResponse::class.java) as CategoryGroupResponse

            if (response.groupIds != null && response.groupIds!!.isNotEmpty()) {
                val listGroup = ArrayList<FilterCategoryData>()
                val filterCategoryData = FilterCategoryData()
                filterCategoryData.id = "-1"
                filterCategoryData.name = "All Group"

                listGroup.add(filterCategoryData)
                val hmIterator: Iterator<*> = response.groupIds!!.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val filterCategoryData = FilterCategoryData()
                    filterCategoryData.id = mapElement.key.toString()
                    filterCategoryData.name = mapElement.value.toString()

                    listGroup.add(filterCategoryData)
                }
                if (listGroup.size > 0) {
                    var groupList: MutableList<String?> = java.util.ArrayList()
                    for (data in listGroup) {
                        groupList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, groupList) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                            (v as TextView).typeface = externalFont
                            v.textSize = 10f
                            v.gravity = Gravity.CENTER
                            return v
                        }

                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                if (listGroup.size == 2) {
//                    llGroup.visibility = View.GONE
//                    inventoryGroupId = listGroup[1].id
//                    CommonUtils.showLogMessage("e", "inventoryGroupId", inventoryGroupId);
//
//                } else {
                    llGroup.visibility = View.VISIBLE
                    spinnerGroup!!.adapter = arrayAdapter

                    spinnerGroup!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            val selectedItem = parent.getItemAtPosition(position).toString()
                            if (position != 0) {
                                inventoryGroupId = listGroup[position].id
                                CommonUtils.showLogMessage("e", "inventoryGroupId", inventoryGroupId);
//                            var getUserManualLocationData = GetManualLocationRequest()
//                            getUserManualLocationData.regionId = regionId
//                            spnCategoryStartState.adapter = null
//                            stateId = null
//                            showLoading()
//                            mTaskFilterViewModel.getStateList(httpManager, getUserManualLocationData, true)
                                //  showLoading()
                                // mInventoryViewModel.getCategoryGroup(httpManager, inventoryCategoryId!!)
                            } else {
                                inventoryGroupId = null
                            }


                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                }


                // }

            }
        }

    }

    override fun linkInventoryResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val intent = Intent()
            intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_ID, buddyId)
            intent.putExtra(AppConstants.Extra.EXTRA_FLEET_ID, fleetId)
            intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }


    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    companion object {
        private val TAG = InventoryActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, InventoryActivity::class.java)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSubmit -> {
                showLoading()
                adapter.clearList()
                isLastPage = false
                isLoading = false
                var request = InventoryRequest()
                request.categoryId = categoryId
                val pagingData = PagingData()
                pagingData.datalimit = 10
                currentPage = PaginationListener.PAGE_START
                pagingData.pageOffset = (currentPage - 1) * 10
                pagingData.pageIndex = currentPage
                request.paginationData = pagingData
                request.inventoryCategoryId = inventoryCategoryId
                request.inventoryGroupId = inventoryGroupId
                mInventoryViewModel.checkInventoryData(httpManager, request, false)


            }
            R.id.btnCancel -> {
                finish()
            }
            R.id.btnProceed -> {
                if (intent.hasExtra(Extra.EXTRA_CTA_ID)) {
                    if (intent.hasExtra(Extra.EXTRA_TASK_ID)) {
                        taskId = intent.getStringExtra(Extra.EXTRA_TASK_ID)
                    }
                    var ctaId = intent.getStringExtra(Extra.EXTRA_CTA_ID)

                    var list = adapter.getAllList().filter { it.isAdded }
                    if (list.isNotEmpty()) {
                        var listInventoryIds = ArrayList<String>()
                        var listInventoryQuantity = ArrayList<Int>()
                        for (data in list) {
                            listInventoryIds.add(data.fleetId!!)
                            listInventoryQuantity.add(data.addquantity)
                        }
                        var linkRequest = LinkInventoryRequest()
                        linkRequest.quantitys = listInventoryQuantity
                        linkRequest.inventoryIds = listInventoryIds
                        linkRequest.categoryId = categoryId
                        linkRequest.taskId = taskId
                        linkRequest.type = inventoryConfig!!.availabilityType
                        linkRequest.autoApprove = inventoryConfig != null &&inventoryConfig!!.approvalType!=null&&inventoryConfig!!.approvalType==ApprovalType.AUTO

                        linkRequest.ctaId = ctaId
                        val listCategory = preferencesHelper.workFlowCategoriesList
                        if (categoryId != null) {
                            val workFlowCategories = WorkFlowCategories()
                            workFlowCategories.categoryId = categoryId
                            if (listCategory.contains(workFlowCategories)) {
                                val position: Int = listCategory.indexOf(workFlowCategories)
                                if (position != -1) {
                                    val myCatData: WorkFlowCategories = listCategory.get(position)
                                    // userGeoReq=myCatData.getAllowGeography();
                                    if (myCatData.inventoryConfig != null &&myCatData.inventoryConfig!!.approvalType!=null&& myCatData.inventoryConfig!!.approvalType==ApprovalType.MANUAL
                                            &&myCatData.inventoryConfig!!.approvalOn!=null&& myCatData.inventoryConfig!!.approvalOn==ApprovalOn.SUB_TASK)  {
                                        linkRequest.createSubTask = true
                                        var subTaskConfig = myCatData.subTaskConfig
                                        if (subTaskConfig != null && subTaskConfig.categories!!.isNotEmpty()) {
                                           // linkRequest.subCategoryId = subTaskConfig.categories!![0]
                                            linkRequest.subTaskCategory = subTaskConfig.categories!![0]
                                            linkRequest.parentTaskId = taskId
                                        }

                                    }
                                    if (myCatData.inventoryConfig != null &&myCatData.inventoryConfig!!.flavorId!=null&&myCatData.inventoryConfig!!.flavorId!!.isNotEmpty())
                                    {
                                        linkRequest.flavorId=myCatData.inventoryConfig!!.flavorId
                                    }
                                }
                            }
                        }
                        val jsonConverter: JSONConverter<LinkInventoryRequest> = JSONConverter()
                        var strRequest = jsonConverter.objectToJson(linkRequest)
                        CommonUtils.showLogMessage("e", "strRequest", strRequest);
                        showLoading()
                        mInventoryViewModel.linkInventory(httpManager, linkRequest)
                    } else {
                        TrackiToast.Message.showShort(this,"Please add items")
                    }


                } else {
                    var list = adapter.getAllList().filter { it.isAdded }
                    if (list.isNotEmpty()) {
                        val intent = NewCreateTaskActivity.newIntent(this)
                        // intent.putExtra(Extra.FROM, "taskListing")
                        val dashBoardBoxItem = DashBoardBoxItem()
                        dashBoardBoxItem.categoryId = categoryId
                        intent.putExtra(Extra.EXTRA_CATEGORIES,
                                Gson().toJson(dashBoardBoxItem))
                        intent.putExtra(Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true)
                        startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK)
                    } else {
                        TrackiToast.Message.showShort(this,"Please add items")
                    }
                }


            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                taskId = data!!.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                buddyName = data!!.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_NAME)
                buddyId = data!!.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_ID)
                fleetId = data!!.getStringExtra(AppConstants.Extra.EXTRA_FLEET_ID)
                var list = adapter.getAllList().filter { it.isAdded }
                var listInventoryIds = ArrayList<String>()
                var listInventoryQuantity = ArrayList<Int>()
                for (data in list) {
                    listInventoryIds.add(data.fleetId!!)
                    listInventoryQuantity.add(data.addquantity)
                }
                var linkRequest = LinkInventoryRequest()
                linkRequest.quantitys = listInventoryQuantity
                linkRequest.inventoryIds = listInventoryIds
                linkRequest.categoryId = categoryId
                linkRequest.taskId = taskId
                linkRequest.type = inventoryConfig!!.availabilityType
                //linkRequest.autoApprove = true
                val listCategory = preferencesHelper.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position: Int = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData: WorkFlowCategories = listCategory.get(position)
                            // userGeoReq=myCatData.getAllowGeography();
                            if (myCatData.inventoryConfig != null &&myCatData.inventoryConfig!!.approvalType!=null&& myCatData.inventoryConfig!!.approvalType==ApprovalType.MANUAL
                                    &&myCatData.inventoryConfig!!.approvalOn!=null&& myCatData.inventoryConfig!!.approvalOn==ApprovalOn.SUB_TASK) {
                                linkRequest.createSubTask = true
                                var subTaskConfig=myCatData.subTaskConfig
                                 if(subTaskConfig!=null&& subTaskConfig.categories!!.isNotEmpty()){
                                     //linkRequest.subCategoryId= subTaskConfig.categories!![0]
                                     linkRequest.subTaskCategory= subTaskConfig.categories!![0]
                                     linkRequest.parentTaskId = taskId
                                 }

                            }
                            if (myCatData.inventoryConfig != null &&myCatData.inventoryConfig!!.flavorId!=null&&myCatData.inventoryConfig!!.flavorId!!.isNotEmpty())
                            {
                                linkRequest.flavorId=myCatData.inventoryConfig!!.flavorId
                            }
                        }
                    }
                }
                val jsonConverter: JSONConverter<LinkInventoryRequest> = JSONConverter()
                var strRequest = jsonConverter.objectToJson(linkRequest)
                CommonUtils.showLogMessage("e", "strRequest", strRequest);
                showLoading()
                mInventoryViewModel.linkInventory(httpManager, linkRequest)

            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }

    }



}