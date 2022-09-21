package com.tracki.ui.placelist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityMyPlaceListBinding
import com.tracki.ui.addplace.AddLocationActivity
import com.tracki.ui.addplace.Hub
import com.tracki.ui.addplace.LocationListResponse
import com.tracki.ui.addplace.PLACETYPE
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import javax.inject.Inject


open class MyPlaceListActivity : BaseActivity<ActivityMyPlaceListBinding,
        MyPlacesViewModel>(), MyPlaceNavigator , OnRefreshListener,MyPlaceListAdapter.DeletePlaceListener{


    private var selectedPosition: Int=0

    @Inject
    lateinit var mMyPlaceViewModel: MyPlacesViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private lateinit var mActivityMyPlaceBinding: ActivityMyPlaceListBinding

    @Inject
    lateinit var httpManager: HttpManager

    lateinit var adapter:MyPlaceListAdapter
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityMyPlaceBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMyPlaceBinding = viewDataBinding
        mMyPlaceViewModel!!.navigator = this

        setToolbar(mActivityMyPlaceBinding.toolbar, getString(R.string.my_place))


        // SwipeRefreshLayout
        mSwipeRefreshLayout = mActivityMyPlaceBinding.swipeContainer
        mSwipeRefreshLayout!!.setOnRefreshListener(this)
        mSwipeRefreshLayout!!.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)
        showLoading()
        val api = TrackiApplication.getApiMap()[ApiType.USER_LOCATIONS]
        mMyPlaceViewModel.getLocation(httpManager, api!!)
        adapter= MyPlaceListAdapter(this)
        mActivityMyPlaceBinding.adapter=adapter
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_place_list
    }

    override fun getViewModel(): MyPlacesViewModel {
        return mMyPlaceViewModel
    }


    override fun handleMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        // Stopping swipe refresh
        mSwipeRefreshLayout!!.isRefreshing = false
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            var jsonConverter = JSONConverter<LocationListResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), LocationListResponse::class.java)
            if (response.successful!!) {
                if(response.hubs!=null && response.hubs!!.isNotEmpty()){
                    var hubs= response.hubs
                    adapter.addData(hubs!!)
                    preferencesHelper.saveUserHubList(hubs)
                    //adapter.addData(ArrayList())

                }else{
                    adapter.addData(ArrayList())
                    preferencesHelper.saveUserHubList(ArrayList())
                }
            }
        }

    }

    override fun handleDeleteMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        // Stopping swipe refresh
        if (CommonUtils.handleResponse(callback, error, result, this)) {
           if(adapter.getList().isNotEmpty()){
               if(preferencesHelper.selectedLocation!=null&& adapter.getList()[selectedPosition].hubId!=null){
                   if(preferencesHelper.selectedLocation.equals(adapter.getList()[selectedPosition].hubId)){
                       preferencesHelper.selectedLocation=null
                   }
               }
               adapter.notifyItemRemoved(selectedPosition)
               adapter.getList().removeAt(selectedPosition)
               if(adapter.getList().size>0){
                   preferencesHelper.selectedLocation=adapter.getList()[0].hubId
               }
               preferencesHelper.saveUserHubList(adapter.getList())
           }
        }

    }

    override fun openAddPlaceActivity() {
        val intent = AddLocationActivity.newIntent(this)
        startActivityForResult(intent, LAUNCH_ADD_LOCATION_ACTIVITY)


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LAUNCH_ADD_LOCATION_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                showLoading()
                val api = TrackiApplication.getApiMap()[ApiType.USER_LOCATIONS]
                mMyPlaceViewModel.getLocation(httpManager, api!!)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    companion object {
        private val TAG = MyPlaceListActivity::class.java.simpleName
        val LAUNCH_ADD_LOCATION_ACTIVITY: Int=111
        fun newIntent(context: Context) = Intent(context, MyPlaceListActivity::class.java)
    }

    override fun onRefresh() {
        showLoading()
        val api = TrackiApplication.getApiMap()[ApiType.USER_LOCATIONS]
        mMyPlaceViewModel.getLocation(httpManager, api!!)
    }

    override fun delete(hub: Hub, position: Int) {

        val message = getString(R.string.are_you_sure_want_to_delete_this_place)
        DoubleButtonDialog(
                this,
                true,
                null,
                message,
                getString(R.string.delete),
                getString(R.string.cancel), object : OnClickListener {
            override fun onClickCancel() {}
            override fun onClick() {
                if(hub.type!=null&&hub.type== PLACETYPE.USER.name)
                {
                    selectedPosition=position
                    val api = TrackiApplication.getApiMap()[ApiType.REMOVE_USER_LOCATION]
                    if(api!=null) {
                        showLoading()
                        mMyPlaceViewModel.deleteUserLocation(httpManager, api!!, hub)
                    }
                }
            }
        }
        ).show()
    }
}
