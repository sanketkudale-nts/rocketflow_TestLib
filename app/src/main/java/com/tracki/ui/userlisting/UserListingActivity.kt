package com.tracki.ui.userlisting

import android.app.Activity
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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AttendanceReq
import com.tracki.data.model.response.config.RoleConfigData
import com.tracki.data.model.response.config.UserData
import com.tracki.data.model.response.config.UserListResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityUserListingBinding
import com.tracki.ui.addcustomer.AddCustomerActivity
import com.tracki.ui.addcustomer.RoleSelectedAdapter
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.ui.userdetails.UserDetailsActivity
import com.tracki.utils.*
import kotlinx.android.synthetic.main.activity_user_listing.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class UserListingActivity : BaseActivity<ActivityUserListingBinding, UserListViewModel>(), UserListNavigator, UserListAdapter.OnUserSelected, View.OnClickListener, RoleSelectedAdapter.RoleSelectedListener {
    private var attendanceReq: AttendanceReq? = null
    private var deleteUserData: UserData? = null
    private var property: String? = null
    private var from: String? = null
    private var type: String? = null
    private var searchView: SearchView? = null
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var rvUserList: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null

    @Inject
    lateinit var mUserListViewModel: UserListViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: UserListAdapter

    lateinit var binding: ActivityUserListingBinding
    private var categoryMap: Map<String, String>? = null
    private var roleId: String? = null
    private var searchByString: String? = null
    private var value: String? = null
    private var fromSearch: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
//        binding.adapter = adapter
        setRecyclerView()
        adapter.setListener(this)
        mUserListViewModel.navigator = this
        var listRolls = preferencesHelper.roleConfigDataList
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            property = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)
            CommonUtils.showLogMessage("e","property",property)
            categoryMap = Gson().fromJson<Map<String, String>>(property, object : TypeToken<HashMap<String?, String?>?>() {}.type)
            if (categoryMap != null && categoryMap!!.containsKey("RoleId")) {
                roleId = categoryMap!!["RoleId"]
                CommonUtils.showLogMessage("e","roleid",roleId)
            }
        }
        if (intent.hasExtra("from")) {
            from = intent.getStringExtra("from")
            if (from.equals(AppConstants.EMPLOYEES)) {
                setToolbar(binding.toolbar, "Employees")
               populateRoleTab(listRolls)
            } else if (from.equals(AppConstants.CUSTOMERS)) {
                setToolbar(binding.toolbar, "Customer")
                adapter.rollStatus(true)
            }
        }

        binding.ivAddUser.setOnClickListener(this)
        getUserListFromServer()
    }

    private fun populateRoleTab(listRolls: List<RoleConfigData>) {
        if (!listRolls.isNullOrEmpty()) {
            var roleConfigData = RoleConfigData(roleId = roleId)
            if (listRolls.contains(roleConfigData)) {
                var index = listRolls.indexOf(roleConfigData)
                if (index != -1) {
                    listRolls[index].isSelected = true
                }
            }
            var roleAdapter = RoleSelectedAdapter()
            roleAdapter.addItems((listRolls as ArrayList<RoleConfigData>?)!!)
            roleAdapter.setListener(this)
            binding.roleAdapter = roleAdapter
            onItemClick(roleAdapter.mList[0])
            binding.llRole.visibility = View.VISIBLE

        }
    }

    fun getUserListFromServer() {
        showLoading()
        attendanceReq = AttendanceReq()
        attendanceReq!!.limit = 10
        attendanceReq!!.offset = (currentPage - 1) * 10
        attendanceReq!!.dataCount = 10
        mUserListViewModel.getUserList(httpManager, roleId, type, attendanceReq!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                openSearchDialog()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        /*  val item = menu!!.findItem(R.id.action_filter)
  //        item.isVisible = preferencesHelper.userGeoFilters()
          val myActionMenuItem = menu.findItem(R.id.action_search)
          myActionMenuItem.isVisible = true
          searchView = myActionMenuItem.actionView as SearchView
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

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_listing
    }

    override fun getViewModel(): UserListViewModel {
        return mUserListViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        this.isLoading = false
        if (CommonUtils.handleResponse(callback, error, result, this@UserListingActivity)) {
            val jsonConverter: JSONConverter<UserListResponse> = JSONConverter()
            var response: UserListResponse = jsonConverter.jsonToObject(result.toString(), UserListResponse::class.java) as UserListResponse
            if (response.data != null) {
                setRecyclerView()
                adapter.addItems(response.data as ArrayList<UserData>)
                CommonUtils.showLogMessage("e", "adapter total_count =>",
                        "" + adapter.itemCount)
                CommonUtils.showLogMessage("e", "fetch total_count =>",
                        "" + response.count)
                isLastPage = adapter.itemCount >= response.count
            } else {
                setRecyclerView()
                adapter.addItems(ArrayList())
            }
        } else {
            setRecyclerView()
            adapter.addItems(ArrayList())
        }
    }

    override fun handleDeleteResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@UserListingActivity)) {
            if (deleteUserData != null) {
                var adapterList = adapter.getAllList()
                if (adapterList.contains(deleteUserData!!)) {
                    var index = adapterList.indexOf(deleteUserData!!)
                    if (index != -1) {
                        adapterList.removeAt(index)
                        adapter.notifyItemRemoved(index)
                    }
                }
            }
        }
    }

    override fun onChatStart(buddyId: String?, buddyName: String?) {
    }

    override fun onDeleteUser(data: UserData) {
        val dialog = DoubleButtonDialog(this,
                true,
                null,
                getString(R.string.delete_user),
                getString(R.string.yes),
                getString(R.string.no),
                object : OnClickListener {
                    override fun onClickCancel() {}
                    override fun onClick() {
                        if (data.userId != null && data.userId!!.isNotEmpty()) {
                            showLoading()
                            deleteUserData = data
                            mUserListViewModel.deleteUser(httpManager, data.userId)
                        }
                    }
                })
        dialog.show()

    }

    companion object {
        private const val ADD_USER_TASK = 345
        private val TAG = UserListingActivity::class.java.simpleName

    }

    override fun showDetails(data: UserData) {
//        var intent = Intent(this, AddCustomerActivity::class.java)
        var intent = Intent(this, UserDetailsActivity::class.java)
        intent.putExtra("userData", data)
        intent.putExtra("from", from)
        intent.putExtra("action", "view")
        //    intent.putExtra(property, AppConstants.Extra.EXTRA_CATEGORIES)
        startActivityForResult(intent, ADD_USER_TASK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == ADD_USER_TASK) {
                    adapter.clearList()
                    getUserListFromServer()
                }

            }
            Activity.RESULT_CANCELED -> {

            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivAddUser -> {
                var intent = Intent(this, AddCustomerActivity::class.java)
                intent.putExtra("from", from)
                intent.putExtra("action", "add")
                if((property==null||property.equals("null"))&&roleId!=null){
                    var userData=HashMap<String,String>()
                    userData["RoleId"]=roleId!!
                    var jsonConverter=JSONConverter<HashMap<String,String>>()
                    property=jsonConverter.objectToJson(userData)
                    CommonUtils.showLogMessage("e","property add",property)
                }
                intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, property)
                intent.putExtra(property, AppConstants.Extra.EXTRA_CATEGORIES)
                startActivityForResult(intent, ADD_USER_TASK)
            }
        }
    }

    override fun onItemClick(response: RoleConfigData) {
        fromSearch = false
        currentPage = PaginationListener.PAGE_START
        if (response.roleId != "-1") {
            type = null
            roleId = response.roleId!!
            adapter.clearList()
            getUserListFromServer()
        } else {
            type = "INTERNAL"
            roleId = null
            adapter.clearList()
            getUserListFromServer()
        }
    }

    private fun openSearchDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_search_employee)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        val btnCancel = dialog.findViewById<ImageView>(R.id.btnCancel)
        val spnSearchBuy = dialog.findViewById<Spinner>(R.id.spnSearchBuy)
        val btnSearch = dialog.findViewById<Button>(R.id.btnSearch)
        val edValue = dialog.findViewById<EditText>(R.id.edValue)
        btnCancel.setOnClickListener { dialog.dismiss() }
        var searchBuy: MutableList<String?> = ArrayList()
        searchBuy.add("Mobile")
        searchBuy.add("Name")
        searchBuy.add("Email")

        var arrayAdapter = object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, searchBuy) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                (v as TextView).typeface = externalFont
                (v as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
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
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                if (selectedItem == "Mobile") {
                    searchByString = "MOBILE"
                    edValue.inputType = InputType.TYPE_CLASS_PHONE
                    edValue.hint = "Enter Phone Number"
                } else if (selectedItem == "Name") {
                    searchByString = "NAME"
                    edValue.inputType = InputType.TYPE_CLASS_TEXT
                    edValue.hint = "Enter Name"
                } else if (selectedItem == "Email") {
                    searchByString = "EMAIL"
                    edValue.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    edValue.hint = "Enter Email"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        btnSearch.setOnClickListener {
            var value = edValue.text.toString().trim()
            if (value.isEmpty()) {
                TrackiToast.Message.showShort(this, "Please " + edValue.hint)
            } else {
                dialog.dismiss()
                if (TrackiApplication.getApiMap().containsKey(ApiType.USER_SEARCH)) {
                    this.searchByString = searchByString
                    this.value = value
                    fromSearch = true
                    adapter.clearList()
                    currentPage = PaginationListener.PAGE_START
                    searchUser(searchByString, value)
                } else {
                    CommonUtils.showLogMessage("e", "USER_SEARCH", "API not in config")
                }
            }
        }
        if (!dialog.isShowing) dialog.show()
    }

    fun searchUser(searchByString: String, value: String) {
        showLoading()
        attendanceReq = AttendanceReq()
        attendanceReq!!.limit = 10
        attendanceReq!!.offset = (currentPage - 1) * 10
        attendanceReq!!.dataCount = 10
        mUserListViewModel.searchUser(httpManager, searchByString, value, attendanceReq!!)
    }

    private fun setRecyclerView() {
        if (rvUserList == null) {
            rvUserList = binding.rvUserList
            //  mLayoutManager= (LinearLayoutManager) rvAttendance.getLayoutManager();
            try {
                mLayoutManager = LinearLayoutManager(this)
                mLayoutManager!!.orientation = RecyclerView.VERTICAL
                rvUserList!!.layoutManager = mLayoutManager
                rvUserList!!.itemAnimator = DefaultItemAnimator()

            } catch (e: IllegalArgumentException) {
            }
        }
        rvUserList!!.adapter = adapter
        rvUserList!!.addOnScrollListener(object : PaginationListener(mLayoutManager!!) {
            override fun loadMoreItems() {
                this@UserListingActivity.isLoading = true
                currentPage++
                if (!fromSearch)
                    getUserListFromServer()
                else {
                    searchUser(searchByString!!, value!!)
                }
            }

            override fun isLastPage(): Boolean {
                return this@UserListingActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@UserListingActivity.isLoading
            }
        })
    }

}