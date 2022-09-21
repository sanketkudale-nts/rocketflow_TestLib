package com.rocketflyer.rocketflow.ui.attendance.teamattendance

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.webkit.TracingConfig
import android.widget.DatePicker
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.TeamAttendanceRequest
import com.tracki.data.model.response.config.AttendanceMap
import com.tracki.data.model.response.config.DashBoardBoxItem
import com.tracki.data.model.response.config.LeaveStatus
import com.tracki.data.model.response.config.TeamAttendanceResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.LayoutTeamAttendanceBinding
import com.tracki.ui.attendance.EmployeeListActivity
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.main.filter.TaskFilterActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil.Companion.getParsedDate
import com.tracki.utils.Log
import com.tracki.utils.TrackiToast
import kotlinx.android.synthetic.main.layout_team_attendance.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * Created by Vikas Kesharvani on 27/10/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TeamAttendanceFragment : BaseFragment<LayoutTeamAttendanceBinding, TeamAttendanceViewModel>(), TeamAttendanceNavigator, View.OnClickListener,  OnChartValueSelectedListener{


    private var request: TeamAttendanceRequest? = null

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    lateinit var mTeamAttendanceViewModel: TeamAttendanceViewModel


    lateinit var mLayoutTeamAttendanceBinding: LayoutTeamAttendanceBinding

    private var fromDate: Long = 0
    private var TAG = "TeamAttendanceFragment"
    private var regionId: String? = null
    private var hubIds: List<String>? = null
    private var cityIdList: ArrayList<String>? = null
    private var stateId: String? = null
    private var cityId: String? = null
    private var hubIdStr: String? = null
    protected var tfRegular: Typeface? = null
    protected var tfLight: Typeface? = null
    companion object {
        fun newInstance(): TeamAttendanceFragment? {
            val args = Bundle()
            val fragment = TeamAttendanceFragment()
            fragment.arguments = args
            return fragment
        }
    }






    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_team_attendance
    }

    override fun getViewModel(): TeamAttendanceViewModel {

        mTeamAttendanceViewModel = ViewModelProviders.of(this, mViewModelFactory).get(TeamAttendanceViewModel::class.java)
        return mTeamAttendanceViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLayoutTeamAttendanceBinding = viewDataBinding
        mTeamAttendanceViewModel.navigator = this

        mLayoutTeamAttendanceBinding.rlDate.setOnClickListener(this)
        mLayoutTeamAttendanceBinding.ivReload.setOnClickListener(this)
        tfRegular = Typeface.createFromAsset(requireContext().getAssets(), "fonts/campton_book.ttf")
        tfLight = Typeface.createFromAsset(requireContext().getAssets(), "fonts/campton_book.ttf")
        val cal = Calendar.getInstance()
        fromDate = cal.time.time
        mLayoutTeamAttendanceBinding.tvDate.setText(getParsedDate(fromDate))
        getAttendanceData()
        mLayoutTeamAttendanceBinding.ivFilter.setOnClickListener {
            val intent = TaskFilterActivity.newIntent(baseActivity)
            intent.putExtra("from", AppConstants.ATTENDANCE)
            startActivityForResult(intent, AppConstants.REQUEST_CODE_FILTER_USER)
        }
        if (preferencesHelper.userGeoFilters()) {
            mLayoutTeamAttendanceBinding.ivFilter.visibility = View.VISIBLE
        } else {
            mLayoutTeamAttendanceBinding.ivFilter.visibility = View.GONE
        }


    }



    private fun getAttendanceData() {

        //hit api
        if (baseActivity != null && baseActivity.isNetworkConnected) {
            showLoading()
            request = TeamAttendanceRequest(fromDate)
            mTeamAttendanceViewModel.getTeamAttendance(httpManager, request!!)
        } else {
//            if (baseActivity != null)
//                showShort(baseActivity, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
        }

    }

    private fun filterData(regionId: String?, stateId: String?, citIdList: List<String>?, hubId: List<String>?) {

        request = TeamAttendanceRequest(fromDate)
        request!!.geoFilter = preferencesHelper.userGeoFilters()
        request!!.cityId = citIdList
        request!!.regionId = regionId
        request!!.stateId = stateId
        request!!.hubId = hubId
        mTeamAttendanceViewModel.getTeamAttendance(httpManager, request!!)
    }

    override fun handleTeamAttendanceResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()
        if (result != null) {
            if (CommonUtils.handleResponse(callback, error, result, baseActivity)) {
                val response: TeamAttendanceResponse = Gson().fromJson(result.toString(), TeamAttendanceResponse::class.java)
                if (response.attendanceStatusMap != null) {
                    var attendanceList = getListofAttandence(response.attendanceStatusMap!!)
                    if(attendanceList!=null)
                    setData(attendanceList)
                }
            }
        }

    }

    private fun getListofAttandence(attendanceMap: AttendanceMap): List<DashBoardBoxItem>? {
        val attandanceList: MutableList<DashBoardBoxItem> = ArrayList()
        val da0 = DashBoardBoxItem()
        da0.stageName = "Active Employees"
        da0.stageConst = LeaveStatus.ALL
        da0.count = attendanceMap.ALL
        attandanceList.add(da0)
        mLayoutTeamAttendanceBinding.tvLabelTotalEmployee.text="Active Employees"
        mLayoutTeamAttendanceBinding.tvTotalEmployee.text=attendanceMap.ALL.toString()
        val da1 = DashBoardBoxItem()
        da1.stageName = "Presents"
        da1.stageConst = LeaveStatus.PRESENT
        da1.count = attendanceMap.PRESENT
        attandanceList.add(da1)
        mLayoutTeamAttendanceBinding.tvLabelPresent.text="Presents"
        mLayoutTeamAttendanceBinding.tvPresent.text=attendanceMap.PRESENT.toString()
        if(attendanceMap.PRESENT>0){
            mLayoutTeamAttendanceBinding.ivNextPresent.visibility=View.VISIBLE
        }else{
            mLayoutTeamAttendanceBinding.ivNextPresent.visibility=View.GONE
        }
        mLayoutTeamAttendanceBinding.llPresent.setOnClickListener {
            if(attendanceMap.PRESENT>0) {
                var intent = Intent(context, EmployeeListActivity::class.java)
                intent.putExtra("status", LeaveStatus.PRESENT.name)
                intent.putExtra("stageName", "Presents")
                intent.putExtra("request", request)
                startActivity(intent)
            }
        }
        val da2 = DashBoardBoxItem()
        da2.stageName = "Absents"
        da2.stageConst = LeaveStatus.ABSENT
        da2.count = attendanceMap.ABSENT
        attandanceList.add(da2)
        mLayoutTeamAttendanceBinding.tvLabelAbsent.text="Absents"
        mLayoutTeamAttendanceBinding.tvAbsent.text=attendanceMap.ABSENT.toString()
        if(attendanceMap.ABSENT>0){
            mLayoutTeamAttendanceBinding.ivNextAbsent.visibility=View.VISIBLE
        }else{
            mLayoutTeamAttendanceBinding.ivNextAbsent.visibility=View.GONE
        }
        mLayoutTeamAttendanceBinding.llAbsent.setOnClickListener {
            if(attendanceMap.ABSENT>0) {
                var intent = Intent(context, EmployeeListActivity::class.java)
                intent.putExtra("status", LeaveStatus.ABSENT.name)
                intent.putExtra("stageName", "Absents")
                intent.putExtra("request", request)
                startActivity(intent)
            }
        }
        val da6 = DashBoardBoxItem()
        da6.stageName = "On Leaves"
        da6.stageConst = LeaveStatus.ON_LEAVE
        da6.count = attendanceMap.ON_LEAVE
        attandanceList.add(da6)
        mLayoutTeamAttendanceBinding.tvLabelOnleave.text="On Leaves"
        mLayoutTeamAttendanceBinding.tvOnleave.text=attendanceMap.ON_LEAVE.toString()
        if(attendanceMap.ON_LEAVE>0){
            mLayoutTeamAttendanceBinding.ivNextOnLeve.visibility=View.VISIBLE
        }else{
            mLayoutTeamAttendanceBinding.ivNextOnLeve.visibility=View.GONE
        }
        mLayoutTeamAttendanceBinding.llOnleave.setOnClickListener {
            if(attendanceMap.ON_LEAVE>0) {
                var intent = Intent(context, EmployeeListActivity::class.java)
                intent.putExtra("status", LeaveStatus.ON_LEAVE.name)
                intent.putExtra("stageName", "On Leaves")
                intent.putExtra("request", request)
                startActivity(intent)
            }
        }
        val da3 = DashBoardBoxItem()
        da3.stageName = "Not Punched In"
        da3.stageConst = LeaveStatus.NOT_UPDATED
        da3.count = attendanceMap.NOT_UPDATED
        if(attendanceMap.NOT_UPDATED>0){
            mLayoutTeamAttendanceBinding.ivNextNotPunchedIn.visibility=View.VISIBLE
        }else{
            mLayoutTeamAttendanceBinding.ivNextNotPunchedIn.visibility=View.GONE
        }
        mLayoutTeamAttendanceBinding.tvLabelNotPunchedIn.text="Not Punched In"
        mLayoutTeamAttendanceBinding.tvNotPunchedIn.text=attendanceMap.NOT_UPDATED.toString()
        attandanceList.add(da3)
        mLayoutTeamAttendanceBinding.llNotPunchedIn.setOnClickListener {
            if(attendanceMap.NOT_UPDATED>0) {
                var intent = Intent(context, EmployeeListActivity::class.java)
                intent.putExtra("status", LeaveStatus.NOT_UPDATED.name)
                intent.putExtra("stageName", "Not Punched In")
                intent.putExtra("request", request)
                startActivity(intent)
            }
        }
//        val da4 = DashBoardBoxItem()
//        da4.stageName = "Day Off"
//        da4.stageConst=LeaveStatus.DAY_OFF
//        da4.count = attendanceMap.DAY_OFF
//        attandanceList.add(da4)
//        val da5 = DashBoardBoxItem()
//        da5.stageName = "Holiday"
//        da5.stageConst=LeaveStatus.HOLIDAY
//        da5.count = attendanceMap.HOLIDAY
//        attandanceList.add(da5)
        return attandanceList
    }


    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }
    fun clearData(){
        var list=getListofAttandence(AttendanceMap())
        if(list!=null) {
            cardViewChart.visibility=View.GONE
           // setData(list)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rlDate -> {
                openDatePicker(mLayoutTeamAttendanceBinding.tvDate)
            }
            R.id.ivReload->{
                getAttendanceData()
            }
        }
    }

    private fun openDatePicker(textView: TextView) {
        val c = Calendar.getInstance()
        c.timeInMillis = fromDate
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        val calMax = Calendar.getInstance()
        calMax[Calendar.HOUR_OF_DAY] = 23
        calMax[Calendar.MINUTE] = 0
        calMax[Calendar.SECOND] = 0


        var minTime = 0L
        var maxTime = calMax.timeInMillis
        if (baseActivity != null) {
            CommonUtils.openDatePicker(baseActivity, mYear, mMonth,
                    mDay, minTime, maxTime) { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val calendar = Calendar.getInstance()
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = monthOfYear
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                fromDate = calendar.timeInMillis
                textView.text = getParsedDate(calendar.timeInMillis)
                clearData()
                getAttendanceData()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_FILTER_USER) {
            if (resultCode == BaseActivity.RESULT_OK) {
                clearData()
                regionId = data!!.getStringExtra("regionId");
                hubIdStr = data!!.getStringExtra("hubId")
                hubIds = null
                if (hubIdStr != null && !hubIdStr!!.isEmpty()) {
                    hubIds = hubIdStr!!.split("\\s*,\\s*").toList()
                }
                stateId = data.getStringExtra("stateId")
                cityId = data.getStringExtra("cityId")
                if (cityId != null) {
                    cityIdList = ArrayList<String>()
                    cityIdList!!.add(cityId!!)
                }
                filterData(regionId, stateId, cityIdList, hubIds)


            } else if (resultCode == BaseActivity.RESULT_CANCELED) {
                regionId = null
                hubIds = null
                hubIdStr = null
                cityIdList = null
                stateId = null
                cityId = null
               clearData()
                getAttendanceData()

            }
        }

    }


    private fun generateCenterSpannableText(): String? {
//        val s = SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda")
//        s.setSpan(RelativeSizeSpan(1.7f), 0, 14, 0)
//        s.setSpan(StyleSpan(Typeface.NORMAL), 14, s.length - 15, 0)
//        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 15, 0)
//        s.setSpan(RelativeSizeSpan(.8f), 14, s.length - 15, 0)
//        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 14, s.length, 0)
//        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 14, s.length, 0)
        return "Overall 100 %"
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

    override fun onNothingSelected() {
    }

    private fun setData(dataList: List<DashBoardBoxItem>) {
        cardViewChart.visibility=View.VISIBLE
        chart.setUsePercentValues(true)
        chart.getDescription().setEnabled(false)
//        chart.legend.orientation= Legend.LegendOrientation.HORIZONTAL
//        chart.legend.direction= Legend.LegendDirection.LEFT_TO_RIGHT
        //chart.legend.isEnabled=false
//        chart.setExtraOffsets(5f, 10f, 5f, 5f)
//
//        chart.setDragDecelerationFrictionCoef(0.95f)

        chart.setCenterTextTypeface(tfLight)
        if(dataList.isNotEmpty()) {
            chart.centerText = generateCenterSpannableText()
        }else{
            chart.centerText = ""
        }

        chart.setDrawHoleEnabled(true)
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        //chart.setTransparentCircleAlpha(110)

        chart.setHoleRadius(63f)
        chart.setTransparentCircleRadius(51f)

        chart.setDrawCenterText(true)

       // chart.setRotationAngle(0f)
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false)
        chart.setHighlightPerTapEnabled(true)

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this)



        chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        // chart.spin(2000, 0, 360);
        val l: Legend = chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling

        // entry label styling
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(12f)
        val entries = java.util.ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        var total=dataList[0].count
        for (data in dataList) {

            if(data.stageConst!=LeaveStatus.ALL) {
                if (data.count != 0) {
                    try {
                        var percentage = (data.count * 100 / total.toFloat())
                        entries.add(PieEntry(percentage.toFloat(),
                                data.stageName))
                    }catch (e: ArithmeticException){

                    }

                }
            }
        }
        var label=""
//        if(entries.size<4)
//            label="\\s \\s \\s \\s \\s \\s"
        val dataSet = PieDataSet(entries, label)
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        // add a lot of colors
        val colors = java.util.ArrayList<Int>()
        for (data in dataList) {
            if(data.stageConst!=LeaveStatus.ALL) {
                if (data.count != 0) {
                    var c = CommonUtils.getAttendanceStatusColor(data.stageConst!!)
                    colors.add(c)
                }
            }
        }

        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);


        //dataSet.setSelectionShift(0f);
//        dataSet.valueLinePart1OffsetPercentage = 80f
//        dataSet.valueLinePart1Length = 0.2f
//        dataSet.valueLinePart2Length = 0.4f
//
//        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        data.setValueTypeface(tfLight)
        chart.data = data
        chart.setDrawEntryLabels(false)

        // undo all highlights
        chart.highlightValues(null)
        chart.invalidate()
    }
}