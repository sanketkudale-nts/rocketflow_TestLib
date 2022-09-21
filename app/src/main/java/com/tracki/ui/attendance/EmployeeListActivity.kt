package com.tracki.ui.attendance

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AttendanceReq
import com.tracki.data.model.request.EmployeeListAttendanceRequest
import com.tracki.data.model.request.TeamAttendanceRequest
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityEmployeeListBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.chat.ChatActivity
import com.tracki.ui.main.filter.TaskFilterActivity
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.ui.tasklisting.PagingData
import com.tracki.ui.userattendancedetails.UserAttendanceDetailsActivity
import com.tracki.utils.*
import kotlinx.android.synthetic.main.activity_employee_list.*
import javax.inject.Inject


class EmployeeListActivity : BaseActivity<ActivityEmployeeListBinding, EmployeeListViewModel>(),
    EmployeeListNavigator, EmployeeListAdapter.OnChatListener {

    private var hubIdStr: String? = null
    private var roomId: String? = null

    @Inject
    lateinit var mEmployeeViewModel: EmployeeListViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: EmployeeListAdapter

    lateinit var binding: ActivityEmployeeListBinding

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var status: String? = null
    private var stageName: String? = null
    private var regionId: String? = null
    private var hubIds: List<String>? = null
    private var cityIdList: ArrayList<String>? = null
    private var stateId: String? = null
    private var cityId: String? = null
    private var isFilterApply: Boolean = false
    var date: Long = 0
    var teamAttendanceRequest: TeamAttendanceRequest? = null

    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mEmployeeViewModel.navigator = this
        adapter.setListener(this)
        if (intent.hasExtra("status")) {
            status = intent.getStringExtra("status")

        }
        if (intent.hasExtra("request")) {
            teamAttendanceRequest = intent.getSerializableExtra("request") as TeamAttendanceRequest?
            if (teamAttendanceRequest != null) {
                isFilterApply = teamAttendanceRequest!!.geoFilter
                date = teamAttendanceRequest!!.date
                regionId = teamAttendanceRequest!!.regionId
                hubIds = teamAttendanceRequest!!.hubId
                cityIdList = teamAttendanceRequest!!.cityId as ArrayList<String>?
                stateId = teamAttendanceRequest!!.stateId
            }

        }
        if (intent.hasExtra("stageName")) {
            stageName = intent.getStringExtra("stageName")
            if (stageName != null && stageName!!.isNotEmpty()) {
                setToolbar(toolbar, stageName)
            }

        }

        binding.adapter = adapter

        getEmployeeList()

        var layoutManger: LinearLayoutManager =
            employeeList!!.layoutManager!! as LinearLayoutManager
        employeeList.addOnScrollListener(object : PaginationListener(layoutManger) {
            override fun loadMoreItems() {
                this@EmployeeListActivity.isLoading = true
                currentPage++
                showLoading()
                var request = EmployeeListAttendanceRequest()
                request.status = status
                request.date = date
                if (isFilterApply)
                    request.geoFilter = preferencesHelper.userGeoFilters()
                val pagingData = PagingData()
                pagingData.datalimit = 10
                pagingData.pageOffset = (currentPage - 1) * 10
                pagingData.pageIndex = currentPage
                request.paginationData = pagingData
                request.status = status
                request.cityId = cityIdList
                request.regionId = regionId
                request.stateId = stateId
                request.hubId = hubIds
                mEmployeeViewModel.getEmployeeList(httpManager, request)

            }

            override fun isLastPage(): Boolean {
                return this@EmployeeListActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@EmployeeListActivity.isLoading
            }
        })
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_employee_list
    }

    override fun getViewModel(): EmployeeListViewModel {
        return mEmployeeViewModel
    }

    fun getEmployeeList() {
        showLoading()
        var request = EmployeeListAttendanceRequest()
        request.date = date
        val pagingData = PagingData()
        pagingData.datalimit = 10
        pagingData.pageOffset = (currentPage - 1) * 10
        pagingData.pageIndex = currentPage
        request.paginationData = pagingData
        if (isFilterApply)
            request.geoFilter = preferencesHelper.userGeoFilters()
        request.status = status
        request.cityId = cityIdList
        request.regionId = regionId
        request.stateId = stateId
        request.hubId = hubIds
        mEmployeeViewModel.getEmployeeList(httpManager, request)
    }


    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@EmployeeListActivity)) {
            val jsonConverter: JSONConverter<EmployeeListResponse> = JSONConverter()
            var response: EmployeeListResponse = jsonConverter.jsonToObject(
                result.toString(),
                EmployeeListResponse::class.java
            ) as EmployeeListResponse
            if (response.data != null) {
                this@EmployeeListActivity.isLoading = false
                adapter.addItems(response.data as ArrayList<EmpData>)
                CommonUtils.showLogMessage(
                    "e", "adapter total_count =>",
                    "" + adapter.getItemCount()
                )
                CommonUtils.showLogMessage(
                    "e", "fetch total_count =>",
                    "" + response.totalCount
                )
                isLastPage = response.totalCount == adapter.getAllList().size


            }
        } else {
        }

    }

    private fun openSearchDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_search_employee)
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
        val btnCancel = dialog.findViewById<ImageView>(R.id.btnCancel)
        val spnSearchBuy = dialog.findViewById<Spinner>(R.id.spnSearchBuy)
        val btnSearch = dialog.findViewById<Button>(R.id.btnSearch)
        val edValue = dialog.findViewById<EditText>(R.id.edValue)
        btnCancel.setOnClickListener { dialog.dismiss() }
        var searchBuy: MutableList<String?> = java.util.ArrayList()
        searchBuy.add("Mobile")
        searchBuy.add("Name")

        var arrayAdapter =
            object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, searchBuy) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    val externalFont =
                        Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                    (v as TextView).typeface = externalFont
                    (v as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
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
                    (v as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                    //v.setBackgroundColor(Color.GREEN);
                    return v
                }
            }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSearchBuy!!.adapter = arrayAdapter

        // mCategoryId.value = "0"
        var searchByString = ""
        spnSearchBuy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem == "Mobile") {
                    searchByString = "MOBILE"
                    edValue.inputType = InputType.TYPE_CLASS_PHONE
                    edValue.hint = "Enter Phone Number"
                } else if (selectedItem == "Name") {
                    searchByString = "NAME"
                    edValue.inputType = InputType.TYPE_CLASS_TEXT
                    edValue.hint = "Enter Name"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        btnSearch.setOnClickListener {
            var value = edValue.text.toString().trim()
            if (value.isEmpty()) {
                TrackiToast.Message.showShort(this, "Please " + edValue.hint)
            } else if (searchByString=="MOBILE"&&!CommonUtils.isMobileValid(value)) {
                TrackiToast.Message.showShort(this, "Please enter valid mobile number")
            }  else {
                dialog.dismiss()
                adapter.clearList()
                currentPage = PaginationListener.PAGE_START
                searchUser(searchByString, value)
            }
        }
        if (!dialog.isShowing) dialog.show()
    }

    fun searchUser(searchByString: String, value: String) {
        showLoading()
        var request = EmployeeListAttendanceRequest()
        request.date = date
        val pagingData = PagingData()
        pagingData.datalimit = 10
        pagingData.pageOffset = (currentPage - 1) * 10
        pagingData.pageIndex = currentPage
        request.paginationData = pagingData
        if (isFilterApply)
            request.geoFilter = preferencesHelper.userGeoFilters()
        request.status = status
        request.cityId = cityIdList
        request.regionId = regionId
        request.stateId = stateId
        request.hubId = hubIds
        if (searchByString == "MOBILE") {
            request.mobile = value
        } else if (searchByString == "NAME") {
            request.name = value
        }
        mEmployeeViewModel.getEmployeeList(httpManager, request)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_employee, menu)
        val item = menu!!.findItem(R.id.action_filter)
        item.isVisible = preferencesHelper.userGeoFilters()
        val myActionMenuItem = menu.findItem(R.id.action_search)
        myActionMenuItem.isVisible = true
        /* searchView = myActionMenuItem.actionView as SearchView
         var textView: TextView = searchView!!.findViewById(R.id.search_src_text)
         val externalFont = Typeface.createFromAsset(this.assets, "fonts/campton_book.ttf")
         textView.typeface = externalFont
         textView.setTextColor(ContextCompat.getColor(this, R.color.black))
         val closeButton: ImageView = searchView!!.findViewById(R.id.search_close_btn)

         closeButton.setOnClickListener(object : View.OnClickListener {
             override fun onClick(v: View?) {
                 searchView?.isIconified = true
                 myActionMenuItem.collapseActionView()
                 // TrackiToast.Message.showShort(this@MessagesActivity, "close click")
                 adapter.populateList()
             }
         })



         searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(query: String): Boolean {

                 if (!(searchView?.isIconified!!)) {
                     searchView?.isIconified = true
                 }
                 if (adapter.mList?.size!! > 0) {
                     adapter.addFilter(query)
                 } *//*else {
                    TrackiToast.Message.showShort(this@MessagesActivity, getString(R.string.cannot_performe_this_operation))
                }*//*

                myActionMenuItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                if (adapter.mList?.size!! > 0) {
                    adapter.addFilter(newText)
                } *//*else {
                    TrackiToast.Message.showShort(this@MessagesActivity, getString(R.string.cannot_performe_this_operation))
                }*//*
                return false
            }
        })*/
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                val intent = TaskFilterActivity.newIntent(this)
                intent.putExtra("from", AppConstants.ATTENDANCE)
                startActivityForResult(intent, AppConstants.REQUEST_CODE_FILTER_USER)
            }
            R.id.action_search -> {
                openSearchDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_FILTER_USER) {
            if (resultCode == RESULT_OK) {
                adapter.clearList()
                regionId = data!!.getStringExtra("regionId");
                hubIdStr = data!!.getStringExtra("hubId");
                hubIds = null
                cityIdList = null
                if (hubIdStr != null && !hubIdStr!!.isEmpty()) {
                    hubIds = hubIdStr!!.split("\\s*,\\s*").toList()
                }
                stateId = data.getStringExtra("stateId")
                cityId = data.getStringExtra("cityId")
                if (cityId != null) {
                    cityIdList = ArrayList<String>()
                    cityIdList!!.add(cityId!!)
                }
                isFilterApply = true
                getEmployeeList()

            } else if (resultCode == RESULT_CANCELED) {
                regionId = null
                hubIds = null
                hubIdStr = null
                cityIdList = null
                stateId = null
                cityId = null
                isFilterApply = false
                getEmployeeList()

            } else if (resultCode == REFRESH_LIST) {
                regionId = null
                hubIds = null
                hubIdStr = null
                cityIdList = null
                stateId = null
                cityId = null
                isFilterApply = false
                adapter.clearList()
                currentPage = PaginationListener.PAGE_START
                getEmployeeList()

            }
        }
    }

    override fun onChatStart(buddyId: String?, buddyName: String?) {
        if (buddyId != null && buddyId.isNotEmpty()) {
            val list = ArrayList<String>()
            list.add(buddyId!!)

            startActivity(
                ChatActivity.newIntent(this)
                    .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, list)
                    .putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
                    .putExtra(AppConstants.Extra.EXTRA_IS_CREATE_ROOM, true)
                    .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId)
            )
        }
    }

    var REFRESH_LIST = 122
    override fun openEmpAttendanceData(data: EmpData?, position: Int) {
        var intent = Intent(this, UserAttendanceDetailsActivity::class.java)
        intent.putExtra("empData", data)
        intent.putExtra("action", "attendance")
        startActivityForResult(intent, REFRESH_LIST)
    }


}