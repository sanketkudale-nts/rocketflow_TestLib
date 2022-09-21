package com.rocketflyer.rocketflow.ui.emergencyphone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.EmergencyContact
import com.tracki.data.model.response.config.SettingResponse
import com.tracki.data.model.response.config.Settings
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityEmergencyContactBinding
import com.tracki.ui.addcontact.AddEmergencyContactActivity
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.utils.ApiType
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.TrackiToast
import javax.inject.Inject

/**
 * Created by rahul on 5/12/18
 */
class EmergencyContactActivity : BaseActivity<ActivityEmergencyContactBinding, EmergencyContactViewModel>(),
        EmergencyContactNavigator, EmergencyContactAdapter.ContactsListener {
    override fun handleSaveSettingResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@EmergencyContactActivity)) {
            val settingResponse = Gson().fromJson<SettingResponse>(result.toString(), SettingResponse::class.java)
            if (settingResponse != null) {
                saveResp(settingResponse.settings!!)
            }
        }
    }

    private fun saveResp(settings: Settings) {
        TrackiApplication.setAutoStart(settings.autoStart!!)
        TrackiApplication.setAlertNotification(settings.alertNotification)
        TrackiApplication.setEmergencyContactList(settings.emergencyContacts)
        TrackiApplication.setSosMessage(settings.sosMessage)
    }

    @Inject
    lateinit var mEmergencyContactViewModel: EmergencyContactViewModel
    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    @Inject
    lateinit var emergencyContactAdapter: EmergencyContactAdapter
    @Inject
    lateinit var httpManager: HttpManager

    private var contactList: ArrayList<EmergencyContact>? = null
    private lateinit var mActivityEmergencyContactBinding: ActivityEmergencyContactBinding

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_emergency_contact
    override fun getViewModel() = mEmergencyContactViewModel

    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityEmergencyContactBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityEmergencyContactBinding = viewDataBinding
        mEmergencyContactViewModel.navigator = this
        setUp()
    }

    private lateinit var toolbar: Toolbar

    private fun setUp() {
        toolbar = mActivityEmergencyContactBinding.toolbar
        val tvMessage = mActivityEmergencyContactBinding.tvMessage
        setToolbar(toolbar, getString(R.string.emergency_contacts))
        contactList = TrackiApplication.getEmergencyContacts()
        if (contactList == null || contactList?.size == 0) {
            contactList = ArrayList()
        } else {
            tvMessage.visibility = View.VISIBLE
        }
        setUpRecyclerView()

        emergencyContactAdapter.addItems(contactList!!)
    }

    private lateinit var rvContactList: RecyclerView

    private fun setUpRecyclerView() {
        rvContactList = mActivityEmergencyContactBinding.rvContactList
        rvContactList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvContactList.adapter = emergencyContactAdapter
        emergencyContactAdapter.setListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.add_number, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawable = item.icon
        if (drawable is Animatable) {
            (drawable as Animatable).start()
        }
        return when (item.itemId) {
            R.id.action_add -> {
                if (contactList != null && contactList!!.size < 3) {
                    startActivityForResult(AddEmergencyContactActivity.newIntent(this), AppConstants.REQUEST_CODE_ADD_EMERGENCY_CONTACT)
                } else {
                    TrackiToast.Message.showLong(this@EmergencyContactActivity, "Cannot add more than 3 contacts")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.REQUEST_CODE_ADD_EMERGENCY_CONTACT -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        updateContactList(data)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            AppConstants.REQUEST_CODE_EDIT_EMERGENCY_CONTACT -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        updateContactList(data)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else -> {
            }
        }
    }

    /**
     * update the contact hashMap.
     */
    private fun updateContactList(data: Intent?) {
        if (data?.hasExtra(AppConstants.Extra.EXTRA_CONTACT_NAME)!! &&
                data.hasExtra(AppConstants.Extra.EXTRA_CONTACT_NUMBER)) {

            val contactResponse = EmergencyContact(
                    data.getStringExtra(AppConstants.Extra.EXTRA_CONTACT_NAME)!!,
                    data.getStringExtra(AppConstants.Extra.EXTRA_CONTACT_NUMBER)!!)
            if (data.hasExtra(AppConstants.Extra.EXTRA_CONTACT_ID)) {
                //if id exist then update that position
                val pos = data.getIntExtra(AppConstants.Extra.EXTRA_CONTACT_ID, -1)
                contactList?.set(pos, contactResponse)
            } else {
                var bool = false
                for (i in contactList!!.indices) {
                    if (contactResponse.mobile == contactList!![i].mobile) {
                        bool = true
                        break
                    }
                }
                if (!bool) {
                    //else add into the hashMap
                    contactList?.add(contactResponse)
                } else {
                    TrackiToast.Message.showShort(this@EmergencyContactActivity,
                            "Contact already exists")
                }
            }

            TrackiApplication.setEmergencyContactList(contactList)
            preferencesHelper.emergencyContacts = contactList

            emergencyContactAdapter.addItems(contactList!!)

            if (contactList!!.size > 0) {
                mActivityEmergencyContactBinding.tvMessage.visibility = View.VISIBLE
            } else {
                mActivityEmergencyContactBinding.tvMessage.visibility = View.GONE
            }

            hitApi()
        }
    }

    private fun hitApi() {

        val settings = Settings()
        settings.emergencyContacts = contactList
        settings.sosMessage = TrackiApplication.getSosMessage()
        settings.alertNotification = TrackiApplication.getAlertNotification()
        settings.autoStart = TrackiApplication.getAutoStart()


        showLoading()
        val api: Api? = TrackiApplication.getApiMap()[ApiType.SAVE_SETTINGS]
        mEmergencyContactViewModel.saveSettings(httpManager, settings, api)
    }

    /*Delete contact from hashMap*/
    override fun onDeleteContact(response: EmergencyContact) {
        val dialog = DoubleButtonDialog(this,
                true,
                null,
                getString(R.string.delete_contact),
                getString(R.string.yes),
                getString(R.string.no),
                object : OnClickListener {
                    override fun onClickCancel() {

                    }

                    override fun onClick() {
                        try {
                            if (contactList != null) {
                                for (i in contactList!!.indices) {
                                    if (response.mobile == contactList!![i].mobile) {
                                        contactList!!.removeAt(i)
                                        break
                                    }
                                }
                                //save contact hashMap into preferences
                                preferencesHelper.emergencyContacts = contactList
                                emergencyContactAdapter.notifyDataSetChanged()

                                if (contactList!!.size > 0) {
                                    mActivityEmergencyContactBinding.tvMessage.visibility = View.VISIBLE
                                } else {
                                    mActivityEmergencyContactBinding.tvMessage.visibility = View.GONE
                                }

                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        dialog.show()
    }

    override fun onEditContact(response: EmergencyContact, position: Int) {
        try {
            //start activity and get result
            startActivityForResult(AddEmergencyContactActivity.newIntent(this)
                    .putExtra(AppConstants.Extra.EXTRA_CONTACT_ID, position)
                    .putExtra(AppConstants.Extra.EXTRA_CONTACT_NAME, response.name)
                    .putExtra(AppConstants.Extra.EXTRA_CONTACT_NUMBER, response.mobile),
                    AppConstants.REQUEST_CODE_EDIT_EMERGENCY_CONTACT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        fun newIntent(context: Context) = Intent(context, EmergencyContactActivity::class.java)
    }
}