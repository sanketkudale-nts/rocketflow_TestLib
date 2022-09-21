package com.tracki.ui.userattendancedetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.EmpData
import com.tracki.data.model.response.config.ProfileInfo
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.databinding.ActivityUserAttendanceDetailsBinding
import com.tracki.ui.attendance.attendance_tab.AttendanceFragment
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.leave.leave_history.LeaveHistoryFragment
import com.tracki.utils.CommonUtils
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class UserAttendanceDetailsActivity : BaseActivity<ActivityUserAttendanceDetailsBinding, UserAttendanceDetailsViewModel>() , HasSupportFragmentInjector,UserAttendanceDetailsNavigator{
    private var action: String?=null

    @Inject
    lateinit var mUserAttendanceDetailsViewModel: UserAttendanceDetailsViewModel

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    lateinit var binding:ActivityUserAttendanceDetailsBinding
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(binding.llMain, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mUserAttendanceDetailsViewModel.navigator = this
        if(intent.hasExtra("action")){
            action=intent.getStringExtra("action")
        }
        if(action=="attendance") {
            setToolbar(binding.toolbar, "Attendance Details")
        }else{
            setToolbar(binding.toolbar, "Leave Details")
        }
        if(intent.hasExtra("empData")){
            var empData: EmpData?=intent.getParcelableExtra("empData")
            if(empData!=null){
                var userData=ProfileInfo()
                userData.userId=empData.userId
                userData.profileImg=empData.userImg
                userData.name=empData.name
                userData.mobile=empData.mobile
                userData.email=empData.email
                userData.status=empData.status
                if(action=="attendance") {
                    addFragmentInContainer(AttendanceFragment.newInstance(userData,true,empData.subordinate!!))
                }else{
                    addFragmentInContainer(LeaveHistoryFragment.newInstance(userData))
                }
            }

        }
    }
    private fun addFragmentInContainer(fragment: Fragment) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.disallowAddToBackStack()
        ft.replace(R.id.container, fragment)
        ft.commit()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
       return R.layout.activity_user_attendance_details
    }

    override fun getViewModel(): UserAttendanceDetailsViewModel {
        return mUserAttendanceDetailsViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
       return fragmentDispatchingAndroidInjector
    }
}