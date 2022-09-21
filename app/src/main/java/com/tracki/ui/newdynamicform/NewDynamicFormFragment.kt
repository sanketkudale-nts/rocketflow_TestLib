package com.tracki.ui.newdynamicform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.iceteck.silicompressorr.SiliCompressor
import com.tracki.BR
import com.tracki.BuildConfig
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.GetTaskDataRequest
import com.tracki.data.model.request.OtpRequest
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ItemDynamicFormVideoBinding
import com.tracki.databinding.NewDynamicFormFragmentBinding
import com.tracki.ui.addcustomer.CustomerInfoScreenFragment
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.dynamicform.dynamicfragment.DynamicAdapter
import com.tracki.ui.dynamicform.dynamicfragment.DynamicFragment
import com.tracki.ui.dynamicform.dynamicfragment.FormSubmitListener
import com.tracki.ui.scanqrcode.ScanQrAndBarCodeActivity
import com.tracki.utils.*
import com.tracki.utils.image_utility.Compressor
import com.tracki.utils.image_utility.ImagePicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NewDynamicFormFragment : BaseFragment<NewDynamicFormFragmentBinding, NewDynamicViewModel>(),
    NewDynamicNavigator, DynamicAdapter.AdapterListener {

    private var mButton: Button? = null
    private lateinit var manager: LinearLayoutManager
    private var imageFilePath: String? = null

    @Inject
    lateinit var mLayoutManager: LinearLayoutManager

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var adapter: DynamicAdapter

    @Inject
    lateinit var httpManager: HttpManager

    private var mDynamicViewModel: NewDynamicViewModel? = null
    private var mFragmentFormListBinding: NewDynamicFormFragmentBinding? = null

    var positions = -1

    private var permissionArray =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var formData: FormData
    private var position = -1
    private var vidViewposition = -1
    lateinit var formDataList: ArrayList<FormData>
    lateinit var productFormData: ArrayList<DynamicFormData>
    private var actualImage: File? = null
    private var compressedImage: File? = null
    var formSubmitListener: FormSubmitListener? = null
    private var formId: String? = null
    private var dynamicFormsNew: DynamicFormsNew? = null
    private var taskId: String? = null
    private var isEditable: Boolean? = true
    private var videoFilePath: String? = null
    private var mBinding: ItemDynamicFormVideoBinding? = null
    var dfdId: String = "";
    var video: File? = null
    var videoPath: String? = null
    private var isOtpApiHit = false
    var vidUri: Uri? = null
    private val AUTOCOMPLETE_REQUEST_CODE = 28487
    var message: String? = ""
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_new_dynamicform_fragment
    }

    override fun getViewModel(): NewDynamicViewModel {
        mDynamicViewModel =
            ViewModelProviders.of(this, mViewModelFactory).get(NewDynamicViewModel::class.java)
        return mDynamicViewModel!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            formSubmitListener = context as FormSubmitListener
        }catch (e:Exception){

        }

    }
    fun setListener(lfragment: CustomerInfoScreenFragment){
        formSubmitListener = lfragment as FormSubmitListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDynamicViewModel?.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentFormListBinding = viewDataBinding
        setUp()
    }

    private fun setUp() {
        try {
            if (arguments != null) {
                formId = arguments?.getString("formId")
                taskId = arguments?.getString("taskId")
                productFormData = arguments?.getSerializable("hashMap") as ArrayList<DynamicFormData>
                isEditable = arguments?.getBoolean("isEditable", true)
                //   CommonUtils.showLogMessage("e","form data",formId)
                //taskAction = getFormType(arguments?.getString("action")!!)
                dynamicFormsNew = CommonUtils.getFormByFormId(formId)
                CommonUtils.showLogMessage("e", "formId", formId)
                var jsonConverter = JSONConverter<DynamicFormsNew>()
                var data = jsonConverter.objectToJson(dynamicFormsNew)

                CommonUtils.showLogMessage("e", "form data", data.toString())
                if (dynamicFormsNew != null) {
                    val dat = dynamicFormsNew?.fields!!
                    formDataList = ArrayList()

                    var fieldArrayList = ArrayList<String>()
                    var allowedFields = ArrayList<Field>()

                    fieldArrayList.add("FIELD1")
                    fieldArrayList.add("FIELD2")
                    fieldArrayList.add("FIELD3")
                    fieldArrayList.add("FIELD4")
                    fieldArrayList.add("FIELD5")
                    fieldArrayList.add("FIELD6")
                    fieldArrayList.add("FIELD7")
                    fieldArrayList.add("FIELD8")
                    fieldArrayList.add("FIELD9")
                    fieldArrayList.add("FIELD10")
                    fieldArrayList.add("FIELD11")
                    fieldArrayList.add("FIELD12")
                    fieldArrayList.add("FIELD13")
                    fieldArrayList.add("FIELD14")
                    fieldArrayList.add("FIELD15")

                    allowedFields = CommonUtils.getAllowedFields(fieldArrayList,taskId,preferencesHelper) as ArrayList<Field>

                    var listFields = ArrayList<FormData>()

                    if (allowedFields.isNotEmpty()){
                        for (allowedField in allowedFields){
                            var formData = FormData()
                            Log.e("allow_FIelds","${allowedField.toString()}")
                            formData.type = allowedField.type
                            formData.label = allowedField.label
                            formData.field = allowedField.field
                            formData.title = allowedField.label
                            formData.value = allowedField.value
                            var lst = ArrayList<WidgetData>()
                            var seperatedValues = allowedField.value?.split(",")?.toTypedArray()
                            if (seperatedValues != null){
                                for (value in seperatedValues) {
                                    var widgetData = WidgetData()
                                    widgetData.value = value
                                    widgetData.title = value
                                    lst.add(widgetData)
                                }
                            }
                            formData.widgetData = lst
                            listFields.add(formData)

                        }
                        formDataList.addAll(listFields)
                    }
                    for (i in dat.indices) {
                        val formData = FormData()
                        formData.type = dat[i].type
                        formData.name = dat[i].name
                        formData.title = dat[i].title
                        formData.placeHolder = dat[i].placeHolder
                        formData.required = dat[i].required
                        formData.errorMessage = dat[i].errorMessage
                        formData.maxLength = dat[i].maxLength
                        formData.operation = dat[i].operation
                        formData.includeForCalculation = dat[i].includeForCalculation
                        formData.actionConfig = dat[i].actionConfig
                        formData.file = dat[i].file

                        if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                            var previousDataList = preferencesHelper.formDataMap[formId]
                            if (previousDataList != null && previousDataList.isNotEmpty()) {
                                for (j in previousDataList.indices) {
                                    if (formData.name!! == previousDataList[j].name) {
                                        formData.value = previousDataList[j].value
                                        formData.maxRange = previousDataList[j].maxRange
                                        formData.minRange = previousDataList[j].minRange
                                        formData.widgetData = previousDataList[j].widgetData
                                        if (formData.type == DataType.SIGNATURE || formData.type == DataType.CAMERA || formData.type == DataType.VIDEO || formData.type == DataType.FILE) {

                                            if (previousDataList[j].file != null) {
                                                var listOfFile = ArrayList<File>()
                                                for (dfile in previousDataList[j].file!!) {
                                                    if (dfile.exists()) {
                                                        listOfFile.add(dfile)
                                                    }
                                                }
                                                if(listOfFile.isNotEmpty()){
                                                    formData.enteredValue = previousDataList[j].enteredValue
                                                    formData.file = listOfFile
                                                }

                                            }

                                        }else{
                                            formData.enteredValue = previousDataList[j].enteredValue
                                        }
                                    }

                                }
                            }

                        } else {
                            formData.maxRange = dat[i].maxRange
                            formData.minRange = dat[i].minRange
                            formData.value = dat[i].value
                            formData.widgetData = dat[i].widgetData
                        }

                        formData.readOnly = dat[i].readOnly
                        formData.minLength = dat[i].minLength
                        formData.weight = dat[i].weight
                        formData.roles = dat[i].roles
                        formData.dynamicSelectLookup = dat[i].dynamicSelectLookup
                        formDataList.add(formData)
                    }

                }
                if(!productFormData.isNullOrEmpty()){
                    for (i in productFormData.indices) {
                        val key = productFormData[i].key
                        val value = productFormData[i].value

                        for (j in formDataList.indices) {
                            if (key!! == formDataList.get(j).name) {
                                formDataList[j].value = value
                                formDataList[j].enteredValue = value
                            }

                        }
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        hitGetTaskDataAPI()

        val rvDynamicForm = mFragmentFormListBinding?.rvDynamicForms
        manager = LinearLayoutManager(baseActivity)
        rvDynamicForm?.layoutManager = manager
        adapter.setAdapterListener(this)
        adapter.setPreferencesHelper(preferencesHelper)
        rvDynamicForm?.adapter = adapter

        var firstVisibleInRecyclerView = -1

        rvDynamicForm?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        Log.e(TAG, "Scroll state is idle")
                        val firstCompleteVisibleItem =
                            manager.findFirstCompletelyVisibleItemPosition()
                        val currentFirstVisible = manager.findFirstVisibleItemPosition()

                        Log.e(TAG, "$currentFirstVisible <----------> $firstCompleteVisibleItem")

                        firstVisibleInRecyclerView = currentFirstVisible

                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        Log.e(TAG, "Scroll state is settling")
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        Log.e(TAG, "Scroll state is Dragging")
                    }
                }
            }

            private var firstVisibleInRecyclerView = -1
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentFirstVisible = manager.findFirstVisibleItemPosition()

                if (currentFirstVisible > firstVisibleInRecyclerView)
                    Log.i("RecyclerView scrolled: ", "scroll up!")
                else
                    Log.i("RecyclerView scrolled: ", "scroll down!")

                firstVisibleInRecyclerView = currentFirstVisible
            }

        })
    }

    override fun onUploadPic(position: Int, formData: FormData) {
        this.position = position
        this.formData = formData
        onPickImage()
    }


    private fun onPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    baseActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                proceedToImagePicking()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_STORAGE
                )
            }
        } else {
            proceedToImagePicking()
        }
    }

    private fun proceedToImagePicking() {
        val chooseImageIntent = ImagePicker.getPickImageIntent(baseActivity)
        startActivityForResult(chooseImageIntent, PICK_IMAGE_FILE_ID)
    }

    @SuppressLint("CheckResult")
    fun compressImage() {
        if (actualImage == null) {
            TrackiToast.Message.showShort(baseActivity, "No image is selected")
        } else {

            // Compress image in main thread
            //compressedImage = new Compressor(this).compressToFile(actualImage);
            //setCompressedImage();

            // Compress image to bitmap in main thread
            //compressedImageView.setImageBitmap(new Compressor(this).compressToBitmap(actualImage));

            // Compress image using RxJava in background thread
            Compressor(baseActivity)
                .setQuality(90)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    compressedImage = file

                    val spilt = compressedImage?.absolutePath?.split("/".toRegex())?.toTypedArray()
                    Log.e(TAG, "image mane is: ${spilt!![spilt.size - 1]}")

                    //set the image name into the adapter
                    // formData.enteredValue = spilt[spilt.size - 1]
                    formData.enteredValue = compressedImage?.path
                    Log.e(TAG, "path: ${compressedImage?.path}")
                    Log.e(TAG, "abspath: ${compressedImage?.absolutePath}")
                    val fileList: ArrayList<File>? = ArrayList()
                    fileList?.add(compressedImage!!)

                    formData.file = fileList
                    //notify adapter
                    adapter.setImage(position, formData)
                }, { throwable ->
                    throwable.printStackTrace()
                    TrackiToast.Message.showShort(baseActivity, throwable.message!!)
                })
        }
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val videoFileName =  timeStamp + "_"
        val videoFileName = "videoFile"
        val storageDir = Environment.getExternalStorageDirectory()
        video = File.createTempFile(videoFileName, ".mp4", storageDir)
        // Save a file: path for use with ACTION_VIEW intents
        videoFilePath = "file:" + video!!.getAbsolutePath()
        return video
    }


    override fun openVidCamera(pos: Int, mBinding: ItemDynamicFormVideoBinding, maxlength: Int) {

        this.vidViewposition = pos;
        this.mBinding = mBinding;

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            val file: File?
            try {
                file = createVideoFile()


                if (file != null && file.exists()) {
                    vidUri = FileProvider.getUriForFile(
                        requireContext().applicationContext,
                        "com.rocketflyer.rocketflow" + ".provider", file
                    )
                    //                    uri = Uri.parse(new File(imageFilePath).toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, vidUri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        intent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            CameraCharacteristics.LENS_FACING_BACK
                        ) // Tested on API 24 Android version 7.0(Samsung S6)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            CameraCharacteristics.LENS_FACING_BACK
                        ) // Tested on API 27 Android version 8.0(Nexus 6P)
                        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                    } else {
                        intent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            1
                        ) // Tested API 21 Android version 5.0.1(Samsung S4)
                    }
                    intent.putExtra("TEST", "String Extra")
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maxlength)
                    startActivityForResult(intent, VIDEO_REQUEST)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_FILE_ID -> {
                actualImage = ImagePicker.getImageFileToUpload(baseActivity, resultCode, data)
                compressImage()
            }
            CAMERA_PIC_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
//                        val bm = MediaStore.Images.Media.getBitmap(baseActivity.contentResolver, Uri.parse(imageFilePath))

                        val file = File(image?.path)

                        if (file.exists()) {
                            //  imageView.setImageURI(image.path?)
//                            var bm = BitmapFactory.decodeFile(imageFilePath)
                            val bm = ImagePicker.getImageResized(context, uri!!)
//                            val rotation = ImagePicker.getRotation(context, uri, true)
//                            bm = ImagePicker.rotate(bm, rotation)
                            // Code to manage the bitmap
                            actualImage = CommonUtils.convertBitmapToFile(
                                baseActivity, bm,
                                "upload_" + Calendar.getInstance().timeInMillis + ".jpg"
                            )


                            if (actualImage != null) {

                                Compressor(baseActivity)
                                    .compressToFileAsFlowable(actualImage)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ fi ->
                                        compressedImage = fi

                                        adapter.setImg(positions, compressedImage!!)
                                    }, { throwable ->
                                        throwable.printStackTrace()
                                        TrackiToast.Message.showShort(
                                            baseActivity,
                                            throwable.message!!
                                        )
                                    })
                            } else {
                                Log.e(TAG, "Image is not captured")
                            }
                        } else {
                            Log.e(TAG, "Image is not captured")
                        }


                    } catch (c: Exception) {
                        c.printStackTrace()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User Cancelled the action
                    TrackiToast.Message.showShort(baseActivity, "User Cancelled the action")
                }
            }

            VIDEO_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
//                        val bm = MediaStore.Images.Media.getBitmap(baseActivity.contentResolver, Uri.parse(imageFilePath))

                        val file = File(video?.path)
                        videoPath = video?.path
                        if (file.exists()) {
                            var thread = Thread(Runnable {

                                if (adapter.formDataList[vidViewposition] != null && adapter.formDataList[vidViewposition].type == DataType.VIDEO) {
                                    var filePath = SiliCompressor.with(baseActivity)
                                        .compressVideo(file.path, file.parent)
                                    adapter.formDataList[vidViewposition].enteredValue = filePath
                                    if (file.exists()) {
                                        if (file.delete()) {
                                            Log.e("file Deleted :", file!!.path!!)
                                        } else {
                                            Log.e("file not Deleted :", file!!.path!!.toString())
                                        }
                                    }
                                    requireActivity().runOnUiThread(Runnable {
                                        setVid(vidViewposition, video!!, filePath!!)
                                        //  adapter.notifyDataSetChanged()
                                    })
                                    if (video != null && video!!.exists()) {
                                        video!!.delete()
                                    }

                                }

                                for (formData in adapter.formDataList) {
                                    if (formData.widgetData != null) {
                                        for (j in formData.widgetData!!.indices) {
                                            val llFormData = formData.widgetData!![j].formDataList
                                            if (llFormData != null) {
                                                for (data in llFormData) {
                                                    if (data.type == DataType.VIDEO) {
                                                        var filePath =
                                                            SiliCompressor.with(baseActivity)
                                                                .compressVideo(
                                                                    file.path,
                                                                    file.parent
                                                                )
                                                        // data.enteredValue=file.path
                                                        data.enteredValue = filePath
                                                        if (file.exists()) {
                                                            if (file.delete()) {
                                                                Log.e("file Deleted :", file!!.path)
                                                            } else {
                                                                Log.e(
                                                                    "file not Deleted :",
                                                                    file!!.path
                                                                )
                                                            }
                                                        }
                                                        requireActivity().runOnUiThread(Runnable {
                                                            setVid(
                                                                vidViewposition,
                                                                video!!,
                                                                filePath!!
                                                            )
                                                            //  adapter.notifyDataSetChanged()
                                                        })
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }


                            })
                            thread.start()
                        }


                    } catch (c: Exception) {
                        c.printStackTrace()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User Cancelled the action
                    TrackiToast.Message.showShort(baseActivity, "User Cancelled the action")
                }
            }

            AUTOCOMPLETE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.i(TAG, "Place: " + place.name + ", " + place.id)
                    var latLong = place.latLng
                    var location =
                        com.tracki.ui.addplace.Location(latLong!!.latitude, latLong.longitude)
                    location.locationId = place.id!!
                    var hubLocation = com.tracki.ui.addplace.HubLocation(location, 0)
                    hubLocation.address = place.name
                    var jsonConverter = JSONConverter<com.tracki.ui.addplace.HubLocation>()
                    // adapter.formDataList[position].enteredValue=jsonConverter.objectToJson(hubLocation)
                    adapter.formDataList[position].enteredValue = place.name
                    CommonUtils.showLogMessage(
                        "e",
                        "enterdvalue",
                        adapter.formDataList[position].enteredValue
                    )
                    // adapter.formDataList[position].value=adapter.formDataList[position].enteredValue
                    adapter.formDataList[position].value = place.name
                    adapter.notifyItemChanged(position)

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i(TAG, status.statusMessage)
                    TrackiToast.Message.showShort(requireContext(), status.statusMessage!!)

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    TrackiToast.Message.showShort(requireContext(), "operation cancelled.")
                }

            }
            AppConstants.REQUEST_CODE_SCAN->{
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.hasExtra("id")) {
                        var id=data.getStringExtra("id")
                        adapter.formDataList[position].enteredValue = id
                        CommonUtils.showLogMessage(
                            "e",
                            "enterdvalue",
                            adapter.formDataList[position].enteredValue
                        )
                        adapter.formDataList[position].value = id
                        adapter.notifyItemChanged(position)
                    }

                }
            }


        }
    }


    fun setVid(pos: Int, file: File, path: String) {
        adapter.itemDynamicFormVideoBinding = mBinding
        adapter.setVid(pos, file!!, path)

    }


    override fun uploadCameraImage(adapterPosition: Int) {
        this.positions = adapterPosition
        if (hasPermission(permissionArray))
            openCamera()
    }


    override fun openPlacePicker(position: Int, formData: FormData?) {
        try {
            this.position = position
            // this.formData=formData!!
            val fields: List<Place.Field> =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent: Intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        } catch (e: GooglePlayServicesRepairableException) {
            Log.e(TAG, e.message)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e(TAG, e.message)
        }
    }

    override fun openScanner(position: Int, formData: FormData?) {
        this.position = position
        var scanActivity = Intent(baseActivity!!, ScanQrAndBarCodeActivity::class.java)
        scanActivity.putExtra("from","taskdetails")
        baseActivity.startActivityForResult(scanActivity, AppConstants.REQUEST_CODE_SCAN)
    }

    override fun getDropdownItems(position: Int, target: String, rollId: String?) {

        if (CommonUtils.containsEnum(target))
            mDynamicViewModel?.executiveMap(position, target, httpManager, rollId)

    }

    var uri: Uri? = null
    private fun openCamera() {
        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (photoIntent.resolveActivity(baseActivity.packageManager) != null) {
            val file: File
            try {
                file = createImageFile()
                if (file != null && file.exists()) {
                    uri = FileProvider.getUriForFile(
                        baseActivity.applicationContext,
                        "com.rocketflyer.rocketflow" + ".provider", file
                    )
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(photoIntent, CAMERA_PIC_REQUEST)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    var image: File? = null
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        image = File.createTempFile(imageFileName, ".jpg", storageDir)
        // Save a file: path for use with ACTION_VIEW intents
        imageFilePath = "file:" + image?.absolutePath
        return image!!
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedToImagePicking()
            }
        } else if (requestCode == AppConstants.PERMISSIONS_REQUEST_CODE_MULTIPLE) {

            val perms = HashMap<String, Int>()
            perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
            perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
            if (grantResults.isNotEmpty()) {
                for (i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]

                val camPer = perms[Manifest.permission.CAMERA]
                val extStorPer = perms[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                // Check for both permissions
                if ((camPer != null && camPer == PackageManager.PERMISSION_GRANTED) &&
                    (extStorPer != null && extStorPer == PackageManager.PERMISSION_GRANTED)
                ) {
                    Log.d(TAG, "Camera and Storage permission granted")
                    openCamera()

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            baseActivity,
                            Manifest.permission.CAMERA
                        ) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            baseActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        showDialogOK(
                            "Camera and Storage Permission required for this app"
                        ) { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> baseActivity.hasPermission(
                                    permissions
                                )
                                DialogInterface.BUTTON_NEGATIVE ->
                                    // proceed with logic by disabling the related features or quit the app.
                                    baseActivity.finish()
                            }
                        }
                    } else {
                        TrackiToast.Message
                            .showLong(baseActivity, "Go to settings and enable permissions")
                    }
                }
            }
        }
    }

    companion object {
        const val CAMERA_PIC_REQUEST: Int = 1277
        const val TAG = "NewDynamicFragment"
        const val REQUEST_READ_STORAGE = 3
        const val PICK_IMAGE_FILE_ID = 235
        const val VIDEO_REQUEST = 546

        /**
         * Method used to get the instance of the fragment.
         *
         * @return fragment instance
         */
        @JvmStatic
        fun getInstance(
            formId: String?,
            taskId: String,
            isEditable: Boolean,
            formList: ArrayList<DynamicFormData>,
        ): NewDynamicFormFragment {
            val fragment = NewDynamicFormFragment()
            val bundle = Bundle()
//            bundle.putString("action", taskAction)
            bundle.putString("formId", formId)
            bundle.putString("taskId", taskId)
            bundle.putSerializable("hashMap", formList)
            bundle.putBoolean("isEditable", isEditable)
            fragment.arguments = bundle
            return fragment
        }

    }

    fun onclickMainButton() {
        if (mButton != null)
            mButton!!.performClick()
       /* else{
            var dynamicActionConfig=DynamicActionConfig()
            dynamicActionConfig.action=Type.DISPOSE
            var formdata=FormData()
            formdata.actionConfig=dynamicActionConfig
            onProcessClick(formdata)
        }*/
    }

    override fun handleExecutiveMapResponse(
        position: Int,
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        if (CommonUtils.handleResponse(callback, error, result, baseActivity)) {
            val executive =
                Gson().fromJson<ExecutiveMap>(result.toString(), ExecutiveMap::class.java)
            executive?.let {
                if (formDataList.isNotEmpty()) {
                    val formData = formDataList[position]
                    formData.apiMap = it.data
                    formDataList[position] = formData
                    adapter.setFormDataList(formDataList)
                    adapter.setIsEditable(isEditable, httpManager)
                    adapter.setIsHideButton(true)
                    // adapter.setDynamicFragmentInstance(this)


                }
            }
        }
    }


    fun hitGetTaskDataAPI() {
        if (dynamicFormsNew != null) {
            var data = GetTaskDataRequest()
            data.dfId = formId
            data.dfVersion = Integer.valueOf(dynamicFormsNew!!.version!!)
            data.taskId = taskId
            mDynamicViewModel!!.getTaskData(httpManager, data)
        } else {
            context?.let {
                TrackiToast.Message.showLong(requireContext(), "Form Did't Bind Properly")
            }

        }
    }


    override fun handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {
      try {
          if (CommonUtils.handleResponse(callback, error, result, baseActivity!!)) {
              val getTaskDataResponse = Gson().fromJson<GetTaskDataResponse>(
                  result.toString(),
                  GetTaskDataResponse::class.java
              ) as GetTaskDataResponse
              if (getTaskDataResponse.successful) {
                  var data: List<TaskData>? = null
                  if (getTaskDataResponse.data != null) {
                      data = getTaskDataResponse.data!!
                  }
                  if (getTaskDataResponse.dfdId != null) {
                      dfdId = getTaskDataResponse.dfdId!!
                  }

                  if (data != null) {
                      for (i in data.indices) {
                          val key = data.get(i).key
                          val value = data.get(i).value

                          for (j in formDataList.indices) {
                              if (key!!.equals(formDataList.get(j).name)) {
                                  formDataList.get(j).value = value
                                  formDataList.get(j).enteredValue = value
                              }

                          }
                      }

                  }
              }


          }

      }catch (e:Exception){

      }

        adapter.setFormDataList(formDataList)
        adapter.setIsEditable(isEditable, httpManager)
        adapter.setIsHideButton(true)
        //adapter.setDynamicFragmentInstance(this)

    }


    override fun onProcessClick(formDataa: FormData?) {
        hideKeyboard()
        //get hashMap from adapter
        val fieldList = adapter.formDataList
        //traverse hashMap and check if required filed is empty then show error

//        val jsonConverter = JSONConverter<ArrayList<FormData>>()
//        val json = jsonConverter.objectToJson(fieldList)
//        Log.e("list", json)

        for (formData: FormData in fieldList!!) {
            if (formData.required) {
                when (formData.type) {
                    DataType.DATE_RANGE -> {
                        if (formData.maxRange == 0L || formData.minRange == 0L) {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            return
                        }
                    }
                    DataType.TIME, DataType.DATE -> {
                        if (formData.maxRange == 0L) {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            return
                        }
                    }
                    DataType.CONDITIONAL_DROPDOWN_STATIC -> {
                        if (formData.formItemKey == null || formData.formItemKey == "") {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            return
                        }
                    }
                    DataType.CONDITIONAL_DROPDOWN_API -> {
                        if (formData.formItemKey == null || formData.formItemKey == "") {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            return
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                val llFiledList = formData.widgetData!![j].formDataList
                                if (llFiledList != null)
                                    if (checkValidation(llFiledList as ArrayList<FormData>))
                                        return

                            }
                        }
                    }
                    DataType.DROPDOWN_API -> {
                        if (formData.formItemKey == null || formData.formItemKey == "") {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            return
                        }
//                        if (formData.widgetData != null) {
//                            for (j in formData.widgetData!!.indices) {
//                                val llFiledList = formData.widgetData!![j].formDataList
//                                if (llFiledList != null)
//                                    if (checkValidation(llFiledList as ArrayList<FormData>))
//                                        return
//
//                            }
//                        }
                    }
                    else -> {
                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            return
                        }
                        /*if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                val llFiledList = formData.widgetData!![j].formDataList
                                if (llFiledList != null)
                                    if (checkValidation(llFiledList as ArrayList<FormData>))
                                        return

                            }
                        }*/
                    }
                }
            }
        }

//        for (formData in fieldList) {
//            if (formData.widgetData != null) {
//                for (j in formData.widgetData!!.indices) {
//                    val llFormData = formData.widgetData!![j].formDataList
//                    if (llFormData != null) {
//                        for (data in llFormData) {
//                            val jsonConverter = JSONConverter<List<FormData>>()
//                            val json = jsonConverter.objectToJson(llFormData)
//                             Log.e("data json", json)
//                            if (data.enteredValue != null) {
//                                Log.e("value json", data.enteredValue)
//                            }
//                        }
//                    }
//                }
//            }
//        }

        outer@ for (formData in fieldList) {
            if (formData.widgetData != null) {
                inner@ for (j in formData.widgetData!!.indices) {
                    val llFiledList = formData.widgetData!![j].formDataList
                    if (llFiledList != null) {
                        if (formData.widgetData!![j].target != null && formData.widgetData!![j].target!!.isNotEmpty()) {
                            var map = HashMap<String, ArrayList<FormData>>()
                            map[formData.widgetData!![j].target!!] =
                                formData.widgetData!![j].formDataList!! as ArrayList<FormData>
                            if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                                val hmIterator: Iterator<*> =
                                    preferencesHelper.formDataMap.entries.iterator()
                                while (hmIterator.hasNext()) {
                                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                                    map[mapElement.key.toString()] =
                                        mapElement.value as ArrayList<FormData>
                                    CommonUtils.showLogMessage(
                                        "e",
                                        "inner map value",
                                        mapElement.value.toString()
                                    )
                                }
                            }
                            preferencesHelper.formDataMap = map
                            var jsonConverter =
                                JSONConverter<HashMap<String, ArrayList<FormData>>>()
                            var data = jsonConverter.objectToJson(map)
                            CommonUtils.showLogMessage(
                                "e",
                                "inner form data value",
                                data.toString()
                            )
                            if (checkValidation(llFiledList as ArrayList<FormData>)) {
                                return

                            }
                        }

                    }


                }
            }
        }

        if (formSubmitListener != null) {
            var map = HashMap<String, ArrayList<FormData>>()
            map[formId!!] = fieldList
           /* if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                val hmIterator: Iterator<*> = preferencesHelper.formDataMap.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    map[mapElement.key.toString()] = mapElement.value as ArrayList<FormData>
                    CommonUtils.showLogMessage("e", "map value", mapElement.value.toString())
                }
            }*/
            preferencesHelper.formDataMap = map
            var jsonConverter = JSONConverter<HashMap<String, ArrayList<FormData>>>()
            var data = jsonConverter.objectToJson(map)
            CommonUtils.showLogMessage("e", "form data value", data.toString())
            formSubmitListener?.onProcessClick(fieldList, formDataa?.actionConfig, formId!!, dfdId)
        }
        //submit data to activity

    }

    override fun sendButtonInstance(button: Button?, isEditable: Boolean) {;
        this.mButton = button
        if (!isEditable)
            mButton!!.isEnabled = false
    }


    override fun onVeriFyOtpButtonClick(formData: FormData?, mobile: String?) {
        isOtpApiHit = true
        hideKeyboard()
        showLoading()
        mDynamicViewModel!!.verifyOtpServerRequest(
            OtpRequest(mobile!!, formData!!.enteredValue!!),
            httpManager
        )

    }

    override fun handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, baseActivity)) {

            if (!isOtpApiHit) {
                val fileUrlsResponse =
                    Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
                val fileResponseMap = fileUrlsResponse.filesUrl
                var fileUrlList: java.util.ArrayList<String>? = java.util.ArrayList<String>()
                var fileUrl: String? = null

            } else {
                val baseResponse = Gson().fromJson(result.toString(), OtpResponse::class.java)
                if (baseResponse.successful) {
                    adapter.isSubmitButtonEnable.value = true
                } else {
                    baseResponse.responseMsg?.let {
                        TrackiToast.Message.showShort(context, it)
                    }

                }

            }
        }
    }

    fun checkValidation(fieldList: ArrayList<FormData>): Boolean {
        var isTrue = false
        outer@ for (formData in fieldList) {
            if (formData.widgetData != null) {
                inner@ for (j in formData.widgetData!!.indices) {
                    val llFiledList = formData.widgetData!![j].formDataList
                    if (llFiledList != null) {
                        if (formData.widgetData!![j].target != null && formData.widgetData!![j].target!!.isNotEmpty()) {
                            var map = HashMap<String, ArrayList<FormData>>()
                            map[formData.widgetData!![j].target!!] =
                                formData.widgetData!![j].formDataList!! as ArrayList<FormData>
                            if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                                val hmIterator: Iterator<*> =
                                    preferencesHelper.formDataMap.entries.iterator()
                                while (hmIterator.hasNext()) {
                                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                                    map[mapElement.key.toString()] =
                                        mapElement.value as ArrayList<FormData>
                                    CommonUtils.showLogMessage(
                                        "e",
                                        "inner map value",
                                        mapElement.value.toString()
                                    )
                                }
                            }
                            preferencesHelper.formDataMap = map
                            var jsonConverter =
                                JSONConverter<HashMap<String, ArrayList<FormData>>>()
                            var data = jsonConverter.objectToJson(map)
                            CommonUtils.showLogMessage(
                                "e",
                                "inner form data value",
                                data.toString()
                            )

                        }

                    }


                }
            }
        }
        user@ for (formData: FormData in fieldList!!) {
            if (formData.required) {
                when (formData.type) {
                    DataType.DATE_RANGE -> {
                        if (formData.maxRange == 0L || formData.minRange == 0L) {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        }

                    }
                    DataType.TIME, DataType.DATE -> {
                        if (formData.maxRange == 0L) {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        }

                    }
                    DataType.CONDITIONAL_DROPDOWN_STATIC -> {


                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        } else {
                            isTrue = false
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                if (formData.widgetData!![j].target != null && formData.widgetData!![j].formDataList != null)
                                    isTrue =
                                        checkValidation(formData.widgetData!![j].formDataList as ArrayList<FormData>)
                            }
                        }


                    }
                    DataType.CONDITIONAL_DROPDOWN_API -> {

                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        } else {
                            isTrue = false
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                if (formData.widgetData!![j].target != null && formData.widgetData!![j].formDataList != null)
                                    isTrue =
                                        checkValidation(formData.widgetData!![j].formDataList as ArrayList<FormData>)
                            }
                        }

                    }
                    DataType.DROPDOWN_API -> {

                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        } else {
                            isTrue = false
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                if (formData.widgetData!![j].target != null && formData.widgetData!![j].formDataList != null)
                                    isTrue =
                                        checkValidation(formData.widgetData!![j].formDataList as ArrayList<FormData>)
                            }
                        }

                    }
                    else -> {

                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(baseActivity, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        } else {
                            isTrue = false
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                if (formData.widgetData!![j].target != null && formData.widgetData!![j].target!!.isNotEmpty() && formData.widgetData!![j].formDataList != null) {
                                    isTrue =
                                        checkValidation(formData.widgetData!![j].formDataList as ArrayList<FormData>)
                                }
                            }
                        }


                    }
                }

            }
        }
        return isTrue

    }
}