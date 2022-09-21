package com.tracki.ui.scanqrcode

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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.tracki.databinding.ProductScanBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.cart.CartActivity
import com.tracki.ui.newcreatetask.NewCreateTaskActivity
import com.tracki.ui.products.ProductDetailsResponse
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.utils.*
import kotlinx.android.synthetic.main.product_scan.*
import java.io.IOException
import javax.inject.Inject


class ProductScan :
    BaseActivity<ProductScanBinding, ProductScanViewModel>(),
    ViewTreeObserver.OnGlobalLayoutListener, ProductScanNavigator, ProductScanAdapter.OnProductAddListener {

    var mSurfaceView: SurfaceView? = null

    var left: View? = null

    private var linkOption: LinkOptions? = null
    private var linkingType: TaggingType? = null
    lateinit var productScanAdapter: ProductScanAdapter
    open var savedOrderMap: HashMap<String, CatalogProduct>? = null
    private var rvProducts: RecyclerView? = null
    private var mLayoutManager: LinearLayoutManager? = null
    @Inject
    lateinit var mPref: PreferencesHelper


    var progressBar3: ProgressBar? = null

    var scannerBar: ImageView? = null

    var scannerLayout: LinearLayout? = null

    var mode: String? = "Task"

    var right: View? = null
    private var isClassCall = false
    private var animator: ObjectAnimator? = null
    private val REQUEST_CAMERA = 3
    lateinit var binding: ProductScanBinding

    lateinit var globalList: ArrayList<CatalogProduct>

    private var deliveryChargeAmount: Float? = 0F
    private var deliverMode: String? = null
    private var ctaId: String? = null
    private var taskId: String? = null
    private var categoryId: String? = null
    private var target: String? = null
    private var flavourId: String?=null
    private var dynamicPricing: Boolean = false
    val PLACE_ORDER = 174

    @Inject
    lateinit var productScanViewModel: ProductScanViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    var from:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        productScanViewModel.navigator = this
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("Scan", MODE_PRIVATE)
        mode = sharedPreferences.getString("Scan","Task")
        productScanAdapter = ProductScanAdapter(this)
        setUp()
        getSavedMap()
        binding.btnPlaceOrder.setOnClickListener {
           viewCart()
        }

    }

    private fun getSavedMap() {
        if (mPref.userDetail != null && mPref.userDetail.userId != null) {
            if (CommonUtils.getTotalItemCount(mPref.userDetail.userId!!, mPref) > 0) {
                if (mPref.getProductInCartWRC() != null && mPref.getProductInCartWRC()!!
                        .containsKey(mPref.userDetail.userId)
                ) {
                    savedOrderMap = mPref.getProductInCartWRC()!![mPref.userDetail.userId]
                }
            } else {
                savedOrderMap = HashMap()
            }
        }
    }


    private fun setUp() {


        globalList = ArrayList()

        if (intent.hasExtra(AppConstants.DELIVERY_CHARGE)) {
            deliveryChargeAmount = intent.getFloatExtra(AppConstants.DELIVERY_CHARGE, 0F)
        }
        if (intent.hasExtra(AppConstants.DELIVERY_MODE)) {
            deliverMode = intent.getStringExtra(AppConstants.DELIVERY_MODE)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET)) {
            target = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
            taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID))
            ctaId = intent.getStringExtra(AppConstants.Extra.EXTRA_CTA_ID)

        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORY_ID))
            categoryId = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORY_ID)
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID)) {
            flavourId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID)
        }
        if(intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING)){
            dynamicPricing=intent.getBooleanExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING,false)
        }
        if(intent.hasExtra("from")){
            from=intent.getStringExtra("from")
        }
        val dataList: ArrayList<CatalogProduct> = intent.getSerializableExtra("dataList") as ArrayList<CatalogProduct>
        if (dataList != null  || dataList.size > 0){

            globalList.addAll(dataList)
            for (data in dataList){
                if (data != null) {
                    savedOrderMap!![data.pid!!] = data
                }
            }
            productScanAdapter.clearList()
            productScanAdapter.addItems(globalList)
        }
        binding.goBack.setOnClickListener { onBackPressed() }
        if (rvProducts == null) {
            rvProducts = binding.rvProducts
            //  mLayoutManager= (LinearLayoutManager) rvAttendance.getLayoutManager();
            try {
                mLayoutManager = LinearLayoutManager(this)
                mLayoutManager!!.orientation = RecyclerView.VERTICAL
                rvProducts!!.layoutManager = mLayoutManager
                rvProducts!!.itemAnimator = DefaultItemAnimator()

            } catch (e: IllegalArgumentException) {
            }
            rvProducts!!.adapter = productScanAdapter
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

    private fun viewCart() {
        if (savedOrderMap == null || savedOrderMap!!.isEmpty()) {
            TrackiToast.Message.showShort(this, getString(R.string.cart_is_empty_please_add_item))
            return
        }
        var saveOrderInCart = HashMap<String, Map<String, CatalogProduct>>()
        if (mPref.getProductInCartWRC() != null)
            saveOrderInCart.putAll(mPref.getProductInCartWRC()!!)
        saveOrderInCart[mPref.userDetail.userId!!] = savedOrderMap!!
        mPref.saveProductInCartWRC(saveOrderInCart)
        var list = savedOrderMap!!
        cartTaskWork()
    }

    private fun cartTaskWork() {
        val map = HashMap<String, Int>()
        for (data in savedOrderMap!!.values) {
            map[data.pid!!] = data.noOfProduct
        }
        val intent = Intent(this, CartActivity::class.java)

        intent.putExtra(AppConstants.DELIVERY_CHARGE, deliveryChargeAmount)
        if (taskId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId)
        if (target != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET, target)
        if (flavourId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID, flavourId)
        if (categoryId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId)
        if (ctaId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_CTA_ID, ctaId)
        if (deliverMode != null)
            intent.putExtra(AppConstants.DELIVERY_MODE, deliverMode)
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING, dynamicPricing)
        startActivityForResult(intent, PLACE_ORDER)
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
                            this@ProductScan,
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
                            if(from==null) {
                                showLoading()
                                productScanViewModel.getQrCodeValue(httpManager, value)
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

        animator!!.repeatMode = ValueAnimator.REVERSE
        animator!!.repeatCount = ValueAnimator.INFINITE
        animator!!.interpolator = AccelerateDecelerateInterpolator()
        animator!!.duration = 3000
        animator!!.start()
    }

    companion object {
        fun newIntent(context: Context,listProducts: ArrayList<CatalogProduct>): Intent {
            val intent = Intent(context, ProductScan::class.java)
            intent.putExtra("dataList",listProducts)
            return intent
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
        return R.layout.product_scan
    }

    override fun getViewModel(): ProductScanViewModel {
        return productScanViewModel
    }

    override fun addProduct(data: CatalogProduct, position: Int) {
        if (linkingType != null && linkingType == TaggingType.SINGLE && linkOption != null && linkOption == LinkOptions.DIRECT) {
            Log.e("position", "" + position)
            savedOrderMap = HashMap()
            if (data.addInOrder) {
                savedOrderMap!![data.pid!!] = data
            } else {
                if (savedOrderMap!!.contains(data.pid)) {
                    savedOrderMap!!.remove(data.pid)
//                    Log.e("remove product", data.name)
                }
            }
            for (index in productScanAdapter.getAllList().indices) {
                if (index != position) {
                    productScanAdapter.getAllList()[index].addInOrder = false
                    productScanAdapter.getAllList()[index].noOfProduct = 0
                }
            }
            productScanAdapter.notifyDataSetChanged()
        } else {
            if (savedOrderMap == null) {
                savedOrderMap = HashMap()
            }
            if (data.addInOrder) {
                savedOrderMap!![data.pid!!] = data
            } else {
                if (savedOrderMap!!.contains(data.pid)) {
                    savedOrderMap!!.remove(data.pid)
                }
            }

        }
        var saveOrderInCart = HashMap<String, Map<String, CatalogProduct>>()
        if (mPref.getProductInCartWRC() != null)
            saveOrderInCart.putAll(mPref.getProductInCartWRC()!!)
        saveOrderInCart[mPref.userDetail.userId!!] = savedOrderMap!!
        mPref.saveProductInCartWRC(saveOrderInCart)
        var jsonConverter2 = JSONConverter<Map<String, Map<String, CatalogProduct>>>()
        var str2 = jsonConverter2.objectToJson(mPref.getProductInCartWRC())


        invalidateOptionsMenu()
        var jsonConverter = JSONConverter<HashMap<String, CatalogProduct>>()
        var str = jsonConverter.objectToJson(savedOrderMap)
        Log.e("map", str)

    }


    override fun removeProduct(data: CatalogProduct, position: Int) {
        productScanAdapter.removeAt(position,globalList.size)
    }


    override fun handleQrCodeResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        if (CommonUtils.handleResponse(callback, error, result, this@ProductScan)) {

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
                QrCodeValueType.PRODUCT -> {
                    if (!response.id.isNullOrBlank()) {
                        productScanViewModel.getProductDetails(httpManager,response.id)
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

    override fun handleProductDetailsResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        pid: String?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ProductScan)) {
            Log.e("checkData","$result")
            val jsonConverter: JSONConverter<ProductDetailsResponse> = JSONConverter()
            var response: ProductDetailsResponse =
                jsonConverter.jsonToObject(result.toString(), ProductDetailsResponse::class.java)
            if (response.data != null) {
                val productDetails = response.data
                Log.e("checkID","in1")
                if (productDetails != null) {
                    Log.e("checkID","in2")
                    if (globalList.isNotEmpty()) {
                        for (data in globalList) {
                            Log.e("checkID", "in3")
                            if (data.pid != productDetails.pid) {
                                Log.e("checkID", "in5")
                                val catalogProduct = CatalogProduct()
                                catalogProduct.active = productDetails.active
                                catalogProduct.addInOrder = true
                                catalogProduct.added = productDetails.added
                                catalogProduct.img = productDetails.images?.get(0)
                                catalogProduct.image = productDetails.images?.get(0)
                                catalogProduct.categoryName = productDetails.cname
                                catalogProduct.name = productDetails.name
                                catalogProduct.pid = productDetails.pid
                                catalogProduct.price = productDetails.sellingPrice
                                catalogProduct.cid = productDetails.cid
                                catalogProduct.cname = productDetails.cname
                                catalogProduct.unitValue = productDetails.unitValue?.toFloat()
                                catalogProduct.unitType = productDetails.unitType
                                catalogProduct.dfId = productDetails.dfId
                                catalogProduct.description = productDetails.description
                                val unitInfo: com.tracki.ui.products.UnitInfo =
                                    com.tracki.ui.products.UnitInfo(
                                        productDetails.packInfo,
                                        productDetails.qty?.toFloat(),
                                        productDetails.unitType
                                    )
                                catalogProduct.unitInfo = unitInfo
                                catalogProduct.packInfo = productDetails.packInfo
                                catalogProduct.noOfProduct = 1
                                catalogProduct.maxOrderLimit = productDetails.maxOrderLimit
                                catalogProduct.productStock = productDetails.productStock
                                catalogProduct.specifications = productDetails.specifications
                                catalogProduct.prodInfoMap = productDetails.prodInfoMap
                                catalogProduct.dfData = productDetails.dfData

                                globalList.add(catalogProduct)
                                savedOrderMap!![catalogProduct.pid!!] = catalogProduct

                            } else {
                                Log.e("checkID", "off")
                            }
                        }
                    }
                    else{
                        val catalogProduct = CatalogProduct()
                        catalogProduct.active = productDetails.active
                        catalogProduct.addInOrder = true
                        catalogProduct.added = productDetails.added
                        catalogProduct.img = productDetails.images?.get(0)
                        catalogProduct.image = productDetails.images?.get(0)
                        catalogProduct.categoryName = productDetails.cname
                        catalogProduct.name = productDetails.name
                        catalogProduct.pid = productDetails.pid
                        catalogProduct.price = productDetails.sellingPrice
                        catalogProduct.cid = productDetails.cid
                        catalogProduct.cname = productDetails.cname
                        catalogProduct.unitValue = productDetails.unitValue?.toFloat()
                        catalogProduct.unitType = productDetails.unitType
                        catalogProduct.dfId = productDetails.dfId
                        catalogProduct.description = productDetails.description
                        val unitInfo: com.tracki.ui.products.UnitInfo =
                            com.tracki.ui.products.UnitInfo(
                                productDetails.packInfo,
                                productDetails.qty?.toFloat(),
                                productDetails.unitType
                            )
                        catalogProduct.unitInfo = unitInfo
                        catalogProduct.packInfo = productDetails.packInfo
                        catalogProduct.noOfProduct = 1
                        catalogProduct.maxOrderLimit = productDetails.maxOrderLimit
                        catalogProduct.productStock = productDetails.productStock
                        catalogProduct.specifications = productDetails.specifications
                        catalogProduct.prodInfoMap = productDetails.prodInfoMap
                        catalogProduct.dfData = productDetails.dfData

                        globalList.add(catalogProduct)
                        savedOrderMap!![catalogProduct.pid!!] = catalogProduct
                    }

                    productScanAdapter.clearList()
                    productScanAdapter.addItems(globalList)

                }
            }
        }else {
            finish()
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ProductScan)) {
            var taskResponse = Gson().fromJson("$result", TaskResponse::class.java)
            taskResponse.taskDetail?.let { task ->
            }
        }else {
            finish()
        }
    }


}