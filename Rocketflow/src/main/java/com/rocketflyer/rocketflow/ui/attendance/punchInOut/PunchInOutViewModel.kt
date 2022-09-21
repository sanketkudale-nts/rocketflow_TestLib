package com.rocketflyer.rocketflow.ui.attendance.punchInOut


import android.os.Handler
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.Location
import com.tracki.data.model.request.PunchInOut
import com.tracki.data.model.response.config.Api
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil
import com.tracki.utils.rx.SchedulerProvider
import java.io.File


class PunchInOutViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<PunchInOutNavigator>(dataManager, schedulerProvider) {

    private var httpManager: HttpManager? = null
    private var event: PunchInOut? = null
    private var api: Api? = null
    var txtGreeting = ObservableField<String>("")
    var txtPunchedIn = ObservableField<String>("")
    var txtTotalTime = ObservableField<String>("")
    var txtDate = ObservableField<String>("")
    var buttonText = ObservableField<String>("Punch In")
    var txtDay = ObservableField<String>("")
    var isPunchedIn = ObservableBoolean(false)
    var time  = 0L
    var btnNotVisibile = ObservableBoolean(false)

    fun setUserDetails(preferences: PreferencesHelper) {
        txtGreeting.set("Hi ${preferences.userDetail.name} !")


        isPunchedIn.set(preferences.punchStatus)

        if (isPunchedIn.get()) {
            txtPunchedIn.set("You have Punched In at: " + DateTimeUtil.getParsedTime(preferences.punchInTime))
            buttonText.set("Punch Out")

          //  txtTotalTime.set("Total: " + 0 + " hrs " + 0 + " min")


            var customHandler: Handler = Handler()


            val updateTimerThread = object : Runnable {
                override fun run() {
                    time = System.currentTimeMillis() - preferences.punchInTime;
                    var hours = time / 1000 / 60 / 60

                    var min = ((time / 1000 - (hours * 60 * 60))
                            / 60)
                    txtTotalTime.set("Total: " + hours + " hrs " + min + " min")

//                    if (hours<8)
//                    {
//                        btnNotVisibile.set(true)
//                    }
//
//                    else
//                        btnNotVisibile.set(false)

//                    if (hours>=8) {
//                        if (navigator != null)
//                            navigator.autoPunchOut()
//                    }

                    customHandler.postDelayed(
                            this
                            , 60000)



                }
            }

            customHandler.post(
                    updateTimerThread
            )


        } else {
            buttonText.set("Punch In")
            txtPunchedIn.set("You have not Punched In today")
        }

        txtDate.set(DateTimeUtil.getParsedDate(System.currentTimeMillis()))
        txtDay.set("${DateTimeUtil.getCurrentDay()}, ${DateTimeUtil.getParsedTime(System.currentTimeMillis())}")
    }


    fun onClickPunchButton(view: View) {
        CommonUtils.preventTwoClick(view)
        if (isPunchedIn.get()) {
            navigator.onClickPunchInOut("Punching Out!", PunchInOut.PUNCH_OUT)
        } else {
            navigator.onClickPunchInOut("Punching In!", PunchInOut.PUNCH_IN)
        }
    }



    fun uploadSelfie(httpManager: HttpManager, images: HashMap<String, ArrayList<File>>) {
        this.httpManager = httpManager
        UploadImage(images).hitApi()
    }

    inner class UploadImage(var imageList: Any) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleImageResponse(this, result, error)
        }

        override fun hitApi() {
            val apiUrl = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
            dataManager.uploadFiles(this, httpManager, imageList, apiUrl)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }
    fun punch(httpManager: HttpManager, data: Any?, event: PunchInOut) {
        this.httpManager = httpManager
        this.event = event
        Punch(data).hitApi()
    }
    inner class Punch(var date: Any?) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.handlePunchInOutResponse(this, result, error, event)
            }
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.PUNCH)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.PUNCH]
                if(dataManager!=null)
                dataManager.punch(this@Punch, httpManager, date, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }


    fun getPunchInPunchOutData(httpManager: HttpManager) {
        this.httpManager = httpManager
        PunchInPunchOutData().hitApi()
    }
    fun validatePunch(httpManager: HttpManager, title:String,  punchInOut:PunchInOut) {
        this.httpManager = httpManager
        ValidatePunch(title,punchInOut).hitApi()
    }
    inner class PunchInPunchOutData() : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_LAST_PUNCH)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.GET_LAST_PUNCH]
                if(dataManager!=null)
                dataManager.punchInPunchOutData(this@PunchInPunchOutData, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}

    }
    inner class ValidatePunch(var title:String,  var punchInOut:PunchInOut) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.validatePunchIn(this, result, error,title,punchInOut)
            }
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_LAST_PUNCH)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.GET_LAST_PUNCH]
                if(dataManager!=null)
                dataManager.punchInPunchOutData(this@ValidatePunch, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {}

    }

    fun validateGeoPunchIn(httpManager: HttpManager, title:String,  punchInOut:PunchInOut,location: Location,from:Int) {
        this.httpManager = httpManager
        ValidateGeoPunch(title,punchInOut,location,from).hitApi()
    }
    inner class ValidateGeoPunch(var title:String,  var punchInOut:PunchInOut,var location: Location,var from: Int) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                navigator.validateGeoPunchIn(this, result, error,title,punchInOut,from)
            }
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.USER_ATTENDANCE_LOCATION)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.USER_ATTENDANCE_LOCATION]
                if(dataManager!=null)
                    dataManager.validateGeoPunchIn(this@ValidateGeoPunch, httpManager,location, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if(navigator!=null) {
                navigator.showTimeOutMessage(callBack)
            }
        }

        override fun onLogout() {}

    }
}




