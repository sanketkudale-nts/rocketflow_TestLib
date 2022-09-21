package com.rocketflyer.rocketflow.ui.main.filter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckedTextView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.tracki.BR
import com.tracki.R
import com.tracki.databinding.ActivityBuddyFilterBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.AppConstants
import javax.inject.Inject


/**
 * Created by rahul on 11/10/18
 */
open class BuddyFilterActivity : BaseActivity<ActivityBuddyFilterBinding, BuddyFilterViewModel>(),
        BuddyFilterNavigator, View.OnClickListener {

    @Inject
    lateinit var mBuddyFilterViewModel: BuddyFilterViewModel

    private lateinit var mActivityBuddyFilterBinding: ActivityBuddyFilterBinding
    private lateinit var ctvAll: CheckedTextView
    private lateinit var ctvLive: CheckedTextView
    private lateinit var ctvIdle: CheckedTextView
    private lateinit var ctvOffline: CheckedTextView

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_buddy_filter
    }

    override fun getViewModel(): BuddyFilterViewModel {
        return mBuddyFilterViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityBuddyFilterBinding = viewDataBinding
        mBuddyFilterViewModel.navigator = this
        setUp()
    }

    private fun setUp() {
        val view = mActivityBuddyFilterBinding.toolbar
        val ivNavigationIcon = view.findViewById<ImageView>(R.id.ivNavigationIcon)
        ivNavigationIcon.setImageResource(R.drawable.ic_cross)
        ivNavigationIcon.setOnClickListener(this)
        val toolbarTitle = view.findViewById<TextView>(R.id.toolbarTitle)
        toolbarTitle.visibility = View.VISIBLE
        toolbarTitle.text = getString(R.string.filters)
        toolbarTitle.textSize = 20f
        val tvMenuText = view.findViewById<TextView>(R.id.tvMenuText)
        tvMenuText.visibility = View.VISIBLE
        tvMenuText.text = getString(R.string.reset)
        tvMenuText.setTextColor(ContextCompat.getColor(this, R.color.blue))
        tvMenuText.setOnClickListener(this)
        val fLayoutNotification = view.findViewById<FrameLayout>(R.id.fLayoutNotification)
        fLayoutNotification.visibility = View.GONE
        val fLayoutRequest = view.findViewById<FrameLayout>(R.id.fLayoutRequest)
        fLayoutRequest.visibility = View.GONE

        ctvAll = mActivityBuddyFilterBinding.ctvAll
        ctvIdle = mActivityBuddyFilterBinding.ctvIdle
        ctvLive = mActivityBuddyFilterBinding.ctvLive
        ctvOffline = mActivityBuddyFilterBinding.ctvOffline

        ctvAll.setOnClickListener(this)
        ctvIdle.setOnClickListener(this)
        ctvLive.setOnClickListener(this)
        ctvOffline.setOnClickListener(this)

        val btnApplyFilter = mActivityBuddyFilterBinding.btnApplyFilter
        btnApplyFilter.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ctvOffline -> ctvOffline.isChecked = !ctvOffline.isChecked
            R.id.ctvAll -> ctvAll.isChecked = !ctvAll.isChecked
            R.id.ctvLive -> ctvLive.isChecked = !ctvLive.isChecked
            R.id.ctvIdle -> ctvIdle.isChecked = !ctvIdle.isChecked
            R.id.ivNavigationIcon -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            R.id.tvMenuText -> {
                ctvOffline.isChecked = false
                ctvAll.isChecked = false
                ctvIdle.isChecked = false
                ctvLive.isChecked = false
            }
            R.id.btnApplyFilter -> {
                applyFilter()
            }
            else -> {
            }
        }
    }

    override fun applyFilter() {
        val intent = Intent()
        val test = ArrayList<Int>()
        if (ctvAll.isChecked){
            test.add(0)
        }
        if (ctvLive.isChecked){
            test.add(1)
        }
        if (ctvIdle.isChecked){
            test.add(2)
        }
        if (ctvOffline.isChecked){
            test.add(3)
        }

        intent.putIntegerArrayListExtra(AppConstants.Extra.EXTRA_FILTER_BUDDY, test)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, BuddyFilterActivity::class.java)
    }
}