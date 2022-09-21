package com.rocketflyer.rocketflow.data.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tracki.data.DataManager
import com.tracki.data.local.prefs.AppPreferencesHelper
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.NotificationEventStatus
import com.tracki.data.model.NotificationModel
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil
import com.tracki.utils.JSONConverter
import org.json.JSONObject
import javax.inject.Inject


/**
 * Class used to handle firebase messeges.
 */
class TrackiFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        val TAG: String? = TrackiFirebaseMessagingService::class.java.simpleName
        const val NOTI_PRIMARY1 = 1100
        const val NOTI_SECONDARY1 = 1200
    }

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var dataManager: DataManager


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        preferencesHelper = AppPreferencesHelper(applicationContext, AppConstants.PREF_NAME)
        try {


            if (remoteMessage.data.isNotEmpty()) {
                var jsonString = Gson().toJson(remoteMessage.data)
                Log.e(TAG, jsonString)
                var strMessage = remoteMessage.data["body"].toString()

                // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
                //{"timeStamp":1597935720000,"event":"PUNCH_OUT"}
                //{"body":"{\"timeStamp\":1597935720000,\"event\":\"PUNCH_OUT\"}","title":"punch out","content":"punch out"}
                Log.e(TAG, "data")
                if (remoteMessage.data.containsKey("navigation")) {
                    var title = remoteMessage.data["title"].toString()
                    var notification:String=remoteMessage.data.get("navigation")!!
                    var jsonConverter = JSONConverter<FeedsJson>()
                    val feedsJson = jsonConverter.jsonToObject(notification,FeedsJson::class.java)
                    if (feedsJson?.data != null&&feedsJson.screen!=null&&feedsJson.screen.equals("FEED_INFO") &&feedsJson.data!!.id!=null){
                        showFeedsNotification(title, strMessage,feedsJson.data!!.id)
                    }else{
                        showNotification(title, strMessage)

                    }


                } else  if (remoteMessage.data.containsKey("data")){
                    var jsonObjectBody = JSONObject(remoteMessage.data["data"])
                    Log.e(TAG, jsonObjectBody.toString())
                    handleNotification(
                        remoteMessage.data["title"].toString(),
                        jsonObjectBody.toString(),
                        strMessage
                    )
                }else {
                    var strMessage = remoteMessage.data["body"].toString()
                    var title = remoteMessage.data["title"].toString()
                    showNotification(title, strMessage)

                }

            }
           /* if (remoteMessage.notification != null) {
                Log.e(TAG, "notification")
                // Check if message contains a notification payload.
                Log.e(
                    TAG,
                    "MessageResponse Notification Body: " + remoteMessage.notification!!.title!!
                )
                Log.e(TAG, "MessageResponse Notification TAG: " + remoteMessage.notification?.tag)
                var jsonObjectBody = JSONObject(remoteMessage.notification?.tag.toString())
                Log.e(TAG, jsonObjectBody.toString())
                handleNotification(
                    remoteMessage.notification?.title.toString(),
                    jsonObjectBody.toString(),
                    ""
                )
            }*/
            // Check if message contains a data payload.

        } catch (e: Exception) {
            var strMessage = remoteMessage.data["body"].toString()
            var title = remoteMessage.data["title"].toString()
            showNotification(title, strMessage)
            Log.e(TAG, "Exception occur inside onMessageReceived(): $e")
        }
    }

    /**
     * Method used to handle the notification for data part and notification part.
     *
     * @param title title of the notification
     * @param body body of the notification
     */
    private fun handleNotification(title: String, body: String, strMessage: String) {
        /**
         * {
        "body": "{\"trackingAction\":\"END\",\"tripId\":\"ccc0affc-5887-4d5c-817b-546152cf19f4\"}",
        "title": "TRACKING_STATE_CHANGE",
        "content": "TRACKING_STATE_CHANGE"
        }
         */
        try {
            val notificationModel = Gson().fromJson(body, NotificationModel::class.java)
            if (notificationModel != null) {

                var username = "";
                if (preferencesHelper.userDetail != null && preferencesHelper.userDetail!!.name != null) {
                    username = preferencesHelper.userDetail!!.name!!
                }
                if (notificationModel.event == NotificationEventStatus.PUNCH_OUT) {
                    var message = "${username} you are punch out from admin side at ${
                        DateTimeUtil.getParsedTime(notificationModel.timeStamp)
                    } at ${DateTimeUtil.getParsedDate(notificationModel.timeStamp)}"
                    //   showNotification(title, message)
                    CommonUtils.handleForceActions(
                        notificationModel,
                        applicationContext,
                        preferencesHelper
                    )
                } else if (notificationModel.event == NotificationEventStatus.PUNCH_IN) {
                    var message = "${username} you are punch in from admin side on ${
                        DateTimeUtil.getParsedTime(notificationModel.timeStamp)
                    } at ${DateTimeUtil.getParsedDate(notificationModel.timeStamp)}"
                    // showNotification(title, message)
                    CommonUtils.handleForceActions(
                        notificationModel,
                        applicationContext,
                        preferencesHelper
                    )
                } else if (notificationModel.event == NotificationEventStatus.TRACKING_STATE_CHANGE) {
                    CommonUtils.handleForceActions(
                        notificationModel,
                        applicationContext,
                        preferencesHelper
                    )

                } else if (notificationModel.event == NotificationEventStatus.ARRIVED) {
                    CommonUtils.handleForceActions(
                        notificationModel,
                        applicationContext,
                        preferencesHelper
                    )
                } else if (notificationModel.event == NotificationEventStatus.COMPLETED) {
                    CommonUtils.handleForceActions(
                        notificationModel,
                        applicationContext,
                        preferencesHelper
                    )

                } else {
                    showNotification(title, strMessage)
                }
            }
        } catch (je: JsonSyntaxException) {
            Log.e(TAG, "Exception Handled inside handleNotification: ${je.message}")
            showNotification(title, strMessage)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Exception Handled inside handleNotification: $e")
            // if we are getting exception while converting the model then it is for
            Log.e(TAG, "Exception Handled inside handleNotification: ${e.message}")
            // simple string.
            showNotification(title, strMessage)
        }
    }

    private fun showNotification(title: String, body: String) {
        Log.d(TAG, "MessageResponse data payload: $body")
        val helper = NotificationHelper(applicationContext)
//        var feedid="a0ab4c2d-8c5b-4786-a827-43b2ca1ec6b5"
        helper.notify(NOTI_PRIMARY1, helper.getNotification1(title, body, 0, null))
    }

    private fun showFeedsNotification(title: String, body: String, feedId: String?) {
        Log.d(TAG, "MessageResponse data payload: $body")
        val helper = NotificationHelper(applicationContext)
        helper.notify(NOTI_PRIMARY1, helper.getNotification1(title, body, 2, feedId))
    }


}
