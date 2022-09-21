package com.tracki.ui.useraddresslist


import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.EmpData
import com.tracki.data.model.response.config.ProfileInfo

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityUserAddressListBinding

import com.tracki.ui.base.BaseActivity

import com.tracki.ui.userdetails.UserAddressListFragment

import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class UserAddressListActivity : BaseActivity<ActivityUserAddressListBinding, UserAddressListActivityViewModel>(), UserAddressListActivityNavigator, HasSupportFragmentInjector {
    private var empData: EmpData?=null
    private var userId: String? = null

    @Inject
    lateinit var mUserAddressViewModel: UserAddressListActivityViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>


    lateinit var binding: ActivityUserAddressListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mUserAddressViewModel.navigator = this
        setToolbar(binding.toolbar, "Address Details")
        if (intent.hasExtra("empData")) {
             empData= intent.getParcelableExtra("empData")
            userId = empData!!.userId
            if(empData!=null){
                var userData= ProfileInfo()
                userData.userId=empData!!.userId
                userData.profileImg=empData!!.userImg
                userData.name=empData!!.name
                userData.mobile=empData!!.mobile
                userData.email=empData!!.email
                addFragmentInContainer(UserAddressListFragment.newInstance(userData))
            }

        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_address_list
    }

    override fun getViewModel(): UserAddressListActivityViewModel {
        return mUserAddressViewModel
    }

    private fun addFragmentInContainer(fragment: Fragment) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.disallowAddToBackStack()
        ft.replace(R.id.container, fragment)
        ft.commit()
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
       return fragmentDispatchingAndroidInjector
    }


}