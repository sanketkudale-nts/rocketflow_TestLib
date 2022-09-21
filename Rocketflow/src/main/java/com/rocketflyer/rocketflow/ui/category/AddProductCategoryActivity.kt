package com.rocketflyer.rocketflow.ui.category

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.UpdateFileRequest
import com.tracki.data.model.response.config.ProfileResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAddProductCategoryBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.selectorder.CataLogProductCategory
import com.tracki.ui.selectorder.CataLogProductCategory.Companion.loadCatalog
import com.tracki.utils.*
import com.tracki.utils.image_utility.Compressor
import com.tracki.utils.image_utility.ImagePicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.HashMap
import javax.inject.Inject

class AddProductCategoryActivity :
    BaseActivity<ActivityAddProductCategoryBinding, AddProductCategoryViewModel>(),
    ProductDescriptionAdapter.OnProductDescriptionListener, AddProductCategoryNavigator,
    View.OnClickListener {

    private var position: Int? = null

    @Inject
    lateinit var addProductCategoryViewModel: AddProductCategoryViewModel


    @Inject
    lateinit var mPref: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    lateinit var adapter: ProductDescriptionAdapter
    lateinit var binding: ActivityAddProductCategoryBinding
    private var action: String? = null
    private var imageUrl: String? = null
    private var compressedImage: File? = null
    private var actualImage: File? = null
    private val REQUEST_READ_STORAGE: Int = 1112
    private val PICK_IMAGE_FILE_ID: Int = 1113
    private var mMapCategory: Map<String, String>? = null
    var flavorId: String? = null
    private var categoryData: CataLogProductCategory?=null
    private var cid:String?=null
    private var parentCategoryId:String?=null
    companion object {
        private val TAG = AddProductCategoryActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, AddProductCategoryActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        addProductCategoryViewModel.navigator = this
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            var categoryMap = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)
            CommonUtils.showLogMessage("e", "categoryMap", categoryMap)
            mMapCategory = Gson().fromJson<Map<String, String>>(
                categoryMap,
                object : TypeToken<HashMap<String?, String?>?>() {}.type
            )
            if (mMapCategory != null) {
                if (mMapCategory!!.containsKey("flavorId")) {
                    flavorId = mMapCategory!!["flavorId"]
                }
            }
        }
        var config=CommonUtils.getFlavourConfigFromFlavourId(flavorId,mPref)
        if (intent.hasExtra("action")) {
            action = intent.getStringExtra("action")
            if (action.equals("Add")) {


                if(intent.hasExtra("parentCategoryId")){
                    parentCategoryId=intent.getStringExtra("parentCategoryId")
                    if(config!=null&&!config.subCatLabel.isNullOrEmpty()){
                        setToolbar(binding.toolbar,"Add ${config.subCatLabel}")
                    }else{
                        setToolbar(binding.toolbar, "Add Sub Category")
                    }

                }else{
                    if(config!=null&&!config.catLabel.isNullOrEmpty()){
                        setToolbar(binding.toolbar,"Add ${config.catLabel}")
                    }else{
                        setToolbar(binding.toolbar, "Add Product Category")
                    }

                }
                binding.btnAddNow.text = getString(R.string.add_now)
            } else {

                binding.btnAddNow.text = getString(R.string.update)
            }
        }
        adapter = ProductDescriptionAdapter(this)
        binding.rvDescriptions.adapter = adapter
        if(intent.hasExtra("data")){
            categoryData=intent.getParcelableExtra("data")!!
            if(categoryData!=null&&categoryData!!.img!=null&&categoryData!!.img!!.isNotEmpty()){
                imageUrl=categoryData!!.img!!
                binding.cardViewTakeImage.visibility = View.GONE
                binding.cardViewActImage.visibility = View.VISIBLE
                loadCatalog(binding.ivActImage, imageUrl)
            }
            if(categoryData!=null&&categoryData!!.name!=null){
                binding.etCategoryName.setText(categoryData!!.name)
            }
            if(categoryData!=null&&categoryData!!.cid!=null){
                cid=categoryData!!.cid
            }
            if(categoryData!=null&&categoryData!!.descriptions!=null&&categoryData!!.descriptions!!.isNotEmpty())
            {
                adapter.addItems(categoryData!!.descriptions!!)
            }
            if(categoryData!=null&&categoryData!!.subCat!!){
                setToolbar(binding.toolbar,"Update ${categoryData!!.name}")
            }else{
                setToolbar(binding.toolbar, "Add Sub Category")
            }

        }

        binding.btnAddNow.setOnClickListener(this)
        binding.rlTakeImage.setOnClickListener(this)
        binding.tvAddDescription.setOnClickListener(this)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_product_category
    }

    override fun getViewModel(): AddProductCategoryViewModel {
        return addProductCategoryViewModel
    }

    private fun openDialogAddDescription(data: ProductDescription?, position: Int?) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_add_product_description)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        val ivBack = dialog.findViewById<View>(R.id.ivBack) as ImageView
        val etHeadingValue = dialog.findViewById<EditText>(R.id.etHeadingValue)
        val etContentValue = dialog.findViewById<EditText>(R.id.etContentValue)
        val btnAdd = dialog.findViewById<Button>(R.id.btnAdd)
        if (data != null) {
            if (data.heading != null)
                etHeadingValue.setText(data.heading!!)
            if (data.content != null)
                etContentValue.setText(data.content!!)
        }

        dialog.window!!.attributes = lp
        ivBack.setOnClickListener { dialog.dismiss() }
        btnAdd.setOnClickListener {
            var heading = etHeadingValue.text.toString().trim()
            var content = etContentValue.text.toString().trim()
            var productDescription = ProductDescription()
            productDescription.heading = heading
            productDescription.content = content
            if (heading.isEmpty()) {
                TrackiToast.Message.showShort(this, getString(R.string.please_enter_heading))
            } else if (content.isEmpty()) {
                TrackiToast.Message.showShort(this, getString(R.string.please_enter_description))
            } else {
                if (data != null && position != null) {
                    adapter.getAllList()[position].content = content
                    adapter.getAllList()[position].heading = heading
                    adapter.notifyItemChanged(position)
                } else {
                    adapter.addItem(productDescription)
                }
            }
            dialog.dismiss()
        }
        if (!dialog.isShowing) dialog.show()
    }


    override fun onEditProductDescription(data: ProductDescription, position: Int) {
        this.position = position
        openDialogAddDescription(data, position)
    }

    override fun removeProductDescription(data: ProductDescription, position: Int) {
        this.position = position
        adapter.removeAt(position, adapter.getAllList().size)
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun handleProductCategoryResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val response = Gson().fromJson(result.toString(), AddCategoryResponse::class.java)
            if (response != null && response.successful) {
                onSuccess()
            }
        }
    }

    fun onSuccess() {
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onCancel() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    override fun handleSendImageResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            val profileResponse = Gson().fromJson(result.toString(), ProfileResponse::class.java)
            if (profileResponse != null) {
                imageUrl = profileResponse.imageUrl
            }
        }
    }

    override fun handleUpdateProductCategoryResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val response = Gson().fromJson(result.toString(), AddCategoryResponse::class.java)
            if (response != null && response.successful) {
                onSuccess()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rlTakeImage -> {
                /* if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                     proceedToImagePicking()
                 } else {
                     if (!checkPermission()) {
                         requestPermission()
                     } else {
                         proceedToImagePicking()

                     }

                 }*/
                onPickImage()
            }
            R.id.tvAddDescription -> {
                openDialogAddDescription(null, null)
            }
            R.id.ivDelete -> {
                binding.cardViewTakeImage.visibility = View.VISIBLE
                binding.cardViewActImage.visibility = View.GONE
                compressedImage = null
                imageUrl = null
            }
            R.id.btnAddNow -> {
                if (action != null) {
                    var name = binding.etCategoryName.text.toString().trim()
                    if (name.isEmpty()) {
                        TrackiToast.Message.showShort(
                            this,
                            getString(R.string.please_enter_category)
                        )
                        return
                    }
                    var subCat = binding.rbChecked.isChecked
                    var request = AddCategoryRequest()
                    request.name = name
                    request.subCategory = subCat
                    request.img = imageUrl
                    if (flavorId != null)
                        request.flavorId = flavorId

                    if (adapter.getAllList().isNotEmpty())
                        request.descriptions = adapter.getAllList()
                    if (action.equals("Add")) {
                        if(parentCategoryId!=null)
                            request.parentCategoryId=parentCategoryId
                        if (TrackiApplication.getApiMap()
                                .containsKey(ApiType.ADD_PRODUCT_CATEGORY)
                        ) {
                            showLoading()
                            addProductCategoryViewModel.addProductCategory(httpManager, request)
                        }
                    } else {
                        request.cid=cid
                        if (TrackiApplication.getApiMap()
                                .containsKey(ApiType.UPDATE_PRODUCT_CATEGORY)
                        ) {
                            showLoading()
                            addProductCategoryViewModel.updateProductcategory(httpManager, request)
                        }
                    }
                }
            }
        }
    }

    fun onPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                proceedToImagePicking()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), REQUEST_READ_STORAGE
                )
            }
        } else {
            proceedToImagePicking()
        }
    }

    private fun proceedToImagePicking() {
        val chooseImageIntent: Intent = ImagePicker.getPickImageIntent(this)
        startActivityForResult(
            chooseImageIntent,
            PICK_IMAGE_FILE_ID
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    proceedToImagePicking()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @SuppressLint("CheckResult")
    private fun compressImage() {
        if (actualImage == null) {
            TrackiToast.Message.showShort(this, getString(R.string.please_choose_a_image))
        } else {


            Compressor(this)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    compressedImage = file
                    binding.cardViewTakeImage.visibility = View.GONE
                    binding.cardViewActImage.visibility = View.VISIBLE
                    binding.ivActImage.setImageURI(Uri.fromFile(compressedImage))
                    val updateFileRequest =
                        UpdateFileRequest(compressedImage!!, FileType.USER_PROFILE, "")
                    if (TrackiApplication.getApiMap()
                            .containsKey(ApiType.UPLOAD_FILE_AGAINEST_ENTITY)
                    ) {
                        showLoading()
                        addProductCategoryViewModel.uploadImage(updateFileRequest, httpManager)
                    }

                }) { throwable ->
                    throwable.printStackTrace()
                    TrackiToast.Message.showShort(
                        this@AddProductCategoryActivity,
                        throwable.message
                    )
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FILE_ID) {
            actualImage = ImagePicker.getImageFileToUpload(this, resultCode, data)
            compressImage()
        }
    }


}
