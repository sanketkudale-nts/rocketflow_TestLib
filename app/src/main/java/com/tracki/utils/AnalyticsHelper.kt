package com.tracki.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.tracki.TrackiApplication
import com.tracki.data.model.BaseResponse
import com.tracki.data.model.request.AnalyticRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.utils.AppConstants.Analytics.Events.*


/**
 * Class used to record screen clicks and events when
 * user open any screen on app.
 *
 * Created by rahul on 16/4/19
 */
class AnalyticsHelper(val context: Context) : ApiCallback {

    private var application: TrackiApplication = context.applicationContext as TrackiApplication
    private lateinit var analyticRequest: AnalyticRequest
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    /**
     * Method used to add the event name and page name and
     * create a map with required keys and params.
     *
     * @param eventName name of the events
     * @param pageName name of the page
     */
    fun addEvent(eventName: String, pageName: String) {
        val objectHashMap = HashMap<String, Any>()
        when (eventName) {
            EVENT_INTRO_SKIP_CLICKED,
            EVENT_INTRO_NEXT_CLICKED,
            EVENT_INTRO_START_CLICKED,
            EVENT_LOGIN_CLICKED,
            EVENT_SEND_OTP_CLICKED,
            EVENT_RESEND_OTP_CLICKED,
            EVENT_REGISTER_CLICKED,
            EVENT_HOME_DASHBOARD_CLICKED,
            EVENT_HOME_NOTIFICATION_CLICKED,
            EVENT_NOTIFICATION_CLEAR_CLICKED,
            EVENT_HOME_BUDDY_REQUEST_CLICKED,
            EVENT_ACCEPT_BUDDY_REQUEST_CLICKED,
            EVENT_REJECT_BUDDY_REQUEST_CLICKED,
            EVENT_HOME_BUDDY_FILTER_CLICKED,
            EVENT_HOME_MENU_CLICKED,
            EVENT_HOME_CREATE_TASK_CLICKED,
            EVENT_MENU_ITEM_MYBUDDY_CLICKED,
            EVENT_MENU_ITEM_FLEET_CLICKED,
            EVENT_MENU_ITEM_TASK_CLICKED,
            EVENT_MENU_ITEM_SETTINGS_CLICKED,
            EVENT_MENU_ITEM_LOGOUT_CLICKED,
            EVENT_MENU_ITEM_OTHERS_CLICKED,
            EVENT_MENU_SUB_ITEM_PRIVACY_POLICY_CLICKED,
            EVENT_MENU_SUB_ITEM_TNC_CLICKED,
            EVENT_MENU_MY_ACCOUNT_CLICKED,
            EVENT_MY_PROFILE_CLICKED,
            EVENT_TRACKING_BUDDY_CLICKED,
            EVENT_I_AM_TRACKING_CLICKED,
            EVENT_TRACKING_ME_CLICKED,
            EVENT_CHANGE_PASSWORD_CLICKED,
            EVENT_CHANGE_PASSWORD_SUBMIT_CLICKED,
            EVENT_ADD_BUDDY_CLICKED,
            EVENT_SEND_INVITATION_CLICKED,
            EVENT_ADD_FLEET_CLICKED,
            EVENT_ADD_FLEET_SUBMIT_CLICKED,
            EVENT_TASK_ASSIGNED_TO_ME_CLICKED,
            EVENT_TASK_I_HAVE_ASSIGNED_CLICKED,
            EVENT_TASK_STATUS_ALL_CLICKED,
            EVENT_TASK_STATUS_PENDING_CLICKED,
            EVENT_TASK_STATUS_SCHEDULED_CLICKED,
            EVENT_TASK_STATUS_LIVE_CLICKED,
            EVENT_TASK_STATUS_CANCELLED_CLICKED,
            EVENT_TASK_STATUS_COMPLETED_CLICKED,
            EVENT_TASK_START_NOW_CLICKED,
            EVENT_TASK_CANCEL_CLICKED,
            EVENT_TASK_ACCEPT_CLICKED,
            EVENT_TASK_REJECT_CLICKED,
            EVENT_TASK_END_CLICKED,
            EVENT_TASK_EDIT_CLICKED,
            EVENT_TASK_CREATE_CLICKED,
            EVENT_TASK_ASSIGN_NOW_CLICKED,
            EVENT_TASK_DETAIL_SHARE_CLICKED,
            EVENT_ALERT_NOTIFICATION_SWITCH_CLICKED,
            EVENT_AUTO_START_TRIP_SWITCH_CLICKED,
            EVENT_EMERGENCY_CONTACT_CLICKED,
            EVENT_ADD_EMERGENCY_CONTACT_CLICKED,
            EVENT_CHOOSE_CONTACT_CLICKED,
            EVENT_EDIT_CONTACT_CLICKED,
            EVENT_DELETE_CONTACT_CLICKED,
            EVENT_TEXT_MESSAGE_CLICKED,
            EVENT_LOGOUT_CLICKED -> {
                objectHashMap["platform"] = "ANDROID"
            }
            else -> {
            }
        }

        val params = Bundle()
        params.putString(AppConstants.Analytics.EXTRA_EVENT_NAME, eventName)
        params.putString(AppConstants.Analytics.EXTRA_PAGE_NAME, pageName)
        firebaseAnalytics.logEvent(AppConstants.Analytics.EXTRA_EVENT, params)
    }

    /**
     * Method indicates that we only track when
     * user is coming to this page
     *
     * @param pageName name of the page user visiting
     */
    fun addEvent(pageName: String) {
        firebaseAnalytics.logEvent(pageName, null)
    }

    override fun onResponse(result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(this, error, result, context)) {
            val baseResponse: BaseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
        }
    }

    override fun hitApi() {
    }

    override fun isAvailable() = true

    override fun onNetworkErrorClose() {
    }

    override fun onRequestTimeOut(callBack: ApiCallback) {
    }

    override fun onLogout() {
    }
}