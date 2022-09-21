package com.rocketflyer.rocketflow.ui.feeds

import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.CommentsPostRequest
import com.tracki.data.model.request.EmployeeListAttendanceRequest
import com.tracki.data.model.request.GetCommentsOfPosts
import com.tracki.data.model.response.config.Comments
import com.tracki.data.model.response.config.CommentsResponse
import com.tracki.data.model.response.config.Post
import com.tracki.data.model.response.config.PostResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityFeedsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.feeddetails.FeedDetailsActivity
import com.tracki.ui.likeslist.LikeListActivity
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.ui.tasklisting.PagingData
import com.tracki.utils.*
import com.tracki.utils.DateTimeUtil.Companion.getParsedDate
import kotlinx.android.synthetic.main.activity_feeds.*
import kotlinx.android.synthetic.main.item_dynamic_form_time.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class FeedsActivity : BaseActivity<ActivityFeedsBinding, FeedsViewModel>(), FeedsNavigator,
    FeedsAdapter.LikePostUser,
    SortTimeListAdapter.OnTimeSelectedListener {
    private var recyclerViewComments: RecyclerView? = null

    @Inject
    lateinit var mFeedViewModel: FeedsViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: FeedsAdapter

    @Inject
    lateinit var commentsAdapter: CommentsAdapter

    lateinit var binding: ActivityFeedsBinding

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var snackBar: Snackbar? = null
    private var fromDate: Long = 0
    private var toDate: Long = 0
    private var timeList: ArrayList<TimeInfo> = ArrayList()
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            binding.rlMain,
            getString(R.string.please_check_your_internet_connection)
        )
    }


    companion object {
        private val TAG = FeedsActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, FeedsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mFeedViewModel.navigator = this

        if (intent.hasExtra(AppConstants.TITLE)) {
            var titile = intent.getStringExtra(AppConstants.TITLE)
            setToolbar(toolbar, titile)
        } else {
            setToolbar(toolbar, "Brodcost")
        }

        var layoutManger: LinearLayoutManager =
            recyclerPost!!.layoutManager!! as LinearLayoutManager
        recyclerPost.addOnScrollListener(object : PaginationListener(layoutManger) {
            override fun loadMoreItems() {
                this@FeedsActivity.isLoading = true
                currentPage++
                /*  showLoading()
                  var request = EmployeeListAttendanceRequest()
                  val pagingData = PagingData()
                  pagingData.datalimit = 10
                  pagingData.pageOffset = (currentPage - 1) * 10
                  pagingData.pageIndex = currentPage
                  request.paginationData = pagingData

                  mFeedViewModel.getAllPost(httpManager, request)*/
                getAllPost()

            }

            override fun isLastPage(): Boolean {
                return this@FeedsActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@FeedsActivity.isLoading
            }
        })

        addCustomDateInList()
        toDayDate(binding.tvDate)
        getAllPost()
        binding.tvDate.setOnClickListener {
            showTimeListDialog(timeList)
        }
    }

    private fun addCustomDateInList() {

        for (i in 0..5){
            when (i){
                0 ->{
                    var timeInfo = TimeInfo()
                    val dateRange = tillNow(null)
                    timeInfo.title = getString(R.string.till_now)
                    timeInfo.from = dateRange.fromDate
                    timeInfo.to = dateRange.toDate
                    timeList.add(timeInfo)
                }
                1 ->{
                    var timeInfo = TimeInfo()
                    val dateRange = toDayDate(null)
                    timeInfo.title = getString(R.string.today)
                    timeInfo.from = dateRange.fromDate
                    timeInfo.to = dateRange.toDate
                    timeList.add(timeInfo)
                }
                2->{
                    var timeInfo = TimeInfo()
                    val dateRange = yesterDay(null)
                    timeInfo.title = getString(R.string.yesterday)
                    timeInfo.from = dateRange.fromDate
                    timeInfo.to = dateRange.toDate
                    timeList.add(timeInfo)
                }
                3 ->{
                    var timeInfo = TimeInfo()
                    val dateRange = lastWeek(null)
                    timeInfo.title = getString(R.string.last_week)
                    timeInfo.from = dateRange.fromDate
                    timeInfo.to = dateRange.toDate
                    timeList.add(timeInfo)
                }

                4 ->{
                    var timeInfo = TimeInfo()
                    val dateRange = lastMonth(null)
                    timeInfo.title = getString(R.string.last_month)
                    timeInfo.from = dateRange.fromDate
                    timeInfo.to = dateRange.toDate
                    timeList.add(timeInfo)
                }

                5 ->{
                    var timeInfo = TimeInfo()
                    timeInfo.title = getString(R.string.custom_range)
                    timeList.add(timeInfo)
                }
            }
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_feeds
    }

    override fun getViewModel(): FeedsViewModel {
        return mFeedViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (binding.adapter == null) {
            binding.adapter = adapter
        }
        if (CommonUtils.handleResponse(callback, error, result, this@FeedsActivity)) {
            val jsonConverter: JSONConverter<PostResponse> = JSONConverter()
            var response: PostResponse = jsonConverter.jsonToObject(
                result.toString(),
                PostResponse::class.java
            ) as PostResponse
            if (response.postList != null) {
                this@FeedsActivity.isLoading = false
                adapter.addItems(response.postList as ArrayList<Post>)
                CommonUtils.showLogMessage(
                    "e", "adapter total_count =>",
                    "" + adapter.getItemCount()
                )
                CommonUtils.showLogMessage(
                    "e", "fetch total_count =>",
                    "" + response.paginationData!!.dataCount
                )
                isLastPage = response.paginationData!!.dataCount == adapter.getAllList().size


            }
        } else {
        }
    }

    override fun handleCommentsResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@FeedsActivity)) {
            val jsonConverter: JSONConverter<CommentsResponse> = JSONConverter()
            var response: CommentsResponse = jsonConverter.jsonToObject(
                result.toString(),
                CommentsResponse::class.java
            ) as CommentsResponse
            if (response.comments != null) {
                this@FeedsActivity.isLoading = false
                if (recyclerViewComments != null && recyclerViewComments!!.adapter == null)
                    recyclerViewComments!!.adapter = commentsAdapter
                commentsAdapter.addItems(response.comments as ArrayList<Comments>)


            }
        } else {
            if (recyclerViewComments != null && recyclerViewComments!!.adapter == null)
                recyclerViewComments!!.adapter = commentsAdapter
        }
    }

    override fun handlePostCommentsResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
    }

    fun getAllPost() {
        showLoading()
        if (currentPage == PaginationListener.PAGE_START) {
            if (::adapter.isInitialized)
                adapter.clearList()
        }
        var request = EmployeeListAttendanceRequest()
        val pagingData = PagingData()
        pagingData.datalimit = 10
        pagingData.pageOffset = (currentPage - 1) * 10
        pagingData.pageIndex = currentPage
        request.from = fromDate
        request.to = toDate
        request.paginationData = pagingData
        mFeedViewModel.getAllPost(httpManager, request)
    }

    private fun openDialogComments(postId: String?, position: Int) {
        commentsAdapter.clearList()
        recyclerViewComments = null
        val dialog = Dialog(this, R.style.DialogAnimation)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_show_all_comments)
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
        recyclerViewComments = dialog.findViewById<RecyclerView>(R.id.recyclerViewComments)
        var etComments = dialog.findViewById<EditText>(R.id.etComments)
        var btnSend = dialog.findViewById<ImageView>(R.id.btnSend)
        var ivBack = dialog.findViewById<ImageView>(R.id.ivBack)

        var request = GetCommentsOfPosts()
        request.postId = postId
        mFeedViewModel.getPostComments(httpManager, request)
        btnSend.setOnClickListener {
            var comments = etComments.text.toString().trim()
            if (comments.isEmpty()) {
                TrackiToast.Message.showShort(this@FeedsActivity, "Please Enter Comments")
            } else {
                etComments.setText("")
                showLoading()
                var request = CommentsPostRequest()
                request.postId = postId
                request.comment = comments
                request.like = false
                var dataComments = adapter.getAllList()[position]
                dataComments.commentCounts = dataComments.commentCounts + 1
                adapter.notifyItemChanged(position)
                mFeedViewModel.sendComments(httpManager, request)
                var comment = Comments()
                comment.comment = comments
                comment.commentedBy = preferencesHelper.userDetail.name
                comment.commentedAt = DateTimeUtil.getParsedDateTime5(System.currentTimeMillis())
                commentsAdapter.addSingleComments(comment)
                recyclerViewComments!!.smoothScrollToPosition(commentsAdapter.getAllList().lastIndex)
            }
        }
        ivBack.setOnClickListener {
            dialog.dismiss()
        }
        if (!dialog.isShowing)
            dialog.show()
    }

    override fun likePost(islike: Boolean, postId: String?, position: Int) {
//        showLoading()
        var comments = adapter.getAllList()[position]
        comments.image = adapter.getAllList()[position].image
        comments.likeCounts = comments.likeCounts + 1
//        if (comments.likeCounts > 0) {
//            if(islike){
//                comments.likeCounts = comments.likeCounts + 1
//            }else{
//                comments.likeCounts = comments.likeCounts - 1
//            }
//        }
        comments.loggedInUserLike = islike
        adapter.notifyItemChanged(position)
        var request = CommentsPostRequest()
        request.postId = postId
        request.comment = ""
        request.like = islike
        mFeedViewModel.sendComments(httpManager, request)
    }

    override fun allLikes(postId: String?) {
        var intent = Intent(this, LikeListActivity::class.java)
        intent.putExtra("postId", postId)
        startActivity(intent)
    }

    override fun openComment(postId: String?, position: Int) {
        openDialogComments(postId, position)
    }

    override fun openDetails(data: Post?, position: Int) {
        val intent = FeedDetailsActivity.getInstance(this,data!!.postId!!)
       startActivity(intent)
    }

    private fun toCurrentTIme(): Date? {
        val cal = Calendar.getInstance()
//        cal.add(Calendar.DAY_OF_MONTH, preferencesHelper.getMaxDateRange());
            cal[Calendar.HOUR_OF_DAY] = 23
            cal[Calendar.MINUTE] = 59
            cal[Calendar.SECOND] = 0
        return cal.time
    }
    private fun tillNow(textView: TextView?): DateRange {

        if (textView != null) {
            fromDate =0
            toDate = 0
            textView.text = getString(R.string.till_now)
        }
        return DateRange(0, 0)
    }


    private fun toDayDate(textView: TextView?): DateRange {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0


        if (textView != null) {
            fromDate = cal.timeInMillis
            toDate = toCurrentTIme()!!.time
            textView.text = getString(R.string.today)
        }
        return DateRange(cal.timeInMillis, toCurrentTIme()!!.time)
    }

    class DateRange(var fromDate: Long = 0, var toDate: Long = 0)

    private fun yesterDay(textView: TextView?): DateRange {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -1)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        val calTo = Calendar.getInstance()
        calTo.add(Calendar.DAY_OF_MONTH, -1)
        calTo[Calendar.HOUR_OF_DAY] = 23
        calTo[Calendar.MINUTE] = 59
        calTo[Calendar.SECOND] = 0
        if (textView != null) {
            fromDate = cal.timeInMillis
            toDate = calTo.timeInMillis
            textView.text = getString(R.string.yesterday)

        }
        return DateRange(cal.timeInMillis, calTo.timeInMillis)


    }

    private fun lastWeek(textView: TextView?): DateRange {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -7)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0

        if (textView != null) {
            fromDate = cal.timeInMillis
            toDate = toCurrentTIme()!!.time
            textView.text = getString(R.string.last_week)
        }
        return DateRange(cal.timeInMillis, toCurrentTIme()!!.time)
    }

    private fun lastMonth(textView: TextView?): DateRange {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        if (textView != null) {
            fromDate = cal.timeInMillis
            toDate = toCurrentTIme()!!.time
            textView.text = getString(R.string.last_month)
        }
        return DateRange(cal.timeInMillis, toCurrentTIme()!!.time)
    }

    private fun openDatePicker(tvFromDate: TextView) {
        val c: Calendar = Calendar.getInstance()
        //c.add(Calendar.DAY_OF_MONTH, preferencesHelper.getMaxDateRange()==0?15:preferencesHelper.getMaxDateRange());
        if (fromDate != 0L) {
            c.timeInMillis = fromDate
        }
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        var minTime: Long = 0L
        val calMinTIme: Calendar = Calendar.getInstance()
        if (preferencesHelper.maxPastDaysAllowed != 0) calMinTIme.add(
            Calendar.DAY_OF_MONTH,
            -1 * preferencesHelper.maxPastDaysAllowed
        )
        calMinTIme.set(Calendar.HOUR_OF_DAY, 0)
        calMinTIme.set(Calendar.MINUTE, 0)
        calMinTIme.set(Calendar.SECOND, 0)
        minTime = calMinTIme.timeInMillis
        CommonUtils.openDatePicker(
            this, mYear, mMonth,
            mDay, minTime, 0
        ) { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            // fromDate=0;
            // tvFromDate.setText(DateTimeUtil.getParsedDate(calendar.getTimeInMillis()));
            // toDate = 0;
            CommonUtils.openDatePicker(this,
                mYear,
                mMonth,
                mDay,
                calendar.timeInMillis,
                0,
                OnDateSetListener { view_: DatePicker?, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int ->
                    fromDate = calendar.timeInMillis
                    val calEnd: Calendar = Calendar.getInstance()
                    calEnd.set(Calendar.YEAR, yearEnd)
                    calEnd.set(Calendar.MONTH, monthOfYearEnd)
                    calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd)
                    calEnd.set(Calendar.HOUR_OF_DAY, 23)
                    calEnd.set(Calendar.MINUTE, 59)
                    calEnd.set(Calendar.SECOND, 0)
                    toDate = calEnd.timeInMillis
                    tvFromDate.setText(
                        getParsedDate(fromDate) + " - " + getParsedDate(
                            toDate
                        )
                    )
                    currentPage = PaginationListener.PAGE_START
                    getAllPost()

                })
        }

    }

    private fun showTimeListDialog(timeList: ArrayList<TimeInfo>) {
        bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.layout_select_feed_time_popup)
        val ivCancel = bottomSheetDialog.findViewById<ImageView>(R.id.ivCancel)
        val rvTime = bottomSheetDialog.findViewById<RecyclerView>(R.id.rvTime)
        var adapter = SortTimeListAdapter(this)
        adapter.addItems(timeList)
        rvTime!!.adapter = adapter
        ivCancel!!.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    override fun onItemSelected(timeInfo: TimeInfo) {
        bottomSheetDialog.dismiss()
        currentPage = PaginationListener.PAGE_START
        if(timeInfo.title == getString(R.string.till_now)){
            tillNow(binding.tvDate)
        }
        else if(timeInfo.title == getString(R.string.today)){
            toDayDate(binding.tvDate)
        }else if(timeInfo.title == getString(R.string.yesterday)) {
            yesterDay(binding.tvDate)
        }else if(timeInfo.title == getString(R.string.last_week)) {
            lastWeek(binding.tvDate)
        }else if(timeInfo.title == getString(R.string.last_month)) {
            lastMonth(binding.tvDate)
        }else if(timeInfo.title == getString(R.string.custom_range)) {
            openDatePicker(binding.tvDate)
        }
        getAllPost()
    }

}