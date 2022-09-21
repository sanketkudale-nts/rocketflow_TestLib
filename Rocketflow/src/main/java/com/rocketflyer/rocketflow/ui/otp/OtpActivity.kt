package com.rocketflyer.rocketflow.ui.otp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.OtpRequest
import com.tracki.data.model.response.config.ConfigResponse
import com.tracki.data.model.response.config.OtpResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityOtpBinding
import com.tracki.ui.attendance.punchInOut.PunchInPunchOutData
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.main.MainActivity
import com.tracki.ui.otp.OtpActivity
import com.tracki.utils.*
import com.tracki.utils.TrackiToast.Message.showLong
import com.tracki.utils.TrackiToast.Message.showShort
import com.trackthat.lib.TrackThat
import java.util.*
import javax.inject.Inject


/**
 * Created by rahul on 5/9/18
 */
class OtpActivity : BaseActivity<ActivityOtpBinding?, OtpViewModel?>(), OtpNavigator, MySMSBroadcastReceiver.OTPReceiveListener {
    private lateinit var smsReceiver: MySMSBroadcastReceiver

    @JvmField
    @Inject
    var mOtpViewModel: OtpViewModel? = null

    @JvmField
    @Inject
    var httpManager: HttpManager? = null

    @JvmField
    @Inject
    var preferencesHelper: PreferencesHelper? = null
    var mobile: String? = null
    private var mActivityOtpBinding: ActivityOtpBinding? = null

    //Declare timer
    private var cTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var snackBar: Snackbar? = null
    private lateinit var mCurrentlyFocusedEditText: EditText
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }


    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityOtpBinding!!.rlMain, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityOtpBinding = viewDataBinding
        mOtpViewModel!!.navigator = this
        startTimer()
        setToolbar(mActivityOtpBinding!!.toolbar, "Verify Mobile Number")
        setFocusListener()
       // setOnTextChangeListener()
//        var appSignature = AppSignatureHelper(this)
//        appSignature.appSignatures
        startSmsListener()
        if (intent != null && intent.hasExtra(AppConstants.Extra.NUMBER_EXTRA)) {
            mobile = intent.getStringExtra(AppConstants.Extra.NUMBER_EXTRA)

//            String[] mob = s.split(" ");
//            mobile = mob[1];
            mActivityOtpBinding!!.tvNumber.text = "+91 $mobile"
        }

        mActivityOtpBinding!!.ivEdit.setOnClickListener {
            onBackPressed()
            /*openDialogForChangeMobile(mobile)*/
        }
    }

    private fun openDialogForChangeMobile(mobile: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_change_mobile)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        val etMobile = dialog.findViewById<EditText>(R.id.etMobile)
        val submit = dialog.findViewById<Button>(R.id.btnSubmit)
        val cancel = dialog.findViewById<Button>(R.id.btnCancel)
        etMobile.setText(mobile)
        dialog.window!!.attributes = lp
        cancel.setOnClickListener { dialog.dismiss() }
        submit.setOnClickListener {
            val stMobile = etMobile.text.toString().trim { it <= ' ' }
            if (CommonUtils.isViewNullOrEmpty(stMobile)) {
                TrackiToast.Message.showShort(this@OtpActivity, getString(R.string.field_cannot_be_empty))
            } else if (!CommonUtils.isMobileValid(stMobile)) {
                TrackiToast.Message.showShort(this@OtpActivity, getString(R.string.invalid_mobile))
            } else {
                this@OtpActivity.mobile = stMobile
                mActivityOtpBinding!!.tvNumber.text = "+91 " + this@OtpActivity.mobile
                dialog.dismiss()
                onClickResend()
            }
        }
        if (!dialog.isShowing) dialog.show()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_otp
    }

    override fun getViewModel(): OtpViewModel {
        return mOtpViewModel!!
    }

    override fun onBackClick() {
        if (intent != null) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_CHANGE_MOBILE)) {
                setResult(RESULT_CANCELED)
                finish()
            } else {
                onBackPressed()
            }
        } else {
            onBackPressed()
        }
    }

    override fun onClickResend() {
        hideKeyboard()
        showLoading()
        cancelTimer()
        val apiUrl = TrackiApplication.getApiMap()[ApiType.LOGIN]
        mOtpViewModel!!.login(mobile, NextScreen.LOGIN, httpManager, apiUrl)
    }
    fun performVerify(){
        CommonUtils.preventTwoClick(findViewById(R.id.btnContinue))
        val firstSrt: String = mActivityOtpBinding!!.etFirst.getText().toString()
        val secondSrt: String = mActivityOtpBinding!!.etSecond.getText().toString()
        val thirdSrt: String = mActivityOtpBinding!!.etThird.getText().toString()
        val fourthSrt: String = mActivityOtpBinding!!.etFourth.getText().toString()
        val fifthSrt: String = mActivityOtpBinding!!.etFifth.getText().toString()
        val sixthSrt: String = mActivityOtpBinding!!.etSixth.getText().toString()
        var otp = firstSrt + secondSrt + thirdSrt + fourthSrt + fifthSrt + sixthSrt
        if (!mOtpViewModel!!.isValidOtp(otp)) {
            TrackiToast.Message.showShort(this@OtpActivity, getString(R.string.invalid_otp))
            return
        }
        hideKeyboard()
        val api = TrackiApplication.getApiMap()[ApiType.VERIFY_MOBILE]
        //        Api api = new Api();
//        api.setName(ApiType.VERIFY_MOBILE);
//        api.setUrl(BuildConfig.BASE_URL+"verifyMobile");
        mOtpViewModel!!.verifyOtpServerRequest(OtpRequest(mobile!!, otp), httpManager, api)
    }

    override fun verifyOtp() {
        CommonUtils.preventTwoClick(findViewById(R.id.btnContinue))
        val firstSrt: String = mActivityOtpBinding!!.etFirst.getText().toString()
        val secondSrt: String = mActivityOtpBinding!!.etSecond.getText().toString()
        val thirdSrt: String = mActivityOtpBinding!!.etThird.getText().toString()
        val fourthSrt: String = mActivityOtpBinding!!.etFourth.getText().toString()
        val fifthSrt: String = mActivityOtpBinding!!.etFifth.getText().toString()
        val sixthSrt: String = mActivityOtpBinding!!.etSixth.getText().toString()
        var otp = firstSrt + secondSrt + thirdSrt + fourthSrt + fifthSrt + sixthSrt
        if (!mOtpViewModel!!.isValidOtp(otp)) {
            TrackiToast.Message.showShort(this@OtpActivity, getString(R.string.invalid_otp))
            return
        }
        hideKeyboard()
        showLoading()
        val api = TrackiApplication.getApiMap()[ApiType.VERIFY_MOBILE]
        //        Api api = new Api();
//        api.setName(ApiType.VERIFY_MOBILE);
//        api.setUrl(BuildConfig.BASE_URL+"verifyMobile");
        mOtpViewModel!!.verifyOtpServerRequest(OtpRequest(mobile!!, otp), httpManager, api)

    }

    override fun deviceChange() {

    }

    override fun handleResponse(apiCallback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            val baseResponse = Gson().fromJson(result.toString(), OtpResponse::class.java)
            if (baseResponse.profileDetail != null) {
                preferencesHelper!!.userDetail = baseResponse.profileDetail!!
            }
            val newShiftMap = baseResponse.shifts
            if (newShiftMap != null && newShiftMap.size > 0) {
                preferencesHelper!!.oldShiftMap = newShiftMap
            } else {
                preferencesHelper!!.remove(AppConstants.OLD_SHIFT_MAP)
            }
            if (baseResponse.verificationId != null) {
                //save verificationId for security purpose
                preferencesHelper!!.verificationId = baseResponse.verificationId
            }
            if (baseResponse.loginToken != null) {
                //save session data
                preferencesHelper!!.loginToken = baseResponse.loginToken
                CommonUtils.showLogMessage("e", "login_token", baseResponse.loginToken)
            }
            if (baseResponse.roleId != null) {
                //save session data
                preferencesHelper!!.userRoleId = baseResponse.roleId
                CommonUtils.showLogMessage("e", "roll_id", baseResponse.roleId)
            }
            if (baseResponse.userTypes != null) preferencesHelper!!.saveUserTypeList(baseResponse.userTypes)
            mOtpViewModel!!.getConfig(httpManager, baseResponse.nextScreen, baseResponse.sdkAccessId)

//            if (getIntent() != null && getIntent().hasExtra(AppConstants.Extra.EXTRA_LOGOUT)) {
//                perFormNextScreenTask(baseResponse.getNextScreen(), baseResponse.getSdkAccessId());
//                if (baseResponse.getUserTypes() != null && baseResponse.getUserTypes().size() > 1) {
//                    Intent intent = new Intent(this, SelectUserTypeActivity.class);
//                    intent.putExtra("mobile", mobile);
//                    intent.putExtra("nextScreen", baseResponse.getNextScreen().name());
//                    intent.putExtra("sdkAccessId", baseResponse.getSdkAccessId());
//                    startActivity(intent);
//                } else {
//                    if (baseResponse.getUserTypes() != null && !baseResponse.getUserTypes().isEmpty())
//                        preferencesHelper.setUserType(baseResponse.getUserTypes().get(0));
//                    mOtpViewModel.getConfig(httpManager, baseResponse.getNextScreen(), baseResponse.getSdkAccessId());
//
//                }
//                // mOtpViewModel.getConfig(httpManager, baseResponse.getNextScreen(), baseResponse.getSdkAccessId());
//            } else {
//
//                TrackThat.setAccessId(preferencesHelper.getAccessId());
//
//                if (baseResponse.getNextScreen() != null) {
//
//                    if (baseResponse.getNextScreen() == NextScreen.HOME) {
//                        if (baseResponse.getUserTypes() != null && baseResponse.getUserTypes().size() > 1) {
//                            Intent intent = new Intent(this, SelectUserTypeActivity.class);
//                            intent.putExtra("mobile", mobile);
//                            intent.putExtra("nextScreen", baseResponse.getNextScreen().name());
//                            intent.putExtra("sdkAccessId", baseResponse.getSdkAccessId());
//                            startActivity(intent);
//                        } else {
//                            if (baseResponse.getUserTypes() != null && !baseResponse.getUserTypes().isEmpty())
//                                preferencesHelper.setUserType(baseResponse.getUserTypes().get(0));
//                            mOtpViewModel.getConfig(httpManager, baseResponse.getNextScreen(), baseResponse.getSdkAccessId());
//
//                        }
//
//                    } else {
//                        goToNext(baseResponse.getNextScreen());
//                    }
//                }
//            }
        } else {
            mActivityOtpBinding!!.btnContinue.visibility = View.VISIBLE
            mActivityOtpBinding!!.llProgressLayout.visibility = View.GONE
        }
    }

    private fun perFormNextScreenTask(nextScreen: NextScreen, sdkAccessId: String?) {
        if (sdkAccessId != null) {
            TrackThat.setAccessId(sdkAccessId)
        }
        if (preferencesHelper!!.idleTrackingInfo != null) {
            if (preferencesHelper!!.idleTrackingInfo.mode != null && preferencesHelper!!.idleTrackingInfo.mode == "ON_PUNCH") {
                showLoading()
                mOtpViewModel!!.getPunchInPunchOutData(httpManager, nextScreen)
            } else {
                CommonUtils.otpgoToNext(this, nextScreen, mobile)
            }
        } else {
            CommonUtils.otpgoToNext(this, nextScreen, mobile)
        }
    }

    override fun handleConfigResponse(callback: ApiCallback, result: Any?, error: APIError?,
                                      nextScreen: NextScreen?, sdkAccessId: String?) {

        if (CommonUtils.handleResponse(callback, error, result, this)) {
            if (sdkAccessId != null) {
                // send accessId to SDK
                TrackThat.setAccessId(sdkAccessId)
            }
            if (result != null) {
                val gson = Gson()
                val configResponse = gson.fromJson(result.toString(), ConfigResponse::class.java)
                CommonUtils.saveConfigDetails(this@OtpActivity, configResponse, preferencesHelper, "2", mobile)
                if (Objects.requireNonNull(configResponse.appConfig)!!.idleTrackingInfo != null) {
                    if (configResponse.appConfig!!.idleTrackingInfo!!.mode != null && configResponse.appConfig!!.idleTrackingInfo!!.mode == "ON_PUNCH" && Objects.requireNonNull(configResponse.appConfig!!.idleTrackingInfo)!!.enableIdleTracking) {
                        preferencesHelper!!.isIdealTrackingEnable = configResponse.appConfig!!.idleTrackingInfo!!.enableIdleTracking
                        showLoading()
                        mOtpViewModel!!.getPunchInPunchOutData(httpManager, nextScreen)
                    } else {
                        CommonUtils.otpgoToNext(this, nextScreen, mobile)
                    }
                } else {
                    CommonUtils.otpgoToNext(this, nextScreen, mobile)
                }
            }
        } else {
            mActivityOtpBinding!!.btnContinue.visibility = View.VISIBLE
            mActivityOtpBinding!!.llProgressLayout.visibility = View.GONE
        }
    }

    override fun handleResendOtpResponse(apiCallback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            showShort(this, "Otp sent successfully")
            startTimer()
            mOtpViewModel!!.retry.set(false)
            mOtpViewModel!!.timerVisibility.set(true)
        }
    }

    private fun playSoundStartTracking() {
        if (preferencesHelper!!.voiceAlertsTracking) {
            mediaPlayer = MediaPlayer.create(this, R.raw.tracking_started)
            mediaPlayer!!.start()
            Handler().postDelayed({
                if (mediaPlayer != null) {
                    mediaPlayer!!.stop()
                }
            }, 2000)
        }
    }

    private fun playSoundStopTracking() {
        if (preferencesHelper!!.voiceAlertsTracking) {
            mediaPlayer = MediaPlayer.create(this, R.raw.tracking_stopped)
            mediaPlayer!!.start()
            Handler().postDelayed({
                if (mediaPlayer != null) {
                    mediaPlayer!!.stop()
                }
            }, 2000)
        }
    }

    override fun handlePunchInPunchOutResponse(apiCallback: ApiCallback, result: Any?, error: APIError?, nextScreen: NextScreen?) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            val jsonConverter: JSONConverter<PunchInPunchOutData> = JSONConverter<PunchInPunchOutData>()
            val punchInPunchOutData = jsonConverter.jsonToObject(result.toString(), PunchInPunchOutData::class.java) as PunchInPunchOutData
            if (punchInPunchOutData.data != null) {
                if (punchInPunchOutData.data!!.status != null && !punchInPunchOutData.data!!.status!!.isEmpty()) {
                    if (punchInPunchOutData.data!!.status == "PRESENT") {
                        if (preferencesHelper!!.isIdealTrackingEnable) {
                            if (punchInPunchOutData.data!!.punchOutAt != 0L) {
                                preferencesHelper!!.punchOutTime = punchInPunchOutData.data!!.punchOutAt
                                if (TrackThat.isTracking()) {
                                    preferencesHelper!!.idleTripActive = false
                                    TrackThat.stopTracking()
                                    preferencesHelper!!.punchId = null
                                    playSoundStopTracking()
                                }
                            } else {
                                if (punchInPunchOutData.data != null && punchInPunchOutData.data!!.punchInAt != 0L) {
                                    preferencesHelper!!.punchStatus = true
                                    if (!preferencesHelper!!.idleTripActive) {
                                        preferencesHelper!!.punchId = punchInPunchOutData.data!!.punchId
                                        if (!TrackThat.isTracking()) {
                                            preferencesHelper!!.idleTripActive = true
                                            TrackThat.startTracking(punchInPunchOutData.data!!.punchId, false)
                                            playSoundStartTracking()
                                        }
                                    }
                                    preferencesHelper!!.punchInTime = punchInPunchOutData.data!!.punchInAt
                                    if (punchInPunchOutData.data!!.punchData != null && punchInPunchOutData.data!!.punchData!!.punchInData != null) {
                                        if (punchInPunchOutData.data!!.punchData!!.punchInData!!.selfie != null) {
                                            val imageUrl = punchInPunchOutData.data!!.punchData!!.punchInData!!.selfie
                                            preferencesHelper!!.selfieUrl = imageUrl
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //CommonUtils.updateSharedContentProvider(this, preferencesHelper)
        }
        CommonUtils.otpgoToNext(this, nextScreen, mobile)
    }

    override fun showTimeOutMessage(callback: ApiCallback) {
        showShort(this, AppConstants.MSG_REQUEST_TIME_OUT_TYR_AGAIN)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::smsReceiver.isInitialized)
            unregisterReceiver(smsReceiver)
        cancelTimer()
        mediaPlayer = null
    }

    //go to next screen
    private fun goToNext(nextScreen: NextScreen) {
        if (nextScreen === NextScreen.HOME) {
            val intent = MainActivity.newIntent(this)
            setFlags(intent)
            startActivity(intent)
            finish()
        } else if (nextScreen === NextScreen.SIGNUP) {
            showLong(this, "Invalid Mobile Number")
            //            Intent intent = RegisterActivity.newIntent(this);
//            intent.putExtra(AppConstants.MOBILE, mobile);
//            setFlags(intent);
//            startActivity(intent);
//            finish();
        } else if (nextScreen === NextScreen.MY_PROFILE) {
            setResult(RESULT_OK)
            finish()
        }
    }

    //cancel timer
    fun cancelTimer() {
        if (cTimer != null) cTimer!!.cancel()
    }

    //start timer function
    fun startTimer() {
        cTimer = object : CountDownTimer(60000.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val `val` = millisUntilFinished / 1000
                var sec = "" + `val`
                if (`val` < 10) {
                    sec = "0$`val`"
                }
                mOtpViewModel!!.timer.set("00:$sec")
            }

            override fun onFinish() {
                cancelTimer()
                mOtpViewModel!!.timer.set("01:00")
                mOtpViewModel!!.retry.set(true)
                mActivityOtpBinding!!.btnContinue.visibility = View.VISIBLE
                mActivityOtpBinding!!.llProgressLayout.visibility = View.GONE
                mOtpViewModel!!.timerVisibility.set(false)
            }
        }
        cTimer!!.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        preferencesHelper!!.accessId = null
    }

    companion object {
        @JvmStatic
        fun newIntent(context: Context?): Intent {
            return Intent(context, OtpActivity::class.java)
        }
    }

    private fun setFocusListener() {
       /* val onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            mCurrentlyFocusedEditText = v as EditText
            mCurrentlyFocusedEditText.setSelection(mCurrentlyFocusedEditText.getText().length)
        }
        mActivityOtpBinding!!.etFirst.setOnFocusChangeListener(onFocusChangeListener)
        mActivityOtpBinding!!.etSecond.setOnFocusChangeListener(onFocusChangeListener)
        mActivityOtpBinding!!.etThird.setOnFocusChangeListener(onFocusChangeListener)
        mActivityOtpBinding!!.etFourth.setOnFocusChangeListener(onFocusChangeListener)
        mActivityOtpBinding!!.etFifth.setOnFocusChangeListener(onFocusChangeListener)
        mActivityOtpBinding!!.etSixth.setOnFocusChangeListener(onFocusChangeListener)*/

        mActivityOtpBinding!!.etFirst.addTextChangedListener(GenericTextWatcher(mActivityOtpBinding!!.etFirst,  mActivityOtpBinding!!.etSecond))
        mActivityOtpBinding!!.etSecond.addTextChangedListener(GenericTextWatcher(mActivityOtpBinding!!.etSecond,  mActivityOtpBinding!!.etThird))
        mActivityOtpBinding!!.etThird.addTextChangedListener(GenericTextWatcher(mActivityOtpBinding!!.etThird,  mActivityOtpBinding!!.etFourth))
        mActivityOtpBinding!!.etFourth.addTextChangedListener(GenericTextWatcher(mActivityOtpBinding!!.etFourth,  mActivityOtpBinding!!.etFifth))
        mActivityOtpBinding!!.etFifth.addTextChangedListener(GenericTextWatcher(mActivityOtpBinding!!.etFifth,  mActivityOtpBinding!!.etSixth))
        mActivityOtpBinding!!.etSixth.addTextChangedListener(GenericTextWatcher(mActivityOtpBinding!!.etSixth,  mActivityOtpBinding!!.etFifth))




        mActivityOtpBinding!!.etFirst.setOnKeyListener(GenericKeyEvent(mActivityOtpBinding!!.etFirst, null))
        mActivityOtpBinding!!.etSecond.setOnKeyListener(GenericKeyEvent(mActivityOtpBinding!!.etSecond, mActivityOtpBinding!!.etFirst))
        mActivityOtpBinding!!.etThird.setOnKeyListener(GenericKeyEvent(mActivityOtpBinding!!.etThird, mActivityOtpBinding!!.etSecond))
        mActivityOtpBinding!!.etFourth.setOnKeyListener(GenericKeyEvent(mActivityOtpBinding!!.etFourth,mActivityOtpBinding!!.etThird))
        mActivityOtpBinding!!.etFifth.setOnKeyListener(GenericKeyEvent(mActivityOtpBinding!!.etFifth,mActivityOtpBinding!!.etFourth))
        mActivityOtpBinding!!.etSixth.setOnKeyListener(GenericKeyEvent(mActivityOtpBinding!!.etSixth,mActivityOtpBinding!!.etFifth))

    }
    inner class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.et_first && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }


    }
    inner class GenericTextWatcher internal constructor(private val currentView: View, private val nextView: View?) : TextWatcher {
        override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
            val text = editable.toString()
            when (currentView.id) {
                R.id.et_first -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_second -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_third -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_fourth -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_fifth -> if (text.length == 1) nextView!!.requestFocus()
                R.id.et_sixth -> {
                    if (text.length == 1) {
                        this@OtpActivity.hideKeyboard()
                    }else{
                        nextView!!.requestFocus()
                    }
                }
                //You can use EditText4 same as above to hide the keyboard
            }
        }

        override fun beforeTextChanged(
                arg0: CharSequence,
                arg1: Int,
                arg2: Int,
                arg3: Int
        ) {
        }

        override fun onTextChanged(
                arg0: CharSequence,
                arg1: Int,
                arg2: Int,
                arg3: Int
        ) {
        }

    }

    private fun setOnTextChangeListener() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                try {
                    if (mCurrentlyFocusedEditText.getText().length >= 1 && mCurrentlyFocusedEditText !== mActivityOtpBinding!!.etSixth) {
                        mCurrentlyFocusedEditText.focusSearch(View.FOCUS_RIGHT).requestFocus()
                    } else if (mCurrentlyFocusedEditText.getText().length >= 1 && mCurrentlyFocusedEditText === mActivityOtpBinding!!.etSixth) {
                        val imm =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                    } else {
                        val currentValue = mCurrentlyFocusedEditText.getText().toString()
                        if (currentValue.length <= 0 && mCurrentlyFocusedEditText.getSelectionStart() <= 0) {
                            mCurrentlyFocusedEditText.focusSearch(View.FOCUS_LEFT).requestFocus()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        mActivityOtpBinding!!.etFirst.addTextChangedListener(textWatcher)
        mActivityOtpBinding!!.etSecond.addTextChangedListener(textWatcher)
        mActivityOtpBinding!!.etThird.addTextChangedListener(textWatcher)
        mActivityOtpBinding!!.etFourth.addTextChangedListener(textWatcher)
        mActivityOtpBinding!!.etFifth.addTextChangedListener(textWatcher)
        mActivityOtpBinding!!.etSixth.addTextChangedListener(textWatcher)

    }

    private fun startSmsListener() {
        try {
            smsReceiver = MySMSBroadcastReceiver()
            smsReceiver.initOTPListener(this)

            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(this /* context */)
            val task = client.startSmsRetriever()
            // Listen for success/failure of the start Task. If in a background thread, this
            // can be made blocking using Tasks.await(task, [timeout]);
            task.addOnSuccessListener {
                // Successfully started retriever, expect broadcast intent
                // ...
                //otp_txt.text = "Waiting for the OTP"

            }

            task.addOnFailureListener {
                // Failed to start retriever, inspect Exception for more details
                // ...
                // otp_txt.text = "Cannot Start SMS Retriever"
            }
        } catch (e: Exception) {
            e.printStackTrace();
        }

    }

    override fun onOTPReceived(otp: String) {
        if (otp.length == 6) {
            var otpArray = otp.toCharArray()
            var firstSrt: String = otpArray[0].toString()
            var secondSrt: String = otpArray[1].toString()
            var thirdSrt: String = otpArray[2].toString()
            var fourthSrt: String = otpArray[3].toString()
            var fifthSrt: String = otpArray[4].toString()
            var sixthSrt: String = otpArray[5].toString()
            mActivityOtpBinding!!.etFirst.setText(firstSrt)
            mActivityOtpBinding!!.etSecond.setText(secondSrt)
            mActivityOtpBinding!!.etThird.setText(thirdSrt)
            mActivityOtpBinding!!.etFourth.setText(fourthSrt)
            mActivityOtpBinding!!.etFifth.setText(fifthSrt)
            mActivityOtpBinding!!.etSixth.setText(sixthSrt)
            mActivityOtpBinding!!.btnContinue.visibility = View.GONE
            mActivityOtpBinding!!.llProgressLayout.visibility = View.VISIBLE

            performVerify()

        }


    }

    override fun onOTPTimeOut() {

    }

}