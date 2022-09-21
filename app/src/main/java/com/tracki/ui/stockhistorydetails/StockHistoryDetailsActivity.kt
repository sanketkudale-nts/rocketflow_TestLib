package com.tracki.ui.stockhistorydetails

import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityStockHistoryDetailsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.selectorder.PaginationRequest
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import javax.inject.Inject

class StockHistoryDetailsActivity : BaseActivity<ActivityStockHistoryDetailsBinding,StockHistoryViewModel>(),StockHistoryNavigator {

    @Inject
    lateinit var mStockHistoryViewModel: StockHistoryViewModel

    @Inject
    lateinit var mPref: PreferencesHelper

    private lateinit var binding: ActivityStockHistoryDetailsBinding

    @Inject
    lateinit var httpManager: HttpManager

    private var snackBar: Snackbar? = null

    var pid: String? = null
    var day: Long = 0L

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var mLayoutManager: LinearLayoutManager? = null
    private var rvStocks: RecyclerView? = null
    private var paginationRequest: PaginationRequest? = null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }


    lateinit var stockAdapter: StockDetailsAdapter


    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            binding.coordinatorLayout,
            getString(R.string.please_check_your_internet_connection)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      binding=viewDataBinding
        mStockHistoryViewModel.navigator=this
        setToolbar(binding.toolbar, "Stock History Details")
        if(intent.hasExtra("pid"))
            pid=intent.getStringExtra("pid")
        if(intent.hasExtra("day"))
            day=intent.getLongExtra("day",0L)
        stockAdapter=StockDetailsAdapter(this)
        if (pid != null) {
            currentPage = PaginationListener.PAGE_START
            showLoading()
            if (pid != null)
                getStockHistoryDetails(pid!!)
        }
    }

    private fun getStockHistoryDetails(pid: String) {
        if (currentPage == PaginationListener.PAGE_START) {
            stockAdapter.clearList()
            binding.rvStocks.removeAllViewsInLayout()

        }
        paginationRequest = PaginationRequest()
        paginationRequest!!.limit = 10
        paginationRequest!!.offset = (currentPage - 1) * 10
        paginationRequest!!.dataCount = 10
        mStockHistoryViewModel.getStockHistory(
            httpManager,
            pid,
            paginationRequest!!,
            day
        )
    }



    override fun getBindingVariable(): Int {
       return BR.viewModel
    }

    override fun getLayoutId(): Int {
       return R.layout.activity_stock_history_details
    }

    override fun getViewModel(): StockHistoryViewModel {
        return mStockHistoryViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handStockHistoryDetailsResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        this.isLoading = false
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<StockHistoryDetailsResponse> = JSONConverter()
            var response: StockHistoryDetailsResponse =
                jsonConverter.jsonToObject(result.toString(), StockHistoryDetailsResponse::class.java)

            if (response.data != null && response.data!!.isNotEmpty()) {
                var list = response.data

                setRecyclerView()
                stockAdapter!!.addItems(list!!)
                CommonUtils.showLogMessage(
                    "e", "adapter total_count =>",
                    "" + stockAdapter.itemCount
                )
                CommonUtils.showLogMessage(
                    "e", "fetch total_count =>",
                    "" + response.totalCount
                )
                isLastPage = stockAdapter.itemCount >= response.totalCount
            }

        }
    }

    private fun setRecyclerView() {
        if (rvStocks == null) {
            rvStocks = binding.rvStocks
            //  mLayoutManager= (LinearLayoutManager) rvAttendance.getLayoutManager();
            try {
                mLayoutManager = LinearLayoutManager(this)
                mLayoutManager!!.orientation = RecyclerView.VERTICAL
                rvStocks!!.layoutManager = mLayoutManager
                rvStocks!!.itemAnimator = DefaultItemAnimator()

            } catch (e: IllegalArgumentException) {
            }
            rvStocks!!.adapter = stockAdapter

        }
        rvStocks!!.addOnScrollListener(object : PaginationListener(mLayoutManager!!) {
            override fun loadMoreItems() {
                this@StockHistoryDetailsActivity.isLoading = true
                if (pid != null) {
                    currentPage++
                    getStockHistoryDetails(pid!!)
                }
            }

            override fun isLastPage(): Boolean {
                return this@StockHistoryDetailsActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@StockHistoryDetailsActivity.isLoading
            }
        })


    }

}