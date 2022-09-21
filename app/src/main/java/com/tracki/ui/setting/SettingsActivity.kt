package com.tracki.ui.setting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.FileUrlsResponse
import com.tracki.data.model.response.config.SettingResponse
import com.tracki.data.model.response.config.Settings
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivitySettingsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.emergencymessage.EmergencyMessageActivity
import com.tracki.ui.emergencyphone.EmergencyContactActivity
import com.tracki.utils.*
import com.tracki.utils.geofence.AddGeoFenceUtil
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
 * Created By Rahul
 */
class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingViewModel>(),
        SettingNavigator, CompoundButton.OnCheckedChangeListener {

    @Inject
    lateinit var mSettingViewModel: SettingViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    var REQUEST_READ_CALNDER = 50

    @Inject
    lateinit var httpManager: HttpManager
    private val REQUEST_READ_STORAGE = 49
    private lateinit var mActivitySettingsBinding: ActivitySettingsBinding
    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_settings
    override fun getViewModel() = mSettingViewModel
    var TAG = "SettingsActivity"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    var addGeoFenceUtil: AddGeoFenceUtil? = null
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivitySettingsBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySettingsBinding = viewDataBinding
        mSettingViewModel.navigator = this
        addGeoFenceUtil = AddGeoFenceUtil(this, preferencesHelper)
        setUp()

    }

    private fun setUp() {
        setToolbar(mActivitySettingsBinding.toolbar, getString(R.string.settings))

        switchAutoStart = mActivitySettingsBinding.switchAutoStart
        switchAlertNotification = mActivitySettingsBinding.switchAlertNotification
        switchVoiceAlert = mActivitySettingsBinding.switchVoiceAlert
        switchTimeReminder = mActivitySettingsBinding.switchTimeReminder
        switchLocationReminder = mActivitySettingsBinding.switchLocationReminder
        spinnerTimerSlot = mActivitySettingsBinding.spinnerTimerSlot
        tvMessageTimer = mActivitySettingsBinding.tvMessageTimer
        rlDropDownTimer = mActivitySettingsBinding.rlDropDownTimer

        switchAutoStart?.setOnCheckedChangeListener(this)
        switchVoiceAlert?.setOnCheckedChangeListener(this)
        switchTimeReminder?.setOnCheckedChangeListener(this)
        switchLocationReminder?.setOnCheckedChangeListener(this)

        switchVoiceAlert?.isChecked = preferencesHelper.voiceAlertsTracking
        switchTimeReminder?.isChecked = preferencesHelper.timeReminderFlag
        switchLocationReminder?.isChecked = preferencesHelper.locationReminderFlag
        if(switchTimeReminder!!.isChecked){
            rlDropDownTimer!!.visibility=View.VISIBLE
            //tvMessageTimer!!.text=getString(R.string.update_your_time_reminder_in_prefrences)
        }else{
            rlDropDownTimer!!.visibility=View.GONE
           // tvMessageTimer!!.text=getString(R.string.update_your_time_reminder_settings)


        }
        timeDropDownWork()
        showLoading()
        val api: Api? = TrackiApplication.getApiMap()[ApiType.SETTINGS]
        mSettingViewModel.getSettings(httpManager, api)
    }

    private fun timeDropDownWork() {
        var list:MutableList<String?> = ArrayList()
        list.add("0")
        list.add("5")
        list.add("10")
        list.add("15")
        list.add("30")
        var arrayAdapter = object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, list) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                (v as TextView).typeface = externalFont
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                val externalFont = Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                (v as TextView).typeface = externalFont
                //v.setBackgroundColor(Color.GREEN);
                return v
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimerSlot!!.adapter = arrayAdapter
        spinnerTimerSlot!!.setSelection(list.indexOf(preferencesHelper.timeBeforeTime))
        spinnerTimerSlot!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                preferencesHelper.setBeforeTime(selectedItem)
                CommonUtils.showLogMessage("e", "selectedItem", selectedItem);
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }


    private var switchAutoStart: Switch? = null
    private var switchAlertNotification: Switch? = null
    private var switchVoiceAlert: Switch? = null
    private var switchTimeReminder: Switch? = null
    private var switchLocationReminder: Switch? = null
    private var rlDropDownTimer: RelativeLayout? = null
    private var spinnerTimerSlot: AppCompatSpinner? = null
    private var tvMessageTimer: TextView? = null

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@SettingsActivity)) {

            val settingResponse = Gson().fromJson(result.toString(), SettingResponse::class.java)
            if (settingResponse != null) {
                saveResp(settingResponse.settings)
            }
        }
    }

    override fun handleSaveSettingResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@SettingsActivity)) {
            val settingResponse = Gson().fromJson<SettingResponse>(result.toString(), SettingResponse::class.java)
            if (settingResponse != null) {
                saveResp(settingResponse.settings)
            }
        }
    }

    override fun handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        CommonUtils.showLogMessage("e", "response", result.toString())
        if (CommonUtils.handleResponse(callback, error, result, this@SettingsActivity)) {
            val fileUrlsResponse = Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
            val fileResponseMap = fileUrlsResponse.filesUrl
            if (fileResponseMap!!.containsKey(CommonUtils.TRACK_THAT_DATABASE_NAME_ONLY)) {
                var fileUrlList: java.util.ArrayList<String> = java.util.ArrayList<String>()
                fileUrlList = fileResponseMap[CommonUtils.TRACK_THAT_DATABASE_NAME_ONLY]!!

            }
        } else {

        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val settings = Settings()
        settings.emergencyContacts = TrackiApplication.getEmergencyContacts()
        settings.sosMessage = TrackiApplication.getSosMessage()

        if (buttonView?.id == R.id.switchAutoStart) {
            TrackiApplication.setAutoStart(isChecked)

            settings.alertNotification = TrackiApplication.getAlertNotification()
            settings.autoStart = isChecked
            showLoading()
            val api: Api? = TrackiApplication.getApiMap()[ApiType.SAVE_SETTINGS]
            mSettingViewModel.saveSettings(httpManager, settings, api)

        } else if (buttonView?.id == R.id.switchAlertNotification) {
            TrackiApplication.setAlertNotification(isChecked)

            settings.alertNotification = isChecked
            settings.autoStart = TrackiApplication.getAutoStart()
            showLoading()
            val api: Api? = TrackiApplication.getApiMap()[ApiType.SAVE_SETTINGS]
            mSettingViewModel.saveSettings(httpManager, settings, api)
        } else if (buttonView?.id == R.id.switchVoiceAlert) {
            preferencesHelper.voiceAlertsTracking = isChecked

        } else if (buttonView?.id == R.id.switchTimeReminder) {
            checkCalenderPermission()
        } else if (buttonView?.id == R.id.switchLocationReminder) {
            preferencesHelper.locationReminderFlag = isChecked
            if (!isChecked)
                addGeoFenceUtil!!.removeAllGeofence()
        }


    }

    private fun saveResp(settings: Settings?) {
        switchAutoStart?.isChecked = settings?.autoStart!!
        switchAlertNotification?.isChecked = settings.alertNotification!!

        TrackiApplication.setAutoStart(settings.autoStart!!)
        TrackiApplication.setAlertNotification(settings.alertNotification)
        TrackiApplication.setEmergencyContactList(settings.emergencyContacts)
        TrackiApplication.setSosMessage(settings.sosMessage)
        // start and stop service according to flag.
        CommonUtils.manageTransitionService(this, TrackiApplication.getAutoStart())
    }

    override fun openEmergencyContactActivity() {
        startActivity(EmergencyContactActivity.newIntent(this))
    }

    override fun openEmergencyMessageActivity() {
        startActivity(EmergencyMessageActivity.newIntent(this))
    }

    override fun sendLocalDbToServerActivity() {
        takePermisson()
    }

    fun takePermisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                performBackUpSyncToServer()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), REQUEST_READ_STORAGE)
            }
        } else {
            performBackUpSyncToServer()
        }
    }

    private fun performBackUpSyncToServer() {

        val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + resources.getString(R.string.app_name))
        var success = true
        if (!folder.exists()) success = folder.mkdirs()
        if (success) {

            val outFileName = Environment.getExternalStorageDirectory().toString() + File.separator + resources.getString(R.string.app_name) + File.separator
            val out = outFileName + CommonUtils.TRACK_THAT_DATABASE_NAME
            val saveFileInDevice = SaveFileInDevice()
            saveFileInDevice.backup(this, out)

            val backupDB = File(out)
            val s = arrayOfNulls<String>(1)
            s[0] = backupDB.absolutePath
            try {
                showLoading()
                ZipManager.zip(s, outFileName + CommonUtils.TRACK_THAT_DATABASE_NAME_ONLY + ".zip")
                val file = File(backupDB.absolutePath)
                file.deleteOnExit()
                var arrayList = ArrayList<File>()
                var filereal = File(outFileName + CommonUtils.TRACK_THAT_DATABASE_NAME_ONLY + ".zip")
                arrayList.add(filereal)
                val hashMapFileRequest = java.util.HashMap<String, ArrayList<File>>()
                hashMapFileRequest[CommonUtils.TRACK_THAT_DATABASE_NAME_ONLY] = arrayList
                mSettingViewModel.uploadFileList(hashMapFileRequest, httpManager)
            } catch (e: IOException) {
                hideKeyboard()
                //  Toast.makeText(this, "Unable to zip database. Retry", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                Log.e("MainActivity", e.message)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    performBackUpSyncToServer()
                }
            }
        } else if (requestCode == REQUEST_READ_CALNDER) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    proceedCalenderWork()
                }else{
                    switchTimeReminder!!.isChecked=false
                    proceedCalenderWork()
                }
            }else{
                switchTimeReminder!!.isChecked=false
                proceedCalenderWork()
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    fun checkCalenderPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                proceedCalenderWork()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR), REQUEST_READ_CALNDER)
            }
        } else {
            proceedCalenderWork()
        }
    }

    private fun proceedCalenderWork() {
        preferencesHelper.timeReminderFlag = switchTimeReminder!!.isChecked
        if(switchTimeReminder!!.isChecked){
            rlDropDownTimer!!.visibility=View.VISIBLE
           // tvMessageTimer!!.text=getString(R.string.update_your_time_reminder_in_prefrences)
        }else{
            rlDropDownTimer!!.visibility=View.GONE
           // tvMessageTimer!!.text=getString(R.string.update_your_time_reminder_settings)

        }
    }


}