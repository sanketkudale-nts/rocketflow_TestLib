package com.rocketflyer.rocketflow.ui.addcustomer


import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper

import com.tracki.data.model.response.config.*

import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAddCustomerBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.newdynamicform.NewDynamicFormFragment
import com.tracki.ui.tasklisting.TaskPagerAdapter
import com.tracki.ui.tasklisting.ihaveassigned.TabDataClass
import com.tracki.ui.useraddresslist.UserAddressListActivity
import com.tracki.ui.userattendancedetails.UserAttendanceDetailsActivity
import com.tracki.utils.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_new_task_details.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

open class AddCustomerActivity : BaseActivity<ActivityAddCustomerBinding, CustomerViewModel>(), HasSupportFragmentInjector, CustomerInfoScreenFragment.OnDataSubmitListener {


    private var userData: UserData?=null
    private var property: String? = null
    private var from: String? = null
    private var action: String? = null
    private var  purpuse = "Add"
    private val fragments: MutableList<TabDataClass> = ArrayList()
    var requestUserType: ArrayList<String>? = null
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var mAddUserViewModel: CustomerViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper


    @Inject
    lateinit var httpManager: HttpManager

    lateinit var binding: ActivityAddCustomerBinding
    private var snackBar: Snackbar? = null

    @Inject
    lateinit var mPagerAdapter: TaskPagerAdapter

    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(binding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding

        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            property = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)
        }
        if (intent.hasExtra("action")){
            action=intent.getStringExtra("action")
        }
        if(intent.hasExtra(AppConstants.REQUESTED_USER_TYPES)){
            requestUserType=intent.getStringArrayListExtra(AppConstants.REQUESTED_USER_TYPES)
        }

        if (intent.hasExtra("from")) {
            from = intent.getStringExtra("from")
             purpuse = "Add"
            if (intent.hasExtra("userData")) {
                purpuse = "Update"
                 userData = intent.getParcelableExtra<UserData>("userData")
                fragments.add(TabDataClass(CustomerInfoScreenFragment.newInstance(from, property, userData,action,null, this), "Basic Info"))
            } else {
                fragments.add(TabDataClass(CustomerInfoScreenFragment.newInstance(from, property, null,action,requestUserType, this), "Basic Info"))
            }

            if (from.equals(AppConstants.EMPLOYEES)) {
                if(action.equals("view")){
                    setToolbar(binding.toolbar, "Employee Details")
                }else{
                    setToolbar(binding.toolbar, "${purpuse} Employee")
                }


            } else if (from.equals(AppConstants.CUSTOMERS)) {
                if(action.equals("view")){
                    setToolbar(binding.toolbar, "Customer Details")
                }else{
                    setToolbar(binding.toolbar, "${purpuse} Customer")
                }

            }
            if(action.equals("view")){
                binding.bottomNavigationView.visibility=View.VISIBLE
            }else{
                binding.bottomNavigationView.visibility=View.GONE
            }
            binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_location -> {
                        openAddressActivity()
                    }
                    R.id.action_attendance -> {
                        openAttendanceActivity()
                    }
                    R.id.action_leave -> {
                        openLeaveActivity()
                    }


                }
                true
            }


        }




        if (fragments.size > 1) {
            tabLayout.visibility = View.VISIBLE
        } else {
            tabLayout.visibility = View.GONE
        }

        mPagerAdapter.setFragments(fragments)
        vpTask.adapter = mPagerAdapter
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.tabMode = TabLayout.MODE_FIXED
        tabLayout.setupWithViewPager(vpTask)

        vpTask.offscreenPageLimit = tabLayout.tabCount
        vpTask.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }

    private fun openAttendanceActivity() {
        var empData = EmpData()
        empData.userImg = userData!!.profileImg
        empData.userId = userData!!.userId
        empData.mobile = userData!!.mobile
        empData.email = userData!!.email
        empData.name = returnName(userData!!)
        var intent = Intent(this, UserAttendanceDetailsActivity::class.java)
        intent.putExtra("empData", empData)
        intent.putExtra("action", "attendance")
        startActivity(intent)
    }
    private fun openLeaveActivity() {
        var empData = EmpData()
        empData.userImg = userData!!.profileImg
        empData.userId = userData!!.userId
        empData.mobile = userData!!.mobile
        empData.email = userData!!.email
        empData.name = returnName(userData!!)
        var intent = Intent(this, UserAttendanceDetailsActivity::class.java)
        intent.putExtra("empData", empData)
        intent.putExtra("action", "leave")
        startActivity(intent)
    }
    private fun openAddressActivity() {
        var empData = EmpData()
        empData.userImg = userData!!.profileImg
        empData.userId = userData!!.userId
        empData.mobile = userData!!.mobile
        empData.email = userData!!.email
        empData.roleId = userData!!.roleId
        empData.name = returnName(userData!!)
        var intent = Intent(this, UserAddressListActivity::class.java)
        intent.putExtra("empData", empData)
        startActivity(intent)
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


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_customer
    }

    override fun getViewModel(): CustomerViewModel {
        return mAddUserViewModel
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onSuccess() {
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(BaseActivity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onCancel() {
        val returnIntent = Intent()
        setResult(BaseActivity.RESULT_CANCELED, returnIntent)
        finish()
    }

    override fun onUpdateDataSucess(userData: UserData?) {
        val returnIntent = Intent()
        returnIntent.putExtra("userData", userData)
        setResult(BaseActivity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onUpdateHeader(roleName: String?) {
        if (roleName!=null) {
            if(action.equals("view")){
                setToolbar(binding.toolbar, "${roleName} Details")
            }else{
                setToolbar(binding.toolbar, "${purpuse} ${roleName}")
            }


        }
       /* if (type!=null&&type.equals("INTERNAL")) {
            if(action.equals("view")){
                setToolbar(binding.toolbar, "Employee Details")
            }else{
                setToolbar(binding.toolbar, "${purpuse} Employee")
            }


        } else if (type!=null&&type.equals("EXTERNAL"))  {
            if(action.equals("view")){
                setToolbar(binding.toolbar, "Customer Details")
            }else{
                setToolbar(binding.toolbar, "${purpuse} Customer")
            }

        }*/
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.uncheckAllItems()
    }
    fun BottomNavigationView.uncheckAllItems() {
        menu.setGroupCheckable(0, true, false)
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }
}