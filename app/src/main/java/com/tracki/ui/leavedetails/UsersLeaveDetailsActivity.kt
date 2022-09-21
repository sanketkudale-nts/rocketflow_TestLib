package com.tracki.ui.leavedetails

import android.os.Bundle
import com.tracki.BR
import com.tracki.R
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.databinding.ActivityUsersLeaveDetailsBinding
import com.tracki.ui.base.BaseActivity
import javax.inject.Inject

class UsersLeaveDetailsActivity : BaseActivity<ActivityUsersLeaveDetailsBinding, LeaveDetailsViewModel>(),LeaveDetailsNavigator {

    lateinit var mActivityUsersLeaveDetailsBinding: ActivityUsersLeaveDetailsBinding
    @Inject
    lateinit var mLeaveDetailsViewModel: LeaveDetailsViewModel
    private  var detailsData:LeaveDetailsData?=null
    private var adapter: LeaveDetailsAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityUsersLeaveDetailsBinding = viewDataBinding
        mLeaveDetailsViewModel.navigator = this
        setToolbar(mActivityUsersLeaveDetailsBinding.toolbar,"Total Leaves")
        if(intent.hasExtra("details")){
            detailsData= intent.getSerializableExtra("details") as LeaveDetailsData?
            if(detailsData!=null) {
                adapter=LeaveDetailsAdapter()
                adapter!!.setList(detailsData!!.summaryList)
                mActivityUsersLeaveDetailsBinding.tvTotalRemainIngCount.setText(detailsData!!.remainingLeave.toString() + "")
                mActivityUsersLeaveDetailsBinding.tvTotalTakenCount.setText(detailsData!!.takenLeave.toString() + "")
                mActivityUsersLeaveDetailsBinding.tvTotalLeavesCount.setText(detailsData!!.totalLeave.toString() + "")
                mActivityUsersLeaveDetailsBinding.adapter=adapter

            }
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_users_leave_details
    }

    override fun getViewModel(): LeaveDetailsViewModel {
        return mLeaveDetailsViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}