package com.rocketflyer.rocketflow.ui.taskdetails

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AcceptRejectRequest
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityTaskDetailBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.chat.ChatActivity
import com.tracki.ui.common.CustomInfoWindowAdapter
import com.tracki.ui.custom.EventDialogFragment
import com.tracki.ui.custom.socket.*
import com.tracki.ui.dynamicformpreview.FormPreviewActivity
import com.tracki.ui.sharetrip.ShareTripActivity
import com.tracki.utils.*
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.util.EventType
import com.trackthat.lib.internal.util.TripState
import com.trackthat.lib.locationobserver.LocationObserver
import com.trackthat.lib.models.Events
import com.trackthat.lib.models.LocationData
import kotlinx.android.synthetic.main.activity_task_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_task_detail.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import javax.inject.Inject


/**
 * Created by rahul on 16/10/18
 *<p>
 * Class used to handle the details of live and completed tasks.
 */
class TaskDetailActivity : BaseActivity<ActivityTaskDetailBinding, TaskDetailViewModel>(),
        TaskDetailNavigator, OnMapReadyCallback, LocationObserver,
        GoogleMap.OnMarkerClickListener, Runnable, WebSocketManager.SocketListener, TaskAlterEventAdapter.OnAlertClick, View.OnClickListener {

    private var dialogAllAlert: Dialog? = null
    private var eventList: ArrayList<AlertEvent>? = null
    private var name: String? = null
    private var buddyId: String? = null

    @Inject
    lateinit var mTaskDetailViewModel: TaskDetailViewModel

    @Inject
    lateinit var httpManager: HttpManager
    var distinationMarker = false;

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private var mobile: String? = null
    private var eventDialog: EventDialogFragment? = null
    private val tag = TaskDetailActivity::class.java.simpleName
    private var api: Api? = null
    private var polyLine: Polyline? = null
    private lateinit var mActivityTaskDetailBinding: ActivityTaskDetailBinding
    private var movingMarker: Marker? = null
    private lateinit var shake: Animation
    private var endTaskData: List<DynamicFormData>? = null
    private var startTaskData: List<DynamicFormData>? = null

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_task_detail
    override fun getViewModel() = mTaskDetailViewModel

    private lateinit var ivNavigationIcon: ImageView
    private lateinit var toolbarTitle: TextView

    private lateinit var tvSOS: TextView
    private lateinit var cardViewTaskDetail: CardView

    private lateinit var tvDistance: TextView
    private lateinit var tvAvgSpeed: TextView
    private lateinit var tvDiverName: TextView
    private lateinit var tvDriverShortCode: TextView
    private lateinit var tvShortCode: TextView
    private lateinit var tvTaskAssignee: TextView
    private lateinit var tvTaskCreatedDate: TextView
    private lateinit var tvTaskEndLocation: TextView
    private lateinit var tvTaskEndTime: TextView
    private lateinit var tvTaskName: TextView
    private lateinit var tvTaskStartLocation: TextView
    private lateinit var tvTaskStartTime: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvFleetDetail: TextView
    private lateinit var ivShare: ImageView
    private lateinit var buttonDetail: Button
    private lateinit var bottomSheetTimeline: View

    private lateinit var mMap: GoogleMap
    private lateinit var viewAutoStart: TextView
    private var taskResponse: TaskResponse? = null
    private var mapFragment: SupportMapFragment? = null


    private var osObject: ArrayList<Any?>? = null
    private var ostObject: ArrayList<Any?>? = null
    private var hcObject: ArrayList<Any?>? = null
    private var hbObject: ArrayList<Any?>? = null
    private var haObject: ArrayList<Any?>? = null
    private var mapList = ArrayList<Any?>()
    private var hashMap: HashMap<String, List<DynamicFormData>>? = null
    private var isCameraZoomEventDone = false
    private var locationSynHandler: Handler? = null
    var mStopHandler = false

    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityTaskDetailBinding.llMain, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityTaskDetailBinding = viewDataBinding
        mTaskDetailViewModel.navigator = this
        setUp()
        buttonDetail = mActivityTaskDetailBinding.bottomSheetTrip.buttonDetail
        buttonDetail.setOnClickListener {
            if (hashMap != null && hashMap!!.isNotEmpty()) {
                startActivity(FormPreviewActivity.newIntent(this@TaskDetailActivity)
                        .putExtra(AppConstants.Extra.EXTRA_FORM_DETAILS, hashMap))
            }

        }



        locationSynHandler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                // do your stuff - don't create a new runnable here!
                updateLocationPercentageTask()
                if (!mStopHandler) {
                    locationSynHandler!!.postDelayed(this, 100)
                }
            }
        }


        locationSynHandler!!.post(runnable)
    }

    override fun onResume() {
        super.onResume()
        mTaskDetailViewModel.onResume(this@TaskDetailActivity)
        initSocket();
        connectSocket(this)
        if (intent != null) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
                taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                if (taskId == "") {
                    finish()
                }
                getTaskByApi()
            }

        }

        if (TrackThat.checkIfTripIdIsValid(taskId!!)) {
            if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
                TrackThat.registerLocationObservable(this)
                getLiveEvent()
                var tripStatsCurrent = TrackThat.getCurrentTripStatistics()
                var jsonConverter = JSONConverter<com.trackthat.lib.models.TripsStatistics>();

                var jsonConverter2 = JSONConverter<TripsStatistics>();
                var tripStatsStr = jsonConverter.objectToJson(tripStatsCurrent)
                CommonUtils.showLogMessage("e", "trip_data", tripStatsCurrent.toString())
                var tripStats: TripsStatistics = jsonConverter2.jsonToObject(tripStatsStr, TripsStatistics::class.java)
                setStats(tripStats.distanceInMeter / 1000, tripStats.tripDurationInMinute, tripStats.maxSpeed)
            }

        }

    }

    private fun getTaskByApi() {

        showLoading()
        api = TrackiApplication.getApiMap()[ApiType.GET_TASK_BY_ID]
        mTaskDetailViewModel.getTaskById(httpManager, AcceptRejectRequest(taskId!!), api)
    }

    private fun getLiveEvent() {
        if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
            CommonUtils.showLogMessage("e", "getLiveEvent call", "" + TrackThat.checkTripIsComplete(taskId!!))
            //  TrackiToast.Message.showShort(this,"getLiveEvent call")

            if (!TrackThat.checkTripIsComplete(taskId!!)) {
                val eventList = TrackThat.getEvent(taskId!!)
                if(eventList!=null&&!eventList.isEmpty()){
                    var tripStats = TripsStatistics()
                    var task = Task()
                    var secondTime: Long = 0
                    var detail: List<LocationData>? = TrackThat.getTripLocationById(taskId!!)

                    var builder = LatLngBounds.builder()
                    if (detail != null && detail.isNotEmpty()) {
                        var sortedLocationList = detail.sortedBy { data -> data.time }
                        var maxSpeedEvent = sortedLocationList.maxByOrNull { data -> data.speed }
                        if (maxSpeedEvent != null)
                            tripStats.maxSpeed = CommonUtils.metersPerSecToKmPerHr(maxSpeedEvent.speed)
                        if (sortedLocationList.size > 1) {

                            tripStats.distance = calculateDistance(sortedLocationList)
                            tripStats.distanceInMeter = calculateDistance(sortedLocationList) / 1000.toDouble()
                        }

                        val polyLine = mTaskDetailViewModel.extractSdkPolyline(detail!!)
                        if (this::mMap.isInitialized)
                            mMap.addPolyline(polyLine)
                        //   isCameraZoomEventDone=false;

                        var initlatlong = LatLng(detail[0].geoCoordinates?.latitude!!, detail[0].geoCoordinates?.longitude!!)
                        builder.include(initlatlong)
                        var startPlace = Place()
                        startPlace.address = CommonUtils.getAddress(this@TaskDetailActivity, LatLng(detail[0].geoCoordinates?.latitude!!, detail[0].geoCoordinates?.longitude!!))
                        var startGetCoards = GeoCoordinates()
                        startGetCoards.latitude = detail[0].geoCoordinates?.latitude!!
                        startGetCoards.longitude = detail[0].geoCoordinates?.longitude!!
                        startPlace.location = startGetCoards
                        task.source = startPlace
                        var finnallatlong = LatLng(detail[detail.size - 1].geoCoordinates?.latitude!!, detail[detail.size - 1].geoCoordinates?.longitude!!)
                        builder.include(finnallatlong)
                        var endPlace = Place()
                        endPlace.address = CommonUtils.getAddress(this@TaskDetailActivity, LatLng(detail[detail.size - 1].geoCoordinates?.latitude!!, detail[detail.size - 1].geoCoordinates?.longitude!!))
                        var endGetCoards = GeoCoordinates()
                        endGetCoards.latitude = detail[detail.size - 1].geoCoordinates?.latitude!!
                        endGetCoards.longitude = detail[detail.size - 1].geoCoordinates?.longitude!!
                        endPlace.location = endGetCoards
                        task.destination = endPlace
                        try {
                            val padding = 250 //offset from edges of the map in pixels
                            val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding)
                            if (this::mMap.isInitialized)
                                mMap.animateCamera(cu)
                        } catch (e: java.lang.Exception) {

                        }

                        isCameraZoomEventDone = true;
                        setMarkers(taskDetail = task)


                    }
                    if (eventList != null && eventList.isNotEmpty()) {
                        var anotherList = eventList.filter { data -> data.eventType.name == EventType.ACTION_LOCATION_CHANGE.name }.distinctBy { data -> data.startCoordinates.latitude }.distinctBy { data -> data.startCoordinates.longitude }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            eventList.removeIf(Predicate { data ->
                                data.eventType.name == EventType.ACTION_LOCATION_CHANGE.name
                            })
                        } else {
                            var locationChangeList = eventList.filter { data -> data.eventType.name == EventType.ACTION_LOCATION_CHANGE.name }
                            eventList.removeAll(locationChangeList)
                        }

                        if (anotherList.isNotEmpty())
                            eventList.addAll(anotherList)

                        if (preferencesHelper.onTripOverStopping != null) {

                            var allOverStoppingList = eventList.filter { data -> data.eventType.name == EventType.OVER_STOPPING.name }.distinctBy { data -> data.startCoordinates.latitude }.distinctBy { data -> data.startCoordinates.longitude }
                            if (allOverStoppingList.isNotEmpty()) {
                                var overStopping: MutableList<Events> = ArrayList()
                                var overstoppingConfig = preferencesHelper.onTripOverStopping
                                for (event in allOverStoppingList) {
                                    var diff = event.endTime - event.startTime
                                    var overStoppingMinute = TimeUnit.MILLISECONDS.toMinutes(diff).toInt()
                                    var isPointOutSide = CommonUtils.isPointOutSideCircle(overstoppingConfig.distanceinMeter, event.startCoordinates.latitude, event.startCoordinates.longitude,
                                            event.endCoordinates.latitude, event.endCoordinates.longitude)
                                    if (overStoppingMinute >= overstoppingConfig.timeInMinute && !isPointOutSide) {
                                        overStopping.add(event)
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    eventList.removeIf(Predicate { data ->
                                        data.eventType.name == EventType.OVER_STOPPING.name
                                    })
                                } else {
                                    var overStoppingList = eventList.filter { data -> data.eventType.name == EventType.OVER_STOPPING.name }
                                    eventList.removeAll(overStoppingList)
                                }
                                if (overStopping.isNotEmpty()) {
                                    eventList.addAll(overStopping)
                                }
                            }

                        }
                        var sortedList = eventList.sortedBy { data -> data.startTime }
                        if (preferencesHelper.isTrackingLiveTrip) {
                            secondTime = System.currentTimeMillis()
                        } else {
                            var allEventList: MutableList<Events>? = TrackThat.getEvent(taskId!!)
                            if (allEventList != null) {

                                var completedEventList = allEventList.filter { data -> data.tripState.name == TripState.COMPLETED.name }
                                var latestCompleted = completedEventList.sortedByDescending { data -> data.startTime }
                                var jsonConvertor = JSONConverter<List<Events>>()
                                var json = jsonConvertor.objectToJson(latestCompleted);
                                CommonUtils.showLogMessage("e", "event list ", "" + json)
                                if (latestCompleted.isNotEmpty())
                                    secondTime = latestCompleted[0].startTime
                                else {

                                    if (preferencesHelper.lastPunchOutTime != 0L)
                                        secondTime = preferencesHelper.lastPunchOutTime
                                    else {
                                        var calculateStartTime = allEventList.sortedByDescending { data -> data.startTime }
                                        secondTime = calculateStartTime[0].startTime
                                    }
                                }
                            }

                        }
                        var firstTime = sortedList[0].startTime
                        var diff = (secondTime - firstTime)

                        tripStats.tripDurationInMinute = TimeUnit.MILLISECONDS.toMinutes(diff).toInt();
                        // fabLayoutAlerts.visibility = View.VISIBLE
                        // setStats(tripStats.distanceInMeter, tripStats.tripDurationInMinute, tripStats.maxSpeed)
                        llAlert.visibility = View.VISIBLE

                        var listEvent = HashMap<String, Int>()
                        for (event in sortedList) {
                            if (listEvent.containsKey(event.eventType.name)) {
                                listEvent[event.eventType.name] = listEvent[event.eventType.name]!! + 1
                            } else {
                                listEvent.put(event.eventType.name, 1)
                            }
                            listEvent[event.eventType.name]
                        }

                        tripStats.events = listEvent
                    }


                    var listevent = ArrayList<Event>()
                    for (eventData in eventList) {
                        var event = Event()
                        event.eventStartTime = eventData.startTime
                        event.eventEndTime = eventData.endTime
                        event.eventName = eventData.eventType.name
                        var eventStartLocation = GeoCoordinates();
                        eventStartLocation.longitude = eventData.startCoordinates.longitude
                        eventStartLocation.latitude = eventData.startCoordinates.latitude
                        event.from = eventStartLocation
                        var eventEndLocation = GeoCoordinates();
                        eventEndLocation.longitude = eventData.endCoordinates.longitude
                        eventEndLocation.latitude = eventData.endCoordinates.latitude
                        event.to = eventEndLocation
                        event.speed = eventData.speed
                        event.tripId = taskId
                        listevent.add(event)

                    }

                    mTaskDetailViewModel.isolateEvents(listevent, this@TaskDetailActivity, preferencesHelper)
                    setEvents(tripStats)
                }

            }
        }
    }

    private fun useLocalDbEvent() {
        val eventList = TrackThat.getEvent(taskId!!)
        var tripStats = TripsStatistics()
        var listevent = ArrayList<Event>()
        var listEvent = HashMap<String, Int>()
        for (event in eventList) {
            if (listEvent.containsKey(event.eventType.name)) {
                listEvent[event.eventType.name] = listEvent[event.eventType.name]!! + 1
            } else {
                listEvent.put(event.eventType.name, 1)
            }
            listEvent[event.eventType.name]
        }

        tripStats.events = listEvent
        for (eventData in eventList) {
            var event = Event()
            event.eventStartTime = eventData.startTime
            event.eventEndTime = eventData.endTime
            event.eventName = eventData.eventType.name
            var eventStartLocation = GeoCoordinates();
            eventStartLocation.longitude = eventData.startCoordinates.longitude
            eventStartLocation.latitude = eventData.startCoordinates.latitude
            event.from = eventStartLocation
            var eventEndLocation = GeoCoordinates();
            eventEndLocation.longitude = eventData.endCoordinates.longitude
            eventEndLocation.latitude = eventData.endCoordinates.latitude
            event.to = eventEndLocation
            event.speed = eventData.speed
            event.tripId = taskId
            listevent.add(event)

        }
        mTaskDetailViewModel.isolateEvents(listevent, this@TaskDetailActivity, preferencesHelper)
        setEvents(tripStats)
    }

    private fun calculateDistance(latLng: List<LocationData>): Double {
        var distance = 0.0;
        for (i in latLng.indices) {
            if (i >= latLng.size - 1)
                break
            else {
                distance += calculateDistance(latLng[i].geoCoordinates.latitude, latLng[i].geoCoordinates.longitude, latLng[i + 1].geoCoordinates.latitude, latLng.get(i + 1).geoCoordinates.longitude)
            }
        }

        return distance
    }


    private fun calculateDistance(initialLat: Double, initialLong: Double,
                                  finalLat: Double, finalLong: Double): Double {
        val R = 6371 // km
        val dLat = toRadians(finalLat - initialLat)
        val dLon = toRadians(finalLong - initialLong)
        val lat1 = toRadians(initialLat)
        val lat2 = toRadians(finalLat)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val dd = String.format("%.4f", R * c)
        val d = dd.toDouble()
        return d * 1000
    }

    private fun toRadians(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    override fun onPause() {
        super.onPause()
        mTaskDetailViewModel.onPause(this@TaskDetailActivity)
        if (handler != null) {
            handler?.removeCallbacks(this)
        }
        if (TrackThat.checkIfTripIdIsValid(taskId!!)) {
            if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId())
                TrackThat.unregisterLocationObservable(this)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            handler?.removeCallbacks(this)
            handler = null
            mTaskDetailViewModel.onDestroy()
            if (locationSynHandler != null) {
                mStopHandler = true
                locationSynHandler!!.removeCallbacks(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside onDestroy(): $e")
        }
       // webSocketManager = null
    }

    override fun onMapReady(mapboxMap: GoogleMap?) {
        this.mMap = mapboxMap!!
        CommonUtils.changeGoogleLogoPosition(mapFragment!!, resources.getDimension(R.dimen.dimen_120).toInt(), mMap)
        //this is for max zoom in
//        this.mMap.setMaxZoomPreference(5f)
        //this is for max zoom out
//        this.mMap.setMinZoomPreference(20f)
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        mMap.setOnMarkerClickListener(this)
        if (TrackThat.checkIfTripIdIsValid(taskId!!)) {
            if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId())
                getLiveEvent()
        }

    }

    /**
     * Called when the user clicks a marker.
     *
     * @param marker marker that is clicked
     */
    override fun onMarkerClick(marker: Marker?): Boolean {
        return if (marker != null && marker.tag != null) {
            // Retrieve the data from the marker.
            val markerTag = marker.tag as String

            markerTag != MARKER_TAG
        } else {
            true
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        try {
            hideLoading()
            if (CommonUtils.handleResponse(callback, error, result, this)) {
                taskResponse = Gson().fromJson("$result", TaskResponse::class.java)
                setTaskDetails()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside handleResponse(): $e")
        }
    }

    var taskId: String? = null

    private fun setUp() {
        try {
            mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)


            llAlert.setOnClickListener(this)


            bottomSheetTimeline = mActivityTaskDetailBinding.bottomSheetTimeline
            ivNavigationIcon = findViewById(R.id.ivNavigationIcon)

            toolbarTitle = findViewById(R.id.toolbarTitleNew)
            val rootView = mActivityTaskDetailBinding.root
            tvSOS = rootView.findViewById(R.id.tvSOS)
            cardViewTaskDetail = rootView.findViewById(R.id.cardViewTaskDetail)

            viewAutoStart = rootView.findViewById(R.id.viewAutoStart)
            tvDistance = rootView.findViewById(R.id.tvDistance)
            tvTime = rootView.findViewById(R.id.tvTime)
            tvAvgSpeed = rootView.findViewById(R.id.tvAvgSpeed)
            tvDriverShortCode = rootView.findViewById(R.id.tvDriverShortCode)
            tvDiverName = rootView.findViewById(R.id.tvDiverName)
            tvShortCode = rootView.findViewById(R.id.tvShortCode)
            tvTaskAssignee = rootView.findViewById(R.id.tvTaskAssignee)
            tvTaskCreatedDate = rootView.findViewById(R.id.tvTaskCreatedDate)
            tvTaskName = rootView.findViewById(R.id.tvTaskName)
            tvTaskStartTime = rootView.findViewById(R.id.tvTaskStartTime)
            tvTaskEndTime = rootView.findViewById(R.id.tvTaskEndTime)
            tvTaskStartLocation = rootView.findViewById(R.id.tvTaskStartLocation)
            tvTaskEndLocation = rootView.findViewById(R.id.tvTaskEndLocation)
            tvFleetDetail = rootView.findViewById(R.id.tvFleetDetail)
            ivShare = rootView.findViewById(R.id.ivShare)
//            btnDetail = rootView.findViewById(R.id.btnDetail)

            toolbarTitle.visibility = View.VISIBLE

            ivNavigationIcon.setOnClickListener {
                onBackPressed()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside setUp(): ${e.message}")
        }
    }

    private var handler: Handler? = null

    private fun setTaskDetails() {
        //  try {
        val detail = taskResponse?.taskDetail
        if (detail != null) {
            if (detail!!.assigneeDetail != null && detail!!.assigneeDetail!!.profileImage != null) {
                Glide.with(this).load(detail!!.assigneeDetail!!.profileImage).placeholder(R.drawable.ic_social_media).apply(RequestOptions().circleCrop()).error(R.drawable.ic_social_media)
                        .into(ivVendor)
            }
            if (detail!!.assigneeDetail != null && detail!!.assigneeDetail!!.name != null)
                tvName.text = detail!!.assigneeDetail!!.name
            if (detail!!.assigneeDetail != null && detail!!.assigneeDetail!!.name != null)
                tvName.text = detail!!.assigneeDetail!!.name
            if (detail!!.assigneeDetail != null && detail!!.assigneeDetail!!.mobile != null) {
                tvMobile.text = detail!!.assigneeDetail!!.mobile
                mobile = detail!!.assigneeDetail!!.mobile
            }
            if (detail!!.assigneeDetail != null) {
                ratingBar.rating = 4.0f

            }
            if (preferencesHelper.userDetail != null &&preferencesHelper.userDetail!!.userId!=null&& detail!!.assigneeDetail!= null&&detail.assigneeDetail!!.buddyId!=null) {
                if (preferencesHelper.userDetail!!.userId.equals(detail.assigneeDetail!!.buddyId)) {
                    rl_chat.visibility = View.GONE
                }else{
                    rl_chat.visibility = View.VISIBLE
                }
            }
            rl_chat.setOnClickListener(View.OnClickListener {
                buddyId = detail!!.assigneeDetail!!.buddyId;
                name = detail!!.assigneeDetail!!.name;
                try {
                    if (webSocketManager != null && webSocketManager.isConnected) {
                        showLoading()
                        webSocketManager.connectPacket(buddyId)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            })


            mTaskDetailViewModel.setContact(detail)
            if (detail.autoCreated) {
                viewAutoStart.text = getString(R.string.autostart)
//                    viewAutoStart.visibility = View.VISIBLE
            } else {
                viewAutoStart.text = "Assigned By: ${detail.assigneeDetail?.name}"
//                    viewAutoStart.visibility = View.GONE
            }

            if (detail.fleetDetail != null) {
                tvFleetDetail.text = "Fleet: ${detail.fleetDetail?.fleetName} | ${detail.fleetDetail?.inventoryId}"
                tvFleetDetail.visibility = View.VISIBLE
            } else {
                tvFleetDetail.visibility = View.GONE
            }
            buttonDetail.visibility = View.GONE
            this.startTaskData = null
            this.endTaskData = null

            hashMap = HashMap()
            if (detail.taskData != null) {
                for (i in detail.taskData?.indices!!) {
                    val data = detail.taskData!![i]
                    hashMap!![data.ctaId!!] = data.taskData!!
                }
            }


            if (hashMap!!.isNotEmpty()) {
                buttonDetail.visibility = View.VISIBLE
            }

            tvSOS.visibility = View.GONE
            if (TrackThat.isTracking())
                ivShare.visibility = View.VISIBLE
            else {
                ivShare.visibility = View.GONE
            }

            var distance = 0.0
            var maxSpeed = 0
            var timed = 0
            ivShare.setOnClickListener {
                startActivity(ShareTripActivity.newIntent(this)
                        .putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId))
            }
            llAlert.visibility = View.VISIBLE
            if (handler == null) {
                handler = Handler()
                handler?.post(this)
            }

            detail.tripData.let { tData ->
                tData?.locations.let { loc ->
                    loc?.let { l ->
                        val polyLine = mTaskDetailViewModel.extractPolyline(l)
                        mMap.addPolyline(polyLine)
                        //  isCameraZoomEventDone=false;
                        if (l.isNotEmpty()) {
                            var builder = LatLngBounds.builder();
                            var initlatlong = LatLng(l[0].geoCoordinates?.latitude!!, l[0].geoCoordinates?.longitude!!)
                            builder.include(initlatlong)
                            var finnallatlong = LatLng(l[l.size - 1].geoCoordinates?.latitude!!, l[l.size - 1].geoCoordinates?.longitude!!)
                            builder.include(finnallatlong)
                            val padding = 250 //offset from edges of the map in pixels
                            val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding)
                            mMap.animateCamera(cu)
                            isCameraZoomEventDone = true
                        }

                    }
                }

            }

            setMarkers(detail)
            detail.tripStatistics?.let { stats ->
                stats?.distanceInMeter?.run { distance = stats.distanceInMeter / 1000 }
                stats?.maxSpeed?.run { maxSpeed = stats.maxSpeed }
                stats?.tripDurationInMinute?.run { timed = stats.tripDurationInMinute }
                stats?.let {
//                    if (eventList != null) {
//                        var listEvent = HashMap<String, Int>()
//
//                        for (event in eventList!!) {
//                            if (listEvent.containsKey(event.name)) {
//                                listEvent[event.name!!] = listEvent[event.name!!]!! + 1
//                            } else {
//                                listEvent.put(event.name!!, 1)
//                            }
//                            listEvent[event.name!!]
//                        }
//
//                        stats.events = listEvent
//
//                        performMakeList(stats.events!!)
//
//                    }
                    val eventList = TrackThat.getEvent(taskId!!)
                    if (eventList != null && eventList.isEmpty()) {
                        useLocalDbEvent()
                    } else {
                        //  performMakeList(stats.events!!)
                        if (detail.tripData!!.events != null && !detail.tripData!!.events!!.isEmpty())
                            mTaskDetailViewModel.isolateEvents(detail.tripData!!.events, this@TaskDetailActivity, preferencesHelper)
                        else if(stats.events!=null)
                            performMakeList(stats.events!!)

                        setEvents(stats)
                    }


                }
            }


            val time = DateTimeUtil.getParsedDate(detail.createdAt) + " at " +
                    DateTimeUtil.getParsedTime(detail.createdAt)
            setStats(distance, timed, maxSpeed)

            if (detail.buddyDetail?.name == null) {
                tvDiverName.text = "${detail.assigneeDetail?.name}"
            } else {
                tvDiverName.text = "${detail.buddyDetail?.name}"
            }

            if (detail.buddyDetail?.shortCode == null) {
                tvDriverShortCode.text = "${detail.assigneeDetail?.shortCode}"
            } else {
                tvDriverShortCode.text = "${detail.buddyDetail?.shortCode}"
            }
            tvShortCode.text = "${detail.assigneeDetail?.shortCode}"
            val mess: String = if (intent != null && intent.hasExtra(AppConstants.Extra.EXTRA_FROM_IHAVE)) {
                "You have assigned a task to " + detail.buddyDetail?.name
            } else {
                detail.assigneeDetail?.name + " has assign you a task"
            }
            tvTaskAssignee.text = mess
            tvTaskCreatedDate.text = time
            val s = "Task Name: ${detail.taskName}"
            tvTaskName.text = CommonUtils.setCustomFontTypeSpan(this, s, 12, s.length, R.font.campton_semi_bold)
            tvTaskStartTime.text = time
            tvTaskEndTime.text = "${DateTimeUtil.getParsedDate(detail.endTime)} at" + " ${DateTimeUtil.getParsedTime(detail.endTime)}"
            tvTaskStartLocation.text = detail.source?.address
            tvTaskEndLocation.text = detail.destination?.address

            toolbarTitle.text = time



            if (taskResponse != null &&
                    detail.status == TaskStatus.LIVE &&
                    (taskResponse?.taskDetail?.taskId!! == TrackThat.getCurrentTrackingId())) {
                Log.i(tag, "My trip (${taskResponse?.taskDetail?.taskId}) is: LIVE")
                setLiveTripData()
                mTaskDetailViewModel.registerLocationObserver()
            } else {
                Log.i(tag, "TripId (${taskResponse?.taskDetail?.taskId}) is: LIVE")
            }
//            if(detail.currentStage!=null){
//                if(detail.currentStage!!.terminal!!){
//                    val snappedPoints: List<LatLng> = ArrayList()
//                    val sourcePoints = ArrayList<LatLng>()
//                    if(detail.tripData!!.locations!=null){
//                        sourcePoints.add(0,LatLng(detail.tripData!!.locations!![0].geoCoordinates?.latitude!!, detail.tripData!!.locations!![0].geoCoordinates?.longitude!!))
//                        var jump=0
//                        var size =detail.tripData!!.locations!!.size
//                        if(size<100){
//                            jump=1
//                        }else{
//                            jump=2
//                        }
//                        for(i in detail.tripData!!.locations!!.indices step jump){
//                            var points=detail.tripData!!.locations!![i]
//                            sourcePoints.add(LatLng(points.geoCoordinates?.latitude!!, points.geoCoordinates?.longitude!!))
//                        }
//                        sourcePoints.add(sourcePoints.lastIndex,LatLng(detail.tripData!!.locations!![detail.tripData!!.locations!!.lastIndex].geoCoordinates?.latitude!!, detail.tripData!!.locations!![detail.tripData!!.locations!!.lastIndex].geoCoordinates?.longitude!!))
//
//                    }
//
//                    GetSnappedPointsAsyncTask().execute(sourcePoints, null, snappedPoints)
//                }
//            }
            //check if payout eligible flag is true then show payment details
            if (detail.payoutEligible && detail.taskStateUpdated && detail.status == TaskStatus.COMPLETED) {
                mTaskDetailViewModel.isPayoutEligible.set(detail.payoutEligible)
                detail.taskstats?.let { stats ->
                    mTaskDetailViewModel.extraKm.set("${stats.distance} " + AppConstants.KM)
                    mTaskDetailViewModel.waitingTime.set("${stats.waitingtime} " + AppConstants.MIN)
                    mTaskDetailViewModel.overtime.set("${stats.tripDuration} " + AppConstants.MIN)
                }
                detail.driverPayoutBreakUps?.let { driverPayout ->
                    mTaskDetailViewModel.estimatedEarnings.set(AppConstants.ESTIMATE_EARNINGS + " " +
                            AppConstants.INR + " " + driverPayout.totalPayout)
                    mTaskDetailViewModel.nightCharges.set("${driverPayout.nightCharge}")
                    mTaskDetailViewModel.baseFare.set("${driverPayout.basefare}")
                    mTaskDetailViewModel.extraKmCharges.set("${driverPayout.extraKmCharge}")
                    mTaskDetailViewModel.waitingTimeCharges.set("${driverPayout.waitingCharge}")
                    mTaskDetailViewModel.overtimeCharges.set("${driverPayout.overtimeCharge}")
                    mTaskDetailViewModel.nightCharges.set("${driverPayout.nightCharge}")
                    mTaskDetailViewModel.totalEarning.set("${driverPayout.totalPayout}")

                }
            }
        }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e(TAG, "Exception Inside setTaskDetail(): $e")
//        }
    }

    private fun performMakeList(events: HashMap<String, Int>) {
        eventList = ArrayList();
        var context = this@TaskDetailActivity
        val hmIterator: Iterator<*> = events!!.entries.iterator()
        while (hmIterator.hasNext()) {
            val mapElement = hmIterator.next() as Map.Entry<*, *>
            var alertEvent = AlertEvent()
            alertEvent.name = mapElement.key.toString()
            if (alertEvent.name == "OVER_STOPPING") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_over_stopping)


            }
            if (alertEvent.name == "HARSH_CORNERING") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_harsh_cornering)

            }
            if (alertEvent.name == "HARSH_BREAKING") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_harsh_braking)

            }
            if (alertEvent.name == "HARSH_ACCELERATION") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_harsh_acceleration)

            }
            if (alertEvent.name == "ACTION_AIRPLANE_MODE") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_airplane_mode)

            }
            if (alertEvent.name == "OUT_OF_NETWORK") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_network_connection_off)

            }

            if (alertEvent.name == "ACTION_MANUAL_MOBILE_DATA_OFF") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_internet)

            }

            if (alertEvent.name == "ACTION_DATE_TIME_CHANGE") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_date_time_change)

            }
            if (alertEvent.name == "GEOFENCE_IN") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_geo_in)

            }
            if (alertEvent.name == "GEOFENCE_OUT") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_geo_in)

            }
            if (alertEvent.name == "LOGOUT") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_force_logout)

            }
            if (alertEvent.name == "FORCE_PUNCH_OUT") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_exit)

            }
            if (alertEvent.name == "ACTION_SHUTDOWN_GRACEFULLY") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_shutdown)

            }
            if (alertEvent.name == "PHONE_USAGE") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_phone_usage)

            }
            if (alertEvent.name == "ACTION_LOCATION_CHANGE") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_location_off_small)

            }
            if (alertEvent.name != "NONE")
                eventList!!.add(alertEvent)
        }

    }

    private fun setStats(distance: Double, timed: Int, maxSpeed: Int) {
        try {
            val speed = ((maxSpeed * 3600) / 1000)
            val distance3digits: Double = String.format("%.3f", distance).toDouble()
            val distance2digits: Double = String.format("%.2f", distance3digits).toDouble()

//            val speed3digits: Double = String.format("%.3f", speed).toDouble()
//            val speed2digits: Double = String.format("%.2f", speed3digits).toDouble()

            val hours = timed / 60 //since both are ints, you get an int
            val minutes = timed % 60

            tvDistance.text = "$distance2digits KMS"
            tvAvgSpeed.text = "$speed KMPH"

            if (hours != 0 && minutes != 0) {
                tvTime.text = "$hours HRS $minutes MINS"
            } else if (hours == 0 && minutes != 0) {
                tvTime.text = "$minutes MINS"
            } else if (hours != 0 && minutes == 0) {
                tvTime.text = "$hours HRS"
            } else {
                tvTime.text = "0 MINS"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside setStats(): $e")
        }
    }

    private fun setMarkers(taskDetail: Task?) {
        try {
            var isMarker = false
            val builder = LatLngBounds.Builder()
            if (taskDetail != null && taskDetail!!.source != null && taskDetail!!.source!!.location != null) {
                var loc = taskDetail!!.source!!.location
                val sourceLat = loc!!.latitude
                val sourceLng = loc!!.longitude
                val srcAddress = taskDetail!!.source!!.address!!
                val srcLatLng = LatLng(sourceLat, sourceLng)

                val srcMarker = mMap.addMarker(
                        MarkerOptions()
                                .position(srcLatLng)
                                .title(srcAddress)
                                .icon(CommonUtils.bitmapDescriptorFromVector(this, R.drawable.source_marker))
                )
                srcMarker?.tag = MARKER_TAG
                builder.include(srcMarker.position)
                isMarker = true
            }
            if (!distinationMarker) {
                if (taskDetail != null && taskDetail!!.destination != null && taskDetail!!.destination!!.location != null) {
                    var loc = taskDetail!!.destination!!.location
                    val destLat = loc!!.latitude
                    val destLng = loc!!.longitude
                    val destAddress = taskDetail!!.destination!!.address!!
                    val destLatLng = LatLng(destLat, destLng)
                    val destMarker = mMap.addMarker(
                            MarkerOptions()
                                    .position(destLatLng)
                                    .title(destAddress)
                                    .icon(CommonUtils.bitmapDescriptorFromVector(this,
                                            R.drawable.destination_marker)))
                    destMarker?.tag = MARKER_TAG
                    builder.include(destMarker.position)
                    isMarker = true
                    distinationMarker = true;
                }

            }

            //check if marker is added into the builder
            if (isMarker && !isCameraZoomEventDone) {
                val padding = 250 //offset from edges of the map in pixels
                val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding)
                mMap.animateCamera(cu)
            }
            taskDetail?.let {


            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside setMarkers(): $e")
        }
    }

    private var lastTimeStamp = 0L

    private fun setLiveTripData() {
        try {
            val locationList = mTaskDetailViewModel.getLiveTrip(lastTimeStamp)
            if (locationList != null && locationList.isNotEmpty()) {
                Log.e(tag, "inside setLiveTripData():  location size : ${locationList.size}")
                lastTimeStamp = locationList[locationList.size - 1].time
                if (polyLine == null) {
                    polyLine = mMap.addPolyline(mTaskDetailViewModel.drawPolyline(locationList))
                } else {
                    val polylineList = polyLine!!.points
                    polyLine?.points = mTaskDetailViewModel.updatePolylineList(polylineList, locationList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside setLiveTripData(): $e")
        }
    }

    private fun updateCameraBearing(bearing: Float, latLng: LatLng) {
        try {
            val camPos = CameraPosition.Builder(
                    mMap.cameraPosition)
                    .bearing(bearing)
                    .target(latLng)
                    .zoom(14.toFloat())
                    .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
        } catch (e: Exception) {
            e.printStackTrace()

            Log.e(TAG, "Exception Inside updateCameraBearing(): $e")
        }
    }

    private fun updateLocationPercentageTask() {
//        if(TrackThat.checkIfTripIdIsValid(taskId!!))
//        {
        if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
            var tripStatsCurrent = TrackThat.getCurrentTripStatistics(taskId)
            var jsonConverter =
                    JSONConverter<com.trackthat.lib.models.TripsStatistics>()
            var jsonConverter2 = JSONConverter<TripsStatistics>()
            var tripStatsStr = jsonConverter.objectToJson(tripStatsCurrent)
            CommonUtils.showLogMessage(
                    "e",
                    "trip_data",
                    tripStatsCurrent.toString()
            )
            var tripStats: TripsStatistics =
                    jsonConverter2.jsonToObject(
                            tripStatsStr,
                            TripsStatistics::class.java
                    )
            setStats(
                    tripStats.distanceInMeter / 1000,
                    tripStats.tripDurationInMinute,
                    tripStats.maxSpeed
            )
            setEvents(tripStats)
            if (!TrackThat.checkTripIsComplete(taskId!!)) {
                progressBar.visibility = View.GONE
                llSync.visibility = View.VISIBLE
                rlSyncing.visibility = View.GONE
                tvSyncStatus.visibility = View.VISIBLE
                tvSyncStatus.text = "Tracking.."
            } else {
                llSync.visibility = View.VISIBLE
                if (TrackThat.getSyncPercentageOfTrip(taskId!!) != null && TrackThat.getSyncPercentageOfTrip(taskId!!) < 100.0) {
                    rlSyncing.visibility = View.VISIBLE
                    tvSyncStatus.visibility = View.VISIBLE
                    tvSyncStatus.text = "Syncing.."
                    progressBar.progress = TrackThat.getSyncPercentageOfTrip(taskId!!).toInt()
                    tvSynProgress.text = "${progressBar.progress}%"
                } else {
                    rlSyncing.visibility = View.VISIBLE
                    tvSyncStatus.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    tvSyncStatus.text = "Synced"
                    if (TrackThat.getSyncPercentageOfTrip(taskId!!) != null) {
                        progressBar.progress = TrackThat.getSyncPercentageOfTrip(taskId!!).toInt()
                        tvSynProgress.text = "${progressBar.progress}%"
                    } else {
                        progressBar.progress = 100
                        tvSynProgress.text = "${progressBar.progress}%"
                    }
                    if (locationSynHandler != null) {
                        mStopHandler = true
                        locationSynHandler!!.removeCallbacks(this)
                    }
                }
            }
            // }

        }
    }

    override fun onLocationChange(location: Location?) {
        //  TrackiToast.Message.showShort(this,"get Location")
        try {

            getLiveEvent()

            setLiveTripData()
            if (location != null) {
                Log.e(tag, "inside requestCurrentLocation() currentLocation: ${location.latitude}, ${location.longitude}")
                val latLng = LatLng(location.latitude, location.longitude)
                //add moving marker here
                if (movingMarker == null) {
                    movingMarker = mMap.addMarker(
                            MarkerOptions()
                                    .position(latLng)
                                    .icon(CommonUtils.bitmapDescriptorFromVector(this, R.drawable.ic_location_marker)))
                } else {
                    movingMarker!!.position = latLng
                }
                //   updateCameraBearing(location.bearing, latLng)
            }
        } catch (e: Exception) {
            Log.e(tag, "Inside Current Location ${e.message}")
        }
    }

    /**
     * Method used to get the events and show their count on screen.
     *
     *@param tripStats stats model from sdk
     *
     * Events are:
     * ACTION_AIRPLANE_MODE,
     * ACTION_MANUAL_BATTERY_REMOVAL,
     * ACTION_MANUAL_MOBILE_DATA_OFF,
     * PHONE_USAGE,
     * ACTION_SHUTDOWN_GRACEFULLY,
     * ACTION_DATE_TIME_CHANGE,
     * OVER_SPEEDING,
     * OVER_STOPPING,
     * HARSH_ACCELERATION,
     * HARSH_BREAKING,
     * HARSH_CORNERING,
     * ACTION_SLOW_DRIVING,
     * GEOFENCE_IN,
     * GEOFENCE_OUT,
     * UNKNOWN,
     * OUT_OF_NETWORK,
     * NONE,
     * ACTION_LOCATION_CHANGE,
     * ACTION_CLEAR_DATA,
     * LOGOUT
     */
    private fun setEvents(tripStats: TripsStatistics) {
        try {
            var overSpeed = 0
            var overStop = 0
            var harshAcceleration = 0
            var harshBraking = 0
            var harshCornering = 0
            var actionAirPlane = 0
            var mobileDataOff = 0
            var phoneUsage = 0
            var shutDown = 0
            var dateTimeChange = 0
            var geoIn = 0
            var geoOut = 0
            var logout = 0
            var forcepuncOut = 0
            var locationOff = 0
            if (tripStats.events != null && tripStats.events!!.isNotEmpty()) {
                if (tripStats.events?.containsKey("OVER_SPEEDING")!!) {
                    overSpeed = tripStats.events!!["OVER_SPEEDING"]!!
                }
                if (tripStats.events!!.containsKey("OVER_STOPPING")) {
                    overStop = tripStats.events!!["OVER_STOPPING"]!!
                }
                if (tripStats.events!!.containsKey("HARSH_ACCELERATION")) {
                    harshAcceleration = tripStats.events!!["HARSH_ACCELERATION"]!!
                }
                if (tripStats.events!!.containsKey("HARSH_BREAKING")) {
                    harshBraking = tripStats.events!!["HARSH_BREAKING"]!!
                }
                if (tripStats.events!!.containsKey("HARSH_CORNERING")) {
                    harshCornering = tripStats.events!!["HARSH_CORNERING"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_AIRPLANE_MODE")) {
                    actionAirPlane = tripStats.events!!["ACTION_AIRPLANE_MODE"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_MANUAL_MOBILE_DATA_OFF")) {
                    mobileDataOff = tripStats.events!!["ACTION_MANUAL_MOBILE_DATA_OFF"]!!
                }
                if (tripStats.events!!.containsKey("PHONE_USAGE")) {
                    phoneUsage = tripStats.events!!["PHONE_USAGE"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_SHUTDOWN_GRACEFULLY")) {
                    shutDown = tripStats.events!!["ACTION_SHUTDOWN_GRACEFULLY"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_DATE_TIME_CHANGE")) {
                    dateTimeChange = tripStats.events!!["ACTION_DATE_TIME_CHANGE"]!!
                }
                if (tripStats.events!!.containsKey("GEOFENCE_IN")) {
                    geoIn = tripStats.events!!["GEOFENCE_IN"]!!
                }
                if (tripStats.events!!.containsKey("GEOFENCE_OUT")) {
                    geoOut = tripStats.events!!["GEOFENCE_OUT"]!!
                }
                if (tripStats.events!!.containsKey("LOGOUT")) {
                    logout = tripStats.events!!["LOGOUT"]!!
                }
                if (tripStats.events!!.containsKey("FORCE_PUNCH_OUT")) {
                    forcepuncOut = tripStats.events!!["FORCE_PUNCH_OUT"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_LOCATION_CHANGE")) {
                    locationOff = tripStats.events!!["ACTION_LOCATION_CHANGE"]!!
                }


                val count = overSpeed + overStop + harshAcceleration + harshBraking + harshCornering + actionAirPlane + mobileDataOff + phoneUsage + shutDown + dateTimeChange + geoIn + geoOut + logout + forcepuncOut + locationOff


                if (count > 0) {
                    tvNumberAlert.text = "$count"
                    //  tvTotalCount.visibility = View.VISIBLE
                    tvNumberAlert.visibility = View.VISIBLE
                } else {
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside setEvents(): $e")
        }
    }

    override fun showEventDialog(showDialog: Boolean, eventName: String) {
        try {
            if (showDialog) {
                if (eventDialog != null) {
                    eventDialog!!.dismiss()
                }
                eventDialog = EventDialogFragment.getInstance(eventName)
                eventDialog!!.show(supportFragmentManager, "dialog")
            } else {
                TrackiToast.Message.showLong(this@TaskDetailActivity, eventName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside showEventDialog(): $e")
        }
    }

    override fun run() {
        try {
            if (handler != null) {
                //   fabAlerts.startAnimation(shake)
                if (taskId != null) {
                    if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId())
                       {
                           getLiveEvent()

                        }
                    else
                        mTaskDetailViewModel.getTaskById(httpManager, AcceptRejectRequest(taskId!!), api)
                }
                handler?.postDelayed(this, 60000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside run(): $e")
        }
    }


    /**
     * Method used to show movingMarker on map.
     *
     * @param polylineAndMarkerObj hashMap of markers.
     */
    private fun showPolyLineAndMarkers(polylineAndMarkerObj: ArrayList<Any?>) {
        try {
            clearMapList()

//            for (aPolylineAndMarkerObj in polylineAndMarkerObj) {
            drawOnMap(polylineAndMarkerObj)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception inside showPolyLineAndMarkers(): $e")
        }
    }

    /**
     * Clear map hashMap.
     */
    private fun clearMapList() {
        if (mapList.size > 0) {
            for (i in mapList.indices) {
                val item = mapList[i]
                if (item is Polyline) {
                    item.remove()
                } else if (item is Marker) {
                    item.remove()
                }
            }
        }
    }

    override fun onCallClick() {
        if (mobile != null) {
            CommonUtils.openDialer(this, mobile)
        }
    }

    @Throws(NullPointerException::class)
    fun drawOnMap(objects: ArrayList<Any?>) {
        if (null != objects[0]) {
            val polylineMap = objects[0] as Map<Int, PolylineOptions>

            for (value in polylineMap.entries) {
                mapList.add(mMap.addPolyline(value.value))
            }
        }
        if (null != objects[1]) {
            val listMarkers = objects[1] as List<MarkerOptions>

            for (i in listMarkers.indices) {
                mapList.add(mMap.addMarker(listMarkers[i]))
            }
        }

    }

    override fun isolateEventResponse(osObject: ArrayList<Any?>, ostObject: ArrayList<Any?>,
                                      hcObject: ArrayList<Any?>, hbObject: ArrayList<Any?>,
                                      haObject: ArrayList<Any?>) {
        this.osObject = osObject
        this.ostObject = ostObject
        this.hcObject = hcObject
        this.hbObject = hbObject
        this.haObject = haObject
    }

    override fun allEventList(list: ArrayList<AlertEvent>) {
        eventList = list
    }

    companion object {
        const val MARKER_TAG = "Moving_Marker_Tag"
        const val TAG = "TaskDetailActivity"

        fun newIntent(context: Context) = Intent(context, TaskDetailActivity::class.java)
    }


    override fun onSocketResponse(eventName: Int, baseModel: BaseModel?) {
        runOnUiThread {
            Log.e(TAG, "onSocketResponse() called")
            Log.e(TAG, "eventName =>" + eventName)
            Log.e(TAG, "baseModel =>" + baseModel)
            hideLoading()
            when (eventName) {
                4 -> {

                    val openCreateRoomModel = baseModel as OpenCreateRoomModel
                    val messageList = openCreateRoomModel.messages
                    var roomId = openCreateRoomModel.roomId
                    var roomName = openCreateRoomModel.roomName

                    val list = ArrayList<String>()
                    list.add(buddyId!!)
                    startActivity(ChatActivity.newIntent(this@TaskDetailActivity)
                            .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, list)
                            .putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, name)
                            .putExtra(AppConstants.Extra.EXTRA_IS_CREATE_ROOM, false)
                            .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId))


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
                    list.add(buddyId!!)
                    webSocketManager?.openCreateRoom(list, null, true, 10)

                }
            }
        }
    }

    override fun onOpen() {
    }

    override fun closed() {
      //  webSocketManager = null
    }

    override fun alert(alertEvent: AlertEvent) {
        if (dialogAllAlert != null)
            dialogAllAlert?.dismiss()
        if (alertEvent.eventData != null)
            showPolyLineAndMarkers(alertEvent.eventData!!)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.llAlert -> {
                if (eventList != null && eventList!!.isNotEmpty()) {
                    openDialogShowEvent(eventList!!)
                }
            }
        }
    }

    open fun openDialogShowEvent(list: ArrayList<AlertEvent>) {
        var adapter = TaskAlterEventAdapter(this@TaskDetailActivity, ArrayList())
        if (dialogAllAlert == null) {
            var cellwidthWillbe = 0
            dialogAllAlert = Dialog(this@TaskDetailActivity)
            dialogAllAlert!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogAllAlert?.window!!.setBackgroundDrawable(
                    ColorDrawable(
                            Color.TRANSPARENT))
            dialogAllAlert!!.setContentView(R.layout.layout_pop_alert)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialogAllAlert?.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.dimAmount = 0.8f
            val window = dialogAllAlert!!.window
            window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window!!.setGravity(Gravity.CENTER)
            val textViewCancel = dialogAllAlert!!.findViewById<TextView>(R.id.cancelText)
            val recyclerViewEvents = dialogAllAlert!!.findViewById<RecyclerView>(R.id.recyclerViewEvents)
            val rlRv = dialogAllAlert!!.findViewById<RelativeLayout>(R.id.rlRv)

            val viewTreeObserver: ViewTreeObserver = rlRv.getViewTreeObserver()
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerViewEvents.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                    val width: Int = recyclerViewEvents.getMeasuredWidth()
                    val height: Int = recyclerViewEvents.getMeasuredHeight()
                    cellwidthWillbe = width / 4
                    adapter.addList(list)
                    adapter.setWidthWillBe(cellwidthWillbe)
                    // recyclerViewEvents.addItemDecoration(SpacesItemDecoration(spacingInPixels))
                    recyclerViewEvents.adapter = adapter
                }
            })

            dialogAllAlert?.window!!.attributes = lp
            textViewCancel.setOnClickListener {
                dialogAllAlert!!.dismiss()
            }
        } else {
            adapter.addList(list)
        }
        if (!dialogAllAlert!!.isShowing)
            dialogAllAlert!!.show()
    }

    private fun buildRequestUrl(trackPoints: List<LatLng>): String {
        val url = StringBuilder()
        url.append("https://roads.googleapis.com/v1/snapToRoads?path=")
        for (trackPoint in trackPoints) {
            url.append(String.format("%8.5f", trackPoint.latitude))
            url.append(",")
            url.append(String.format("%8.5f", trackPoint.longitude))
            url.append("|")
        }
        url.delete(url.length - 1, url.length)
        url.append("&interpolate=true")
        url.append(String.format("&key=%s", "AIzaSyATO_5mNZJ8h6V64L6eHeZfiVjk63803ec"))
        Log.e("url",url.toString())
        return url.toString()
    }

    inner class GetSnappedPointsAsyncTask : AsyncTask<List<LatLng?>, Void?, List<LatLng>>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }



        override fun onPostExecute(result: List<LatLng>) {
            super.onPostExecute(result)
            val polyLineOptions = PolylineOptions()
            polyLineOptions.addAll(result)
            polyLineOptions.width(5f)
            polyLineOptions.color(Color.RED)
            mMap.addPolyline(polyLineOptions);
            val builder = LatLngBounds.Builder()
            builder.include(result[0])
            builder.include(result[result.size - 1])
            val bounds = builder.build()
              mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
        }

        override fun doInBackground(vararg params: List<LatLng?>?): List<LatLng> {
            val snappedPoints: MutableList<LatLng> = java.util.ArrayList()
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            try {
                val url = URL(buildRequestUrl(params[0] as List<LatLng>))
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                val jsonStringBuilder = StringBuilder()
                val buffer = StringBuffer()
                var line = ""
                while (reader.readLine().also { line = it } != null) {
                    buffer.append(line)
                    jsonStringBuilder.append(line)
                    jsonStringBuilder.append("\n")
                }
                Log.e("response",jsonStringBuilder.toString())
                val jsonObject = JSONObject(jsonStringBuilder.toString())
                val snappedPointsArr = jsonObject.getJSONArray("snappedPoints")
                for (i in 0 until snappedPointsArr.length()) {
                    val snappedPointLocation = (snappedPointsArr[i] as JSONObject).getJSONObject("location")
                    val lattitude = snappedPointLocation.getDouble("latitude")
                    val longitude = snappedPointLocation.getDouble("longitude")
                    snappedPoints.add(LatLng(lattitude, longitude))
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return snappedPoints
        }
    }

}
