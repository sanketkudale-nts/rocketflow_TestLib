package com.tracki.ui.uploadDocument

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.UpdateFileRequest
import com.tracki.data.model.response.config.ProfileResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityUploadDocumentBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.myDocument.DocsData
import com.tracki.ui.myDocument.MyDocumentActivity
import com.tracki.ui.myDocument.MyDocumentViewModel
import com.tracki.utils.*
import com.tracki.utils.DateTimeUtil.Companion.getParsedDate
import com.tracki.utils.image_utility.Compressor
import com.tracki.utils.image_utility.ImagePicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_upload_document.*
import java.io.File
import java.util.*
import javax.inject.Inject

class UploadDocumentActivity :
    BaseActivity<ActivityUploadDocumentBinding, UploadDocumentViewModel>(), UploadDocumentNavigator,
    View.OnClickListener {

    private var expireDate: Long = 0
    private var selectedDocType: DocType? = null

    @Inject
    lateinit var mUploadDocumentNewViewModel: UploadDocumentViewModel
    lateinit var binding: ActivityUploadDocumentBinding

    @Inject
    lateinit var mPref: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager
    private var action: String? = null
    private var imageUrl: String? = null
    private var compressedImage: File? = null
    private var actualImage: File? = null
    private val REQUEST_READ_STORAGE: Int = 1112
    private val PICK_IMAGE_FILE_ID: Int = 1113
    private var isUpdate=false
    private var docdata: DocsData?=null

    companion object {
        private val TAG = UploadDocumentActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, UploadDocumentActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mUploadDocumentNewViewModel.navigator = this
        binding.tvBrowseFile.setOnClickListener(this)
        binding.ivBackArrow.setOnClickListener(this)
        binding.btnAdd.setOnClickListener(this)
        binding.tvPreview.setOnClickListener(this)
        binding.rlDate.setOnClickListener(this)
        if(intent.hasExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE))
        isUpdate=intent.getBooleanExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE,false)
        if(isUpdate)
            binding.btnAdd.text="Update"
        else
            binding.btnAdd.text="Add"
        if(intent.hasExtra(AppConstants.Extra.EXTRA_DATA)){
            docdata=intent.getParcelableExtra(AppConstants.Extra.EXTRA_DATA)
            if(docdata!=null){
                binding.tvTitle.text="Update Document"
                binding.etDocName.setText(docdata!!.name!!)
                binding.etDocNumber.setText(docdata!!.number!!)
                imageUrl=docdata!!.imageUrl
                if(imageUrl!=null) {
                    binding.tvPreview.visibility = View.VISIBLE
                }
                expireDate = docdata!!.validTill!!
                binding.tvExpireDate.text = getParsedDate(expireDate)
            }
        }

        populateDocTypeDropDown()

    }

    private fun populateDocTypeDropDown() {
        var doclist: MutableList<Documents> = java.util.ArrayList()

        var pan = Documents("PAN", DocType.PAN)
        doclist.add(pan)
        var aadhar = Documents("AADHAR", DocType.AADHAR)
        doclist.add(aadhar)
        var driving = Documents("DRIVING LICENCE", DocType.DRIVING_LICENCE)
        doclist.add(driving)

        var voterId = Documents("VOTER ID", DocType.VOTER_ID)
        doclist.add(voterId)

        var passport = Documents("PASSPORT", DocType.PASSPORT)
        doclist.add(passport)

        var docNameList: MutableList<String?> = java.util.ArrayList()
        for (list in doclist) {
            docNameList.add(list.name)
        }

        var arrayAdapter = object : ArrayAdapter<String?>(
            this,
            android.R.layout.simple_spinner_item,
            docNameList
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val v = super.getView(position, convertView, parent)
                val externalFont = Typeface.createFromAsset(
                    parent.context.assets,
                    "fonts/campton_book.ttf"
                )
                (v as TextView).typeface = externalFont
                return v
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val v = super.getDropDownView(position, convertView, parent)
                val externalFont = Typeface.createFromAsset(
                    parent.context.assets,
                    "fonts/campton_book.ttf"
                )
                (v as TextView).typeface = externalFont
                //v.setBackgroundColor(Color.GREEN);
                return v
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnDocumentsType.visibility = View.VISIBLE
        binding.spnDocumentsType!!.adapter = arrayAdapter
        binding.spnDocumentsType!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem =
                        parent.getItemAtPosition(position).toString()
                    selectedDocType = doclist[position].type
//                        CommonUtils.showLogMessage("e", "hubId", hubId);


                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }

            }
        if(docdata!=null){
            if(!docdata!!.type.isNullOrEmpty()){
                var findType=Documents(type = DocType.valueOf(docdata!!.type!!))
                var index=doclist.indexOf(findType)
                if(index!=-1){
                    binding.spnDocumentsType!!.setSelection(index)
                }
            }

        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_upload_document
    }

    override fun getViewModel(): UploadDocumentViewModel {
        return mUploadDocumentNewViewModel
    }

    override fun handleSendImageResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val profileResponse = Gson().fromJson(result.toString(), ProfileResponse::class.java)
            if (profileResponse != null) {
                imageUrl = profileResponse.imageUrl
                binding.tvPreview.visibility = View.VISIBLE
            }
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            setResult(RESULT_OK,Intent().putExtra(AppConstants.Extra.DOC_TYPE,selectedDocType!!.name))
            finish()
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
//                    binding.cardViewTakeImage.visibility = View.GONE
//                    binding.tvPreview.visibility = View.VISIBLE
//                    binding.ivActImage.setImageURI(Uri.fromFile(compressedImage))
                    val updateFileRequest =
                        UpdateFileRequest(compressedImage!!, FileType.USER_PROFILE, "")
                    if (TrackiApplication.getApiMap()
                            .containsKey(ApiType.UPLOAD_FILE_AGAINEST_ENTITY)
                    ) {
                        showLoading()
                        mUploadDocumentNewViewModel.uploadImage(updateFileRequest, httpManager)
                    }

                }) { throwable ->
                    throwable.printStackTrace()
                    TrackiToast.Message.showShort(
                        this@UploadDocumentActivity,
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_browseFile -> {
                onPickImage()
            }
            R.id.iv_back_arrow -> {
                onBackPressed()
            }
            R.id.btn_add -> {
                addDocuments()

            }
            R.id.rlDate -> {
                openDatePicker()

            }
            R.id.tv_preview -> {
                if (imageUrl != null)
                    openDialogShowImage(imageUrl!!)

            }
        }
    }

    private fun openDatePicker() {
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        val minTime: Long = 0L
        CommonUtils.openDatePicker(
            this, mYear, mMonth,
            mDay, minTime, 0
        ) { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 0)
            expireDate = calendar.timeInMillis
            binding.tvExpireDate.text = getParsedDate(expireDate)
        }

    }

    private fun addDocuments() {
        var name = binding.etDocName.text.toString()
        var docnumber = binding.etDocNumber.text.toString()
        if (name.isNullOrEmpty()) {
            TrackiToast.Message.showShort(this, "Please enter name")
        } else if (docnumber.isNullOrEmpty()) {
            TrackiToast.Message.showShort(this, "Please Enter document number")
        } else if (imageUrl == null) {
            TrackiToast.Message.showShort(this, "Please Select document ")
        }/*else if(expireDate==0L){
            TrackiToast.Message.showShort(this,"Please Select Expire Date ")
        }*/
        else {
            var request = UploadDocumentRequest()
            request.name = name
            request.type = selectedDocType!!.name.toString()
            request.number = docnumber
            request.validTill = expireDate
            request.imageUrl = imageUrl

            if(isUpdate){
                    request.createdBy = docdata?.createdBy
                request.documentId=docdata?.documentId
                if (TrackiApplication.getApiMap().containsKey(ApiType.UPDATE_DOCUMENT)) {
                    showLoading()
                    mUploadDocumentNewViewModel.updateDocument(request, httpManager)


                }
            }else{
                if (mPref.userDetail != null && mPref.userDetail.name != null)
                    request.createdBy = mPref.userDetail.name
                if (TrackiApplication.getApiMap().containsKey(ApiType.CREATE_DOCUMENT)) {
                    showLoading()
                    mUploadDocumentNewViewModel.addDocument(request, httpManager)


                }
            }

        }
    }

    private fun openDialogShowImage(url: String) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_show_image_big)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
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
        val imageView = dialog.findViewById<View>(R.id.ivImages) as ImageView
        showLoading()

        Glide.with(this)
            .asBitmap()
            .load(url)
            .error(R.drawable.ic_picture)
            .placeholder(R.drawable.ic_picture)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {
                    imageView.setImageBitmap(resource)
                    hideLoading()
                }
            })
        /*  Glide.with(context!!)
                  .load(url)
                  .error(R.drawable.ic_picture)
                  .placeholder(R.drawable.ic_picture)
                  .into(imageView)*/
        dialog.window!!.attributes = lp
        // imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }

}