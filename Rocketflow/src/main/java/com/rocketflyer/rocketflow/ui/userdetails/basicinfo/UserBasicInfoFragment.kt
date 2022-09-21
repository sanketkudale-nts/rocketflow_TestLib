package com.rocketflyer.rocketflow.ui.userdetails.basicinfo

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.ProfileInfo
import com.tracki.data.model.response.config.UpdateResponse
import com.tracki.data.model.response.config.UserData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.LayoutFragmentUserBasicInfoBinding
import com.tracki.ui.addcustomer.AddUserNavigator
import com.tracki.ui.addcustomer.AddUserViewModel
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.userdetails.UserAddressListFragment

import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import javax.inject.Inject

class UserBasicInfoFragment : BaseFragment<LayoutFragmentUserBasicInfoBinding, AddUserViewModel>(), AddUserNavigator {
    private var userId: String? = null
    private var roleId: String? = null
    lateinit var mAddUserViewModel: AddUserViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper


    @Inject
    lateinit var httpManager: HttpManager


    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: LayoutFragmentUserBasicInfoBinding
    private var userData: UserData? = null
    companion object{
        fun newInstance(userData: UserData?): UserBasicInfoFragment {
            val args = Bundle()
            args.putParcelable("userData", userData)
            val fragment = UserBasicInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_fragment_user_basic_info
    }

    override fun getViewModel(): AddUserViewModel {
        mAddUserViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AddUserViewModel::class.java)
        return mAddUserViewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding = viewDataBinding

        mAddUserViewModel.navigator = this
        if (arguments != null) {
            userData = requireArguments().getParcelable<UserData>("userData")
            if (userData != null) {

                if (userData != null && userData!!.roleId != null && userData!!.roleId!!.isNotEmpty()) {
                    roleId = userData!!.roleId!!
                    if (userData!!.userId != null && userData!!.userId!!.isNotEmpty()) {
                        userId = userData!!.userId!!
                        showLoading()
                        mAddUserViewModel.getUserDetail(httpManager, userData!!)
                    }
                }
            }
            binding.viewModel = mAddUserViewModel
            if (roleId != null && baseActivity != null)
                binding.viewModel!!.performShowHideView(baseActivity!!, preferencesHelper, roleId!!)


        }


    }

    override fun handleAddUserResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handleUserDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
            val jsonConverter: JSONConverter<UpdateResponse> = JSONConverter()
            var response: UpdateResponse = jsonConverter.jsonToObject(result.toString(), UpdateResponse::class.java) as UpdateResponse
            if (response.data != null) {
                var data = response.data
                if (data!!.firstName == null || data.firstName!!.isEmpty()) {
                    binding.llFirstName.visibility = View.GONE
                }
                if (data.middleName == null || data.middleName!!.isEmpty()) {
                    binding.llMiddleName.visibility = View.GONE
                }
                if (data.lastName == null || data.lastName!!.isEmpty()) {
                    binding.llLastName.visibility = View.GONE
                }
                if (data.mobile == null || data.mobile!!.isEmpty()) {
                    binding.llMobile.visibility = View.GONE
                }
                if (data.email == null || data.email!!.isEmpty()) {
                    binding.llEmail.visibility = View.GONE
                }
                if (data.fatherName == null || data.fatherName!!.isEmpty()) {
                    binding.llFather.visibility = View.GONE
                }
                if (data.motherName == null || data.motherName!!.isEmpty()) {
                    binding.llMother.visibility = View.GONE
                }
                if (data.dateOfBirth == null || data.dateOfBirth!!.isEmpty()) {
                    binding.llDob.visibility = View.GONE
                }
                if (data.dateOfJoining == null || data.dateOfJoining!!.isEmpty()) {
                    binding.llDoj.visibility = View.GONE
                }
                if (data.personId == null || data.personId!!.isEmpty()) {
                    binding.llEmployeeId.visibility = View.GONE
                }
                binding.request = response.data
            }

        } else {
        }
    }


    override fun handleProfilePicResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?) {
    }

    override fun handleDeleteResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun openPlaceAutoComplete(view: View) {
    }

    override fun selectDateTime(view: View) {
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}