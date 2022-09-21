package com.tracki.ui.messages

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.BuddiesRequest
import com.tracki.data.model.response.config.Buddy
import com.tracki.data.model.response.config.BuddyListResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityMessageBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.buddylisting.BuddyListingActivity
import com.tracki.ui.chat.ChatActivity
import com.tracki.ui.custom.socket.*
import com.tracki.utils.*
import com.tracki.utils.AppConstants.Extra.*
import kotlinx.android.synthetic.main.activity_message.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MessagesActivity : BaseActivity<ActivityMessageBinding, MessageViewModel>(),
        MessageNavigator, MessageAdapter.MessageListener, WebSocketManager.SocketListener {

    @Inject
    lateinit var mMessageViewModel: MessageViewModel

    @Inject
    lateinit var adapter: MessageAdapter

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private var buddyIdsArrayList: ArrayList<String>? = null
    private var searchView: SearchView? = null
    private var list: ArrayList<Buddy>? = null
    private lateinit var mActivityMessageBinding: ActivityMessageBinding
    private lateinit var toolbar: Toolbar
    private var rvMessage: RecyclerView? = null

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_message
    override fun getViewModel() = mMessageViewModel
    private var snackBar: Snackbar? = null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityMessageBinding.coordinator, getString(R.string.please_check_your_internet_connection))
    }

    var TAG = "MessagesActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMessageBinding = viewDataBinding
        mMessageViewModel.navigator = this
        adapter.setListener(this)

        toolbar = mActivityMessageBinding.toolbar
        //set toolbar taskName and enable home button
        setToolbar(toolbar, getString(R.string.messages))

        showLoading()
        initSocket()
        connectSocket(this)
        if(intent.hasExtra(AppConstants.Extra.FROM)) {
            if (intent.getStringExtra(AppConstants.Extra.FROM).equals(AppConstants.Extra.TASK_DETAILS)) {
                mActivityMessageBinding.fabAddBuddy.visibility = View.GONE
                if(intent.hasExtra(EXTRA_SELECTED_BUDDY)){
                    runOnUiThread {
                        list=intent.getSerializableExtra(EXTRA_SELECTED_BUDDY) as ArrayList<Buddy>
                        setRecyclerView(list!!)
                        performChatWithBuddyId(list)
                    }

                }


            }
        }
    }

    override fun onResume() {
        super.onResume()

//        if (intent.hasExtra(AppConstants.Extra.FROM)) {
//            mActivityMessageBinding.fabAddBuddy.visibility = View.GONE
//
//        } else {
//            hitBuddyApi()
//        }
    }

    private fun hitBuddyApi() {
        if (::mMessageViewModel.isInitialized) {
            val statusList = ArrayList<BuddyStatus>()
            statusList.add(BuddyStatus.ON_TRIP)
            statusList.add(BuddyStatus.OFFLINE)
            statusList.add(BuddyStatus.IDLE)
            val buddyRequest = BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, true)
            val api = TrackiApplication.getApiMap()[ApiType.BUDDIES]
            showLoading()
            mMessageViewModel.fetchBuddyList(buddyRequest, httpManager, api!!)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (webSocketManager != null) {
            webSocketManager?.disconnect()
            webSocketManager = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_view, menu)

        val myActionMenuItem = menu.findItem(R.id.action_search)
        if (intent.hasExtra(AppConstants.Extra.FROM)) {
            myActionMenuItem.isVisible = false
        }
        searchView = myActionMenuItem.actionView as SearchView
        var textView: TextView = searchView!!.findViewById(R.id.search_src_text)
        val externalFont = Typeface.createFromAsset(this.assets, "fonts/campton_book.ttf")
        textView.typeface = externalFont
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        val closeButton: ImageView = searchView!!.findViewById(R.id.search_close_btn)

        closeButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                searchView?.isIconified = true
                myActionMenuItem.collapseActionView()
                // TrackiToast.Message.showShort(this@MessagesActivity, "close click")
                adapter.populateList()
            }
        })



        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                if (!(searchView?.isIconified!!)) {
                    searchView?.isIconified = true
                }
                if (adapter.mList?.size!! > 0) {
                    adapter.addFilter(query)
                } /*else {
                    TrackiToast.Message.showShort(this@MessagesActivity, getString(R.string.cannot_performe_this_operation))
                }*/

                myActionMenuItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                if (adapter.mList?.size!! > 0) {
                    adapter.addFilter(newText)
                } /*else {
                    TrackiToast.Message.showShort(this@MessagesActivity, getString(R.string.cannot_performe_this_operation))
                }*/
                return false
            }
        })
        return true
    }

    override fun onItemClick(response: Buddy) {
        val list = ArrayList<String>()
        list.add(response.buddyId!!)
        startActivityForResult(ChatActivity.newIntent(this@MessagesActivity)
                .putExtra(EXTRA_SELECTED_BUDDY, list)
                .putExtra(EXTRA_BUDDY_NAME, response.name)
                .putExtra(EXTRA_IS_CREATE_ROOM, false)
                .putExtra(EXTRA_ROOM_ID, response.roomId),
                AppConstants.REQUEST_CODE_CHAT_FROM_AFTER_SELECT_BUDDY
        )
    }

    override fun onFabClick() {
        CommonUtils.preventTwoClick(fabAddBuddy)
        buddyIdsArrayList = ArrayList()
        startActivityForResult(BuddyListingActivity.newIntent(this)
                .putExtra(EXTRA_SELECTED_BUDDY, buddyIdsArrayList)
                .putExtra(EXTRA_IS_YOU, true)
                /*.putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU, true),*/
                .putExtra(EXTRA_BUDDY_LIST_CALLING_FROM_MESSAGE_LIST, true),
                AppConstants.REQUEST_CODE_DASHBOARD_BUDDIES)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.REQUEST_CODE_DASHBOARD_BUDDIES -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (data.hasExtra(EXTRA_SELECTED_BUDDY)) {
                        buddyIdsArrayList = data.getStringArrayListExtra(EXTRA_SELECTED_BUDDY)
                        val intent = ChatActivity.newIntent(this@MessagesActivity)
                        intent.putExtra(EXTRA_BUDDY_NAME, data.getStringExtra(EXTRA_BUDDY_NAME))
                        intent.putExtra(EXTRA_SELECTED_BUDDY, buddyIdsArrayList)
                        //check if map contains the room id For this key
                        if (buddyIdsArrayList != null && buddyIdsArrayList!!.isNotEmpty() && TrackiApplication.getChatMap().containsKey(buddyIdsArrayList!![0])) {
                            val roomId = TrackiApplication.getChatMap()[buddyIdsArrayList!![0]]
                            intent.putExtra(EXTRA_IS_CREATE_ROOM, false)
                            intent.putExtra(EXTRA_ROOM_ID, roomId)
                        }
                        startActivityForResult(intent, AppConstants.REQUEST_CODE_CHAT_FROM_AFTER_SELECT_BUDDY)
                    }
                }
            }
            AppConstants.REQUEST_CODE_CHAT_FROM_AFTER_SELECT_BUDDY -> {
                showLoading()
                initSocket()
                connectSocket(this)
                if (intent.hasExtra(AppConstants.Extra.FROM)) {
                    mActivityMessageBinding.fabAddBuddy.visibility = View.GONE
                    if(intent.hasExtra(EXTRA_SELECTED_BUDDY)){
                        runOnUiThread {
                            list=intent.getSerializableExtra(EXTRA_SELECTED_BUDDY) as ArrayList<Buddy>
                            setRecyclerView(list!!)
                            performChatWithBuddyId(list)
                        }

                    }

                } else {
                    hitBuddyApi()
                }
            }
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val buddyListResponse = Gson().fromJson(result.toString(), BuddyListResponse::class.java)
            if (buddyListResponse.buddies != null && buddyListResponse.buddies!!.isNotEmpty()) {
                list = ArrayList();
                list!!.addAll(buddyListResponse.buddies!!)
                if (list != null && list!!.isNotEmpty()) {
                    var connectionIds = ""
                    for (i in list?.indices!!) {
                        connectionIds += list!![i].buddyId

                        var p = i
                        p += 1
                        //if array hashMap contains next item then add comma into the string
                        if (list!!.size > p) {
                            connectionIds += ","
                        }
                    }
                    try {
                        if (webSocketManager?.isConnected!!) {
                            // get the status of connections from connect ids
                            webSocketManager?.connectPacket(connectionIds)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                hideLoading()
                setRecyclerView(ArrayList())
            }

        }
    }

    private fun setRecyclerView(buddyList: ArrayList<Buddy>) {
        if (rvMessage == null) {
            rvMessage = mActivityMessageBinding.rvMessageList
            rvMessage?.layoutManager = LinearLayoutManager(this)
        }
        if (rvMessage?.adapter == null) {
            rvMessage?.adapter = adapter
        }
        adapter.addItems(buddyList)
    }

    override fun onSocketResponse(eventName: Int, baseModel: BaseModel?) {
        runOnUiThread {
            hideLoading()
            Log.e(TAG, "onSocketResponse() called")
            Log.e(TAG, "eventName =>" + eventName)
            Log.e(TAG, "baseModel =>" + baseModel)
            when (eventName) {
                4 -> {

                    val openCreateRoomModel = baseModel as OpenCreateRoomModel
//                    println("open & create room: $openCreateRoomModel")
                    val messageList = openCreateRoomModel.messages
                    var roomId = openCreateRoomModel.roomId
                    var roomName = openCreateRoomModel.roomName
                    val buddy = list!![0]
                    buddy.roomId = roomId
                    if (messageList != null) {
                        buddy.lastMessage = messageList[messageList.size - 1].message!!.data
                        buddy.lastMsgTime = messageList[messageList.size - 1].time
                        if (!messageList[messageList.size - 1].message!!.self!!)
                            buddy.chatUserStatus = CommonUtils.getChatUserStatus(openCreateRoomModel
                                    .connectionsStatus!!.get(messageList[messageList.size - 1].sender!!))
                    }
                    setRecyclerView(list!!)
                    /* onItemClick(list!![0])
                    finish()*/

                }
                1 -> {
                    //connection hashMap
                    val connectionInfoList = ArrayList<ConnectionInfo>()
                    //buddy message hashMap
                    val buddyList = ArrayList<Buddy>()
                    val map = HashMap<String, String>()
                    val connectionResponse = baseModel as ConnectionResponse
                    // check room-ids if not null then get the hashMap from buddy hashMap and populate into the
                    for (key in connectionResponse.connections?.keys!!) {
                        val value = connectionResponse.connections!![key] as ConnectionInfo
                        if (value.roomId != null) {
                            map[value.connectionId!!] = value.roomId!!
                            connectionInfoList.add(value)
                            println("connectionsId: ${value.connectionId} roomId: ${value.roomId}")
                        }
                    }
                    if (connectionInfoList.isNotEmpty()) {
                        for (i in connectionInfoList.indices) {
                            val c = connectionInfoList[i]
                            val cId = c.connectionId
                            for (j in list?.indices!!) {
                                if (list!![j].buddyId.equals(cId, ignoreCase = true)) {
                                    val buddy = list!![j]
                                    buddy.lastMessage = c.lastMessage?.message?.data
                                    buddy.roomId = c.roomId
                                    buddy.type = c.lastMessage?.message?.type
                                    buddy.chatUserStatus = CommonUtils.getChatUserStatus(c.status)
                                    c.lastMessage?.let {
                                        buddy.lastMsgTime = c.lastMessage?.time!!
                                    }
                                    buddyList.add(buddy)
                                }
                            }
                        }
                    }
                    //set all the connection ids and its room ids into the map and save into the app.
                    TrackiApplication.setChatMap(map)
                    // sort based on time stamp
                    Collections.sort(buddyList, object : Comparator<Buddy> {
                        override fun compare(o1: Buddy, o2: Buddy): Int {
                            var value = 0

                            if (o1.lastMsgTime > o2.lastMsgTime) {
                                value = -1
                            } else if (o1.lastMsgTime < o2.lastMsgTime) {
                                value = 1
                            }

                            println("time stamp is: $value ${o1.lastMsgTime} ${o2.lastMsgTime}")
                            return value

                        }
                    })

                    // set recycler view here
                    setRecyclerView(buddyList)
                }
                11 -> {
                    // update the status of the user if user is online, offline, idle or busy.
                    val connectionInfo = baseModel as ConnectionDetail
                    if (adapter.mList != null) {
                        for (i in adapter.mList?.indices!!) {
                            //if connection Id match in the hashMap then notify adapter
                            if (connectionInfo.connectionId == adapter.mList!![i].buddyId!!) {
                                adapter.mList!![i].chatUserStatus = CommonUtils.getChatUserStatus(connectionInfo.state)
                                adapter.notifyItemChanged(i)
                                break
                            }
                        }
                    }
                }
                else -> {
                    hideLoading()
                }
            }
        }
    }

    fun performChatWithBuddyId(list:ArrayList<Buddy>?){
        if (list!=null&&list.isNotEmpty()) {
            var connectionIds = ""
            for (i in list.indices!!) {
                connectionIds += list[i].buddyId

                var p = i
                p += 1
                //if array hashMap contains next item then add comma into the string
                if (list!!.size > p) {
                    connectionIds += ","
                }
            }
            try {
                if (webSocketManager?.isConnected!!) {
                    // get the status of connections from connect ids
                    webSocketManager?.connectPacket(connectionIds)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    override fun onOpen() {
        if (!intent.hasExtra(AppConstants.Extra.FROM))
        {
            runOnUiThread {
                hitBuddyApi()
            }
        }
        else {
            if (intent.getStringExtra(AppConstants.Extra.FROM).equals(AppConstants.Extra.TASK_DETAILS)) {
                if(intent.hasExtra(EXTRA_SELECTED_BUDDY)){
                    runOnUiThread {
                        mActivityMessageBinding.fabAddBuddy.visibility = View.GONE
                        list=intent.getSerializableExtra(EXTRA_SELECTED_BUDDY) as ArrayList<Buddy>
                        setRecyclerView(list!!)
                        performChatWithBuddyId(list)
                    }

                }


            }else{
                runOnUiThread {
                    // TrackiToast.Message.showShort(this,"else")
                    var buddy = Buddy()
                    buddy.buddyId = intent.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_ID)
                    buddy.name = intent.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_NAME)
                    list = ArrayList<Buddy>()
                    list!!.add(buddy)
                    if (list != null) {
                        var connectionIds = ""
                        for (i in list?.indices!!) {
                            connectionIds += list!![i].buddyId

                            var p = i
                            p += 1
                            //if array hashMap contains next item then add comma into the string
                            if (list!!.size > p) {
                                connectionIds += ","
                            }
                        }
                        try {
                            if (webSocketManager?.isConnected!!) {
                                // get the status of connections from connect ids
                                // Log.e(TAG, connectionIds)
                                //webSocketManager?.connectPacket(connectionIds)
                                var list = ArrayList<String>()
                                list.add(buddy.buddyId!!)
                                webSocketManager?.openCreateRoom(list, null, true, 10)

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

            }

        }

    }

    override fun closed() {
        //close the socket here
        webSocketManager = null
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MessagesActivity::class.java)
        }
    }

}