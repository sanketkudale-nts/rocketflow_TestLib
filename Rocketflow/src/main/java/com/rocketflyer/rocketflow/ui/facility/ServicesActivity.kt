package com.rocketflyer.rocketflow.ui.facility

import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.UpdateServiceRequest
import com.tracki.data.model.response.config.ConfigResponse
import com.tracki.data.model.response.config.GetSavedServicesResponse
import com.tracki.data.model.response.config.InitiateSignUpResponse
import com.tracki.data.model.response.config.Service
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityServicesBinding
import com.tracki.ui.attendance.punchInOut.PunchInPunchOutData
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.*
import com.trackthat.lib.TrackThat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ServicesActivity : BaseActivity<ActivityServicesBinding, ServiceViewModel>(), ServiceNavigator, View.OnClickListener {

    private var from: String? = null


    @Inject
    lateinit var mServiceViewModel: ServiceViewModel

    @Inject
    lateinit var mServicesAdapter: ServicesAdapter

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    lateinit var binding: ActivityServicesBinding

    private var listServices: ArrayList<Service>? = null

    private var draftId: String? = null
    private var merchantId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mServiceViewModel.navigator = this
        if (intent != null && intent.hasExtra("from")) {
            from = intent.getStringExtra("from")
            if (from.equals("direct")) {
                if (intent != null && intent.hasExtra(AppConstants.TITLE)) {
                    var titile = intent.getStringExtra(AppConstants.TITLE)
                    setToolbar(binding.toolbar, titile)
                }

                if (intent != null && intent.hasExtra(AppConstants.DRAFT_ID)) {
                    draftId = intent.getStringExtra(AppConstants.DRAFT_ID)
                }
                if (intent != null && intent.hasExtra(AppConstants.MERCHNAT_ID)) {
                    merchantId = intent.getStringExtra(AppConstants.MERCHNAT_ID)
                }
                if (intent != null && intent.hasExtra("service")) {
                    listServices = intent.getParcelableArrayListExtra("service")
                    if (listServices != null) {
                        mServicesAdapter.addData(listServices as java.util.ArrayList<Service>)
                        binding.adapter = mServicesAdapter
                    }
                }
            } else {
                setToolbar(binding.toolbar, getString(R.string.update_service))
                binding.btnNext.text = "Update"
                var api = TrackiApplication.getApiMap()[ApiType.GET_SERVICE_PREF]
                if (api != null) {
                    showLoading()
                    mServiceViewModel.getServices(httpManager, api)
                }
            }
        }

        binding.btnNext.setOnClickListener(this)

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_services
    }

    override fun getViewModel(): ServiceViewModel {
        return mServiceViewModel

    }

    override fun handleUpdateServiceResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            var jsonConverter = JSONConverter<InitiateSignUpResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), InitiateSignUpResponse::class.java)
            if (response != null) {
                if (response.loginToken != null && response.loginToken!!.isNotEmpty()) {
                    preferencesHelper.loginToken = response.loginToken
                }
                if (response.accessId != null && response.accessId!!.isNotEmpty()) {
                    preferencesHelper.accessId = response.accessId
                }

            }
            if (response?.nextScreen != null) {
                if (response.refreshConfig) {
                    showLoading()
                    mServiceViewModel.getConfig(httpManager, response.nextScreen, response.sdkAccessId)
                } else {

                    if (response.sdkAccessId != null && response.sdkAccessId!!.isNotEmpty()) {
                        TrackThat.setAccessId(response.sdkAccessId)
                    }
                    CommonUtils.otpgoToNext(this, response.nextScreen, "")
                }

            }
        }

    }

    override fun handleConfigResponse(callback: ApiCallback, result: Any?, error: APIError?, nextScreen: NextScreen?, sdkAccessId: String?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            if (sdkAccessId != null) {
                // send accessId to SDK
                TrackThat.setAccessId(sdkAccessId)
            }
            if (result != null) {
                val gson = Gson()
                val configResponse = gson.fromJson(result.toString(), ConfigResponse::class.java)
                CommonUtils.saveConfigDetails(this, configResponse, preferencesHelper, "2", "")
                if (Objects.requireNonNull(configResponse.appConfig)!!.idleTrackingInfo != null) {
                    if (configResponse.appConfig!!.idleTrackingInfo!!.mode != null && configResponse.appConfig!!.idleTrackingInfo!!.mode == "ON_PUNCH" && Objects.requireNonNull(configResponse.appConfig!!.idleTrackingInfo)!!.enableIdleTracking) {
                        preferencesHelper!!.isIdealTrackingEnable = configResponse.appConfig!!.idleTrackingInfo!!.enableIdleTracking
                        showLoading()
                        mServiceViewModel.getPunchInPunchOutData(httpManager, nextScreen)
                    } else {
                        CommonUtils.otpgoToNext(this, nextScreen, "")
                    }
                } else {
                    CommonUtils.otpgoToNext(this, nextScreen, "")
                }
            }
        }
    }

    override fun handlePunchInPunchOutResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?, nextScreen: NextScreen?) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            val jsonConverter: JSONConverter<PunchInPunchOutData> = JSONConverter<PunchInPunchOutData>()
            val punchInPunchOutData = jsonConverter.jsonToObject(result.toString(), PunchInPunchOutData::class.java) as PunchInPunchOutData
            if (punchInPunchOutData.data != null) {
                if (punchInPunchOutData.data!!.status != null && !punchInPunchOutData.data!!.status!!.isEmpty()) {
                    if (punchInPunchOutData.data!!.status == "PRESENT") {
                        if (preferencesHelper!!.isIdealTrackingEnable) {
                            if (punchInPunchOutData.data!!.punchOutAt != 0L) {
                                preferencesHelper!!.punchOutTime = punchInPunchOutData.data!!.punchOutAt
                                if (TrackThat.isTracking()) {
                                    preferencesHelper!!.idleTripActive = false
                                    TrackThat.stopTracking()
                                    preferencesHelper!!.punchId = null
                                }
                            } else {
                                if (punchInPunchOutData.data != null && punchInPunchOutData.data!!.punchInAt != 0L) {
                                    preferencesHelper!!.punchStatus = true
                                    if (!preferencesHelper!!.idleTripActive) {
                                        preferencesHelper!!.punchId = punchInPunchOutData.data!!.punchId
                                        if (!TrackThat.isTracking()) {
                                            preferencesHelper!!.idleTripActive = true
                                            TrackThat.startTracking(punchInPunchOutData.data!!.punchId, false)
                                        }
                                    }
                                    preferencesHelper!!.punchInTime = punchInPunchOutData.data!!.punchInAt
                                    if (punchInPunchOutData.data!!.punchData != null && punchInPunchOutData.data!!.punchData!!.punchInData != null) {
                                        if (punchInPunchOutData.data!!.punchData!!.punchInData!!.selfie != null) {
                                            val imageUrl = punchInPunchOutData.data!!.punchData!!.punchInData!!.selfie
                                            preferencesHelper!!.selfieUrl = imageUrl
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //CommonUtils.updateSharedContentProvider(this, preferencesHelper)
        }
        CommonUtils.otpgoToNext(this, nextScreen, "")
    }

    override fun handleGetSavedServiceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            var jsonConverter = JSONConverter<GetSavedServicesResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), GetSavedServicesResponse::class.java)
            if (response?.services != null) {
                listServices = response?.services as ArrayList<Service>?
                mServicesAdapter.addData(listServices as java.util.ArrayList<Service>)
                binding.adapter = mServicesAdapter

            }
        }

    }

    override fun handleUpdateSavedServiceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            var jsonConverter = JSONConverter<InitiateSignUpResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), InitiateSignUpResponse::class.java)
            if (response.refreshConfig) {
                showLoading()
                mServiceViewModel.getConfig(httpManager, NextScreen.HOME, response.sdkAccessId)
            } else {

                if (response.sdkAccessId != null && response.sdkAccessId!!.isNotEmpty()) {
                    TrackThat.setAccessId(response.sdkAccessId)
                }
                CommonUtils.otpgoToNext(this, NextScreen.HOME, "")
            }
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnNext -> {
                var listSelected = mServicesAdapter.getList().filter { it -> it.selected }
                if (listSelected.isNullOrEmpty()) {
                    TrackiToast.Message.showShort(this, "Please select service ")
                } else {
                    var idsList = ArrayList<String>()
                    for (offering in listSelected) {
                        idsList.add(offering.id)
                    }
                    if (from.equals("direct")) {
                        var data = UpdateServiceRequest()
                        data.serviceIds = idsList
                        data.draftId = draftId
                        data.merchantId = merchantId
                        data.roleId = preferencesHelper.userRoleId

                        var api = TrackiApplication.getApiMap()[ApiType.SIGNUP_AND_UPDATE_PREFERENCE]
                        if (api != null) {
                            showLoading()
                            mServiceViewModel.updateService(httpManager, api, data)
                        }

                    } else {
                        var data = UpdateServiceRequest()
                        data.serviceIds = idsList


                        var api = TrackiApplication.getApiMap()[ApiType.UPDATE_SERVICE_PREF]
                        if (api != null) {
                            showLoading()
                            mServiceViewModel.updateSavedService(httpManager, api, data)
                        }

                    }

                }


            }
        }
    }
}