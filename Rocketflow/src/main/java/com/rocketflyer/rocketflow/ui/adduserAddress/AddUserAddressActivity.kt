
package com.rocketflyer.rocketflow.ui.adduserAddress

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.Address
import com.tracki.data.model.request.AddressInfo
import com.tracki.data.model.request.LocationX
import com.tracki.data.model.response.config.EmpData
import com.tracki.data.model.response.config.GeoCoordinates
import com.tracki.data.model.response.config.ProfileInfo
import com.tracki.data.model.response.config.UserData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAdduserAddressBinding
import com.tracki.ui.addcustomer.CustomerInfoScreenFragment
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.Log
import com.tracki.utils.TrackiToast
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.network.TrackThatCallback
import com.trackthat.lib.models.ErrorResponse
import com.trackthat.lib.models.SuccessResponse
import com.trackthat.lib.models.TrackthatLocation
import javax.inject.Inject

class AddUserAddressActivity : BaseActivity<ActivityAdduserAddressBinding,AddAddressViewModel>() ,AddAddressNavigator,View.OnClickListener{

    init {
        System.loadLibrary("keys")
    }

    external fun getGoogleMapKey(): String?
    private var placeName: String?=null
    private var userId: String? = null
    private var rollId: String? = null

    @Inject
    lateinit var mAddAddressViewModel: AddAddressViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    lateinit var binding: ActivityAdduserAddressBinding

    private var userData: ProfileInfo?=null
    private var addressInfo:AddressInfo?=null
    private var action: String? = null
    private var currentLocation: GeoCoordinates? = null
    private lateinit var startLatLng: LatLng
    private var isUpdate: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=viewDataBinding
        mAddAddressViewModel.navigator=this
        Places.initialize(this, getGoogleMapKey()!!)
        if(intent.hasExtra("action")){
            action=intent.getStringExtra("action")
            if(action=="edit"){
                binding.btnCLick.text="Update"
                isUpdate=true
                setToolbar(binding.toolbar, "Update Address")
            }else{
                binding.btnCLick.text="Add "
                isUpdate=false
                setToolbar(binding.toolbar, "Add Address")
                getCurrentLocation()

            }
        }
        binding.btnCLick.setOnClickListener(this)
        if(intent.hasExtra("empData")) {
            userData= intent.getSerializableExtra("empData") as ProfileInfo?
            rollId=userData!!.roleId
            userId=userData!!.userId
            mAddAddressViewModel.performShowHideView(this,preferencesHelper,rollId!!)
            binding.tvLocationLabel.text=mAddAddressViewModel.addressLabel.get()

        }
        if(intent.hasExtra("dataModel")) {
            addressInfo=intent.getParcelableExtra("dataModel")
            if(addressInfo!=null&&addressInfo!!.pincode!=null) {
                binding.edPincode.setText(addressInfo!!.pincode!!)
            }
            if(addressInfo!!.address!=null&& addressInfo!!.address!!.isNotEmpty()){
                binding.edLandMark.setText(addressInfo!!.address!!)
            }
            if(addressInfo!!.location!=null&&addressInfo!!.location!!.address!=null&& addressInfo!!.location!!.address!!.isNotEmpty()){
                binding.edLocation.setText(addressInfo!!.location!!.address)
            }
            if(addressInfo!=null&&addressInfo!!.address!=null&&addressInfo!!.location!!.address!=null) {
                startLatLng= LatLng(addressInfo!!.location!!.location!!.latitude!!,addressInfo!!.location!!.location!!.longitude!!);
            }

        }



    }

    override fun getBindingVariable(): Int {
       return BR.viewModel
    }

    override fun getLayoutId(): Int {
      return R.layout.activity_adduser_address
    }

    override fun getViewModel(): AddAddressViewModel {
       return mAddAddressViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            onSuccess()
        } else {
            onCancel()
        }
    }

    override fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
                onSuccess()
        } else {
            onCancel()
        }
    }

    override fun openPlaceAutoComplete(view: View) {
        try {
            val fields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
            startActivityForResult(intent,AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.e(TAG, e.message)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e(TAG, e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            hideKeyboard()
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.e(TAG, "Place: " + place.name + ", " + place.id)
                    startLatLng = place.latLng!!
                    if (place.name != null) {
                        placeName = place.name
                        binding.edLocation.setText(placeName)
                    }
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


    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 23487
        private val TAG = AddUserAddressActivity::class.java.simpleName


    }
    fun getCurrentLocation() {
        TrackThat.getCurrentLocation(object : TrackThatCallback() {
            override fun onSuccess(successResponse: SuccessResponse) {
                val loc = successResponse.responseObject as TrackthatLocation
                currentLocation = GeoCoordinates()
                currentLocation!!.latitude = loc.latitude
                currentLocation!!.longitude = loc.longitude
                startLatLng = LatLng(loc.latitude, loc.longitude)
                    placeName = CommonUtils.getAddress(this@AddUserAddressActivity, startLatLng)
                binding.edLocation.setText(placeName)
            }

            override fun onError(errorResponse: ErrorResponse) {
                currentLocation = null
            }
        })
    }

    override fun onClick(v: View?) {
       when(v!!.id){
           R.id.btnCLick->{
               var location = binding.edLocation.text.toString().trim()
               var pincode=binding.edPincode.text.toString().trim()
               var landmark=binding.edLandMark.text.toString().trim()
               if (mAddAddressViewModel.isAddress.get()!! && mAddAddressViewModel.isAddressRequired.get()!! && location.isEmpty()) {
                   TrackiToast.Message.showShort(this, "Please Enter Address")
               }
               else if(pincode.length<6){
                   TrackiToast.Message.showShort(this, "Pincode is not less than 6 digit")
               }else if(landmark.isEmpty()){
                   TrackiToast.Message.showShort(this, "Please enter landmark")
               }
               else if(pincode.isEmpty()){
                   TrackiToast.Message.showShort(this, "Please enter pincode")
               }else{
                   var addressInfo = AddressInfo()
                   addressInfo.address = landmark
                   var loc = Address()
                   loc.address = location
                   var locx = LocationX()
                   locx.latitude = startLatLng.latitude
                   locx.longitude = startLatLng.longitude
                   var coordinates = ArrayList<Double>()
                   coordinates.add(locx.latitude!!)
                   coordinates.add(locx.longitude!!)
                   locx.coordinates = coordinates
                   loc.location = locx
                   addressInfo.location = loc
                   addressInfo.pincode=pincode
                   addressInfo.userId=userId
                   if (!isUpdate) {
                       if (TrackiApplication.getApiMap() != null && TrackiApplication.getApiMap()[ApiType.USER_ADDRESS_ADD] != null) {
                           showLoading()
                           var api = TrackiApplication.getApiMap()[ApiType.USER_ADDRESS_ADD]
                           mAddAddressViewModel.addAddAddress(httpManager, addressInfo, api!!)
                       }
                   } else {
                       showLoading()
                       if (this.addressInfo!!.placeId != null && this.addressInfo!!.placeId!!.isNotEmpty()) {
                           addressInfo.placeId = this.addressInfo!!.placeId
                       }
                       if (TrackiApplication.getApiMap() != null && TrackiApplication.getApiMap()[ApiType.USER_ADDRESS_UPDATE] != null) {
                           var api = TrackiApplication.getApiMap()[ApiType.USER_ADDRESS_UPDATE]
                           mAddAddressViewModel.updateAddress(httpManager, addressInfo, api!!)
                       }
                   }
               }

           }
       }
    }
     fun onSuccess() {
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(BaseActivity.RESULT_OK, returnIntent)
        finish()
    }

     fun onCancel() {
        val returnIntent = Intent()
        setResult(BaseActivity.RESULT_CANCELED, returnIntent)
        finish()
    }
}