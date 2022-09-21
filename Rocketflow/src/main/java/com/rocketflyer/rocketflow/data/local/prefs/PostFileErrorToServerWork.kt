package com.rocketflyer.rocketflow.data.local.prefs

import android.content.Context
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tracki.BuildConfig
import com.tracki.data.AppDataManager
import com.tracki.data.DataManager
import com.tracki.data.model.response.config.ApiErrorRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.data.network.NetworkManagerImpl
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.rx.AppSchedulerProvider
import dagger.android.AndroidInjection

import java.util.HashMap
import javax.inject.Inject

class PostFileErrorToServerWork(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams),
    PostErrorToServerNavigator {



    lateinit var postErrorViewModel: PostFileErrorToServerViewModel


    lateinit var httpManager: HttpManager

    lateinit var preferencesHelper: PreferencesHelper
    lateinit var dataManager: DataManager
    override fun doWork(): Result {
        preferencesHelper= AppPreferencesHelper(context, AppConstants.PREF_NAME)

        dataManager= AppDataManager(context,null,preferencesHelper,NetworkManagerImpl())
        httpManager = HttpManager.getInstance(context, preferencesHelper)
        postErrorViewModel= PostFileErrorToServerViewModel(dataManager, AppSchedulerProvider())

        val request1 = ApiErrorRequest()
        //request1.appVersion = BuildConfig.VERSION_NAME
        try {
            if(inputData.getString("error")!=null)
            {
                val error = inputData.getString("error")
                request1.error = error
            }

        }catch (e:Exception){

        }

        val deviceMeta = HashMap<String, String>()
        try {
            deviceMeta["MANUFACTURER"] = Build.MANUFACTURER
            deviceMeta["MODEL"] = Build.MODEL
            deviceMeta["BOARD"] = Build.BOARD
            deviceMeta["HARDWARE"] = Build.HARDWARE
            deviceMeta["API_LEVEL"] = Build.VERSION.SDK_INT.toString() + ""
            deviceMeta["BUILD_ID"] = Build.ID
            val code = inputData.getInt("code",0)
            deviceMeta["ERROR_CODE"] = code.toString();
            deviceMeta["BUILD_TIME"] = Build.TIME.toString() + ""
            deviceMeta["Version"] = Build.VERSION.RELEASE + ""
            request1.deviceMeta = deviceMeta
        }catch (e:Exception){

        }


        postErrorViewModel.navigator = this
        postErrorViewModel.postFileErrorToServer(httpManager, request1)
        return Result.success()
    }

    override fun showTimeOutMessage(callback: ApiCallback) {
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handlePostErrorResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
        if (CommonUtils.handleResponse(apiCallback, error, result, context)) {

        }
    }


}