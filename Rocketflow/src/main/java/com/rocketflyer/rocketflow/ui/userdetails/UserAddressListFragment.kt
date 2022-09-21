package com.rocketflyer.rocketflow.ui.userdetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AddressInfo
import com.tracki.data.model.response.config.ProfileInfo
import com.tracki.data.model.response.config.UserAddressResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.LayoutFragmentUserAddressListBinding
import com.tracki.ui.adduserAddress.AddUserAddressActivity
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import java.util.ArrayList
import javax.inject.Inject

class UserAddressListFragment: BaseFragment<LayoutFragmentUserAddressListBinding, UserAddressViewModel>() , UserAddressNavigator, UserAddressAdapter.UserDeletePlaceListener, View.OnClickListener{
    private var empData: ProfileInfo?=null
    private var userId: String? = null
    private var CONST_ADDRESS_UPDATE: Int = 222


    lateinit var mUserAddressViewModel: UserAddressViewModel

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: UserAddressAdapter
    private var dataModel: AddressInfo? = null
    private var position: Int? = null

    private lateinit  var binding:LayoutFragmentUserAddressListBinding
    companion object{
        fun newInstance(userData: ProfileInfo?): UserAddressListFragment {
            val args = Bundle()
            args.putSerializable("empData", userData)
            val fragment = UserAddressListFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun getBindingVariable(): Int {
       return BR.viewModel
    }

    override fun getLayoutId(): Int {
       return R.layout.layout_fragment_user_address_list
    }

    override fun getViewModel(): UserAddressViewModel {
        mUserAddressViewModel = ViewModelProviders.of(this, mViewModelFactory).get(UserAddressViewModel::class.java)
        return mUserAddressViewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding = viewDataBinding
        mUserAddressViewModel.navigator = this
        binding.adapter = adapter
        if(arguments!=null) {
            if (requireArguments().getSerializable("empData")!=null) {
                empData = requireArguments().getSerializable("empData") as ProfileInfo?
                userId = empData!!.userId
                getUserAddressFromServer()
            }
        }

        adapter.setListener(this)
        binding.ivAddAddress.setOnClickListener(this)
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
            val jsonConverter: JSONConverter<UserAddressResponse> = JSONConverter()
            var response: UserAddressResponse = jsonConverter.jsonToObject(result.toString(), UserAddressResponse::class.java) as UserAddressResponse
            if (response.data != null) {
                adapter.addData(response.data as ArrayList<AddressInfo>)

            } else {

            }
        } else {

        }

    }

    override fun handleDeleteResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
            if (dataModel != null) {
                var adapterList = adapter.getList()
                if (adapterList.contains(dataModel!!)) {
                    var index = adapterList.indexOf(dataModel!!)
                    if (index != -1) {
                        adapterList.removeAt(index)
                        adapter.notifyItemRemoved(index)
                    }
                }
            }
        }
    }

    fun getUserAddressFromServer() {
        showLoading()
        mUserAddressViewModel.getUserAddress(httpManager, userId)
    }

    fun deleteAddress(placeId: String?) {
        showLoading()
        mUserAddressViewModel.deleteAddress(httpManager, placeId)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivAddAddress -> {
                var intent = Intent(baseActivity, AddUserAddressActivity::class.java)
                intent.putExtra("empData", empData)
                intent.putExtra("action", "add")
                startActivityForResult(intent,CONST_ADDRESS_UPDATE)
            }
        }
    }

    override fun delete(dataModel: AddressInfo, position: Int) {

        val dialog = DoubleButtonDialog(baseActivity,
                true,
                null,
                getString(R.string.delete_user_address),
                getString(R.string.yes),
                getString(R.string.no),
                object : OnClickListener {
                    override fun onClickCancel() {}
                    override fun onClick() {
                        if (dataModel.placeId != null && dataModel.placeId!!.isNotEmpty()) {
                            showLoading()
                            this@UserAddressListFragment.dataModel = dataModel
                            this@UserAddressListFragment.position = position
                            deleteAddress(dataModel.placeId)
                        }
                    }
                })
        dialog.show()


    }

    override fun edit(dataModel: AddressInfo, position: Int) {
        this.dataModel = dataModel
        this.position = position
        var intent = Intent(baseActivity!!, AddUserAddressActivity::class.java)
        intent.putExtra("empData", empData)
        intent.putExtra("action", "edit")
        intent.putExtra("dataModel", dataModel)
        startActivityForResult(intent,CONST_ADDRESS_UPDATE)

    }

    override fun view(dataModel: AddressInfo, position: Int) {
        /*  this.dataModel = dataModel
          this.position = position
          var intent = Intent(this, AddUserAddressActivity::class.java)
          intent.putExtra("empData", empData)
          intent.putExtra("action", "add")
          startActivity(intent)*/


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CONST_ADDRESS_UPDATE){
            if(resultCode== Activity.RESULT_OK){
                getUserAddressFromServer()
            }else{

            }
        }
    }



}