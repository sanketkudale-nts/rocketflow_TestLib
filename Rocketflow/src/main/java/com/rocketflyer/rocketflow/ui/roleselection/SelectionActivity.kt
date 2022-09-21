package com.rocketflyer.rocketflow.ui.roleselection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AccountListRequest
import com.tracki.data.model.response.config.SelectAccountListResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivitySelectionBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.tasklisting.ihaveassigned.TabDataClass
import com.tracki.utils.ApiType
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SelectionActivity : BaseActivity<ActivitySelectionBinding, SelectAccountViewModel>(), SelectAccountNavigator, HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var mSelectAccountViewModel: SelectAccountViewModel

    @Inject
    lateinit var mPagerAdapter: AccountPagerAdapter

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var mActivitySelectionBinding: ActivitySelectionBinding
    private var draftId: String? = null
    private val fragments: ArrayList<TabDataClass> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySelectionBinding = viewDataBinding
        setToolbar(mActivitySelectionBinding.toolbar,"Selection")
        mSelectAccountViewModel.navigator = this
        if (intent != null && intent.hasExtra(AppConstants.DRAFT_ID)) {
            draftId = intent.getStringExtra(AppConstants.DRAFT_ID)
        }
        if (::mSelectAccountViewModel.isInitialized) {
            var api = TrackiApplication.getApiMap()[ApiType.SIGNUP_ACCOUNTS]
            if (api != null) {
                var data = AccountListRequest(draftId!!)
                showLoading()
                mSelectAccountViewModel.getAccountList(httpManager, api, data)
            }

        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_selection
    }

    override fun getViewModel(): SelectAccountViewModel {
        return mSelectAccountViewModel
    }

    override fun handleSelectAccountResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        if (CommonUtils.handleResponse(callback, error, result, this)) {
            var jsonConverter = JSONConverter<SelectAccountListResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), SelectAccountListResponse::class.java)
            if (response != null && response.successful) {
                for (data in response.accountsAndOfferings) {
                    fragments.add(TabDataClass(TaskSelectionFragment.newInstance(response.profileDetail, data,draftId!!), data.accTitle))
                }
            }

            mPagerAdapter.setFragments(fragments)
            if (fragments.size > 1) {
                mActivitySelectionBinding.tabLayout.visibility = View.VISIBLE
                if (fragments.size > 2) {
                    mActivitySelectionBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)
                    mActivitySelectionBinding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE)
                } else {
                    mActivitySelectionBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)
                    mActivitySelectionBinding.tabLayout.setTabMode(TabLayout.MODE_FIXED)
                }
            } else {
                mActivitySelectionBinding.tabLayout.visibility = View.GONE
            }
            // tabLayout.getTabAt(0).select();
            // tabLayout.getTabAt(0).select();
            mActivitySelectionBinding.viewPager.setAdapter(mPagerAdapter)

            mActivitySelectionBinding.tabLayout.setupWithViewPager(mActivitySelectionBinding.viewPager)

            mActivitySelectionBinding.viewPager.setOffscreenPageLimit(mActivitySelectionBinding.tabLayout.getTabCount())
            mActivitySelectionBinding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(mActivitySelectionBinding.tabLayout))
            mActivitySelectionBinding.viewPager.setCurrentItem(0, true)
            hideLoading()
        }else{
            hideLoading()
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }
}