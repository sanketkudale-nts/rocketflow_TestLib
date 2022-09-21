package com.rocketflyer.rocketflow.ui.userlisting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.ResponseBasic
import com.tracki.data.model.ResponseBasic2
import com.tracki.data.model.request.ExecuteUpdateRequest
import com.tracki.data.model.request.TaskData
import com.tracki.data.model.response.config.DynamicFormData
import com.tracki.data.model.response.config.UserData
import com.tracki.data.model.response.config.UserListResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityUserListNewBinding
import com.tracki.databinding.ActivityUserListingBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.main.MainActivity
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import com.tracki.utils.Log
import com.tracki.utils.TrackiToast
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserListNewActivity : BaseActivity<ActivityUserListNewBinding, UserListNewViewModel>(), UserListNewNavigator, UserListNewAdapter.onUserSelected{
    @Inject
    lateinit var mUserListViewModel: UserListNewViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: UserListNewAdapter

    lateinit var searchView: SearchView
    
    var listSelectedUsers = ArrayList<String>()

    private var mLayoutManager: LinearLayoutManager? = null

    lateinit var binding: ActivityUserListNewBinding
    var roleIds: String = "na"
    var users: String = ""
    lateinit var request: ExecuteUpdateRequest
    lateinit var usersList: ArrayList<UserData>

    private var rvUserList: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        usersList = ArrayList()
        searchView = binding.svSearchUsers
        setRecyclerView()
        adapter.setListener(this)

        mUserListViewModel.navigator = this
        
        if (intent.hasExtra("roleIds"))
        {
            roleIds = intent.getStringExtra("roleIds").toString()
            getUsers()
            request = intent.getSerializableExtra("request") as ExecuteUpdateRequest
        }
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null || newText != "")
                    adapter.addFilter(newText.toString())
                else
                    adapter.addFilter("")
                return true
            }
        })
        binding.btnUsersSubmit.setOnClickListener {

            if (listSelectedUsers.size > 0){
                for (i in 0 until listSelectedUsers.size){
                    users += if (i > 0)
                        ",${listSelectedUsers[i]}"
                    else
                        listSelectedUsers[i]
                }
                var dynamicFormData = DynamicFormData()
                dynamicFormData.key = "SELECT_BUDDY"
                dynamicFormData.value = users
                var list = ArrayList<DynamicFormData>()
                list.add(dynamicFormData)
                val taskData = TaskData(request.ctaId,list,request.timestamp)
                request.taskData = taskData
                mUserListViewModel.executeUpdates(httpManager,request)

            }
            else{
                TrackiToast.Message.showShort(this,"Please select atleast one user")
            }
        }


    }

    private fun getUsers(){
        Log.e("getUsers","$roleIds")
        mUserListViewModel.getUserList(httpManager,roleIds,null,null,true)
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

            } catch (e: Exception) {
            }
        }
        rvUserList!!.adapter = adapter
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_list_new
    }

    override fun getViewModel(): UserListNewViewModel {
        return mUserListViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@UserListNewActivity)) {
            val jsonConverter: JSONConverter<UserListResponse> = JSONConverter()
            var response: UserListResponse = jsonConverter.jsonToObject(result.toString(), UserListResponse::class.java) as UserListResponse
            if (response.data != null) {
                setRecyclerView()
                val userList = response.data as ArrayList<UserData>
                Log.e("listUserList","$userList")
                usersList = response.data as ArrayList<UserData>
                adapter.addItems(usersList)
            } else {
                setRecyclerView()
                adapter.addItems(ArrayList())
            }
        } else {
            setRecyclerView()
            adapter.addItems(ArrayList())
        }
    }

    override fun handleExecuteUpdateResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        val gson = Gson()
        val responseBasic = gson.fromJson(
            result.toString(),
            ResponseBasic2::class.java
        )
        if (responseBasic != null){
            if (responseBasic.successful == true){
                startActivity(Intent(this,MainActivity::class.java))
            }
        }
        else{
            TrackiToast.Message.showShort(this,"Problem in Server Please try later")
        }
    }

    override fun addUser(userId: String) {
        listSelectedUsers.add(userId)
    }

    override fun removeUser(userId: String) {
        listSelectedUsers.remove(userId)
    }
}