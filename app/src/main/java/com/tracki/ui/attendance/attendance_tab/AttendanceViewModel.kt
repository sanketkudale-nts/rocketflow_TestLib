package com.tracki.ui.attendance.attendance_tab


import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.tracki.TrackiApplication
import com.tracki.data.DataManager
import com.tracki.data.model.request.PunchInOut
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.ApiType
import com.tracki.utils.rx.SchedulerProvider

open class AttendanceViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<AttendanceNavigator>(dataManager, schedulerProvider) {

    private var httpManager: HttpManager? = null
    var isDataVisible = ObservableField(false)
    var isMainDataVisible = ObservableField(false)

    public open var isPunchedIn = ObservableBoolean(false)
    public open var isPunchedInVisible= ObservableBoolean(false)
    public open var buttonText = ObservableField("")
    fun onClickSearch() {
       // navigator.onClickSearch()
    }

    fun getAttendance(httpManager: HttpManager, data: Any) {
        this.httpManager = httpManager
        isDataVisible.set(false)
        Attendance(data).hitApi()
    }

    inner class Attendance(var data: Any) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                isDataVisible.set(true)
                navigator.handleAttendanceResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.USER_ATTENDANCE_BREAKUP)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.USER_ATTENDANCE_BREAKUP]
                if(dataManager!=null)
                dataManager.getAttendance(this, httpManager, data, apiUrl)
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

    fun todayAttendance(httpManager: HttpManager, data: Any) {
        this.httpManager = httpManager
        isMainDataVisible.set(false)
        ToDayAttendance(data).hitApi()
    }

    inner class ToDayAttendance(var data: Any) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                isMainDataVisible.set(true)
                navigator.handleTodayAttendanceResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.GET_ATTENDANCE)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.GET_ATTENDANCE]
                if(dataManager!=null)
                dataManager.getAttendance(this, httpManager, data, apiUrl)
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

    fun getAttendanceCount(httpManager: HttpManager, data: Any) {
        this.httpManager = httpManager
        AttendanceCountMap(data).hitApi()
    }

    inner class AttendanceCountMap(var data: Any) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null) {
                isMainDataVisible.set(true)
                navigator.handleAttendanceMapResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(TrackiApplication.getApiMap().containsKey(ApiType.USER_ATTENDANCE_MAP)) {
                val apiUrl = TrackiApplication.getApiMap()[ApiType.USER_ATTENDANCE_MAP]
                if(dataManager!=null)
                dataManager.getAttendance(this, httpManager, data, apiUrl)
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

    fun punch(httpManager: HttpManager, data: Any?, event: PunchInOut) {
        this.httpManager = httpManager
        Punch(data,event).hitApi()
    }
    inner class Punch(var date: Any?, var event: PunchInOut) : ApiCallback {
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


}