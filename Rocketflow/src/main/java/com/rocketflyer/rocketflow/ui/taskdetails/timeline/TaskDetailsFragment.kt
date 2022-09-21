package com.rocketflyer.rocketflow.ui.taskdetails.timeline

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.local.prefs.StartIdealTrackWork
import com.tracki.data.model.GeofenceData
import com.tracki.data.model.request.*
import com.tracki.data.model.request.TaskData
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ItemDynamicFormVideoBinding
import com.tracki.databinding.LayoutFrgmrntTaskDetailsBinding
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.chat.ChatActivity
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.ui.custom.socket.*
import com.tracki.ui.dynamicform.DynamicFormActivity
import com.tracki.ui.dynamicform.DynamicFormActivity.Companion.newIntent
import com.tracki.ui.dynamicform.dynamicfragment.DynamicAdapter
import com.tracki.ui.dynamicform.dynamicfragment.DynamicFragment
import com.tracki.ui.dynamicform.dynamicfragment.DynamicViewModel
import com.tracki.ui.dynamicform.dynamicfragment.ShowDynamicFormDataAdapter
import com.tracki.ui.main.taskdashboard.TaskDashBoardFragment
import com.tracki.ui.messages.MessagesActivity
import com.tracki.ui.newcreatetask.NewCreateTaskActivity
import com.tracki.ui.scanqrcode.ScanQrAndBarCodeActivity
import com.tracki.ui.selectorder.SelectOrderActivity
import com.tracki.ui.taskdetails.*
import com.tracki.ui.tasklisting.TaskClickListener
import com.tracki.ui.tasklisting.assignedtome.TaskAssignToMeViewModel
import com.tracki.ui.userlisting.UserListNewActivity
import com.tracki.ui.webview.WebViewActivity
import com.tracki.utils.*
import com.tracki.utils.AppConstants.Extra
import com.tracki.utils.geofence.AddGeoFenceUtil
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.network.TrackThatCallback
import com.trackthat.lib.models.ErrorResponse
import com.trackthat.lib.models.SuccessResponse
import com.trackthat.lib.models.TrackthatLocation
import kotlinx.android.synthetic.main.layout_frgmrnt_task_details.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList


class TaskDetailsFragment :
    BaseFragment<LayoutFrgmrntTaskDetailsBinding, NewTaskDetailsViewModel>(), TaskAssignToMeViewModel.AssignedtoMeItemViewModelListener,
    TaskClickListener, TaskTimeLineAdapter.PreviousFormListener, WebSocketManager.SocketListener,
     TDNavigator, DynamicAdapter.AdapterListener{


    private val REQUEST_CAMERA: Int=101
    lateinit var foundWidgetItem: FoundWidgetItem
    private var recyclerCtaButton: RecyclerView? = null
    private var tvLabelTakeAction: TextView? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private var roomId: String? = null
    private var buddyName: String? = null
    private var buddyId: String? = null
    private var categoryId: String? = null
    private var mobile: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var listBuddy = ArrayList<Buddy>()


    lateinit var mNewTaskViewModel: NewTaskDetailsViewModel

    lateinit var rvDynamicForm: RecyclerView

    lateinit var rvFields: RecyclerView

    var scannerFieldName: String? = null
    var scannerFieldValue: String? = null
    private var dynamicFragment: DynamicFragment? = null
    private var ctaId: String? = null
    private var taskAction: String? = null
    private var tcfId: String? = null
    private var geofenceData: GeofenceData? = null
    private var formId: String? = null
    private var dynamicFormsNew: DynamicFormsNew? = null
    private var isEditable: Boolean = false
    private var isHideButton: Boolean = false

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    private lateinit var mActivityNewTaskDetailBinding: LayoutFrgmrntTaskDetailsBinding

    private var task: Task? = null

    @Inject
    lateinit var httpManager: HttpManager

    lateinit var showDynamicFormDataAdapter: ShowDynamicFormDataAdapter

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    var addGeoFenceUtil: AddGeoFenceUtil? = null

    var taskId: String? = null
    private var api: Api? = null
    private var taskResponse: TaskResponse? = null


    private var currentLocation: GeoCoordinates? = null
    private var callToActions: CallToActions? = null
    private var ctaID: String? = null
    private var dfId: String? = null
    private var dfdId: String? = null
    private var FROM: String? = null
    private val formList = ArrayList<String>()

    private var fieldArrayList = ArrayList<String>()
    private var allowedFields = ArrayList<Field>()

    private lateinit var timeLineAdapter: TaskTimeLineAdapter

    private val TAG = "TaskDetailsFragment"

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_frgmrnt_task_details
    }


    override fun getViewModel(): NewTaskDetailsViewModel {
        mNewTaskViewModel =
            ViewModelProviders.of(this, mViewModelFactory).get(NewTaskDetailsViewModel::class.java)
        return mNewTaskViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNewTaskViewModel.navigator = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivityNewTaskDetailBinding = viewDataBinding

        //container = mActivityNewTaskDetailBinding.container
        timeLineAdapter = TaskTimeLineAdapter(this)
        mActivityNewTaskDetailBinding.timeLineAdapter = timeLineAdapter
        val colayout = mActivityNewTaskDetailBinding.coradinatorLL as CoordinatorLayout
        var bottomSheet = colayout.findViewById<View>(R.id.bottomSheet)
        recyclerCtaButton = bottomSheet.findViewById(R.id.recyclerCtaButton)
        tvLabelTakeAction = bottomSheet.findViewById(R.id.tvLabelTakeAction)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
        sheetBehavior.isHideable = false
        sheetBehavior.isFitToContents = true
        sheetBehavior.setPeekHeight(CommonUtils.dpToPixel(activity, 80), true)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        getTaskData()
        expandCollapse()

        showDynamicFormDataAdapter = ShowDynamicFormDataAdapter(ArrayList())

        rvDynamicForm = mActivityNewTaskDetailBinding.rvDynamicFormsMini
        rvDynamicForm.layoutManager = LinearLayoutManager(baseActivity)
        rvDynamicForm.adapter = showDynamicFormDataAdapter

        //rvDynamicForm.adapter = showDynamicFormDataAdapter

        addGeoFenceUtil = AddGeoFenceUtil(baseActivity, preferencesHelper)
        tvLabelTakeAction!!.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
//        mActivityNewTaskDetailBinding.cvChat.setOnClickListener {
////            initSocket()
////            connectSocket(this)
//            openRoom()
//        }
    }

    companion object {
        fun newInstance(taskId: String?, categoryId: String?, FROM: String?): TaskDetailsFragment? {
            val args = Bundle()
            args.putString(AppConstants.Extra.EXTRA_TASK_ID, taskId)
            args.putString(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId)
            args.putString(AppConstants.Extra.FROM, FROM)
            val fragment = TaskDetailsFragment()
            fragment.arguments = args
            return fragment
        }

    }


    private fun getTaskData() {
        if (arguments != null) {

            if (requireArguments().getString(AppConstants.Extra.EXTRA_TASK_ID) != null) {
                taskId = requireArguments().getString(AppConstants.Extra.EXTRA_TASK_ID)

            }
            if (requireArguments().getString(AppConstants.Extra.FROM) != null) {
                FROM = requireArguments().getString(AppConstants.Extra.FROM)

            }
            if (requireArguments().getString(AppConstants.Extra.EXTRA_CATEGORY_ID) != null) {
                categoryId = requireArguments().getString(AppConstants.Extra.EXTRA_CATEGORY_ID)

            }
            showLoading()
            api = TrackiApplication.getApiMap()[ApiType.GET_TASK_BY_ID]
            if (api != null && ::mNewTaskViewModel.isInitialized)
                mNewTaskViewModel.getTaskById(httpManager, AcceptRejectRequest(taskId!!), api)
            val startLocationLabel =
                CommonUtils.getAllowFieldLabelName("START_LOCATION", categoryId, preferencesHelper)
            if (!startLocationLabel.isEmpty()) {
                if (labelStartLocation != null)
                    labelStartLocation.text = startLocationLabel
            }
            if (categoryId != null) {
                val taskId =
                    CommonUtils.getAllowFieldLabelName("TASK_ID", categoryId, preferencesHelper)
                if (!taskId.isEmpty()) {
                    // label_id.setText(taskId)
                }
                val reffenceId = CommonUtils.getAllowFieldLabelName(
                    "REFERENCE_ID",
                    categoryId,
                    preferencesHelper
                )
                if (!reffenceId.isEmpty()) {
                    tvLabelRefferenceId.text = reffenceId
                }
                val pocLabel = CommonUtils.getAllowFieldLabelName(
                    "POINT_OF_CONTACT",
                    categoryId,
                    preferencesHelper
                )
                if (!pocLabel.isEmpty()) {
                    tvLabelPoc.text = pocLabel
                }
                val descLabel =
                    CommonUtils.getAllowFieldLabelName("DESCRIPTION", categoryId, preferencesHelper)
                if (!descLabel.isEmpty()) {
                    tvLabelDesc.text = descLabel
                }

                val endLocationName = CommonUtils.getAllowFieldLabelName(
                    "END_LOCATION",
                    categoryId,
                    preferencesHelper
                )
                if (!endLocationName.isEmpty()) {
                    labelEndLocation.text = endLocationName
                }
                val startTime =
                    CommonUtils.getAllowFieldLabelName("START_TIME", categoryId, preferencesHelper)
                if (!startTime.isEmpty()) {
                    labelScheduledAt.text = startTime
                }
                val endTime =
                    CommonUtils.getAllowFieldLabelName("END_TIME", categoryId, preferencesHelper)
                if (!endTime.isEmpty()) {
                    labelEndAt.text = endTime
                }
                val assigneeLabel =
                    CommonUtils.getAssigneeLabel( categoryId, preferencesHelper)
                if (!assigneeLabel.isEmpty()) {
                    tvClientsDetails.text = assigneeLabel
                }
                val buddyLabel =
                    CommonUtils.getBuddyLabel( categoryId, preferencesHelper)
                if (!buddyLabel.isEmpty()) {
                    tvAssignedTolable.text = buddyLabel
                }




            }






        }
    }

    fun setMargin(dp: Int) {
        var layoutParams: RelativeLayout.LayoutParams =
            nestedScrollView.layoutParams as (RelativeLayout.LayoutParams)
        layoutParams.setMargins(0, 0, 0, CommonUtils.dpToPixel(activity, dp))
        nestedScrollView.layoutParams = layoutParams;
    }


    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        try {

            if (CommonUtils.handleResponse(callback, error, result, baseActivity)) {

                taskResponse = Gson().fromJson("$result", TaskResponse::class.java)
                if (taskResponse != null) {

                    task = taskResponse!!.taskDetail
                    if(categoryId.isNullOrEmpty())
                        categoryId=task?.categoryId
                    val itemViewModel = TaskAssignToMeViewModel(
                        task,
                        this, baseActivity, preferencesHelper, categoryId
                    )
                    mActivityNewTaskDetailBinding.data = itemViewModel
                    llMain.visibility = View.VISIBLE
                    hideLoading()
                    var isShow = CommonUtils.handleCallToActionsNew(
                        baseActivity,
                        task,
                        recyclerCtaButton,
                        this
                    )
                    if (isShow) {
                        tvLabelTakeAction!!.visibility = View.VISIBLE
                        setMargin(50)

                    } else {
                        tvLabelTakeAction!!.visibility = View.GONE
                        setMargin(0)
                    }
                    if (task!!.clientTaskId != null && task!!.clientTaskId!!.isNotEmpty()) {
                        tvTaskId.text = task!!.clientTaskId!!
                    }
                    perFormTimerReminder(task)
                    perFormLocationReminder(task)
                    if (task!!.trackingState != null) {
                        handleTracking(task!!.trackingState!!, task!!.taskId!!)
                    }


                    if (task!!.orderDetails != null && task!!.orderDetails!!.isNotEmpty()) {
//                        val listOrderDetais = ArrayList<OrderInnerData>()

                        /* val hmIterator: Iterator<*> = task!!.orderDetails!!.entries.iterator()
                         while (hmIterator.hasNext()) {
                             val mapElement = hmIterator.next() as Map.Entry<*, *>
                             val orderInnerData = OrderInnerData()
                             orderInnerData.orderId = mapElement.key.toString()
                             orderInnerData.orderDetails = mapElement.value as OrderDetails?

                             listOrderDetais.add(orderInnerData)
                         }*/
                        cardOrders.visibility = View.VISIBLE
                        var adapter = OrderListAdapter(task!!.orderDetails)
                        rvOrderList.adapter = adapter

                    } else if (task!!.products != null && task!!.products!!.isNotEmpty()) {

                        cardOrders.visibility = View.VISIBLE
                        tvInventoriesLabel.text = getString(R.string.inventory_label)
                        var adapter = ProductOrderAdapter(task!!.products)
                        rvOrderList.adapter = adapter

                    } else {
                        cardOrders.visibility = View.GONE
                    }
                    ivPinStartLocation.setOnClickListener {

                        if (task!!.source != null && task!!.source!!.location != null) {
                            CommonUtils.showLogMessage(
                                "e",
                                "source point map",
                                "source start points map"
                            )
                            val geoCoordinates = task!!.source!!.location
                            CommonUtils.openGoogleMapWithOneLocation(
                                context,
                                geoCoordinates!!.latitude,
                                geoCoordinates.longitude
                            )
                        }

                    }
                    ivPinEndLocation.setOnClickListener {

                        if (task!!.destination != null && task!!.destination!!.location != null) {
                            CommonUtils.showLogMessage(
                                "e",
                                "desination point map",
                                "source start points map"
                            )
                            val geoCoordinates = task!!.destination!!.location
                            CommonUtils.openGoogleMapWithOneLocation(
                                context,
                                geoCoordinates!!.latitude,
                                geoCoordinates.longitude
                            )
                        }

                    }

                    if (task!!.trackable) {
                        ivCurrentStageLocation.visibility = View.VISIBLE
//                        ivCurrentStageLocation.visibility = View.GONE
                        ivCurrentStageLocation.setOnClickListener {
                            if (FROM.equals(AppConstants.Extra.ASSIGNED_TO_ME)) {
                                startActivity(
                                    TaskDetailActivity.Companion.newIntent(baseActivity)
                                        .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task!!.taskId)
                                );
                            } else {
                                if (preferencesHelper.userDetail != null && preferencesHelper.userDetail.userId != null && !preferencesHelper.userDetail!!.userId!!.isEmpty()) {
                                    val navigation = Navigation()
                                    val actionConfig = ActionConfig()
                                    val userId = preferencesHelper.userDetail.userId
                                    actionConfig.actionUrl =
                                        task!!.trackingUrl + "&userId=" + userId
                                    // actionConfig.actionUrl = task!!.trackingUrl
                                    navigation.actionConfig = actionConfig
                                    navigation.title = "Tracking Details"
                                    startActivity(
                                        WebViewActivity.newIntent(baseActivity)
                                            .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation)
                                    )
                                }
                            }
                        }
                    }
                    rlAssignedToTrack.setOnClickListener(View.OnClickListener {
                        if (FROM.equals(AppConstants.Extra.ASSIGNED_TO_ME)) {
                            startActivity(
                                TaskDetailActivity.Companion.newIntent(baseActivity)
                                    .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task!!.taskId)
                            );
                        } else {
                            if (preferencesHelper.userDetail != null && preferencesHelper.userDetail.userId != null && !preferencesHelper.userDetail!!.userId!!.isEmpty()) {
                                val navigation = Navigation()
                                val actionConfig = ActionConfig()
                                val userId = preferencesHelper.userDetail.userId
                                actionConfig.actionUrl = task!!.trackingUrl + "&userId=" + userId
                                // actionConfig.actionUrl = task!!.trackingUrl
                                navigation.actionConfig = actionConfig
                                navigation.title = "Tracking Details"
                                startActivity(
                                    WebViewActivity.newIntent(baseActivity)
                                        .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation)
                                )
                            }
                        }
                    })

                    if (task!!.stageHistory != null && task!!.stageHistory!!.isNotEmpty()) {
                        var list = ArrayList(task!!.stageHistory!!)
                            .sortedWith(compareByDescending<StageHistoryData> { it.timeStamp }
                            )
                        var history = list[list.size - 1]

                        if (history != null) {
                            if (history.dfdId != null) {
                                formId = history.dfdId
                                if (formId != null && formId!!.isNotEmpty()) {
                                    var data = GetTaskDataRequest()
                                    data.dfdId = history.dfdId
                                    dynamicFormsNew = CommonUtils.getFormByFormId(formId!!)
                                    if (dynamicFormsNew != null && dynamicFormsNew!!.version != null) {
                                        data.dfVersion = Integer.valueOf(dynamicFormsNew!!.version!!)
                                    }
                                    data.taskId = taskId
                                    mNewTaskViewModel.getTaskData(httpManager, data)

                                }
                                //llDetails.visibility = View.VISIBLE
                            } else {
                                //llDetails.visibility = View.GONE
                            }
                        }

                        timeLineAdapter.addData(list)
                        //dataForm = list[list.size - 1]
                        /*llDetails.setOnClickListener {
                            if (list.isNotEmpty()) {
                                openForm(list[list.size - 1])
                            }
                        }*/
                    } else {
                        //llDetails.visibility = View.GONE
                    }


                }

            } else {
                hideLoading()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside handleResponse(): $e")
        }
    }

     override fun handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {
         hideLoading()

         if (CommonUtils.handleResponse(callback, error, result, context)) {
             val getTaskDataResponse = Gson().fromJson<GetTaskDataResponse>(
                 result.toString(),
                 GetTaskDataResponse::class.java
             )
             var data: List<com.tracki.data.model.response.config.TaskData>? = null
             if (getTaskDataResponse != null && getTaskDataResponse.successful) {

                 if (getTaskDataResponse.data != null) {
                     data = getTaskDataResponse.data!!
                     getTaskDataResponse.data!!.let {
                         var list1 = ArrayList<com.tracki.data.model.response.config.TaskData>()
                         list1.addAll(it as ArrayList<com.tracki.data.model.response.config.TaskData>)



                         showDynamicFormDataAdapter.addData(list1)

                     }
                 }
                 if (getTaskDataResponse.dfdId != null) {
                     dfdId = getTaskDataResponse.dfdId!!
                 }
             }

         }
     }

     private fun perFormLocationReminder(task: Task?) {
        if (task != null) {
            if (task.currentStage != null && !task.currentStage!!.terminal!! && task.startTime > System.currentTimeMillis()) {
                if (task.source != null && task.source!!.location != null) {
                    var taskId = task.taskId + "#" + "Start Location"

//                    task.source!!.location!!.latitude=25.216191
//                    task.source!!.location!!.longitude=80.925242
                    addGeoFenceUtil!!.addGeofence(taskId, task.source!!.location, 500f)
                }
            }
            if (task.currentStage != null && !task.currentStage!!.terminal!! && task.endTime > System.currentTimeMillis()) {
                if (task.destination != null && task.destination!!.location != null) {
                    var taskId = task.taskId + "#" + "End Location"
                    addGeoFenceUtil!!.addGeofence(taskId, task.destination!!.location, 500f)
                }
            }
        }
    }

    private fun perFormTimerReminder(task: Task?) {
        if (task != null) {
            var map = preferencesHelper.eventIdMap
            if (map != null && map.isNotEmpty()) {
                if (!map.containsKey(task.taskId)) {
//                    var eventId = map[task.taskId]
//                    CommonUtils.deleteEventUri(context, eventId, task.taskId, preferencesHelper)
                    if (task.currentStage != null && !task.currentStage!!.terminal!! && task.startTime > System.currentTimeMillis()) {
                        var titile =
                            "Task going to start in " + preferencesHelper.timeBeforeTime + " minutes "
                        var desc = ""
                        var place = ""
                        var startDate = task.startTime
                        var needReminder = true
                        var needMainSerVice = false
                        var taskId = task.taskId
//                        if (task.clientTaskId != null)
//                            titile = task.clientTaskId!!
                        if (task.description != null)
                            desc = task.description!!
                        if (task.source != null && task.source!!.address != null)
                            place = task.source!!.address!!
                        CommonUtils.addAppointmentsToCalender(
                            baseActivity, titile, desc, place, 1, startDate, needReminder,
                            needMainSerVice, taskId, preferencesHelper
                        )
                    }
                    if (task.currentStage != null && !task.currentStage!!.terminal!! && task.endTime > System.currentTimeMillis()) {
                        var titile =
                            "Task going to end in " + preferencesHelper.timeBeforeTime + " minutes "
                        var desc = ""
                        var place = ""
                        var startDate = task.endTime
                        var needReminder = true
                        var needMainSerVice = false
                        var taskId = task.taskId
//                        if (task.clientTaskId != null)
//                            titile = task.clientTaskId!!
                        if (task.description != null)
                            desc = task.description!!
                        if (task.destination != null && task.destination!!.address != null)
                            place = task.destination!!.address!!
                        CommonUtils.addAppointmentsToCalender(
                            baseActivity, titile, desc, place, 1, startDate, needReminder,
                            needMainSerVice, taskId, preferencesHelper
                        )
                    }
                }

            } else {
                if (task.currentStage != null && !task.currentStage!!.terminal!! && task.startTime > System.currentTimeMillis()) {
                    var titile =
                        "Task going to start in " + preferencesHelper.timeBeforeTime + " minutes "
                    var desc = ""
                    var place = ""
                    var startDate = task.startTime
                    var needReminder = true
                    var needMainSerVice = false
                    var taskId = task.taskId

                    if (task.description != null)
                        desc = task.description!!
                    if (task.source != null && task.source!!.address != null)
                        place = task.source!!.address!!
                    CommonUtils.addAppointmentsToCalender(
                        baseActivity, titile, desc, place, 1, startDate, needReminder,
                        needMainSerVice, taskId, preferencesHelper
                    )
                }
                if (task.currentStage != null && !task.currentStage!!.terminal!! && task.endTime > System.currentTimeMillis()) {
                    var titile =
                        "Task going to end in " + preferencesHelper.timeBeforeTime + " minutes "
                    var desc = ""
                    var place = ""
                    var startDate = task.endTime
                    var needReminder = true
                    var needMainSerVice = false
                    var taskId = task.taskId
                    if (task.description != null)
                        desc = task.description!!
                    if (task.destination != null && task.destination!!.address != null)
                        place = task.destination!!.address!!
                    CommonUtils.addAppointmentsToCalender(
                        baseActivity, titile, desc, place, 1, startDate, needReminder,
                        needMainSerVice, taskId, preferencesHelper
                    )
                }
            }


        }

    }

    override fun expandCollapse() {
//        if (mActivityNewTaskDetailBinding.collapse.visibility == View.VISIBLE) {
//            mActivityNewTaskDetailBinding.collapse.visibility = View.GONE
//            ivTimeLineCollapse.setImageDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.ic_plus))
//
//        } else {
//            mActivityNewTaskDetailBinding.collapse.visibility = View.VISIBLE
//            ivTimeLineCollapse.setImageDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.ic_minus))
//        }

    }

    override fun onDetailsClick(task: Task?) {

    }

    override fun onChatStart(buddyId: String?, buddyName: String?) {
        if (buddyId != null && buddyId.isNotEmpty()) {
            val list = ArrayList<String>()
            list.add(buddyId!!)

            startActivity(
                ChatActivity.newIntent(baseActivity)
                    .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, list)
                    .putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
                    .putExtra(AppConstants.Extra.EXTRA_IS_CREATE_ROOM, true)
                    .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId)
            )
        }

    }


    override fun onClickMapIcon(task: Task?) {


        //if current location is not available then notify user by showing toast else open google map.
        if (currentLocation != null) {
            var sLat = 0.0
            var sLng = 0.0
            var dLat = 0.0
            var dLng = 0.0
            //if status is arrived then get the source location and open g-map.
            if (task!!.status === TaskStatus.ACCEPTED) {
                if (task!!.source != null && task!!.source!!.location != null) {
                    sLat = currentLocation!!.latitude
                    sLng = currentLocation!!.longitude
                    dLat = task!!.source!!.location!!.latitude
                    dLng = task!!.source!!.location!!.longitude
                }
            } else if (task!!.status === TaskStatus.LIVE) {
                if (task!!.destination != null && task!!.destination!!.location != null) {
                    sLat = currentLocation!!.latitude
                    sLng = currentLocation!!.longitude
                    dLat = task!!.destination!!.location!!.latitude
                    dLng = task!!.destination!!.location!!.longitude
                }
            }
            if (sLat != 0.0 && sLng != 0.0 && dLat != 0.0 && dLng != 0.0) {
                //open google map activity
                CommonUtils.openGoogleMap(baseActivity, sLat, sLng, dLat, dLng)
            }
        } else {
            TrackiToast.Message.showShort(baseActivity, "No location found. Try Again")
        }
    }


    override fun onItemClick(bean: Task?) {

    }

    override fun onCallClick(mobile: String?) {
        if (mobile != null) {
            CommonUtils.openDialer(baseActivity, mobile)
        }
    }

    override fun onChatClick(buddyId: String?, name: String?) {

    }

    override fun onExecuteUpdates(id: String?, task: Task?) {
        ctaID = id
        this.task = task
        Log.e("checkID","$ctaID")
        Log.e(TAG, "onExecuteUpdates: $task")


        if (task!!.currentStage != null) {
            val callToActionList = task.currentStage!!.callToActions



                if (callToActionList != null && callToActionList.size > 0) {
                    for (i in callToActionList.indices) {
                        if (callToActionList[i].id.equals(id, ignoreCase = true)) {
                            callToActions = callToActionList[i]
                            break
                        }
                    }
                }
                if (callToActions != null) {


                        // if (callToActions.getTargetInfo() != null && callToActions.getTargetInfo().getTarget() == TRAGETINFO.CREATE_TASK)
                    if (callToActions!!.targetInfo != null && callToActions!!.targetInfo!!.target == TRAGETINFO.CREATE_TASK) {
                        //                    val message = "Are you sure you want to perform ?"
                        //                    val dialog = DoubleButtonDialog(requireContext(),
                        //                            true,
                        //                            null,
                        //                            message,
                        //                            getString(R.string.yes),
                        //                            getString(R.string.no),
                        //                            object : OnClickListener {
                        //                                override fun onClickCancel() {}
                        //                                override fun onClick() {
                        //                                    val intent = newIntent(baseActivity)
                        //                                    intent.putExtra(Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, false)
                        //                                    intent.putExtra(Extra.FROM, "taskListing")
                        //                                    if (callToActions!!.categoryId != null) {
                        //                                        val dashBoardBoxItem = DashBoardBoxItem()
                        //                                        dashBoardBoxItem.categoryId = callToActions!!.categoryId
                        //                                        val map = Gson().toJson(dashBoardBoxItem)
                        //                                        intent.putExtra(Extra.EXTRA_CATEGORIES, map)
                        //                                        intent.putExtra(Extra.EXTRA_PAREN_TASK_ID, task.taskId)
                        //                                        intent.putExtra(Extra.EXTRA_PARENT_REF_ID, task.referenceId)
                        //                                    }
                        //                                    startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK)
                        //                                }
                        //                            })
                        //                    dialog.show()
                        val intent = NewCreateTaskActivity.newIntent(baseActivity)
                        intent.putExtra(
                            Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU,
                            false
                        )
                        intent.putExtra(Extra.FROM, "taskListing")
                        if (callToActions!!.categoryId != null) {
                            val dashBoardBoxItem = DashBoardBoxItem()
                            // dashBoardBoxItem.categoryId = callToActions!!.categoryId
                            if (callToActions!!.targetInfo!!.category != null)
                                dashBoardBoxItem.categoryId =
                                    callToActions!!.targetInfo!!.category
                            val map = Gson().toJson(dashBoardBoxItem)
                            intent.putExtra(Extra.EXTRA_CATEGORIES, map)
                            intent.putExtra(Extra.EXTRA_PAREN_TASK_ID, task.taskId)
                            intent.putExtra(Extra.EXTRA_PARENT_REF_ID, task.referenceId)
                        }
                        startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK)
                    } else {
                        perFormCtaAction()
                    }

            }

        }
    }
    fun getInvAction(categoryId: String?): String? {
        var invAction:String?=null
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper.workFlowCategoriesList
        if (!workFlowCategoriesList.isNullOrEmpty()) {
            for (i in workFlowCategoriesList) {
                if (i.categoryId != null && categoryId != null)
                    if (i.categoryId == categoryId) {

                        if (i.inventoryConfig != null && i.inventoryConfig!!.invAction != null &&  i.inventoryConfig!!.invAction!!.name.isNotEmpty())
                            invAction=i.inventoryConfig!!.invAction!!.name
                    }
            }
        }


        return invAction
    }

   fun String.getCtaInventoryConfig():CtaInventoryConfig{
       var ctaInventoryConfig=CtaInventoryConfig()
        try {
            var jsonConverter=JSONConverter<CtaInventoryConfig>()
            ctaInventoryConfig=jsonConverter.jsonToObject(this,CtaInventoryConfig::class.java)
        }catch (e:JsonParseException){
            return ctaInventoryConfig
        }
       return ctaInventoryConfig
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    openScanActivity()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (baseActivity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openScanActivity()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                   REQUEST_CAMERA
                )
            }
        } else {
            openScanActivity()
        }
    }

    private fun openScanActivity() {
        var scanActivity = Intent(baseActivity!!, ScanQrAndBarCodeActivity::class.java)
        scanActivity.putExtra("from","taskdetails")
        baseActivity.startActivityForResult(scanActivity, AppConstants.REQUEST_CODE_SCAN)
    }

    private fun perFormCtaAction() {
        Log.e("checkLog","${callToActions!!.categoryId}")
        if (callToActions!!.dynamicFormId != null && callToActions!!.dynamicFormId!!.isNotEmpty() ) {


            if(calculateCondition()) {
                foundWidgetItem = CommonUtils.getFormByFormIdContainsWidget(
                    callToActions!!.dynamicFormId,
                    DataType.SCANNER
                )
                if (foundWidgetItem.isPresent) {

                    if (baseActivity != null) {
                        getCameraPermission()
                    }
                } else {
                    openDynamicFormScreen()
                }
            }


        } else {
            var setDirect = false
            setDirect = callToActions!!.targetInfo!!.target == TRAGETINFO.ASSIGN_EXECUTIVE
            if (setDirect){
                try {
                    if (callToActions!!.targetInfo != null && callToActions!!.targetInfo!!.target === TRAGETINFO.TAG_INVENTORY) {
                        if (activity != null) {
                            val intent = SelectOrderActivity.newIntent(requireActivity())
                            val dashBoardBoxItem = DashBoardBoxItem()

                            dashBoardBoxItem.categoryId = categoryId
                            intent.putExtra(
                                Extra.EXTRA_CATEGORIES,
                                Gson().toJson(dashBoardBoxItem)
                            )
                            intent.putExtra(Extra.EXTRA_CTA_ID, callToActions!!.id)
                            intent.putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                            if(callToActions!!.targetInfo!!.category!=null&&callToActions!!.targetInfo!!.category!!.isNotEmpty())
                                intent.putExtra(Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID, callToActions!!.targetInfo!!.category)


                            if(!callToActions!!.targetInfo!!.targetInfo.isNullOrEmpty()){
                                var ctaInventoryConfig=callToActions!!.targetInfo!!.targetInfo!!.getCtaInventoryConfig()
                                if(!ctaInventoryConfig.invAction.isNullOrEmpty()) {
                                    intent.putExtra(
                                        Extra.EXTRA_TASK_TAG_INV_TARGET,
                                        ctaInventoryConfig.invAction
                                    )
                                }
                                else{
                                    var invAction=getInvAction(categoryId)
                                    if(invAction!=null&&invAction.isNotEmpty())
                                        intent.putExtra(Extra.EXTRA_TASK_TAG_INV_TARGET, invAction)
                                }
                                if(ctaInventoryConfig.dynamicPricing!=null)
                                    intent.putExtra(Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING, ctaInventoryConfig.dynamicPricing)

                            }
                            else{
                                var invAction=getInvAction(categoryId)
                                if(invAction!=null&&invAction.isNotEmpty())
                                    intent.putExtra(Extra.EXTRA_TASK_TAG_INV_TARGET, invAction)
                            }
                            startActivityForResult(
                                intent,
                                AppConstants.REQUEST_CODE_TAG_INVENTORY
                            )
                        }
                    } else {
                        checkConditionsAndRequestAPI(null)
                    }
                } catch (e: java.lang.Exception) {
                    hideLoading()
                    Log.e(TAG, "onExecuteUpdates: ${e}")
                    e.printStackTrace()
                }
            }
            else {

                val message = "Are you sure you want to perform ?"
                val dialog = DoubleButtonDialog(requireContext(),
                    true,
                    null,
                    message,
                    getString(R.string.yes),
                    getString(R.string.no),
                    object : OnClickListener {
                        override fun onClickCancel() {}
                        override fun onClick() {
                            try {
                                if (callToActions!!.targetInfo != null && callToActions!!.targetInfo!!.target === TRAGETINFO.TAG_INVENTORY) {
                                    if (activity != null) {
                                        val intent = SelectOrderActivity.newIntent(activity!!)
                                        val dashBoardBoxItem = DashBoardBoxItem()

                                        dashBoardBoxItem.categoryId = categoryId
                                        intent.putExtra(
                                            Extra.EXTRA_CATEGORIES,
                                            Gson().toJson(dashBoardBoxItem)
                                        )
                                        intent.putExtra(Extra.EXTRA_CTA_ID, callToActions!!.id)
                                        intent.putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                                        if (callToActions!!.targetInfo!!.category != null && callToActions!!.targetInfo!!.category!!.isNotEmpty())
                                            intent.putExtra(
                                                Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID,
                                                callToActions!!.targetInfo!!.category
                                            )


                                        if (!callToActions!!.targetInfo!!.targetInfo.isNullOrEmpty()) {
                                            var ctaInventoryConfig =
                                                callToActions!!.targetInfo!!.targetInfo!!.getCtaInventoryConfig()
                                            if (!ctaInventoryConfig.invAction.isNullOrEmpty()) {
                                                intent.putExtra(
                                                    Extra.EXTRA_TASK_TAG_INV_TARGET,
                                                    ctaInventoryConfig.invAction
                                                )
                                            } else {
                                                var invAction = getInvAction(categoryId)
                                                if (invAction != null && invAction.isNotEmpty())
                                                    intent.putExtra(
                                                        Extra.EXTRA_TASK_TAG_INV_TARGET,
                                                        invAction
                                                    )
                                            }
                                            if (ctaInventoryConfig.dynamicPricing != null)
                                                intent.putExtra(
                                                    Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING,
                                                    ctaInventoryConfig.dynamicPricing
                                                )

                                        } else {
                                            var invAction = getInvAction(categoryId)
                                            if (invAction != null && invAction.isNotEmpty())
                                                intent.putExtra(
                                                    Extra.EXTRA_TASK_TAG_INV_TARGET,
                                                    invAction
                                                )
                                        }
                                        startActivityForResult(
                                            intent,
                                            AppConstants.REQUEST_CODE_TAG_INVENTORY
                                        )
                                    }
                                } else {
                                    checkConditionsAndRequestAPI(null)
                                }
                            } catch (e: java.lang.Exception) {
                                hideLoading()
                                Log.e(TAG, "onExecuteUpdates: ${e}")
                                e.printStackTrace()
                            }
                        }
                    })
                dialog.show()
            }

        }
    }

    private fun openDynamicFormScreen(id:String?=null) {
        var dfIntent=DynamicFormActivity.Companion.newIntent(baseActivity)
            .putExtra(AppConstants.Extra.EXTRA_FORM_TYPE, callToActions!!.name)
            .putExtra(AppConstants.Extra.EXTRA_FORM_ID, callToActions!!.dynamicFormId)
            .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task!!.taskId)
            .putExtra(AppConstants.Extra.EXTRA_CTA_ID, callToActions!!.id)
            .putExtra(
                AppConstants.Extra.EXTRA_IS_EDITABLE,
                callToActions!!.dynamicFormEditable
            )
        if(::foundWidgetItem.isInitialized&&id!=null){
            dfIntent.apply {
                putExtra(AppConstants.Extra.EXTRA_SCANNER_FIELD_NAME,foundWidgetItem.name)
            }
            dfIntent.apply {
                putExtra(AppConstants.Extra.EXTRA_SCANNER_FIELD_VALUE,id)
            }
        }
        startActivityForResult(
            dfIntent ,
            AppConstants.REQUEST_CODE_DYNAMIC_FORM
        )
    }


    override fun onClickMapIcon(task: Task?, position: Int) {
    }

    override fun onCallClick(task: Task?, position: Int) {
    }

    override fun onDetailsTaskClick(task: Task?) {

    }

    override fun onItemClick(task: Task?, position: Int) {

    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()
        initSocket()
        connectSocket(this)
    }

    /**
     * Method used to get the current location from the sdk.
     */
    private fun getCurrentLocation() {
        TrackThat.getCurrentLocation(object : TrackThatCallback() {
            override fun onSuccess(successResponse: SuccessResponse) {
                val loc = successResponse.responseObject as TrackthatLocation
                currentLocation = GeoCoordinates()
                currentLocation!!.latitude = loc.latitude
                currentLocation!!.longitude = loc.longitude
                Log.e(TAG, "getCurrentLocation(): onSuccess: $currentLocation")
            }

            override fun onError(errorResponse: ErrorResponse) {
//                if(currentLocation==null)
//                currentLocation = null
                Log.e(TAG, "onError: " + errorResponse.errorMessage)
            }
        })
    }

    fun hideBottomSheetFromOutSide(event: MotionEvent) {
        if (::sheetBehavior.isInitialized && sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            val outRect = Rect()
            bottomSheet.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()))
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    /**
     * This method is used to check all the conditions and if pass then
     * hit the api with cta current time and task id.
     */
    @Throws(java.lang.Exception::class)
    private fun checkConditionsAndRequestAPI(formData: TaskData?) {
        //perform action on conditions else if conditions are null then hit api with name
        if (callToActions != null) {
            //check for all the conditions and hit API
            if (callToActions!!.conditions != null) {
                val conditionList = callToActions!!.conditions
                if (conditionList!!.size > 0) {
                    for (i in conditionList.indices) {
                        val conditionItem = conditionList[i]
                        Log.e(TAG, "checkConditionsAndRequestAPI: $conditionItem")
                        if (AppConstants.GEOFENCE == conditionItem.type) {
                            var geoCoordinates: GeoCoordinates? = GeoCoordinates()
                            val locationType =
                                conditionItem.properties!![AppConstants.LOCATION_TYPE]
                            if (locationType != null) {
                                if (locationType.equals(AppConstants.START, ignoreCase = true) &&
                                    task!!.source != null
                                ) {
                                    geoCoordinates = task!!.source!!.location
                                } else if (locationType.equals(
                                        AppConstants.END,
                                        ignoreCase = true
                                    ) &&
                                    task!!.destination != null
                                ) {
                                    geoCoordinates = task!!.destination!!.location
                                }
                            }

                            //if any of the below condition is true then show message else hit api
                            if (currentLocation == null || currentLocation!!.latitude == 0.0 || currentLocation!!.longitude == 0.0 || geoCoordinates == null || geoCoordinates.latitude == 0.0 || geoCoordinates.longitude == 0.0) {
                                conditionItem.isPassed = false
                                TrackiToast.Message.showShort(
                                    baseActivity,
                                    AppConstants.MSG_NO_LOCATION
                                )
                                return
                            }
                            var radius = conditionItem.properties!![AppConstants.RADIUS]
                            if (radius == null) {
                                radius = "200"
                            }
                            Log.e(TAG, "radius: $radius")

                            if (!CommonUtils.isPointOutSideCircle(
                                    radius.toInt(),
                                    geoCoordinates.latitude, geoCoordinates.longitude,
                                    currentLocation!!.latitude, currentLocation!!.longitude
                                )
                            ) {
                                baseActivity.openDialougShowLocationError(
                                    "Not inside the " +
                                            Objects.requireNonNull<String>(locationType)
                                                .toLowerCase() + " location"
                                )
                                //                                TrackiToast.Message.showShort(getBaseActivity(), "Not inside the " +
//                                        Objects.requireNonNull(locationType).toLowerCase() + " location");
                                conditionItem.isPassed = false
                                return
                            }
                            conditionItem.isPassed = true
                        } else if (AppConstants.TIMED.equals(
                                conditionItem.type,
                                ignoreCase = true
                            )
                        ) {
                            conditionItem.isPassed = false
                            if (conditionItem.properties != null && conditionItem.properties!!.containsKey(
                                    AppConstants.TIME
                                )
                            ) {
                                val timeInSeconds =
                                    conditionItem.properties!![AppConstants.TIME]!!.toInt()
                                if (System.currentTimeMillis() - task!!.stageUpdatedAt >= timeInSeconds * 1000) {
                                    TrackiToast.Message.showShort(
                                        baseActivity,
                                        AppConstants.MSG_TIME_PASSED
                                    )
                                    return
                                }
                                conditionItem.isPassed = true
                            }
                        }
                    }
                }
                var isAllPassed = true
                for (i in conditionList.indices) {
                    if (!conditionList[i].isPassed) {
                        isAllPassed = false
                        break
                    }
                }
                Log.e(TAG, "isAllPassed: $isAllPassed")
                if (isAllPassed) {
                    //if(formData!=null)
                    if (NetworkUtils.isConnectedFast(baseActivity))
                        manageTracking(task!!.taskId!!, formData)
                    else {
                        //  showShort(baseActivity, getString(R.string.please_check_your_internet_connection_you_are_offline_now))

                    }
                }
            } else {
                if (NetworkUtils.isConnectedFast(baseActivity))
                    manageTracking(task!!.taskId!!, formData)
                else {
                    //  showShort(baseActivity, getString(R.string.please_check_your_internet_connection_you_are_offline_now))

                }
            }
        }
    }

    fun startTracking(taskId: String) {
        TrackThat.startTracking(taskId, true)
        preferencesHelper.isTrackingLiveTrip = true
        preferencesHelper.setActiveTaskId(taskId)
        preferencesHelper.setActiveTaskCategoryId(categoryId)
        playSoundStartTracking()
    }

    fun stopTracking() {
        TrackThat.stopTracking()
        preferencesHelper.isTrackingLiveTrip = false
        preferencesHelper.saveCurrentTask(null)
        preferencesHelper.setActiveTaskId("")
        preferencesHelper.setActiveTaskCategoryId("")
    }

    fun handleTracking(trackingState: TrackingState, taskId: String) {
        if (trackingState == TrackingState.ENABLED) {
            if (preferencesHelper.idleTripActive) {
                TrackThat.stopTracking()
                preferencesHelper.idleTripActive = false
                playSoundStopTracking()
            }


//            if (TrackThat.isTracking()) {
            if (preferencesHelper.isTrackingLiveTrip) {
                if (TrackThat.getCurrentTrackingId() != null && taskId != TrackThat.getCurrentTrackingId()) {
                    stopTracking()
                    startTracking(taskId)
                }

            } else {
                startTracking(taskId)
            }
        } else if (trackingState == TrackingState.DISABLED) {
            if (preferencesHelper.isTrackingLiveTrip && TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
                stopTracking()
                //   playSoundStopTracking()
                if (preferencesHelper.punchStatus) {
                    if (!preferencesHelper.idleTripActive) {
                        Log.e("AssignedFragment", "startIdealTrack=>")
                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                        val myWorkRequest: WorkRequest =
                            OneTimeWorkRequest.Builder(StartIdealTrackWork::class.java)
                                .setConstraints(constraints)
                                .setInitialDelay(60, TimeUnit.SECONDS)
                                .build()
                        val workManager = WorkManager.getInstance()
                        workManager.enqueue(myWorkRequest)

                    }
                }
            }

        }

    }

    /**
     * Manage tracking of particular task on the basis of tracking enum
     * and task id.
     *
     * @param taskId   task id of the user
     * @param formData form data if not null.
     */
    private fun manageTracking(taskId: String, formData: TaskData?) {
        //start the tracking if no ongoing task is running
        if (callToActions!!.tracking === Tracking.START) {
            if (preferencesHelper.idleTripActive) {
                TrackThat.stopTracking()
                preferencesHelper.idleTripActive = false
                playSoundStopTracking()
            }


//            if (TrackThat.isTracking()) {
            if (preferencesHelper.isTrackingLiveTrip) {
                TrackiToast.Message.showShort(baseActivity, AppConstants.MSG_ONGOING_TASK)
                return
            } else {
                TrackThat.startTracking(taskId, true)
                preferencesHelper.isTrackingLiveTrip = true
                preferencesHelper.setActiveTaskId(taskId)
                preferencesHelper.setActiveTaskCategoryId(categoryId)
                playSoundStartTracking()
            }
        } else if (callToActions!!.tracking === Tracking.END &&  /*TrackThat.isTracking()*/
            preferencesHelper.isTrackingLiveTrip
        ) {
            TrackThat.stopTracking()
            preferencesHelper.isTrackingLiveTrip = false
            preferencesHelper.saveCurrentTask(null)
            preferencesHelper.setActiveTaskId("")
            preferencesHelper.setActiveTaskCategoryId("")
            playSoundStopTracking()
            if (preferencesHelper.punchStatus) {
                if (!preferencesHelper.idleTripActive) {
                    Log.e("AssignedFragment", "startIdealTrack=>")
                    val constraints = Constraints.Builder()
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .setRequiresStorageNotLow(true)
                        .build()
                    val myWorkRequest: WorkRequest =
                        OneTimeWorkRequest.Builder(StartIdealTrackWork::class.java)
                            .setConstraints(constraints)
                            .setInitialDelay(60, TimeUnit.SECONDS)
                            .build()
                    val workManager = WorkManager.getInstance()
                    workManager.enqueue(myWorkRequest)

                }
            }
        }
        showLoading()
        val api = TrackiApplication.getApiMap()[ApiType.EXECUTE_UPDATE]
        val request = ExecuteUpdateRequest(
            taskId, ctaID!!,
            DateTimeUtil.getCurrentDateInMillis(), formData
        )
        val ctaLocation = CtaLocation()
        if (currentLocation != null) {
            ctaLocation.location = currentLocation
            ctaLocation.address = CommonUtils.getAddress(
                baseActivity,
                LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            )
            request.location = ctaLocation
        }
        if (callToActions!!.dynamicFormId != null)
            request.dfdId = dfdId
        if (callToActions!!.dynamicFormId != null)
            request.dfId = callToActions!!.dynamicFormId
        if(callToActions!!.targetInfo!!.target == TRAGETINFO.ASSIGN_EXECUTIVE){
            Log.e("assignExec","${callToActions?.targetInfo?.targetValues} - Values")

            if (callToActions?.targetInfo?.targetValues != null) {
                var rIds = ""
                val intent = Intent(context,UserListNewActivity::class.java)
                val listIds = callToActions?.targetInfo?.targetValues!!
                for (i in 0 until listIds.size) {
                    rIds += if (i>0)
                        ",${listIds[i]}"
                    else
                        listIds[i]
                }
                intent.putExtra("roleIds", rIds)

                intent.putExtra("request",request as ExecuteUpdateRequest)
                context?.startActivity(intent)
            }
            else {
                TrackiToast.Message.showShort(context,"No Id Found")
            }

        }else {
            mNewTaskViewModel.executeUpdates(httpManager, request, api!!)
        }
        dfId = null
        dfdId = null
    }

    open fun calculateCondition(): Boolean {
        var isAllPassed = true
        if (callToActions!!.conditions != null) {
            val conditionList = callToActions!!.conditions
            if (conditionList!!.size > 0) {
                for (i in conditionList!!.indices) {
                    val conditionItem = conditionList!![i]
                    Log.e(TAG, "checkConditionsAndRequestAPI: $conditionItem")
                    if (AppConstants.GEOFENCE == conditionItem.type) {
                        var geoCoordinates: GeoCoordinates? = GeoCoordinates()
                        val locationType = conditionItem.properties!![AppConstants.LOCATION_TYPE]
                        if (locationType != null) {
                            if (locationType.equals(AppConstants.START, ignoreCase = true) &&
                                task!!.source != null
                            ) {
                                geoCoordinates = task!!.source!!.location
                            } else if (locationType.equals(AppConstants.END, ignoreCase = true) &&
                                task!!.destination != null
                            ) {
                                geoCoordinates = task!!.destination!!.location
                            }
                        }
                        getCurrentLocation()
                        //if any of the below condition is true then show message else hit api
                        if (currentLocation == null || currentLocation!!.latitude == 0.0 || currentLocation!!.longitude == 0.0 || geoCoordinates == null || geoCoordinates.latitude == 0.0 || geoCoordinates.longitude == 0.0) {
                            conditionItem.isPassed = false
                            TrackiToast.Message.showShort(
                                baseActivity,
                                AppConstants.MSG_NO_LOCATION
                            )
                            return false
                        }
                        var radius = conditionItem.properties!![AppConstants.RADIUS]
                        if (radius == null) {
                            radius = "200"
                        }
                        if (!CommonUtils.isPointOutSideCircle(
                                radius.toInt(),
                                geoCoordinates.latitude, geoCoordinates.longitude,
                                currentLocation!!.latitude, currentLocation!!.longitude
                            )
                        ) {
                            baseActivity.openDialougShowLocationError(
                                "Not inside the " +
                                        Objects.requireNonNull(locationType)!!
                                            .toLowerCase() + " location"
                            )
                            conditionItem.isPassed = false
                            return false
                        }
                        //reaches here if user is inside the current location.
                        conditionItem.isPassed = true
                    } else if (AppConstants.TIMED.equals(conditionItem.type, ignoreCase = true)) {
                        conditionItem.isPassed = false
                        if (conditionItem.properties != null && conditionItem.properties!!.containsKey(
                                AppConstants.TIME
                            )
                        ) {
                            val timeInSeconds =
                                conditionItem.properties!![AppConstants.TIME]!!.toInt()
                            if (System.currentTimeMillis() - task!!.stageUpdatedAt >= timeInSeconds * 1000) {
                                TrackiToast.Message.showShort(
                                    baseActivity,
                                    AppConstants.MSG_TIME_PASSED
                                )
                                return false
                            }
                            conditionItem.isPassed = true
                        }
                    } else {
                        conditionItem.isPassed = true
                    }
                }
            }
            for (i in conditionList!!.indices) {
                if (!conditionList!![i].isPassed) {
                    isAllPassed = false
                    break
                }
            }
        }
        return isAllPassed
    }

    override fun handleExecuteUpdateResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, baseActivity)) {

            getTaskData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_DYNAMIC_FORM) {
            if (resultCode == Activity.RESULT_OK) {
                var taskData: TaskData? = null
                if (data != null && data.hasExtra(AppConstants.Extra.EXTRA_FORM_MAP)) {

                    val formData: DynamicFormMainData =
                        data.getParcelableExtra(AppConstants.Extra.EXTRA_FORM_MAP)!!
                    taskData = formData.taskData
                }
                if (data != null && data.hasExtra(AppConstants.DFID)) {
                    dfId = data.getStringExtra(AppConstants.DFID)
                }
                if (data != null && data.hasExtra(AppConstants.DFDID)) {
                    dfdId = data.getStringExtra(AppConstants.DFDID)
                }

                //after filling the form check other conditions for this task.
                if (callToActions != null) {
                    try {
                        checkConditionsAndRequestAPI(taskData!!)
                    } catch (e: java.lang.Exception) {
                        Log.e(TAG, "onActivityResult: $e")
                        e.printStackTrace()
                    }
                }
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_TAG_INVENTORY) {
            if (resultCode == Activity.RESULT_OK) {
                if (callToActions != null) {
                    try {
                        checkConditionsAndRequestAPI(null)
                    } catch (e: java.lang.Exception) {
                        Log.e(TAG, "onActivityResult: $e")
                        e.printStackTrace()
                    }
                }

            }
        } else if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                perFormCtaActionWithoutDialog()
            }
        }
        else if (requestCode == AppConstants.REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra("id")) {
                   var id=data.getStringExtra("id")
                    openDynamicFormScreen(id)
                }else{
                    openDynamicFormScreen()
                }

            }else{
                openDynamicFormScreen()
            }
        }
    }

    private fun perFormCtaActionWithoutDialog() {
//        if (callToActions!!.nextStageId != null && !callToActions!!.nextStageId!!.isEmpty()) {
//            stageId = callToActions!!.nextStageId
//        }
        if (callToActions!!.dynamicFormId != null && !callToActions!!.dynamicFormId!!.isEmpty()) {

            if(calculateCondition()) {
                startActivityForResult(
                    newIntent(baseActivity)
                        .putExtra(Extra.EXTRA_FORM_TYPE, callToActions!!.name)
                        .putExtra(Extra.EXTRA_FORM_ID, callToActions!!.dynamicFormId)
                        .putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                        .putExtra(Extra.EXTRA_CTA_ID, callToActions!!.id)
                        .putExtra(Extra.EXTRA_IS_EDITABLE, callToActions!!.dynamicFormEditable),
                    AppConstants.REQUEST_CODE_DYNAMIC_FORM
                )
            }
        } else {
            try {
                checkConditionsAndRequestAPI(null)
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "onExecuteUpdates: $e")
                e.printStackTrace()
            }
        }
    }

    override fun openForm(dataModel: StageHistoryData) {
        if (dataModel.dfdId != null) {

            /*isEditable = false
            isHideButton = true
            ctaId = dataModel.ctaId
            formId = dataModel.dfdId
            tcfId = dataModel.dfdId
            taskAction = dataModel.stage!!.name
            dynamicFragment = DynamicFragment.getInstance(formId!!, taskId!!, isEditable, tcfId, isHideButton,scannerFieldName,scannerFieldValue, ArrayList())
            //replaceFragment(dynamicFragment!!, formId)*/
            startActivityForResult(
                DynamicFormActivity.Companion.newIntent(baseActivity)
                    .putExtra(AppConstants.Extra.EXTRA_FORM_TYPE, dataModel.stage!!.name)
                    .putExtra(AppConstants.Extra.EXTRA_FORM_ID, dataModel.dfdId)
                    .putExtra(AppConstants.Extra.EXTRA_TCF_ID, dataModel.dfdId)
                    .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task!!.taskId)
                    .putExtra(AppConstants.Extra.EXTRA_CTA_ID, dataModel.ctaId)
                    .putExtra(AppConstants.Extra.EXTRA_IS_EDITABLE, false)
                    .putExtra(AppConstants.Extra.HIDE_BUTTON, true),
                AppConstants.REQUEST_CODE_DYNAMIC_FORM
            )
        } else {
            TrackiToast.Message.showShort(requireContext(), "form not available")
        }
    }

    fun hitGetTaskDataAPI() {
        /*formId = dataForm?.dfdId
        Log.e("formId","formId = $formId")
        if (formId != null && formId!!.isNotEmpty()) {
            showLoading()
            var data = GetTaskDataRequest()
            data.dfId = formId
            if (dynamicFormsNew != null && dynamicFormsNew!!.version != null) {
                data.dfVersion = Integer.valueOf(dynamicFormsNew!!.version!!)
            }
            data.taskId = taskId
            mDynamicViewModel!!.getTaskData(httpManager, data)

        }*/
    }

    override fun onSocketResponse(eventName: Int, baseModel: BaseModel?) {
        baseActivity.runOnUiThread {
            Log.e(TAG, "onSocketResponse() called")
            Log.e(TAG, "eventName => $eventName")
            Log.e(TAG, "baseModel => $baseModel")
            hideLoading()
            when (eventName) {
                4 -> {

                    val openCreateRoomModel = baseModel as OpenCreateRoomModel
                    val messageList = openCreateRoomModel.messages
                    roomId = openCreateRoomModel.roomId
                    var roomName = openCreateRoomModel.roomName
                    if (openCreateRoomModel.connections != null) {
                        val list = ArrayList<String>()

                        list.addAll(openCreateRoomModel.connections!!)
//                        startActivity(ChatActivity.newIntent(baseActivity)
//                                .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, list)
//                                .putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
//                                .putExtra(AppConstants.Extra.EXTRA_IS_CREATE_ROOM, false)
//                                .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId))
                        startActivity(
                            MessagesActivity.newIntent(baseActivity)
                                .putExtra(AppConstants.Extra.FROM, Extra.TASK_DETAILS)
                                .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, listBuddy)
                                .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId)
                        )

                    }


                }
                1 -> {
                    //connection hashMap
                    val connectionInfoList = ArrayList<ConnectionInfo>()
                    //buddy message hashMap
                    val buddyList = ArrayList<Buddy>()
                    val map = HashMap<String, String>()
                    val connectionResponse = baseModel as ConnectionResponse
                    // check room-ids if not null then get the hashMap from buddy hashMap and populate into the
                    for (key in connectionResponse.connections?.keys!!) {
                        val value = connectionResponse.connections!![key] as ConnectionInfo
                        if (value.roomId != null) {
                            map[value.connectionId!!] = value.roomId!!
                            connectionInfoList.add(value)
                            println("connectionsId: ${value.connectionId} roomId: ${value.roomId}")
                        }
                    }
                    var list = ArrayList<String>()
                    for (data in listBuddy)
                        list.add(data.buddyId!!)
                    webSocketManager?.openCreateRoom(list, roomId, true, 10)

                }
                2 -> {
                    CommonUtils.showLogMessage("e", "heartbeatsend", "heartbeatsend")
                    if (roomId != null)
                        webSocketManager?.sendHeartBeat("2:", roomId)
                }
            }
        }
    }

    override fun onOpen() {
        baseActivity.runOnUiThread {
            hideLoading()
//            if(buddyId!=null){
//                webSocketManager!!.connectPacket(buddyId)
//            }


        }
    }

    override fun closed() {
        //   Log.e("conection close call", "close call")
    }

    private fun playSoundStartTracking() {
        if (preferencesHelper.voiceAlertsTracking) {
            if (baseActivity != null) {
                mediaPlayer = MediaPlayer.create(baseActivity, R.raw.tracking_started)
                mediaPlayer!!.start()
                Handler().postDelayed({
                    if (mediaPlayer != null) {
                        mediaPlayer!!.stop()
                    }
                }, 2000)
            }
        }
    }

    private fun playSoundStopTracking() {
        if (preferencesHelper.voiceAlertsTracking) {
            if (baseActivity != null) {
                mediaPlayer = MediaPlayer.create(baseActivity, R.raw.tracking_stopped)
                mediaPlayer!!.start()
                Handler().postDelayed({
                    if (mediaPlayer != null) {
                        mediaPlayer!!.stop()
                    }
                }, 2000)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mediaPlayer = null
    }

    fun openRoom() {
        if (taskId != null) {
            var connectionIds = ""
            if (preferencesHelper.userDetail != null && preferencesHelper.userDetail!!.userId != null) {
                var buddy = Buddy()
                buddy.buddyId = preferencesHelper.userDetail!!.userId
                if (listBuddy.contains(buddy)) {
                    listBuddy.remove(buddy)
                }
            }
            for (i in listBuddy?.indices!!) {
                connectionIds += listBuddy!![i].buddyId

                var p = i
                p += 1
                //if array hashMap contains next item then add comma into the string
                if (listBuddy!!.size > p) {
                    connectionIds += ","
                }
            }
            if (webSocketManager.isConnected && webSocketManager.isOpen) {
                webSocketManager!!.connectPacket(connectionIds)
            } else {
                startActivity(
                    MessagesActivity.newIntent(baseActivity)
                        .putExtra(AppConstants.Extra.FROM, Extra.TASK_DETAILS)
                        .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, listBuddy)
                        .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId)
                )
            }
            //   webSocketManager?.openCreateRoom(listBuddy, roomId, true, 10)
        }
    }

     override fun onUploadPic(position: Int, formData: FormData?) {

     }

     override fun uploadCameraImage(adapterPosition: Int) {

     }

     override fun onProcessClick(formData: FormData?) {

     }

     override fun getDropdownItems(position: Int, target: String?, rollId: String?) {

     }

     override fun openVidCamera(pos: Int, mBinding: ItemDynamicFormVideoBinding?, maxLength: Int) {

     }

     override fun onVeriFyOtpButtonClick(formData: FormData?, mobile: String?) {

     }

     override fun openPlacePicker(position: Int, formData: FormData?) {

     }

     override fun openScanner(position: Int, formData: FormData?) {

     }

     override fun sendButtonInstance(button: Button?, isEditable: Boolean) {

     }

 }