package com.rocketflyer.rocketflow.ui.taskdetails

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.model.*
import com.tracki.R
import com.tracki.data.DataManager
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AcceptRejectRequest
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.Event
import com.tracki.data.model.response.config.Location
import com.tracki.data.model.response.config.Task
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.ui.base.BaseViewModel
import com.tracki.utils.Log
import com.tracki.utils.rx.SchedulerProvider
import com.trackthat.lib.TrackThat
import com.trackthat.lib.TrackThatConstants
import com.trackthat.lib.locationobserver.LocationObserver
import com.trackthat.lib.models.LocationData
import kotlin.collections.set


/**
 * Created by rahul on 16/10/18
 */
class TaskDetailViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<TaskDetailNavigator>(dataManager, schedulerProvider), ApiCallback,
        LocationObserver {

    var isContactAvail = ObservableBoolean(false)
    var isPayoutEligible = ObservableBoolean(false)

    var contact = ObservableField<String>()
    var baseFare = ObservableField<String>()
    var extraKm = ObservableField<String>()
    var extraKmCharges = ObservableField<String>()
    var waitingTime = ObservableField<String>()
    var waitingTimeCharges = ObservableField<String>()
    var overtime = ObservableField<String>()
    var overtimeCharges = ObservableField<String>()
    var nightCharges = ObservableField<String>()
    var estimatedEarnings = ObservableField<String>()
    var totalEarning = ObservableField<String>()

    private var unUsedEvents: ArrayList<Event>? = null
    private var harshBraking: ArrayList<Event>? = null
    private var harshAcceleration: ArrayList<Event>? = null
    private var overStopping: ArrayList<Event>? = null
    private var harshCornering: ArrayList<Event>? = null
    private var overSpeed: ArrayList<Event>? = null
    private var airPlaneMode: ArrayList<Event>? = null
    private var mobileDataOff: ArrayList<Event>? = null
    private var phoneUsage: ArrayList<Event>? = null
    private var forcepunchout: ArrayList<Event>? = null
    private var locationOff: ArrayList<Event>? = null
    private var dateTimeChange: ArrayList<Event>? = null
    private var geoIn: ArrayList<Event>? = null
    private var geoOut: ArrayList<Event>? = null
    private var logout: ArrayList<Event>? = null
    private var shutDown: ArrayList<Event>? = null
    private var outOfNetWOrk: ArrayList<Event>? = null
    private val tag = "TaskDetailViewModel"
    private var polylineWidth = 10f
    lateinit var httpManager: HttpManager
    lateinit var request: AcceptRejectRequest
    lateinit var api: Api

    private val eventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleAlerts(intent)
        }
    }

    private fun setIsContactAvail(isContact: Boolean) {
        isContactAvail.set(isContact)
    }

    private fun setContact(name: String) {
        contact.set(name)
    }

    fun setContact(task: Task) {
        if (task.contact != null) {
            if (task.contact!!.name != null) {
                setContact(task.contact?.name!!)
            } else if (task.contact!!.mobile != null) {
                setContact(task.contact?.mobile!!)
            }

            if (task.contact!!.mobile != null) {
                setIsContactAvail(true)
            } else {
                setIsContactAvail(false)
            }
        }
    }

    fun onCallClick() {
        navigator.onCallClick()
    }

    fun getTaskById(httpManager: HttpManager, request: AcceptRejectRequest, api: Api?) {
        this.httpManager = httpManager
        this.request = request
        this.api = api!!
        hitApi()
    }

    override fun onResponse(result: Any?, error: APIError?) {
        navigator.handleResponse(this, result, error)
    }

    override fun hitApi() {
        dataManager.getTaskById(this, httpManager, request, api)
    }

    override fun isAvailable() = true

    override fun onNetworkErrorClose() {

    }

    override fun onRequestTimeOut(callBack: ApiCallback) {
        navigator.showTimeOutMessage(callBack)
    }

    override fun onLogout() {

    }

    fun drawPolyline(locationList: List<LocationData>): PolylineOptions {
        val latLng = ArrayList<LatLng>(locationList.size)
        for (i in locationList.indices) {
            val points = locationList[i]
            latLng.add(LatLng(points.geoCoordinates?.latitude!!, points.geoCoordinates?.longitude!!))
        }
        return PolylineOptions()
                .addAll(latLng)
                .color(Color.parseColor("#33CE7E"))
                .width(polylineWidth)
    }

    fun extractPolyline(locationList: List<Location>): PolylineOptions {
        val latLng = ArrayList<LatLng>(locationList.size)
        for (i in locationList.indices) {
            val points = locationList[i]
            latLng.add(LatLng(points.geoCoordinates?.latitude!!, points.geoCoordinates?.longitude!!))
        }
        return PolylineOptions()
                .addAll(latLng)
                .color(Color.parseColor("#33CE7E"))
                 .geodesic(true)
                .width(polylineWidth)
    }
    fun extractSdkPolyline(locationList: List<LocationData>): PolylineOptions {
        val latLng = ArrayList<LatLng>(locationList.size)
        for (i in locationList.indices) {
            val points = locationList[i]
            latLng.add(LatLng(points.geoCoordinates?.latitude!!, points.geoCoordinates?.longitude!!))
        }
        return PolylineOptions()
                .addAll(latLng)
                .color(Color.parseColor("#33CE7E"))
                .geodesic(true)
                .width(polylineWidth)
    }

    fun updatePolylineList(polylineList: MutableList<LatLng>, locationList: List<LocationData>): MutableList<LatLng>? {
        for (i in locationList.indices) {
            val points = locationList[i]
            polylineList.add(LatLng(points.geoCoordinates.latitude, points.geoCoordinates.longitude))
        }
        return polylineList
    }

    /**
     * Show markers on map with respect to event button.
     *
     * @param events event array.
     */
    fun isolateEvents(events: List<Event>?, context: Context, preferencesHelper: PreferencesHelper) {

        if (events != null && events.isNotEmpty()) {
            var list = ArrayList<AlertEvent>()
            overSpeed = ArrayList()
            harshCornering = ArrayList()
            harshAcceleration = ArrayList()
            harshBraking = ArrayList()
            overStopping = ArrayList()
            unUsedEvents = ArrayList()
            airPlaneMode = ArrayList()
            mobileDataOff = ArrayList()
            dateTimeChange = ArrayList()
            geoIn = ArrayList()
            geoOut = ArrayList()
            logout = ArrayList()
            shutDown = ArrayList()
            outOfNetWOrk = ArrayList()
            phoneUsage = ArrayList()
            forcepunchout = ArrayList()
            locationOff = ArrayList()

            for (i in events.indices) {
                val event = events[i]
                if (event.eventName != null) {

                    when (event.eventName) {

                        "OVER_SPEEDING" -> {
                            overSpeed!!.add(event)
                        }
                        "HARSH_CORNERING" -> {
                            harshCornering!!.add(event)
                        }
                        "HARSH_ACCELERATION" -> {
                            harshAcceleration!!.add(event)
                        }
                        "HARSH_BREAKING" -> {
                            harshBraking!!.add(event)
                        }
                        "ACTION_AIRPLANE_MODE" -> {
                            airPlaneMode!!.add(event)
                        }
                        "ACTION_MANUAL_MOBILE_DATA_OFF" -> {
                            mobileDataOff!!.add(event)
                        }
                        "ACTION_DATE_TIME_CHANGE" -> {
                            dateTimeChange!!.add(event)
                        }
                        "GEOFENCE_IN" -> {
                            geoIn!!.add(event)
                        }
                        "GEOFENCE_OUT" -> {
                            geoOut!!.add(event)
                        }
                        "LOGOUT" -> {
                            logout!!.add(event)
                        }
                        "OVER_STOPPING" -> {
//                            if(preferencesHelper.onIdleOverStopping!=null)
//                            {
//                                var overstoppingConfig= preferencesHelper.onIdleOverStopping
//                                var diff=event.eventEndTime-event.eventStartTime
//                                var overStoppingMinute= TimeUnit.MILLISECONDS.toMinutes(diff).toInt()
//                                var isPointOutSide= CommonUtils.isPointOutSideCircle(overstoppingConfig.distanceinMeter,event.from!!.latitude,event.from!!.longitude,
//                                        event.to!!.latitude,event.to!!.longitude)
//                                if(overStoppingMinute>=overstoppingConfig.timeInMinute&&!isPointOutSide){
//                                    overStopping!!.add(event)
//                                }
//                            }else{
//                                overStopping!!.add(event)
//                            }
                            overStopping!!.add(event)
                        }
                        "OUT_OF_NETWORK" -> {
                            outOfNetWOrk!!.add(event)
                        }

                        "ACTION_SHUTDOWN_GRACEFULLY" -> {
                            shutDown!!.add(event)
                        }
                        "PHONE_USAGE" -> {
//                            var diff=event.eventEndTime-event.eventStartTime
//                            var phoneUsageTimeInMinutes= TimeUnit.MILLISECONDS.toMinutes(diff).toInt()
//                            if(phoneUsageTimeInMinutes>=preferencesHelper.phoneUsageLimitInMinutesOnTaskTrip)
                                phoneUsage!!.add(event)
                        }
                        "FORCE_PUNCH_OUT" -> {
                            forcepunchout!!.add(event)
                        }
                        "ACTION_LOCATION_CHANGE" -> {
                            locationOff!!.add(event)
                        }
                        //
                        else -> {
                            unUsedEvents!!.add(event)
                        }
                    }
                }
            }

            val osObject = createListOfPolygonAndMarkers(overSpeed!!,
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))

            if(overStopping!=null&&overStopping!!.isNotEmpty()) {
                val ostObject = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                        overStopping!!, "OVER_STOPPING")
                list.add(AlertEvent("OVER_STOPPING", ContextCompat.getDrawable(context, R.drawable.ic_over_stopping), ostObject, overStopping!!.size))

            }
            if(harshCornering!=null&&harshCornering!!.isNotEmpty()) {
                val hcObject = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        harshCornering!!, "HARSH_CORNERING")
                list.add(AlertEvent("HARSH_CORNERING", ContextCompat.getDrawable(context, R.drawable.ic_harsh_cornering), hcObject,harshCornering!!.size))
            }
            if(harshBraking!=null&&harshBraking!!.isNotEmpty()) {
                val hbObject = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                        harshBraking!!, "HARSH_BREAKING")

                list.add(AlertEvent("HARSH_BREAKING", ContextCompat.getDrawable(context, R.drawable.ic_harsh_braking), hbObject,harshBraking!!.size))
            }
            if(harshAcceleration!=null&&harshAcceleration!!.isNotEmpty()) {
                val haObject = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        harshAcceleration!!, "HARSH_ACCELERATION")
                list.add(AlertEvent("HARSH_ACCELERATION", ContextCompat.getDrawable(context, R.drawable.ic_harsh_acceleration), haObject,harshAcceleration!!.size))
            }
            if (airPlaneMode != null && airPlaneMode!!.isNotEmpty()) {
                val airPlaneMode = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        airPlaneMode!!, "ACTION_AIRPLANE_MODE")
                list.add(AlertEvent("ACTION_AIRPLANE_MODE", ContextCompat.getDrawable(context, R.drawable.ic_airplane_mode), airPlaneMode,this.airPlaneMode!!.size))
            }
            if (outOfNetWOrk != null && outOfNetWOrk!!.isNotEmpty()) {
                val airPlaneMode = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        outOfNetWOrk!!, "OUT_OF_NETWORK")
                list.add(AlertEvent("OUT_OF_NETWORK", ContextCompat.getDrawable(context, R.drawable.ic_network_connection_off), airPlaneMode,outOfNetWOrk!!.size))
            }

            if (mobileDataOff != null && mobileDataOff!!.isNotEmpty()) {
                val mobileData = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        mobileDataOff!!, "ACTION_MANUAL_MOBILE_DATA_OFF")
                list.add(AlertEvent("ACTION_MANUAL_MOBILE_DATA_OFF", ContextCompat.getDrawable(context, R.drawable.ic_internet), mobileData,mobileDataOff!!.size))
            }

            if (dateTimeChange != null && dateTimeChange!!.isNotEmpty()) {
                val dateTimeChange = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        dateTimeChange!!, "ACTION_DATE_TIME_CHANGE")
                list.add(AlertEvent("ACTION_DATE_TIME_CHANGE", ContextCompat.getDrawable(context, R.drawable.ic_date_time_change), dateTimeChange,this.dateTimeChange!!.size))
            }
            if (geoIn != null && geoIn!!.isNotEmpty()) {
                val geoIn = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        geoIn!!, "GEOFENCE_IN")
                list.add(AlertEvent("GEOFENCE_IN", ContextCompat.getDrawable(context, R.drawable.ic_geo_in), geoIn,this.geoIn!!.size))
            }
            if (geoOut != null && geoOut!!.isNotEmpty()) {
                val geoOut = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        geoOut!!, "GEOFENCE_OUT")
                list.add(AlertEvent("GEOFENCE_OUT", ContextCompat.getDrawable(context, R.drawable.ic_geo_in), geoOut,this.geoOut!!.size))
            }
            if (logout != null && logout!!.isNotEmpty()) {
                val logout = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        logout!!, "LOGOUT")
                list.add(AlertEvent("LOGOUT", ContextCompat.getDrawable(context, R.drawable.ic_force_logout), logout,this.logout!!.size))
            }
            if (forcepunchout != null && forcepunchout!!.isNotEmpty()) {
                val forcepunchout = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        forcepunchout!!, "FORCE_PUNCH_OUT")
                list.add(AlertEvent("FORCE_PUNCH_OUT", ContextCompat.getDrawable(context, R.drawable.ic_exit), forcepunchout,this.forcepunchout!!.size))
            }
            if (shutDown != null && shutDown!!.isNotEmpty()) {
                val logout = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        shutDown!!, "ACTION_SHUTDOWN_GRACEFULLY")
                list.add(AlertEvent("ACTION_SHUTDOWN_GRACEFULLY", ContextCompat.getDrawable(context, R.drawable.ic_shutdown), logout,shutDown!!.size))
            }
            if (phoneUsage != null && phoneUsage!!.isNotEmpty()) {
                val phoneUsage = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        phoneUsage!!, "PHONE_USAGE")
                list.add(AlertEvent("PHONE_USAGE", ContextCompat.getDrawable(context, R.drawable.ic_phone_usage), phoneUsage,this.phoneUsage!!.size))
            }
            if (locationOff != null && locationOff!!.isNotEmpty()) {
                val locationOff = createListOfPolygonAndMarkers(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        locationOff!!, "ACTION_LOCATION_CHANGE")
                list.add(AlertEvent("ACTION_LOCATION_CHANGE", ContextCompat.getDrawable(context, R.drawable.ic_location_off_small), locationOff,this.locationOff!!.size))
            }
            // send to view with extracted event data.
            if (navigator != null) {
                navigator.allEventList(list)
            }
//            if (navigator != null) {
//                navigator.isolateEventResponse(osObject, ostObject, hcObject, hbObject, haObject)
//            }
        }
    }


    /**
     * extract hashMap of polyline and markers for event HC,HA,HB.
     *
     * @param icon      icon
     * @param eventList eventList
     * @param eventType event type
     * @return return object array type with 0 position for map of polyline
     * and 1 position with hashMap of markers.
     */
    private fun createListOfPolygonAndMarkers(bitmapDescriptor: BitmapDescriptor,
                                              eventList: ArrayList<Event>,
                                              eventType: String): ArrayList<Any?> {
        val objects = ArrayList<Any?>(2)
        objects.add(null)
        objects.add(null)


        val polylineMap = HashMap<Int, PolylineOptions>()
        val markersList = ArrayList<MarkerOptions>()
        var osPolyline = ArrayList<LatLng>()
        if (eventList.size == 1) {
            markersList.add(
                    MarkerOptions()
                            .position(LatLng(eventList[0].from?.latitude!!, eventList[0].from?.longitude!!))
                            .icon(bitmapDescriptor)
                            .flat(true)
            )
            objects[0] = polylineMap
            objects[1] = markersList

            return objects
        }
        var count = 0
        for (i in eventList.indices) {
            val start = eventList[i]
            if (i + 1 <= eventList.size - 1) {
                val end = eventList[i + 1]

                //compare time if start time and end time difference is <= 2 sec
                // then create a hashMap of lat lng and add into the map else create a hashMap of
                // markers and increase count value by 1 and initialize new hashMap object.
                if (end.eventStartTime - start.eventEndTime <= 2 * 1000) {
                    osPolyline.add(LatLng(start.from?.latitude!!, start.from?.longitude!!))
                    var color = Color.YELLOW
                    when {
                        eventType.equals("HARSH_CORNERING", ignoreCase = true) -> {
                            color = Color.parseColor("#75AE34")
                        }
                        eventType.equals("HARSH_ACCELERATION", ignoreCase = true) -> {
                            color = Color.parseColor("#EC4B9A")
                        }
                        eventType.equals("HARSH_BREAK", ignoreCase = true) -> {
                            color = Color.parseColor("#EC4B9A")
                        }
                    }

                    polylineMap[count] = PolylineOptions()
                            .addAll(osPolyline)
                            .color(color)
                            .width(polylineWidth)
                } else {
                    if (polylineMap.containsKey(count)) {
                        count++
                        osPolyline = ArrayList()
                    }
                    markersList.add(
                            MarkerOptions()
                                    .position(LatLng(start.from?.latitude!!, start.from?.longitude!!))
                                    .icon(bitmapDescriptor)
                    )
                }
            }
        }

        objects[0] = polylineMap
        objects[1] = markersList

        return objects
    }


    /**
     * extract hashMap of polyline and markers for event Over Speed.
     *
     * @param icon      icon
     * @param eventList eventList
     * @return return object array type with 0 position for map of polyline
     * and 1 position with hashMap of markers.
     */
    private fun createListOfPolygonAndMarkers(eventList: ArrayList<Event>,
                                              bitmapDescriptor: BitmapDescriptor): ArrayList<Any?> {
        val objects = ArrayList<Any?>(2)
        objects.add(null)
        objects.add(null)

        val polylineMap = HashMap<Int, PolylineOptions>()

        val markersList = ArrayList<MarkerOptions>()
        var osPolyline = ArrayList<LatLng>()

        if (eventList.size == 1) {
            markersList.add(
                    MarkerOptions()
                            .position(LatLng(eventList[0].from?.latitude!!,
                                    eventList[0].from?.longitude!!))
                            .icon(bitmapDescriptor)
            )
            objects[0] = polylineMap
            objects[1] = markersList

            return objects
        }
        var count = 0

        for (i in eventList.indices) {
            val start = eventList[i]
            if (i + 1 <= eventList.size - 1) {
                val end = eventList[i + 1]

                Log.e(tag, "${end.eventTime} :event time: ${start.eventTime}")
                //compare time if start time and end time difference is <= 2 sec
                // then create a hashMap of lat-lng and add into the map else create a hashMap of
                // markers and increase count value by 1 and initialize new hashMap object.
                if (end.eventTime - start.eventTime <= 2 * 1000) {
                    osPolyline.add(LatLng(start.from?.latitude!!, start.from?.longitude!!))
                    polylineMap[count] = PolylineOptions()
                            .addAll(osPolyline)
                            .color(Color.RED)
                            .width(polylineWidth)
                } else {
                    if (polylineMap.containsKey(count)) {
                        count++
                        osPolyline = ArrayList()
                    }
                    markersList.add(
                            MarkerOptions()
                                    .position(LatLng(start.from?.latitude!!, start.from?.longitude!!))
                                    .icon(bitmapDescriptor)
                    )
                }
            }
        }

        objects[0] = polylineMap
        objects[1] = markersList

        return objects
    }

    fun onResume(taskDetailActivity: TaskDetailActivity) {
        try {
            val intentFilter = IntentFilter()
            intentFilter.addAction(TrackThat.EVENT_NOFITY)
            LocalBroadcastManager.getInstance(taskDetailActivity)
                    .registerReceiver(eventReceiver, intentFilter)
        } catch (e: Exception) {
            Log.e(tag, "inside onResume(): $e")
            e.printStackTrace()
        }
    }

    fun onPause(context: Context) {
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(eventReceiver)
        } catch (e: Exception) {
            Log.e(tag, "inside onPause(): $e")
            e.printStackTrace()
        }
    }

    fun handleAlerts(intent: Intent) {
        try {
            if (intent.hasExtra(TrackThatConstants.TT_EVENT_TYPE_KEY)) {

                when (val eventName = intent.getStringExtra(TrackThatConstants.TT_EVENT_TYPE_KEY)) {
                    "OVER_SPEEDING",
                    "OVER_STOPPING",
                    "HARSH_ACCELERATION",
                    "HARSH_BREAKING",
                    "HARSH_CORNERING",
                    "PHONE_USAGE",
                    "GEOFENCE_IN",
                    "GEOFENCE_OUT"  -> {
                        navigator.showEventDialog(true, eventName)

                    }

                    else -> {
                        navigator.showEventDialog(false, eventName!!)
                        /*
                        OUT_OF_NETWORK
                        NONE
                        ACTION_LOCATION_CHANGE
                        ACTION_CLEAR_DATA
                        CONNECTIVITY_CHANGE
                        ACTION_BOOT_COMPLETED
                        ACTION_SHUTDOWN
                        ACTION_AIRPLANE_MODE
                    ACTION_MANUAL_BATTERY_REMOVAL
                    ACTION_MANUAL_MOBILE_DATA_OFF
                    PHONE_USAGE
                    ACTION_SHUTDOWN_GRACEFULLY
                    ACTION_DATE_TIME_CHANGE
                         */
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TaskDetailViewModel", "Exception Inside handleAlerts(): $e")
        }
    }

    fun registerLocationObserver() {
        TrackThat.registerLocationObservable(this)
    }

    fun onDestroy() {
        TrackThat.unregisterLocationObservable(this)
    }

    override fun onLocationChange(p0: android.location.Location?) {
        navigator.onLocationChange(p0)
    }

    fun getLiveTrip(lastTimeStamp: Long): List<LocationData>? {
        return TrackThat.getLiveTrip(lastTimeStamp)
    }

}
