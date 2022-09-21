package com.tracki.ui.idealtrip

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.GeoCoordinates
import com.tracki.data.model.response.config.Place
import com.tracki.data.model.response.config.Task
import com.tracki.data.model.response.config.TripsStatistics
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityIdealTripDetailsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.common.CustomInfoWindowAdapter
import com.tracki.ui.custom.EventDialogFragment
import com.tracki.ui.taskdetails.AlertEvent
import com.tracki.ui.taskdetails.TaskAlterEventAdapter
import com.tracki.ui.taskdetails.TaskDetailActivity
import com.tracki.ui.taskdetails.TaskDetailNavigator
import com.tracki.utils.*
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.util.EventType
import com.trackthat.lib.internal.util.TripState
import com.trackthat.lib.locationobserver.LocationObserver
import com.trackthat.lib.models.Events
import com.trackthat.lib.models.LocationData
import kotlinx.android.synthetic.main.item_bottom_sheet_ideal_track.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class IdealTripDetailsActivity : BaseActivity<ActivityIdealTripDetailsBinding, IdealTripViewModel>(),
        TaskDetailNavigator, OnMapReadyCallback, LocationObserver,
        GoogleMap.OnMarkerClickListener, Runnable, View.OnClickListener, TaskAlterEventAdapter.OnAlertClick {


    @Inject
    lateinit var mTaskDetailViewModel: IdealTripViewModel

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private var mobile: String? = null
    private var eventDialog: EventDialogFragment? = null
    private val tag = IdealTripDetailsActivity::class.java.simpleName
    private var polyLine: Polyline? = null
    private lateinit var mActivityTaskDetailBinding: ActivityIdealTripDetailsBinding
    private var movingMarker: Marker? = null
    private lateinit var shake: Animation
    private var dialogAllAlert: Dialog? = null
    private var eventList: ArrayList<AlertEvent>? = null


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
    private var mapFragment: SupportMapFragment? = null

    private lateinit var tvTotalCount: TextView

    private var osObject: ArrayList<Any?>? = null
    private var ostObject: ArrayList<Any?>? = null
    private var hcObject: ArrayList<Any?>? = null
    private var hbObject: ArrayList<Any?>? = null
    private var haObject: ArrayList<Any?>? = null
    private var mapList = ArrayList<Any?>()
    private var punchId: String? = null
    private var isCameraZoomEventDone = false
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityTaskDetailBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityTaskDetailBinding = viewDataBinding
        mTaskDetailViewModel.navigator = this
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        if (intent.hasExtra("taskId")) {
            punchId = intent.getStringExtra("taskId")
        }
        setUp()


    }

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_ideal_trip_details
    override fun getViewModel() = mTaskDetailViewModel

    override fun onResume() {
        super.onResume()
        mTaskDetailViewModel.onResume(this@IdealTripDetailsActivity)


        TrackThat.registerLocationObservable(this)

    }

    override fun onPause() {
        super.onPause()
        mTaskDetailViewModel.onPause(this@IdealTripDetailsActivity)
        if (handler != null) {
            handler?.removeCallbacks(this)
        }
        TrackThat.unregisterLocationObservable(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            handler?.removeCallbacks(this)
            handler = null
            mTaskDetailViewModel.onDestroy()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside onDestroy(): $e")
        }
    }

    override fun onMapReady(mapboxMap: GoogleMap?) {
        this.mMap = mapboxMap!!
        CommonUtils.changeGoogleLogoPosition(mapFragment!!, resources.getDimension(R.dimen.dimen_120).toInt(), mMap)
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        mMap.setOnMarkerClickListener(this)
        setTaskDetails()

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

    }


    private fun setUp() {
        try {
//            mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//            mapFragment?.getMapAsync(this)
            shake = AnimationUtils.loadAnimation(this, R.anim.shake)

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
            llUserCommOption.visibility = View.GONE
            toolbarTitle.visibility = View.VISIBLE

            ivNavigationIcon.setOnClickListener {
                onBackPressed()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside setUp(): $e")
        }
    }

    private var handler: Handler? = null

    private fun setTaskDetails() {
        try {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
            val cal: Calendar = Calendar.getInstance()
            var date: Date = cal.time
            val todayWithZeroTime: Date = dateFormat.parse(dateFormat.format(date))
            CommonUtils.showLogMessage("e", "time", "" + todayWithZeroTime.time)
            var detail: List<LocationData>? = null;
            if (punchId != null) {
                detail = TrackThat.getTripLocationById(punchId)
            }else{
                detail = TrackThat.getIdealTrip(todayWithZeroTime.time)
            }


            var userData = preferencesHelper.userDetail
            tvName.text = userData.name
            tvMobile.text = userData.mobile
            mTaskDetailViewModel.setContact(userData)
            var task = Task()
            var tripStats = TripsStatistics()
            var eventList: MutableList<Events>? = null;
            if (punchId != null) {
                eventList = TrackThat.getEvent(punchId)
            }else{
                eventList = TrackThat.getTodayEvent(todayWithZeroTime.time)
            }



//            var jsonConvertor = JSONConverter<List<Events>>()
//            var json = jsonConvertor.objectToJson(eventList);
//            CommonUtils.showLogMessage("e", "eventlist", "" + json)
            mTaskDetailViewModel.isolateEvents(eventList, this@IdealTripDetailsActivity, preferencesHelper)
            var builder = LatLngBounds.builder()
            if (detail != null && detail.isNotEmpty()) {

                val polyLine = mTaskDetailViewModel.extractPolyline(detail)
                mMap.addPolyline(polyLine)
                //   isCameraZoomEventDone=false;

                var initlatlong = LatLng(detail[0].geoCoordinates?.latitude!!, detail[0].geoCoordinates?.longitude!!)
                builder.include(initlatlong)
                var startPlace = Place()
                startPlace.address = CommonUtils.getAddress(this@IdealTripDetailsActivity, LatLng(detail[0].geoCoordinates?.latitude!!, detail[0].geoCoordinates?.longitude!!))
                var startGetCoards = GeoCoordinates()
                startGetCoards.latitude = detail[0].geoCoordinates?.latitude!!
                startGetCoards.longitude = detail[0].geoCoordinates?.longitude!!
                startPlace.location = startGetCoards
                task.source = startPlace
                var finnallatlong = LatLng(detail[detail.size - 1].geoCoordinates?.latitude!!, detail[detail.size - 1].geoCoordinates?.longitude!!)
                builder.include(finnallatlong)
                var endPlace = Place()
                endPlace.address = CommonUtils.getAddress(this@IdealTripDetailsActivity, LatLng(detail[detail.size - 1].geoCoordinates?.latitude!!, detail[detail.size - 1].geoCoordinates?.longitude!!))
                var endGetCoards = GeoCoordinates()
                endGetCoards.latitude = detail[detail.size - 1].geoCoordinates?.latitude!!
                endGetCoards.longitude = detail[detail.size - 1].geoCoordinates?.longitude!!
                endPlace.location = endGetCoards
                task.destination = endPlace
                mMap.setOnMapLoadedCallback {
                    val padding = 250 //offset from edges of the map in pixels
                    val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding)
                    mMap.animateCamera(cu)
                }
                isCameraZoomEventDone = true;
                setMarkers(taskDetail = task)
                var sortedLocationList = detail.sortedBy { data -> data.time }
                val integerList = java.util.ArrayList<Int>()
                for (llocatiolist in sortedLocationList){
                    integerList.add(llocatiolist.speed.toInt())
                }
                var maxSpeed =0
                if (integerList.size > 0) {
                    maxSpeed = Collections.max(integerList)
                }
               // var maxSpeedEvent = sortedLocationList.maxBy { data -> data.speed }
                    tripStats.maxSpeed = CommonUtils.metersPerSecToKmPerHr(maxSpeed.toFloat())
                if (preferencesHelper.idleTripActive) {
                    val time = DateTimeUtil.getParsedDate(System.currentTimeMillis()) + " at " +
                            DateTimeUtil.getParsedTime(System.currentTimeMillis())
                    toolbarTitle.text = time
                } else {
                    if (sortedLocationList.size > 1) {

                        val time = DateTimeUtil.getParsedDate(sortedLocationList[sortedLocationList.size - 1].time) + " at " +
                                DateTimeUtil.getParsedTime(sortedLocationList[sortedLocationList.size - 1].time)
                        toolbarTitle.text = time

                    }
                }
                if (sortedLocationList.size > 1) {

                    tripStats.distance = calculateDistance(sortedLocationList)
                    tripStats.distanceInMeter = calculateDistance(sortedLocationList) / 1000.toDouble()
                }

            } else {

                if (eventList != null && eventList.isNotEmpty()) {

                    if (preferencesHelper.idleTripActive) {
                        val time = DateTimeUtil.getParsedDate(System.currentTimeMillis()) + " at " +
                                DateTimeUtil.getParsedTime(System.currentTimeMillis())
                        toolbarTitle.text = time
                    } else {
                        val time = DateTimeUtil.getParsedDate(eventList[eventList.size - 1].startTime) + " at " +
                                DateTimeUtil.getParsedTime(eventList[eventList.size - 1].startTime)
                        toolbarTitle.text = time
                    }
                }

            }


            var secondTime: Long = 0
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

                if (preferencesHelper.onIdleOverStopping != null) {

                    var allOverStoppingList = eventList.filter { data -> data.eventType.name == EventType.OVER_STOPPING.name }.distinctBy { data -> data.startCoordinates.latitude }.distinctBy { data -> data.startCoordinates.longitude }
                    if (allOverStoppingList.isNotEmpty()) {
                        var overStopping: MutableList<Events> = ArrayList()
                        var overstoppingConfig = preferencesHelper.onIdleOverStopping
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
                if (preferencesHelper.idleTripActive) {
                    secondTime = System.currentTimeMillis()
                } else {
                    var allEventList: MutableList<Events>? = null;
                    if (punchId != null) {
                        allEventList = TrackThat.getEvent(punchId)
                    }else{
                        allEventList = TrackThat.getTodayEvent(todayWithZeroTime.time)
                    }

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

            setEvents(tripStats)
//            var tripStatsEvent=TrackThat.getCurrentIdleEStatistics(todayWithZeroTime.time)

            setStats(tripStats.distanceInMeter, tripStats.tripDurationInMinute, tripStats.maxSpeed)


        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside setTaskDetails(): $e")
        }
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

    private fun setStats(distance: Double, timed: Int, maxSpeed: Int) {
        try {
            //  val speed = ((maxSpeed * 3600) / 1000)
            val distance3digits: Double = String.format("%.3f", distance).toDouble()
            val distance2digits: Double = String.format("%.2f", distance3digits).toDouble()


            val hours = timed / 60 //since both are ints, you get an int
            val minutes = timed % 60

            tvDistance.text = "$distance2digits KMS"
            tvAvgSpeed.text = "$maxSpeed KMPH"

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
            taskDetail?.let {
                var isMarker = false
                val builder = LatLngBounds.Builder()
                it.source?.let { src ->
                    src.location?.let { loc ->
                        val sourceLat = loc.latitude
                        val sourceLng = loc.longitude
                        val srcAddress = src.address!!
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
                }
                it.destination?.let { dest ->
                    dest.location?.let { loc ->
                        val destLat = loc.latitude
                        val destLng = loc.longitude
                        val destAddress = dest.address!!
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
                    }
                }
                //check if marker is added into the builder
                if (isMarker && !isCameraZoomEventDone) {
                    mMap.setOnMapLoadedCallback {
                        val padding = 250 //offset from edges of the map in pixels
                        val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding)
                        mMap.animateCamera(cu)
                    }

                }
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


    override fun onLocationChange(location: Location?) {
        //  TrackiToast.Message.showShort(this,"get Location")
        try {

            CommonUtils.showLogMessage("e", "lattitude", "" + location!!.latitude)
            CommonUtils.showLogMessage("e", "longitude", "" + location!!.longitude)
            //  setTaskDetails()
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
                updateCameraBearing(location.bearing, latLng)
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
                    tvTotalCount.text = "$count"
                    tvNumberAlert.text = "$count"
                    // tvTotalCount.visibility = View.VISIBLE
                    tvNumberAlert.visibility = View.VISIBLE
                } else {
                    tvTotalCount.visibility = View.GONE
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
                TrackiToast.Message.showLong(this@IdealTripDetailsActivity, eventName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception Inside showEventDialog(): $e")
        }
    }

    override fun run() {
        try {
            if (handler != null) {
                setTaskDetails()
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
        const val TAG = "IdealTripDetailsActivity"
        fun newIntent(context: Context) = Intent(context, TaskDetailActivity::class.java)
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
        var adapter = TaskAlterEventAdapter(this@IdealTripDetailsActivity, ArrayList())
        if (dialogAllAlert == null) {
            var cellwidthWillbe = 0
            dialogAllAlert = Dialog(this@IdealTripDetailsActivity)
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
            //val spacingInPixels = resources.getDimensionPixelSize(R.dimen.dimen_4)
            // recyclerViewEvents.addItemDecoration(SpacesItemDecoration(spacingInPixels))


            val viewTreeObserver: ViewTreeObserver = rlRv.getViewTreeObserver()
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerViewEvents.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                    val width: Int = recyclerViewEvents.getMeasuredWidth()
                    val height: Int = recyclerViewEvents.getMeasuredHeight()
                    cellwidthWillbe = width / 4
                    adapter.setWidthWillBe(cellwidthWillbe)
                    adapter.addList(list)
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

}