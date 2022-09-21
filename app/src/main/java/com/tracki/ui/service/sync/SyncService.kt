package com.tracki.ui.service.sync

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.tracki.BuildConfig
import com.tracki.data.DataManager
import com.tracki.data.local.db.Action
import com.tracki.data.local.db.ApiEventModel
import com.tracki.data.local.db.DatabaseHelper
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.*
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.HttpManager
import com.tracki.data.network.SyncCallback
import com.tracki.ui.custom.ExecutorThread
import com.tracki.utils.*
import com.trackthat.lib.TrackThat
import com.trackthat.lib.models.BaseResponse
import dagger.android.AndroidInjection
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Class used to sync the data with the server once internet is available.
 *<p>
 * Created by rahul on 10/4/19
 */
class SyncService : IntentService("SyncService"), Runnable {
    private var handlerThread: ExecutorThread? = null
    private var databaseHelper: DatabaseHelper? = null
    private val TAG = "SyncService"
    private var mHandler: Handler? = null
    private var api: Api? = null
    private var formDataList: ArrayList<FormData>? = null
    private var delayed = 20000L
    private var taskId: String? = null
    private var isHandlerRunning = false
    private var stopService: Boolean? = null
    private var mServiceLooper: Looper? = null
    private var mServiceHandler: ServiceHandler? = null
    private var uploadTask: ApiEventModel? = null
    private var formId:String?=null
    private var ctaId:String?=null
    var context: Context = this
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                /*For Success */0 -> {
                if (CommonUtils.stringListHashMap.isNotEmpty()) {

                    //get hashMap from adapter and match the name with key of maps
                    // if found then replace entered value with url of image
                    if (formDataList?.isNotEmpty()!!) {
                        val finalMap = HashMap<String, String>()
                        for (i in formDataList?.indices!!) {
                            try {
                                if (formDataList!![i].type != DataType.BUTTON) {
                                    if (CommonUtils.stringListHashMap?.containsKey(formDataList!![i].name)!!) {
                                        //Log.e("Upload Form List--->", mainData!![i].name!!)
                                        formDataList!![i].enteredValue = CommonUtils.getCommaSeparatedList(CommonUtils.stringListHashMap[formDataList!![i].name])
                                    }
                                    finalMap[formDataList!![i].name!!] = formDataList!![i].enteredValue!!
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        //assign empty object to map again
                        CommonUtils.stringListHashMap = ConcurrentHashMap()
                        try {

                            var dynamicFormsNew:DynamicFormsNew = CommonUtils.getFormByFormId(formId!!)
                            var dfVersion = dynamicFormsNew.version

                            val dynamicFormMainData = CommonUtils.createFormData(formDataList, ctaId, taskId!!, formId, dfVersion)
                            api = preferencesHelper.apiMap[ApiType.UPDATE_TASK_DATA]

                            uploadDynamicForm(dynamicFormMainData, object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(uploadTask, 1)
                                        } else {
                                            databaseHelper?.syncWithServer(uploadTask, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(uploadTask, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            })

                        } catch (e: KotlinNullPointerException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    Log.e(TAG, "Map is empty...Try Again")
                }
            }
                /*For Error*/1 -> {
                if (count == 0) {
                    count++
                    //after getting error form the thread we interrupt the thread
                    handlerThread?.interrupt()
                }
            }
            }
        }
    }

    var count = 0

    @Inject
    lateinit var httpManager: HttpManager
    @Inject
    lateinit var dataManager: DataManager
    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    override fun onHandleIntent(intent: Intent?) {

    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        //db initialization
        databaseHelper = DatabaseHelper.getInstance(applicationContext)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            // if service is not running in foreground then run it else leave it as it is.
            if (!CommonUtils.isServiceRunningInForeground(this, this.javaClass.name)) {
                CommonUtils.createAndShowForegroundNotification(this, AppConstants.SERVICE_ID_2)
            }
            // get intent to check if user wants to stop the service.
            if (intent != null && intent.hasExtra(AppConstants.Extra.EXTRA_STOP_SERVICE)) {
                stopService = intent.getBooleanExtra(AppConstants.Extra.EXTRA_STOP_SERVICE, false)
            }

            if (mHandler == null) {
                val workerThread = HandlerThread(TAG)
                workerThread.start()
                mHandler = Handler(workerThread.looper)
            }
            if (!isHandlerRunning) {
                mHandler?.post(this@SyncService)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception occur inside SyncService.onStartCommand(): $e")
        }
        return START_REDELIVER_INTENT
    }

    override fun run() {
        try {
            if (NetworkUtils.isNetworkConnected(applicationContext)) {
                isHandlerRunning = true
                val eventModelList = databaseHelper?.checkIfRecordExists()
                if (eventModelList?.isNotEmpty()!!) {
                    val task = eventModelList[0] as ApiEventModel
                    ctaId= task.ctaId
                    formId= task.formId
                    Log.e(TAG, "Record Found for " + task.action?.name)
                    Log.e(TAG, "Record Found  " + task.data)
                    Log.e(TAG, "Record " + task.isAutoStart)

                    when (eventModelList[0].action) {

                        Action.CREATE_TASK -> {
                            api = preferencesHelper.apiMap[ApiType.CREATE_TASK]!!
                            createTask(object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {

                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(task, 1)
                                        } else {
                                            databaseHelper?.syncWithServer(task, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            }, task.data as CreateTaskRequest)
                        }
                        Action.ARRIVE_TASK -> {
                            api = preferencesHelper.apiMap[ApiType.ARRIVE_REACH_TASK]!!
                            arriveTask(task, object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(task, 1)
                                            /*send the broadcast when entry of cancel task is added into the database and
                                            service is started for uploading the data.
                                            send the broadcast when entry of cancel task is added into the database and
                                            service is started for uploading the data.*/
                                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(
                                                    Intent(AppConstants.ACTION_REFRESH_TASK_LIST))
                                        } else {
                                            databaseHelper?.syncWithServer(task, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            })
                        }
                        Action.START_TASK -> {
                            api = preferencesHelper.apiMap[ApiType.START_TASK]!!
//                        StartTask(AcceptRejectRequest(task.tripId!!)).hitApi()
                            startTask(task, object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(task, 1)
                                        } else {
                                            databaseHelper?.syncWithServer(task, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            })
                        }
                        Action.END_TASK -> {
                            api = preferencesHelper.apiMap[ApiType.END_TASK]!!
//                        val endTaskRequest = task.data as EndTaskRequest
                            endTask(object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(task, 1)
                                            /*send the broadcast when entry of cancel task is added into the database and
                                            service is started for uploading the data.
                                            send the broadcast when entry of cancel task is added into the database and
                                            service is started for uploading the data.*/
                                            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(
                                                    Intent(AppConstants.ACTION_REFRESH_TASK_LIST))
                                        } else {
                                            databaseHelper?.syncWithServer(task, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            }, task.isAutoStart!!, task.data!!)
                        }
                        Action.ACCEPT_TASK -> {
                            api = preferencesHelper.apiMap[ApiType.ACCEPT_TASK]!!
//                        AcceptTask(AcceptRejectRequest(task.tripId!!)).hitApi()
                            acceptTask(task, object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(task, 1)
                                        } else {
                                            databaseHelper?.syncWithServer(task, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            })
                        }
                        Action.REJECT_TASK -> {
                            api = preferencesHelper.apiMap[ApiType.REJECT_TASK]!!
//                        RejectTask(task.data as RejectTaskRequest).hitApi()
                            rejectTask(task, object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(task, 1)
                                        } else {
                                            databaseHelper?.syncWithServer(task, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            })
                        }
                        Action.REJECT_INVITATION -> {
                            api = preferencesHelper.apiMap[ApiType.REJECT_INVITATION]!!
//                        AcceptReject(task.data as AcceptRejectBuddyRequest).hitApi()
                            rejectInvitation(task, object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(task, 1)
                                        } else {
                                            databaseHelper?.syncWithServer(task, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            })
                        }
                        Action.CANCEL_TASK -> {
                            api = preferencesHelper.apiMap[ApiType.CANCEL_TASK]!!
//                        CancelTask(AcceptRejectRequest(task.tripId!!)).hitApi()
                            cancelTask(task, object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                        if (baseResponse != null && baseResponse.isSuccessful) {
                                            databaseHelper?.syncWithServer(task, 1)
                                        } else {
                                            databaseHelper?.syncWithServer(task, 0)
                                        }
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
                                        mHandler?.postDelayed(this@SyncService, delayed)
                                    }
                                }
                            })
                        }
                        Action.UPLOAD_FILE -> {
                            api = preferencesHelper.apiMap[ApiType.UPLOAD_FILE]!!

                            uploadTask = task
                            val hashMapFileRequest = task.data as HashMap<String, ArrayList<File>>
                            formDataList = task.formList as ArrayList<FormData>
                            taskId = task.taskId

                            count = 0
                            val thread = HandlerThread("workkker")
                            thread.start()
                            mServiceLooper = thread.looper
                            //start a handler
                            mServiceHandler = ServiceHandler(mServiceLooper!!)
//                        start a thread other than main
                            handlerThread = ExecutorThread()
//                          start a thread other than main
                            handlerThread?.setRequestParams(mServiceHandler!!, hashMapFileRequest, httpManager, api)
                            //start thread
                            handlerThread?.start()
                        }
                        Action.EXECUTE_UPDATE -> {
                            taskId = task.taskId
                            api = preferencesHelper.apiMap[ApiType.EXECUTE_UPDATE]!!
                            var jsonData=Gson().toJson(task.data)
                            CommonUtils.showLogMessage("e","json_data",jsonData)
                            var jsonConverter=JSONConverter<ExecuteUpdateRequest>()
                            var data:ExecuteUpdateRequest=jsonConverter.jsonToObject(jsonData,ExecuteUpdateRequest::class.java)
                            executeUpdate(object : ISyncCallback {
                                override fun onResponse(result: Any?, error: APIError?) {
//                                    if (CommonUtils.handleResponse(this, error, result, applicationContext)) {
//                                        CommonUtils.showLogMessage("e","response execute update",result.toString())
//                                        val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
//                                        if (baseResponse != null && baseResponse.isSuccessful) {
//                                            databaseHelper?.syncWithServer(task, 1)
//                                            //remove from the preferences after successfully
//                                            preferencesHelper.timerCallToAction.remove(taskId)
//                                        } else {
//                                            databaseHelper?.syncWithServer(task, 0)
//                                        }
//                                        mHandler?.postDelayed(this@SyncService, delayed)
//                                    } else {
//                                        databaseHelper?.syncWithServer(task, 0)
//                                        Log.e(TAG, "Inside onError() ${error?.errorType}")
//                                        mHandler?.postDelayed(this@SyncService, delayed)
//                                    }
                                    val baseResponse = Gson().fromJson(result.toString(), BaseResponse::class.java)
                                    if (baseResponse != null && baseResponse.isSuccessful) {
                                        databaseHelper?.syncWithServer(task, 1)
                                        //remove from the preferences after successfully
                                        preferencesHelper.timerCallToAction.remove(taskId)
                                        if (preferencesHelper.pendingTime.containsKey(task.taskId)) {
                                            preferencesHelper.pendingTime.remove(task.taskId)
                                        }

                                        //send the broadcast when entry of cancel task is added into the database and
                                        //service is started for uploading the data.
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(
                                                Intent(AppConstants.ACTION_REFRESH_TASK_LIST))

                                    } else {
                                        databaseHelper?.syncWithServer(task, 0)
                                    }

                                    mHandler?.postDelayed(this@SyncService, delayed)
                                }
                            }, data)
                        }
                    }
                } else {
                    //delete the already sync data
                    databaseHelper?.deleteData()
                    stopService()
                }
            } else {
                stopService()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception inside tracking: run(): $e")
        }
    }

    private fun executeUpdate(iSyncCallback: ISyncCallback, executeUpdateRequest: ExecuteUpdateRequest) {
        dataManager.executeUpdateTask(iSyncCallback, httpManager, executeUpdateRequest, api)
    }

    private fun uploadDynamicForm(dynamicFormMainData: Any, iSyncCallback: ISyncCallback) {
        dataManager.uploadFormList(iSyncCallback, httpManager, dynamicFormMainData, api)
    }

    private fun stopService() {
        mHandler?.removeCallbacks(this@SyncService)
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            isHandlerRunning = false
            mHandler = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createTask(iSyncCallback: ISyncCallback, createTaskRequest: CreateTaskRequest) {
        try {
            if (createTaskRequest.source!!.address == null &&
                    createTaskRequest.source!!.location != null &&
                    createTaskRequest.source!!.location?.latitude != 0.0 &&
                    createTaskRequest.source!!.location?.longitude != 0.0) {
                createTaskRequest.source!!.address = TrackThat.getAddress(createTaskRequest.source!!.location?.latitude!!, createTaskRequest.source!!.location?.longitude!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dataManager.createTask(iSyncCallback, httpManager, createTaskRequest, api)
    }

    private fun endTask(iSyncCallback: ISyncCallback, autoStart: Boolean?, task: Any) {
        var endTaskRequest: Any? = null
        try {
            var isAutoStart = false
            if (autoStart != null && autoStart) {
                isAutoStart = true
            }
            if (isAutoStart) {
                endTaskRequest = task as EndAutoTaskRequest

                if (endTaskRequest.destination.address == null &&
                        endTaskRequest.destination.location != null &&
                        endTaskRequest.destination.location?.latitude != 0.0 &&
                        endTaskRequest.destination.location?.longitude != 0.0) {
                    endTaskRequest.destination.address = TrackThat.getAddress(endTaskRequest.destination.location?.latitude!!,
                            endTaskRequest.destination.location?.longitude!!)
                }

            } else {
                endTaskRequest = task as EndTaskRequest
                if (endTaskRequest.destination?.address == null &&
                        endTaskRequest.destination?.location != null &&
                        endTaskRequest.destination?.location?.latitude != 0.0 &&
                        endTaskRequest.destination?.location?.longitude != 0.0) {
                    endTaskRequest.destination?.address = TrackThat.getAddress(endTaskRequest.destination?.location?.latitude!!,
                            endTaskRequest.destination?.location?.longitude!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        dataManager.endTask(iSyncCallback, httpManager, endTaskRequest, api)
    }

    private fun cancelTask(task: ApiEventModel, iSyncCallback: ISyncCallback) {
        dataManager.cancelTask(iSyncCallback, httpManager, CancelRequest(task.tripId!!, task.isAutoCancel), api)
    }



    private fun rejectInvitation(task: ApiEventModel, iSyncCallback: ISyncCallback) {
        dataManager.acceptRejectRequest(iSyncCallback, httpManager, task.data as AcceptRejectBuddyRequest, api!!)
    }

    private fun rejectTask(task: ApiEventModel, iSyncCallback: ISyncCallback) {
        dataManager.rejectTask(iSyncCallback, httpManager, task.data as RejectTaskRequest, api!!)
    }

    private fun acceptTask(task: ApiEventModel, iSyncCallback: ISyncCallback) {
        dataManager.acceptTask(iSyncCallback, httpManager, AcceptRejectRequest(task.tripId!!), api)
    }

    private fun startTask(task: ApiEventModel, iSyncCallback: ISyncCallback) {
        dataManager.startTask(iSyncCallback, httpManager, AcceptRejectRequest(task.tripId!!), api)
    }

    private fun arriveTask(task: ApiEventModel, iSyncCallback: ISyncCallback) {
        dataManager.arriveReachTask(iSyncCallback, httpManager, ArriveReachRequest(task.time, task.tripId!!, null), api)
    }
}

/**
 * Interface used to handle callback when used hit api.
 */
interface ISyncCallback : SyncCallback {

    override fun isAvailable(): Boolean {
        return true
    }
}