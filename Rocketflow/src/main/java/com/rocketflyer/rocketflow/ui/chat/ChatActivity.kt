package com.rocketflyer.rocketflow.ui.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.iceteck.silicompressorr.SiliCompressor
import com.tracki.BR
import com.tracki.BuildConfig
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.DocType
import com.tracki.data.model.DocumentType
import com.tracki.data.model.SupportedMediaType
import com.tracki.data.model.response.config.ChatResponse
import com.tracki.data.model.response.config.FileUrlsResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityChatBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.custom.socket.BaseModel
import com.tracki.ui.custom.socket.Messages
import com.tracki.ui.custom.socket.OpenCreateRoomModel
import com.tracki.ui.custom.socket.WebSocketManager
import com.tracki.utils.*
import com.tracki.utils.AppConstants.Extra.*
import com.tracki.utils.TrackiToast.Message.showLong
import com.tracki.utils.TrackiToast.Message.showShort
import com.tracki.utils.image_utility.Compressor
import com.tracki.utils.image_utility.ImagePicker
import com.tracki.widget.MenuEditText
import com.tracki.widget.SoftKeyBoardPopup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.set


class ChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>(), ChatNavigator,
        WebSocketManager.SocketListener, View.OnClickListener, ChatAdapter.ChatListener, SoftKeyBoardPopup.DocumentsSelectListener, MenuEditText.PopupListener {

    private var snackBar: Snackbar?=null
    private val MY_PERMISSIONS_REQUEST_RECORD_VIDEO: Int = 1324
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO: Int = 1325
    private val REQUEST_READ_STORAGE: Int = 223
    private val REQUEST_READ_CAMERA: Int = 222
    private val REQUEST_DOC: Int = 224
    private var pn = 1
    private var text: String? = null
    private var buddyIds: ArrayList<String>? = null
    private var uri: Uri? = null

    @Inject
    lateinit var mChatViewModel: ChatViewModel

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var adapter: ChatAdapter

    private lateinit var mActivityChatBinding: ActivityChatBinding
    private lateinit var toolbar: Toolbar
    private var rvChatList: RecyclerView? = null
    private var mChatList: ArrayList<ChatResponse>? = null
    private var ibSend: ImageView? = null
    private var msgCount = 10
    private var image: File? = null

    //    private var totMsg = 10
    private var loadMore = true
    private var roomId: String? = null
    private var roomName: String? = null
    private var imageUrlList: List<String> = java.util.ArrayList()
    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_chat
    override fun getViewModel() = mChatViewModel
    private val PICK_IMAGE_FILE_ID = 235
    private val CAMERA_PIC_REQUEST = 1277
    private val PICKFILE_REQUEST_CODE = 239
    private val PICK_AUDIO_REQUEST_CODE = 240
    private val PICK_VIDEO_REQUEST_CODE = 241
    private var actualImage: File? = null
    private var compressedImage: File? = null
    var mediaType: SupportedMediaType? = null
    lateinit var parentView: View
    lateinit var menuKeyboard: SoftKeyBoardPopup
    lateinit var rootView: ConstraintLayout
    lateinit var edMessage: MenuEditText
    private var isLastPage = false
    private var isLoading = false
    var name: String? = null
    var mobile: String? = null
    var video: File? = null
    var vidUri: Uri? = null

    val VIDEO_REQUEST = 546
    val AUDIO_REQUEST = 547
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityChatBinding = viewDataBinding
        mChatViewModel.navigator = this

        toolbar = mActivityChatBinding.toolbar
        //set toolbar taskName and enable home button
        name = intent.getStringExtra(EXTRA_BUDDY_NAME)
        setToolbar(toolbar, intent.getStringExtra(EXTRA_BUDDY_NAME))
        rootView = findViewById(R.id.rootView)
        parentView = findViewById<View>(android.R.id.content)
        edMessage = mActivityChatBinding.edMessage

        edMessage.popupListener = this

        menuKeyboard = SoftKeyBoardPopup(
                this,
                rootView,
                edMessage,
                edMessage,
                menuChatContainer
        )
        rvChatList = mActivityChatBinding.rvChatList
        adapter.setListener(this@ChatActivity)
        val manager = WrapContentLinearLayoutManager(this)
        manager.stackFromEnd = true

        rvChatList?.layoutManager = manager
        rvChatList?.adapter = adapter
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvChatList?.setHasFixedSize(true)

        edMessage = mActivityChatBinding.edMessage
        ibSend = mActivityChatBinding.ibSend
        ibSend?.setOnClickListener(this)

        if (intent != null) {
            if (intent.hasExtra(EXTRA_SELECTED_BUDDY)) {
                buddyIds = intent.getStringArrayListExtra(EXTRA_SELECTED_BUDDY)
                mobile = buddyIds?.get(0)
                roomId = intent.getStringExtra(EXTRA_ROOM_ID)
            }
        }
        ivAttachment.setOnClickListener {
            toggle()
        }
//        var layoutManger: LinearLayoutManager = rvChatList!!.layoutManager!! as WrapContentLinearLayoutManager
//        rvChatList!!.addOnScrollListener(object : PaginationListener(layoutManger) {
//            override fun loadMoreItems() {
//                this@ChatActivity.isLoading = true
//                onLoadMore()
//
//            }
//
//            override fun isLastPage(): Boolean {
//                return this@ChatActivity.isLastPage
//            }
//
//            override fun isLoading(): Boolean {
//                return this@ChatActivity.isLoading
//            }
//        })

    }

    override fun onResume() {
        super.onResume()

        initSocket()
        connectSocket(this)
        //if intent is called from item of user hashMap then open room in on start else
        if (intent.hasExtra(EXTRA_IS_CREATE_ROOM)) {
            openOrCreateChatRoom(buddyIds, roomId, loadMore, msgCount)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideKeyboard()
        finish()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ibSend -> {
                try {
                    text = edMessage?.text.toString()
                    if (text?.length == 0) {
                        return
                    }

                    edMessage?.setText("")
                    //if message count is 0 then user haven't open a room yet and we will call
                    //create room first and then on its response we send the message. else we directly
                    //send the message
                    if (!(intent.hasExtra(EXTRA_IS_CREATE_ROOM))) {
                        showLoading()
                        openOrCreateChatRoom(buddyIds, roomId, loadMore, msgCount)
                    } else {
                        if (roomId != null) {
                            // showLoading()
                            webSocketManager?.send(text, roomId)
                            text = ""
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    /**
     * Method used to create or open chat rooms form the server
     * and return the response message.
     */
    private fun openOrCreateChatRoom(buddyIds: ArrayList<String>?, roomId: String?,
                                     loadMsgs: Boolean, msgCount: Int) {
        try {
            if (webSocketManager.isConnected) {
                webSocketManager?.openCreateRoom(buddyIds, roomId, loadMsgs, msgCount)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This method is used to get the response from the server and act accordingly
     */
    override fun onSocketResponse(eventName: Int, baseModel: BaseModel?) {
        Log.e("eventName =>", "" + eventName)
        runOnUiThread {
            hideLoading()

            when (eventName) {

                3 -> {
                    progressBar.visibility = View.GONE
                    var totalMsg = msgCount
                    //get the message here and set the last message into the adapter
                    val message = baseModel as Messages
//                    println("message is: $message")
//                    val msg = ArrayList<Messages>()
//                    msg.add(message)

                    val chatResp = ChatResponse()
                    chatResp.message = message.message?.data
                    chatResp.isMe = message.message?.self
                    chatResp.time = message.time.toString()
                    chatResp.name = message.sender
                    chatResp.type = message.message?.type
                    if (mChatList == null) {
                        mChatList = ArrayList()
                    }
                    mChatList?.add(chatResp)
                    totalMsg += mChatList!!.size
                    if (adapter.getList() != null && adapter.getList()!!.isEmpty())
                        adapter.addItems(mChatList!!, true)
                    //update total count here every time
//                    totMsg = mChatList?.size!!
                    // adapter.addItems(mChatList!!, true)
                    // rvChatList?.smoothScrollToPosition(0)
                    adapter.notifyItemInserted(mChatList?.size!! - 1)
                    rvChatList?.smoothScrollToPosition(mChatList?.size!! - 1)
                }
                4 -> {
                    var totalMsg = msgCount
                    //if user initiate a chat for the first time then handle
                    //this for message count and chat listing
                    val openCreateRoomModel = baseModel as OpenCreateRoomModel
//                    println("open & create room: $openCreateRoomModel")
                    val messageList = openCreateRoomModel.messages
                    roomId = openCreateRoomModel.roomId
                    roomName = openCreateRoomModel.roomName
                    if (openCreateRoomModel.hm == null || !openCreateRoomModel.hm!!) {
                        isLastPage = true
                    }
                    //if message hashMap is not null then set the hashMap into
                    // the adapter and update msg count.
                    if (messageList != null) {
                        totalMsg += messageList.size
                        mChatList = ArrayList()
                        // load more messages into the hashMap.
                        loadMessages(openCreateRoomModel.hm, messageList)
                        //rvChatList?.smoothScrollToPosition(0)
                        rvChatList?.smoothScrollToPosition(mChatList?.size!! - 1)
                    }
                    //if count is 10 and
                    if (!(intent.hasExtra(EXTRA_IS_CREATE_ROOM))) {
                        webSocketManager?.send(text, roomId)
                        text = ""
                    }
                }
                7 -> {
                    this@ChatActivity.isLoading = false
                    var totalMsg = msgCount
                    // only 3 fields contains data here are message,roomId,hm(HasMore).
                    val openCreateRoomModel = baseModel as OpenCreateRoomModel

                    val messageList = openCreateRoomModel.messages
                    roomId = openCreateRoomModel.roomId
                    if (openCreateRoomModel.hm == null || !openCreateRoomModel.hm!!) {
                        isLastPage = true
                    }
                    if (openCreateRoomModel.hm != null)
                        Log.e("eventName", "=> " + openCreateRoomModel.hm)
                    if (messageList != null) {
                        totalMsg += messageList.size

                        // load more messages into the hashMap.
                        loadMessages(openCreateRoomModel.hm, messageList)
                    }
                }
                2 -> {
                    CommonUtils.showLogMessage("e", "heartbeatsend", "heartbeatsend")
                    webSocketManager?.sendHeartBeat("2:", roomId)
                    text = ""
                }
                else -> {
                    hideLoading()
                    println("inside else eventName $eventName")
                }
            }
        }
    }

    /**
     * Method used to load the messages and load more button.
     *
     * @param hm if has more true the add load more buddto into the hashMap
     * @param messageList hashMap of messages that needs to be added.
     * @param totalMsg
     */
    private fun loadMessages(hm: Boolean?, messageList: List<Messages>) {
        val bool: Boolean = mChatList?.size!! > 0
        val copyList: ArrayList<ChatResponse> = mChatList!!
        mChatList = ArrayList()
        // add load more at last place only if hm(Has More)  is true and
        // after adding all other messages
        if (hm!!) {
            val loadMore = ChatResponse()
            loadMore.isLoadMore = true
            mChatList?.add(0, loadMore)
        }

        for (i in messageList.indices) {
            val message = messageList[i].message
            val chatResp = ChatResponse()
            chatResp.message = message?.data
            chatResp.isMe = message?.self
            chatResp.time = messageList[i].time.toString()
            chatResp.name = messageList[i].sender
            chatResp.type = messageList[i].message?.type
            mChatList?.add(chatResp)
        }

        if (bool) {
            //add all previous messages
            mChatList?.addAll(copyList)
        }
        /* // add load more at last place only if hm(Has More)  is true and
         // after adding all other messages
         if (hm!!) {
             val loadMore = ChatResponse()

             loadMore.isLoadMore = true
             mChatList?.add(loadMore)
         }*/

        //update total count here every time
//        totMsg = mChatList?.size!!
        adapter.addItems(mChatList!!, true)
    }

    override fun onLoadMore() {
        try {
//            val lastItem = mChatList?.size!! - 1
            val lastItem = 0
            val data = mChatList?.get(lastItem) as ChatResponse
            if (data.isLoadMore != null && data.isLoadMore!!) {
                //Remove the load more view from 0th position and add loader into that place.
                mChatList?.removeAt(lastItem)
            }
            pn += 1 // add the page no. into the field
            webSocketManager?.loadMore(roomId!!, pn, msgCount)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOpen() {
        runOnUiThread {
            //if intent is called from item of user hashMap then open room in on start else
            if (intent.hasExtra(EXTRA_IS_CREATE_ROOM)) {
                openOrCreateChatRoom(buddyIds, roomId, loadMore, msgCount)
            }
        }
    }

    override fun closed() {
        //close the socket here
        webSocketManager = null
        runOnUiThread {
            hideLoading()
        }

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ChatActivity::class.java)
        }
    }


    open fun proceedToImagePicking() {
        val chooseImageIntent = ImagePicker.getPickImageIntent(this)
        startActivityForResult(chooseImageIntent, PICK_IMAGE_FILE_ID)
    }

    open fun proceedToGetFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, PICKFILE_REQUEST_CODE)
    }

    open fun proceedToPickAudio() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE)

    }

    open fun proceedToPickVideo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "video/*"
        startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE)

    }

    override fun onItemClick(doc: DocumentType) {
        //TrackiToast.Message.showShort(this, doc.type!!.type)
        when (doc.type) {
            DocType.DOC -> {

                mediaType = SupportedMediaType.DOCUMENT
                checkDocPermission()
            }
            DocType.GALLERY -> {
                mediaType = SupportedMediaType.IMAGE
                checkStoragePermission()
            }
            DocType.CAMERA -> {
                mediaType = SupportedMediaType.IMAGE
                checkCameraPermission()
            }
            DocType.AUDIO -> {
                //proceedToPickAudio()

                mediaType = SupportedMediaType.AUDIO
                askUserForRecordCameraGallery(mediaType!!)
            }
            DocType.VIDEO -> {
                //proceedToPickVideo()
                mediaType = SupportedMediaType.VIDEO
                askUserForRecordCameraGallery(mediaType!!)
            }
            DocType.PAYMENT -> {
                openWebView()
            }
        }
    }

    private fun askUserForRecordCameraGallery(mediaType: SupportedMediaType) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_camera_gallery)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        val llRecord = dialog.findViewById<LinearLayout>(R.id.llrecord)
        val llGallery = dialog.findViewById<LinearLayout>(R.id.llChooseGallery)
        val ivGallery = dialog.findViewById<ImageView>(R.id.ivGallery)
        val ivRecord = dialog.findViewById<ImageView>(R.id.ivRecord)
        val tvRecord = dialog.findViewById<TextView>(R.id.tvRecord)
        val tvGallery = dialog.findViewById<TextView>(R.id.tvGallery)
        if (mediaType == SupportedMediaType.VIDEO) {
            ivRecord.setImageResource(R.drawable.ic_video_call)
            tvRecord.text = "Record video"
            tvGallery.text = "Choose video from gallery"

        } else if (mediaType == SupportedMediaType.AUDIO) {
            ivRecord.setImageResource(R.drawable.ic_voice_search)
            tvRecord.text = "Record audio"
            tvGallery.text = "Choose audio from gallery"
        }
        llRecord.setOnClickListener {
            dialog.dismiss()
            if (mediaType == SupportedMediaType.VIDEO) {
                checkRecordVideo()
            } else if (mediaType == SupportedMediaType.AUDIO) {
                checkRecordAudio()
            }
        }
        llGallery.setOnClickListener {
            dialog.dismiss()
            if (mediaType == SupportedMediaType.VIDEO) {
                this.mediaType = SupportedMediaType.VIDEO
                proceedToPickVideo()

            } else if (mediaType == SupportedMediaType.AUDIO) {
                this.mediaType = SupportedMediaType.AUDIO
                proceedToPickAudio()

            }
        }

        dialog.window!!.attributes = lp
        if (!dialog.isShowing) dialog.show()
    }

    fun openWebView() {
        var myname = name!!.replace(" ", "%20").trim()
        CommonUtils.showLogMessage("e", "name", myname)
        var url = "https://qa2.rocketflyer.in/payment?mob=${mobile}&name=${myname}&amt=1.0&roomid=${roomId}"
        // var url = URLEncoder.encode("https://qa2.rocketflyer.in/payment?mob=${mobile}&name=${myname}&amt=1.0", "UTF-8")
        CommonUtils.showLogMessage("e", "url in", url)
//        val navigation = Navigation()
//        val actionConfig = ActionConfig()
//        actionConfig.actionUrl = url
//        navigation.actionConfig = actionConfig
//        navigation.title = "Pay To ${name}"
//        startActivity(WebViewActivity.newIntent(this@ChatActivity)
//                .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation))
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {

        }
    }

    override fun onDestroy() {
        menuKeyboard.clear()
        super.onDestroy()
//        if (webSocketManager != null) {
//            webSocketManager?.disconnect()
//            webSocketManager = null
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            //  progressBar.visibility = View.VISIBLE
            when (requestCode) {
                PICK_IMAGE_FILE_ID -> {
                    progressBar.visibility = View.VISIBLE
                    actualImage = ImagePicker.getImageFileToUpload(this, resultCode, data)
                    compressImage()
                }
                PICKFILE_REQUEST_CODE -> {
                    progressBar.visibility = View.VISIBLE
                    var file = FileUtils.getPdfFileToUpload(this, resultCode, data)
                    val images = java.util.ArrayList<File>()
                    images.add(file)
                    val map = HashMap<String, java.util.ArrayList<File>>()
                    map[AppConstants.CAPTURED_KEY] = images
                    showLoading()
                    mChatViewModel.uploadImage(httpManager, map)
                }
                PICK_AUDIO_REQUEST_CODE -> {
                    progressBar.visibility = View.VISIBLE
                    var file = FileUtils.getPdfFileToUpload(this, resultCode, data)
                    val images = java.util.ArrayList<File>()
                    images.add(file)
                    val map = HashMap<String, java.util.ArrayList<File>>()
                    map[AppConstants.CAPTURED_KEY] = images
                    showLoading()
                    mChatViewModel.uploadImage(httpManager, map)
                }
                PICK_VIDEO_REQUEST_CODE -> {
                    var file = FileUtils.getPdfFileToUpload(this, resultCode, data)
                    val images = java.util.ArrayList<File>()
                    images.add(file)
                    val map = HashMap<String, java.util.ArrayList<File>>()
                    map[AppConstants.CAPTURED_KEY] = images
                    showLoading()
                    mChatViewModel.uploadImage(httpManager, map)
                }
                CAMERA_PIC_REQUEST -> {
                    progressBar.visibility = View.VISIBLE
                    try {
                        val file = File(image!!.path)
                        if (file.exists()) {
                            val bm = ImagePicker.getImageResized(this, uri)
                            // Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                            // Code to manage the bitmap
                            actualImage = CommonUtils.convertBitmapToFile(this, bm,
                                    "upload_" + Calendar.getInstance().timeInMillis + ".jpg")
                            if (actualImage != null) {
                                Compressor(this)
                                        .compressToFileAsFlowable(actualImage)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ file1: File ->
                                            compressedImage = file1
                                            val images = java.util.ArrayList<File>()
                                            images.add(compressedImage!!)
                                            val map = HashMap<String, java.util.ArrayList<File>>()
                                            map[AppConstants.CAPTURED_KEY] = images
                                            showLoading()
                                            mChatViewModel.uploadImage(httpManager, map)

                                        }) { error: Throwable -> showShort(this, error.message) }
                            }
                        } else {
                        }
                    } catch (c: java.lang.Exception) {
                        c.printStackTrace()
                    }


                }
                AUDIO_REQUEST -> {
//                    val file = File(audio?.path)
//                    val images = java.util.ArrayList<File>()
//                    images.add(file)
//                    val map = HashMap<String, java.util.ArrayList<File>>()
//                    map[AppConstants.CAPTURED_KEY] = images
//                    showLoading()
//                    mChatViewModel.uploadImage(httpManager, map)
                    progressBar.visibility = View.VISIBLE
                    var file = FileUtils.getPdfFileToUpload(this, resultCode, data)
                    val images = java.util.ArrayList<File>()
                    images.add(file)
                    val map = HashMap<String, java.util.ArrayList<File>>()
                    map[AppConstants.CAPTURED_KEY] = images
                    showLoading()
                    mChatViewModel.uploadImage(httpManager, map)
                }
                VIDEO_REQUEST -> {
                    try {


                        var thread = Thread(Runnable {
                            val file = File(video?.path)
                            var filePath = SiliCompressor.with(this).compressVideo(file.path, file.parent)
                            if (file.exists()) {
                                if (file.delete()) {
                                    Log.e("file Deleted :", file!!.path!!)
                                } else {
                                    Log.e("file not Deleted :", file!!.path!!.toString())
                                }
                            }
                            runOnUiThread(Runnable {
                                var fileCompress = File(filePath)
                                val images = java.util.ArrayList<File>()
                                images.add(fileCompress)
                                val map = HashMap<String, java.util.ArrayList<File>>()
                                map[AppConstants.CAPTURED_KEY] = images
                                showLoading()
                                mChatViewModel.uploadImage(httpManager, map)
                            })

                        })
                        thread.start()


                    } catch (c: Exception) {
                        c.printStackTrace()
                    }

                }

                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else if (resultCode == RESULT_CANCELED) {
            // User Cancelled the action
            showLong(this, "User Cancelled the action")
        }

    }

    @SuppressLint("CheckResult")
    private fun compressImage() {
        if (actualImage == null) {
            showShort(this, "Please choose an image!")
        } else {
            Compressor(this)
                    .compressToFileAsFlowable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ file1: File ->
                        compressedImage = file1
                        val images = java.util.ArrayList<File>()
                        images.add(compressedImage!!)
                        val map = HashMap<String, java.util.ArrayList<File>>()
                        map[AppConstants.CAPTURED_KEY] = images
                        mChatViewModel.uploadImage(httpManager, map)

                    }) { error: Throwable -> showShort(this, error.message) }

        }
    }

    override fun handleImageResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?) {
        //hideLoading()

        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            val fileUrlsResponse = Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
            val imageResponseMap = fileUrlsResponse.filesUrl

            imageUrlList = imageResponseMap!![AppConstants.CAPTURED_KEY]!!
            if (imageUrlList.isNotEmpty()) {
                var imageUrl = imageUrlList[0]
                if (roomId != null) {
                    showLoading()
                    webSocketManager?.sendMedia(imageUrl, roomId, mediaType!!.name)
                    text = ""
                }
            } else {
                hideLoading()
                progressBar.visibility = View.GONE
            }

        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }

    private fun openCamera() {
        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (photoIntent.resolveActivity(this.getPackageManager()) != null) {
            val file: File?
            try {
                file = createImageFile()
                if (file != null && file.exists()) {
                    uri = FileProvider.getUriForFile(this.getApplicationContext(),
                            "com.rocketflyer.rocketflow" + ".provider", file)
                    //                    uri = Uri.parse(new File(imageFilePath).toString());
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        photoIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK) // Tested on API 24 Android version 7.0(Samsung S6)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        photoIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK) // Tested on API 27 Android version 8.0(Nexus 6P)
                        photoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                    } else {
                        photoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1) // Tested API 21 Android version 5.0.1(Samsung S4)
                    }
                    photoIntent.putExtra("TEST", "String Extra")
                    startActivityForResult(photoIntent, CAMERA_PIC_REQUEST)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun toggle() {
        if (menuKeyboard.isShowing) {
            menuKeyboard.dismiss()
        } else {
            menuKeyboard.show()
        }
    }


    override fun getPopup(): PopupWindow {
        return menuKeyboard
    }

    fun checkRecordVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    &&checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                performCaptureVideo()
            } else {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_RECORD_VIDEO)
            }
        } else {
            performCaptureVideo()
        }
    }

    fun checkRecordAudio() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    &&checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                recordAudio()
            } else {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
            }
        } else {
            recordAudio()
        }
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = Environment.getExternalStorageDirectory()
        video = File.createTempFile(timeStamp, ".mp4", storageDir)
        return video
    }


    private fun recordAudio() {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        startActivityForResult(intent, AUDIO_REQUEST)

    }

    private fun performCaptureVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

        if (intent.resolveActivity(this!!.packageManager) != null) {
            val file: File?
            try {
                file = createVideoFile()


                if (file != null && file.exists()) {
                    vidUri = FileProvider.getUriForFile(applicationContext,
                        "com.rocketflyer.rocketflow" + ".provider", file)
                    //                    uri = Uri.parse(new File(imageFilePath).toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, vidUri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        intent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK) // Tested on API 24 Android version 7.0(Samsung S6)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK) // Tested on API 27 Android version 8.0(Nexus 6P)
                        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                    } else {
                        intent.putExtra("android.intent.extras.CAMERA_FACING", 1) // Tested API 21 Android version 5.0.1(Samsung S4)
                    }
                    intent.putExtra("TEST", "String Extra")
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
                    startActivityForResult(intent, VIDEO_REQUEST)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECORD_VIDEO -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    performCaptureVideo()
                } else {
                    showShort(this@ChatActivity, "Please Grant Permissions")
                }
                return
            }
            REQUEST_READ_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    TrackiToast.Message.showShort(this@ChatActivity, "Please Grant Permissions")
                }
                return
            }
            REQUEST_READ_STORAGE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    proceedToImagePicking()
                } else {
                    TrackiToast.Message.showShort(this@ChatActivity, "Please Grant Permissions")
                }
                return
            }
            REQUEST_DOC -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    proceedToGetFile()
                } else {
                    TrackiToast.Message.showShort(this@ChatActivity, "Please Grant Permissions")
                }
                return
            }
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    recordAudio()
                } else {
                    showShort(this@ChatActivity, "Please Grant Permissions")
                }
                return
            }
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.action_audio_call -> {
//                TrackiToast.Message.showShort(this,"Coming Soon..")
//            }
//            R.id.action_video_call -> {
//                TrackiToast.Message.showShort(this,"Coming Soon..")
//
//            }
            android.R.id.home -> {
                onBackPressed()
            }

            else -> return false
        }
        return true
    }
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.option_chat, menu)
//
//        return true
//    }


    fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED )
                     {
                         openCamera()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), REQUEST_READ_CAMERA)
            }
        } else {
            openCamera()
        }
    }
    fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED )
            {
                proceedToImagePicking()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), REQUEST_READ_STORAGE)
            }
        } else {
            proceedToImagePicking()
        }
    }
    fun checkDocPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
            {
                proceedToGetFile()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_DOC)
            }
        } else {
            proceedToGetFile()
        }
    }

    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityChatBinding.rootView, getString(R.string.please_check_your_internet_connection))
    }
}
