package com.rocketflyer.rocketflow.ui.scanqrcode

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.*
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityScanQrAndBarCodeBinding
import com.tracki.databinding.ProductScanBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.productdetails.ProductDetailsActivity
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.ui.taskdetails.NewTaskDetailsActivity
import com.tracki.ui.userdetails.UserDetailsActivity
import com.tracki.utils.*
import java.io.IOException
import javax.inject.Inject


class ScanQrAndBarCodeActivity :
    BaseActivity<ActivityScanQrAndBarCodeBinding, QrCodeValueViewModel>(),
    ViewTreeObserver.OnGlobalLayoutListener, QrCodeNavigator {


    var mSurfaceView: SurfaceView? = null

    var left: View? = null

    var progressBar3: ProgressBar? = null

    var scannerBar: ImageView? = null

    var scannerLayout: LinearLayout? = null

    var mode: String? = "Task"

    var right: View? = null
    private var isClassCall = false
    private var animator: ObjectAnimator? = null
    private val REQUEST_CAMERA = 3
    lateinit var binding: ActivityScanQrAndBarCodeBinding

    @Inject
    lateinit var qrCodeValueViewModel: QrCodeValueViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager



    var from:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        qrCodeValueViewModel.navigator = this
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("Scan", MODE_PRIVATE)
        mode = sharedPreferences.getString("Scan","Task")
        setUp()

    }

    private fun setUp() {
        if(intent.hasExtra("from")){
            from=intent.getStringExtra("from")
        }
        mSurfaceView = findViewById(R.id.surface_view)
        left = findViewById(R.id.left)
        right = findViewById(R.id.right)
        scannerBar = findViewById(R.id.scannerBar)
        scannerLayout = findViewById(R.id.scannerLayout)
        getCameraPermisson()
        val vto = scannerLayout!!.viewTreeObserver
        vto.addOnGlobalLayoutListener(this)
    }

    private fun createCameraSource() {
        val detector: BarcodeDetector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        val cameraSource: CameraSource =
            CameraSource.Builder(this, detector).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024).build()
        mSurfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScanQrAndBarCodeActivity,
                            Manifest.permission.CAMERA
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    cameraSource.start(mSurfaceView!!.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodeSparseArray: SparseArray<Barcode> = detections.getDetectedItems()
                if (barcodeSparseArray.size() > 0) {
                    (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(40)
//                    ringtone()
                    playSound()
                    var value = barcodeSparseArray.valueAt(0).displayValue
                    CommonUtils.showLogMessage("e", "BARCODE_Value", value)
                    runOnUiThread {
                        if (!isClassCall) {
                            isClassCall = true
                            cameraSource.stop()
                            if(from==null) {
                                showLoading()
                                qrCodeValueViewModel.getQrCodeValue(httpManager, value)
                            }else{
                                hideLoading()
                                var resultIntent=Intent();
                                resultIntent.putExtra("id",value)
                                setResult(Activity.RESULT_OK,resultIntent)
                                finish()
                            }

                        }
                    }


                }
            }
        })
    }
/*
    fun ringtone() {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/

    override fun onGlobalLayout() {
        scannerLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            scannerLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
        } else {
            scannerLayout!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }

        val destination = scannerLayout!!.y +
                scannerLayout!!.height

        animator = ObjectAnimator.ofFloat(
            scannerBar, "translationY",
            scannerLayout!!.y,
            destination
        )

        animator!!.setRepeatMode(ValueAnimator.REVERSE)
        animator!!.setRepeatCount(ValueAnimator.INFINITE)
        animator!!.setInterpolator(AccelerateDecelerateInterpolator())
        animator!!.setDuration(3000)
        animator!!.start()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ScanQrAndBarCodeActivity::class.java)
        }
    }

    fun getCameraPermisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                createCameraSource()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    ), REQUEST_CAMERA
                )
            }
        } else {
            createCameraSource()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createCameraSource()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {

        return R.layout.activity_scan_qr_and_bar_code
    }

    override fun getViewModel(): QrCodeValueViewModel {
        return qrCodeValueViewModel
    }

    override fun handleQrCodeResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {

            val jsonConverter: JSONConverter<QrCodeResponse> = JSONConverter()
            var response: QrCodeResponse = jsonConverter.jsonToObject(
                result.toString(),
                QrCodeResponse::class.java
            ) as QrCodeResponse

            callScanDetailsApi(response)
        } else {
            hideLoading()
            finish()
        }

    }

    private fun callScanDetailsApi(response: QrCodeResponse) {
        response.type?.let { type ->
            when (type) {
                QrCodeValueType.TASK -> {
                    if (!response.id.isNullOrBlank()) {
                        qrCodeValueViewModel.getTaskDetails(httpManager, response.id)
                    } else {
                        hideLoading()
                    }

                }
                QrCodeValueType.PRODUCT -> {
//                        hideLoading()
//                        openProductDetailsActivity(response.id)
                    if (!response.id.isNullOrBlank()) {
                        qrCodeValueViewModel.getProductDetails(httpManager,response.id)
                    } else {
                        hideLoading()
                    }

                }
                QrCodeValueType.USER -> {
                    if (!response.id.isNullOrBlank()) {
                        qrCodeValueViewModel.getUserDetail(httpManager, response.id)
                    } else {
                        hideLoading()
                    }

                }
            }
        }

    }

    private var mediaPlayer: MediaPlayer? = null
    private fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        mediaPlayer!!.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
            }
        }, 200)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer = null
    }


    override fun handleUserDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {
            val jsonConverter: JSONConverter<UpdateResponse> = JSONConverter()
            var response: UpdateResponse = jsonConverter.jsonToObject(
                result.toString(),
                UpdateResponse::class.java
            ) as UpdateResponse
            if (response.data != null) {
                var data = response.data
                var userData = UserData()
                if (!data!!.firstName.isNullOrBlank()) {
                    userData.firstName = data!!.firstName
                }
                if (!data!!.userId.isNullOrBlank()) {
                    userData.userId = data!!.userId
                }
                if (!data.middleName.isNullOrBlank()) {
                    userData.middleName = data!!.middleName
                }
                if (!data.lastName.isNullOrBlank()) {
                    userData.lastName = data!!.lastName
                }
                if (!data.mobile.isNullOrBlank()) {
                    userData.mobile = data!!.mobile
                }
                if (!data.email.isNullOrBlank()) {
                    userData.email = data!!.email
                }
                if (!data.fatherName.isNullOrBlank()) {
                    userData.fatherName = data!!.fatherName
                }
                if (!data.motherName.isNullOrBlank()) {
                    userData.motherName = data!!.motherName
                }
                if (!data.dateOfBirth.isNullOrBlank()) {
                    userData.dateOfBirth = data!!.dateOfBirth
                }
                if (!data.dateOfJoining.isNullOrBlank()) {
                    userData.dateOfJoining = data!!.dateOfJoining
                }
                if (!data.roleId.isNullOrBlank()) {
                    userData.roleId = data!!.roleId
                }
                if (!data.roleName.isNullOrBlank()) {
                    userData.roleName = data!!.roleName
                }
                openUserDetailsActivity(userData)

            }
        }else {
            finish()
        }
    }

    override fun handleProductDetailsResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        pid: String?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {
            openProductDetailsActivity(pid)
        }else {
            finish()
        }
    }

    private fun openProductDetailsActivity(id: String?) {
        var product = CatalogProduct()
        product.pid = id
        val addproduct = Intent(this, ProductDetailsActivity::class.java)
        addproduct.putExtra("data", product)
        startActivity(addproduct)
        finish()
    }

    private fun openUserDetailsActivity(data: UserData) {
        var intent = Intent(this, UserDetailsActivity::class.java)
        intent.putExtra("userData", data)
        intent.putExtra("from", AppConstants.EMPLOYEES)
        intent.putExtra("action", "view")
        startActivity(intent)
        finish()
    }

    private fun openTaskDetailsActivity(task: Task?) {
        if (task != null) {
            val intent = Intent(this@ScanQrAndBarCodeActivity, NewTaskDetailsActivity::class.java)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.taskId)
            intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, task.allowSubTask)
            intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, task.subCategoryIds)
            intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, task.referenceId)
            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, task.categoryId)
            intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_TO_ME)
            startActivity(intent)
            finish()
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {
            var taskResponse = Gson().fromJson("$result", TaskResponse::class.java)
            taskResponse.taskDetail?.let { task ->
                openTaskDetailsActivity(task)

            }

        }else {
            finish()
        }
    }


}