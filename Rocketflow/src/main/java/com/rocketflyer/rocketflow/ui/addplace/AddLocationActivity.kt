package com.rocketflyer.rocketflow.ui.addplace

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.BaseResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAddLocationBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.common.CustomInfoWindowAdapter
import com.tracki.ui.main.MainActivity
import com.tracki.utils.*
import kotlinx.android.synthetic.main.activity_add_location.*
import javax.inject.Inject

open class AddLocationActivity : BaseActivity<ActivityAddLocationBinding, AddPlaceViewModel>(),
        OnMapReadyCallback, AddPlaceNavigator {
    init {
        System.loadLibrary("keys")
    }

    external fun getGoogleMapKey(): String?
    private var snackBar: Snackbar? = null
    private var hubs: Hub? = null
    private var hubId: String? = ""
    private var cityId: String? = null
    private var regionId: String? = null
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private val TAG = "AddLocationActivity"
    lateinit var view: View
    var hubLocation: HubLocation? = null

    @Inject
    lateinit var mAddPlaceViewModel: AddPlaceViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper


    @Inject
    lateinit var httpManager: HttpManager

    var circle: Circle? = null
    private var address: String? = ""
    private lateinit var mActivityAddLocationBinding: ActivityAddLocationBinding

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu_skip, menu)
        var menuItem = menu.findItem(R.id.action_skip)
        if (intent.hasExtra("from")) {
            //menuItem.isVisible = intent.getStringExtra("from").equals("direct")
            menuItem.isVisible = false
        } else {
            menuItem.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_skip -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_location
    }

    override fun getViewModel(): AddPlaceViewModel {
        return mAddPlaceViewModel!!
    }

    var stepSize = 500
    override fun onBackPressed() {
        if (intent.hasExtra("from")) {
            if(!intent.getStringExtra("from").equals("direct")){
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddLocationBinding = viewDataBinding
        mAddPlaceViewModel!!.navigator = this
        Places.initialize(this@AddLocationActivity, getGoogleMapKey()!!)
        if (intent.hasExtra("from")) {
            if(intent.getStringExtra("from").equals("direct")){
                setToolbarHideBackArrow(mActivityAddLocationBinding.toolbar, getString(R.string.place))
            }else{
                setToolbar(mActivityAddLocationBinding.toolbar, getString(R.string.place))
            }
        } else {
            setToolbar(mActivityAddLocationBinding.toolbar, getString(R.string.place))
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment != null) {
            mapFragment!!.getMapAsync(this)
        }

        seekBar.setMax(50000)
        if(TrackiApplication.getApiMap().containsKey(ApiType.USER_LOCATIONS)) {
            showLoading()
            val api = TrackiApplication.getApiMap()[ApiType.USER_LOCATIONS]
            mAddPlaceViewModel.getLocation(httpManager, api!!)
            if (intent.hasExtra("data")) {
                hubs = intent.getParcelableExtra("data")
                mActivityAddLocationBinding.btnAddPlace.text = getString(R.string.update)
            }
        }

    }

    private val seekBarListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, b: Boolean) {
            var progress = Math.round(progress / stepSize.toFloat()).toInt() * stepSize

            seekBar!!.progress = progress
            tvRange.text = "${progress} Meter";
            if (circle != null)
                circle!!.radius = progress.toDouble()
            else {
                circle = mMap!!.addCircle(CircleOptions()
                        .center(currentLatLng)
                        .radius(progress.toDouble())
                        .strokeColor(Color.parseColor("#3BE731"))
                        .strokeWidth(4f)
                        .fillColor(Color.parseColor("#663BE731")))
            }
            CommonUtils.showLogMessage("e", "radious", "" + progress)


            val cameraPosition: CameraPosition = CameraPosition.Builder()
                    .target(currentLatLng) // Sets the center of the map to location user
                    .zoom(getZoomLevel(circle)) // Sets the zoom
                    /*.bearing(90f) // Sets the orientation of the camera to east
                    .tilt(40f)*/ // Sets the tilt of the camera to 30 degrees
                    .build() // Creates a CameraPosition from the builder
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            var location = Location(currentLatLng.latitude, currentLatLng.longitude)
            hubLocation = HubLocation(location, progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult == null) {
                return
            }
            for (location in locationResult.locations) {
                if (location != null) {
                    removeLocationUpdates(this)
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    // mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
                    // mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
                    seekBar.setProgress(stepSize)
                    tvRange.text = "${stepSize} Meter";
                    if (circle != null)
                        circle!!.remove()
                    circle = mMap!!.addCircle(CircleOptions()
                            .center(currentLatLng)
                            .radius(stepSize.toDouble())
                            .strokeColor(Color.parseColor("#3BE731"))
                            .strokeWidth(4f)
                            .fillColor(Color.parseColor("#663BE731")))
                    var zoomLevel = getZoomLevel(circle)
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel.toFloat()))

                    val cameraPosition: CameraPosition = CameraPosition.Builder()
                            .target(currentLatLng) // Sets the center of the map to location user
                            .zoom(zoomLevel.toFloat()) // Sets the zoom
                            /*.bearing(90f) // Sets the orientation of the camera to east
                            .tilt(40f)*/ // Sets the tilt of the camera to 30 degrees
                            .build() // Creates a CameraPosition from the builder
                    mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    var loc = Location(currentLatLng.latitude, currentLatLng.longitude)
                    hubLocation = HubLocation(loc, stepSize)
                    mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    seekBar.setOnSeekBarChangeListener(seekBarListener)
                    address = CommonUtils.getAddress(this@AddLocationActivity, currentLatLng)
                    mActivityAddLocationBinding.edLocation.setText(CommonUtils.getAddress(this@AddLocationActivity, currentLatLng))
                    // mActivityAddLocationBinding.edPlaceName.setText(CommonUtils.getAddress(this@AddLocationActivity,currentLatLng))

//
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            mMap = googleMap
            CommonUtils.changeGoogleLogoPosition(mapFragment!!, resources.getDimension(R.dimen.dimen_60).toInt(), mMap)



            if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        AppConstants.PERMISSIONS_REQUEST_CODE_LOCATION)
                return
            }
            //request for current location
            //request for current location

//            mMap!!.isMyLocationEnabled=true
            mMap!!.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
            mMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
                override fun onMapClick(latLng: LatLng?) {
                    currentLatLng = latLng!!
                    seekBar.setProgress(stepSize)
                    if (circle != null)
                        circle!!.remove()

                    circle = mMap!!.addCircle(CircleOptions()
                            .center(currentLatLng)
                            .radius(stepSize.toDouble())
                            .strokeColor(Color.parseColor("#3BE731"))
                            .strokeWidth(4f)
                            .fillColor(Color.parseColor("#663BE731")))
                    var zoomLevel = getZoomLevel(circle)
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel.toFloat()))

                    val cameraPosition: CameraPosition = CameraPosition.Builder()
                            .target(currentLatLng) // Sets the center of the map to location user
                            .zoom(zoomLevel) // Sets the zoom
                            /*.bearing(90f) // Sets the orientation of the camera to east
                            .tilt(40f)*/ // Sets the tilt of the camera to 30 degrees
                            .build() // Creates a CameraPosition from the builder
                    mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    var location = Location(currentLatLng.latitude, currentLatLng.longitude)
                    hubLocation = HubLocation(location, stepSize)
                    address = CommonUtils.getAddress(this@AddLocationActivity, currentLatLng)
                    mActivityAddLocationBinding.edLocation.setText(CommonUtils.getAddress(this@AddLocationActivity, currentLatLng))
                }
            })

        }

    }

    override fun openMainActivity() {
        if (intent.hasExtra("from")) {
            if(intent.getStringExtra("from").equals("direct")){
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }else{
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        } else {
            val returnIntent = Intent()
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 21487
        private val TAG = AddLocationActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, AddLocationActivity::class.java)
    }

    override fun openPlaceAutoComplete(view: View) {
        this.view = view
        try {
            val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.e(TAG, e.message)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e(TAG, e.message)
        }
    }

    override fun addLocation(view: View) {
        var placename = edPlaceName.text.toString().trim();
        if (placename.isEmpty()) {
            TrackiToast.Message.showShort(this, "Please Enter Place Name")
        } else if (seekBar.progress<500) {
            TrackiToast.Message.showShort(this, "Please increase radius ")
        } /*else if (regionId == null) {
            TrackiToast.Message.showShort(this, "Please Select Your Region")

        } else if (cityId == null) {
            TrackiToast.Message.showShort(this, "Please Select Your City")
        } */else {
            if (hubLocation != null) {
                showLoading()
                var addPlaceRequest = AddPlaceRequest(cityId, hubId, hubLocation, placename, regionId)
                val api = TrackiApplication.getApiMap()[ApiType.UPDATE_USER_LOCATION]
                mAddPlaceViewModel.updateLocation(httpManager, addPlaceRequest, api!!)
            }

        }

    }

    override fun handleAddPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            var jsonConverter = JSONConverter<BaseResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), BaseResponse::class.java)
            if(response.hubId!=null&&response.hubId!!.isNotEmpty()) {
                if (preferencesHelper.userHubList != null && preferencesHelper.userHubList.isNotEmpty()) {
                    var hubs = preferencesHelper.userHubList
                    var hub = Hub()
                    hub.hubId=response.hubId!!
                    hub.name=edPlaceName.text.toString().trim()
                    hub.hubLocation=hubLocation
                    hubs.add(hub)
                    preferencesHelper.saveUserHubList(hubs)
                } else {
                    var hubs = ArrayList<Hub>()
                    var hub = Hub()
                    hub.hubId=response.hubId!!
                    hub.name=edPlaceName.text.toString().trim()
                    hub.hubLocation=hubLocation
                    hubs.add(hub)
                    preferencesHelper.saveUserHubList(hubs)
                }
            }
            openMainActivity()

        }

    }

    open fun getZoomLevel(circle: Circle?): Float {
        var zoomLevel = 15f
        if (circle != null) {
            val radius = circle.radius
            val scale = radius / 500
            zoomLevel = (16 - Math.log(scale) / Math.log(2.0)).toFloat()
        }
        CommonUtils.showLogMessage("e", "zoomLevel", "" + zoomLevel)
        return zoomLevel - 0.7f
    }

    override fun handleStateCityResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            var jsonConverter = JSONConverter<LocationListResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), LocationListResponse::class.java)
            if (response.successful!!) {
                if (hubs != null) {
                    seekBar.setOnSeekBarChangeListener(seekBarListener)
                    // hubs= response.hubs!![0]
                    if (hubs != null) {
                        hubId = hubs!!.hubId
                        cityId = hubs!!.cityId
                        regionId = hubs!!.regionId
                        hubLocation = hubs!!.hubLocation
                        var loc = hubLocation!!.location
                        currentLatLng = LatLng(loc!!.latitude!!, loc.longitude!!)

                        if (hubLocation!!.radius != null) {
                            seekBar.setProgress(hubLocation!!.radius!!.toInt())
                            tvRange.text = "${hubLocation!!.radius!!.toInt()} Meter";
                            if (circle != null)
                                circle!!.remove()

                            circle = mMap!!.addCircle(CircleOptions()
                                    .center(currentLatLng)
                                    .radius(hubLocation!!.radius!!.toDouble())
                                    .strokeColor(Color.parseColor("#3BE731"))
                                    .strokeWidth(4f)
                                    .fillColor(Color.parseColor("#663BE731")))
                            var zoomLevel = getZoomLevel(circle)
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel))

                        }


                        mActivityAddLocationBinding.edLocation.setText(CommonUtils.getAddress(this, currentLatLng))
                        mActivityAddLocationBinding.edPlaceName.setText(hubs!!.name)
                    }

                } else {
                    requestCurrentLocation(locationCallback)
                }

                var list = response.regionCityMap
                var hubs = response.hubs
                if(hubs!=null&& hubs.isNotEmpty()){
                    preferencesHelper.saveUserHubList(hubs)

                }



                if (list != null && list.isNotEmpty()) {
                    var regionList = ArrayList<String>()
                    var regionIdList = ArrayList<String>()
                    for (data in list) {
                        regionList.add(data.regionName!!)
                        regionIdList.add(data.regionId!!)
                    }
                    if (regionIdList.size <= 1) {
                        if (regionIdList.isNotEmpty()) {
                            regionId = regionIdList[0]
                            var cityList = list.get(0).cities
                            if (cityList != null) {
                                var cityNameList = ArrayList<String>()
                                var cityIdList = ArrayList<String>()
                                for (data in cityList) {
                                    cityNameList.add(data.name!!)
                                    cityIdList.add(data.cityId!!)
                                }
                                if (cityIdList.size <= 1) {
                                    if (cityIdList.isNotEmpty())
                                        cityId = cityIdList[0]
                                    llCity.visibility = View.GONE
                                } else {
                                    llCity.visibility = View.VISIBLE
                                }
                            }
                        }
                        llState.visibility = View.GONE
                    } else {
                        llState.visibility = View.VISIBLE
                    }


                    var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regionList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnState!!.adapter = arrayAdapter
                    if (regionId != null) {
                        var position = regionIdList.indexOf(regionId!!)
                        if (position != -1)
                            spnState.setSelection(position)

                    }
                    spnState!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            regionId = regionIdList[position]
                            CommonUtils.showLogMessage("e", "regonid", regionId)
                            spnCity.adapter = null
                            var cityList = list.get(position).cities
                            if (cityList != null) {
                                var cityNameList = ArrayList<String>()
                                var cityIdList = ArrayList<String>()
                                for (data in cityList) {
                                    cityNameList.add(data.name!!)
                                    cityIdList.add(data.cityId!!)
                                }
                                if (cityIdList.size <= 1) {
                                    if (cityIdList.isNotEmpty())
                                        cityId = cityIdList[0]
                                    llCity.visibility = View.GONE
                                } else {
                                    llCity.visibility = View.VISIBLE
                                }
                                var cityarrayAdapter = ArrayAdapter(this@AddLocationActivity, android.R.layout.simple_spinner_item, cityNameList);
                                cityarrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnCity!!.adapter = cityarrayAdapter
                                if (cityId != null) {
                                    var pos = cityIdList.indexOf(cityId!!)
                                    if (pos != -1)
                                        spnCity.setSelection(pos)
                                }
                                spnCity!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(p0: AdapterView<*>?) {

                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                        cityId = cityIdList[position]
                                        CommonUtils.showLogMessage("e", "cityid", cityId)
                                    }

                                }
                            }

                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }


                }


            }


        }
    }


    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.e(TAG, "Place: " + place.name + ", " + place.id)
                    hideKeyboard()
                    currentLatLng = place.latLng!!


                    seekBar.setProgress(stepSize)
                    tvRange.text = "${stepSize} Meter";

                    if (circle != null)
                        circle!!.remove()

                    circle = mMap!!.addCircle(CircleOptions()
                            .center(currentLatLng)
                            .radius(stepSize.toDouble())
                            .strokeColor(Color.parseColor("#3BE731"))
                            .strokeWidth(4f)
                            .fillColor(Color.parseColor("#663BE731")))
                    var zoomLevel = getZoomLevel(circle)
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel.toFloat()))

                    val cameraPosition: CameraPosition = CameraPosition.Builder()
                            .target(currentLatLng) // Sets the center of the map to location user
                            .zoom(zoomLevel.toFloat()) // Sets the zoom
                            /*.bearing(90f) // Sets the orientation of the camera to east
                            .tilt(40f)*/ // Sets the tilt of the camera to 30 degrees
                            .build() // Creates a CameraPosition from the builder
                    mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    var location = Location(currentLatLng.latitude, currentLatLng.longitude)
                    location.locationId = place.id.toString()
                    hubLocation = HubLocation(location, stepSize)

                    mActivityAddLocationBinding.edLocation.setText(place.name)
                    address = CommonUtils.getAddress(this@AddLocationActivity, currentLatLng)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i(TAG, status.statusMessage)
                    TrackiToast.Message.showShort(this, status.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> // The user canceled the operation.
                    TrackiToast.Message.showShort(this, "operation cancelled.")
            }
        }


    }

    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityAddLocationBinding.rlMain, getString(R.string.please_check_your_internet_connection))
    }




}
