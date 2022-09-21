package com.tracki.ui.scanqrcode

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.tracki.R
import com.tracki.data.model.request.QrCodeRequest
import com.tracki.ui.taskdetails.NewTaskDetailsActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.EncryptionAndDecryption
import com.tracki.utils.JSONConverter
import java.io.IOException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException

class ScanQrCodeActivity : AppCompatActivity(), OnGlobalLayoutListener {


    var mSurfaceView: SurfaceView? = null

    var left: View? = null

    var progressBar3: ProgressBar? = null

    var scannerBar: ImageView? = null

    var scannerLayout: LinearLayout? = null

    var right: View? = null
    private var isClassCall = false
    private var animator: ObjectAnimator? = null
    private val REQUEST_CAMERA = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code)
        setUp()

    }

    private fun setUp() {
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
        val cameraSource: CameraSource = CameraSource.Builder(this, detector).setAutoFocusEnabled(true).setRequestedPreviewSize(1600, 1024).build()
        mSurfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(this@ScanQrCodeActivity, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    cameraSource.start(mSurfaceView!!.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodeSparseArray: SparseArray<Barcode> = detections.getDetectedItems()
                if (barcodeSparseArray.size() > 0) {
                    // vibration for 800 milliseconds
                    (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(40)
                    val encryptionAndDecryption = EncryptionAndDecryption()
                    //Toast.makeText(ScanBarCodeActivity.this,barcodeSparseArray.valueAt(0)+"" , Toast.LENGTH_SHORT).show();
                    Log.d("BARCODE", "BAT " + barcodeSparseArray.valueAt(0).displayValue)
                    try {
                        Log.d("BARCODE", "BAT " + encryptionAndDecryption.getDecryptData(barcodeSparseArray.valueAt(0).displayValue))
                        val jsonConverter = JSONConverter<QrCodeRequest>()
                        val barcodeData: QrCodeRequest = jsonConverter.jsonToObject(encryptionAndDecryption.getDecryptData(barcodeSparseArray.valueAt(0).displayValue), QrCodeRequest::class.java) as QrCodeRequest
                        runOnUiThread {
                            if (!isClassCall) {
                                isClassCall = true
                                cameraSource.stop()
                                val intent = Intent(this@ScanQrCodeActivity, NewTaskDetailsActivity::class.java)
                                intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, barcodeData.taskId)
                                intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, barcodeData.allowSubTask)
                                intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, barcodeData.subTaskCategoryId)
                                intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, barcodeData.referenceId)
                                intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, barcodeData.categoryId)
                                intent.putExtra(AppConstants.Extra.FROM, barcodeData.from)
                                intent.putExtra(AppConstants.Extra.FROM_DATE, barcodeData.fromDate)
                                intent.putExtra(AppConstants.Extra.FROM_TO, barcodeData.toDate)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } catch (e: InvalidKeySpecException) {
                        e.printStackTrace()
                    } catch (e: NoSuchAlgorithmException) {
                        e.printStackTrace()
                    }


                }
            }
        })
    }

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
            return Intent(context, ScanQrCodeActivity::class.java)
        }
    }

    fun getCameraPermisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                createCameraSource()
            } else {
                requestPermissions(arrayOf(
                        Manifest.permission.CAMERA), REQUEST_CAMERA)
            }
        } else {
            createCameraSource()
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    createCameraSource()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}