package com.rocketflyer.rocketflow.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import com.tracki.R
import com.tracki.data.model.response.config.LeaveStatus
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil.Companion.getParsedDate
import com.tracki.utils.TrackiToast
import java.util.*

/**
 * Created by rahul on 5/12/18
 */
class FilterDialog(var ctx: Context, val listener: OnClickSearchListener, val statusList : List<String>, val title:String)
    : Dialog(ctx, R.style.DialogTheme) {
    var toInMillis = 0L
    var fromInMillis = 0L
    var spnStatus: Spinner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_search_att_leave)
        val btnDateFrom = findViewById<Button>(R.id.btnDateFrom)
        val btnDateTo = findViewById<Button>(R.id.btnDateTo)
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val btnCancel = findViewById<ImageView>(R.id.btnCancel)
        spnStatus = findViewById<Spinner>(R.id.spnStatus)


        tvTitle.setText(title)

        val spnAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item,
                statusList)
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnStatus!!.adapter = spnAdapter



        btnSearch.setOnClickListener {
            if (toInMillis < fromInMillis) {
                TrackiToast.Message.showLong(context, "End date cannot be less than start date")
            } else {
                listener.onClickSearch(fromInMillis, toInMillis,
                        CommonUtils.getLeaveStatusConstant(spnStatus!!.selectedItem.toString())
                )
            }
        }

        btnCancel.setOnClickListener {
            listener.onClickCancel()
        }

        val c = Calendar.getInstance()
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        var mYear = c[Calendar.YEAR]
        var mMonth = c[Calendar.MONTH]
        var mDay = c[Calendar.DAY_OF_MONTH]
        toInMillis = c.timeInMillis
        btnDateTo.text = getParsedDate(toInMillis)

        val cCur = Calendar.getInstance()

        val fromCal = c.clone() as Calendar
        fromCal.add(Calendar.DATE, -30)
        var yr = fromCal[Calendar.YEAR]
        var mnth = fromCal[Calendar.MONTH]
        var dayofmnth = fromCal[Calendar.DAY_OF_MONTH]

        fromInMillis = fromCal.timeInMillis
        btnDateFrom.text = getParsedDate(fromInMillis)

        btnDateFrom.setOnClickListener { v: View? ->
            CommonUtils.openDatePicker(context, yr, mnth,
                    dayofmnth, 0, 0) { vw: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                //                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                fromCal[Calendar.YEAR] = year
                fromCal[Calendar.MONTH] = monthOfYear
                fromCal[Calendar.DAY_OF_MONTH] = dayOfMonth
                fromCal[Calendar.HOUR_OF_DAY] = 0
                fromCal[Calendar.MINUTE] = 0
                fromCal[Calendar.SECOND] = 0

                    fromInMillis = fromCal.timeInMillis
                    btnDateFrom.text = getParsedDate(fromInMillis)
                yr = fromCal[Calendar.YEAR]
                mnth = fromCal[Calendar.MONTH]
                dayofmnth = fromCal[Calendar.DAY_OF_MONTH]

            }
        }
        btnDateTo.setOnClickListener { v: View? ->
            CommonUtils.openDatePicker(context, mYear, mMonth,
                    mDay, fromInMillis, 0) { vw2: DatePicker?, year2: Int, monthOfYear2: Int, dayOfMonth2: Int ->
                //                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                c[Calendar.YEAR] = year2
                c[Calendar.MONTH] = monthOfYear2
                c[Calendar.DAY_OF_MONTH] = dayOfMonth2
                c[Calendar.HOUR_OF_DAY] = 0
                c[Calendar.MINUTE] = 0
                c[Calendar.SECOND] = 0
                    toInMillis = c.timeInMillis
                    btnDateTo.text = getParsedDate(toInMillis)
                mYear = c[Calendar.YEAR]
                mMonth = c[Calendar.MONTH]
                mDay = c[Calendar.DAY_OF_MONTH]

                cCur[Calendar.YEAR] = year2
                cCur[Calendar.MONTH] = monthOfYear2
                cCur[Calendar.DAY_OF_MONTH] = dayOfMonth2
            }
        }

    }
}

    interface OnClickSearchListener {
        fun onClickSearch(from: Long, to: Long, status: LeaveStatus)
        fun onClickCancel()
    }