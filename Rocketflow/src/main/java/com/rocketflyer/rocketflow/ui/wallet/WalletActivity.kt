package com.rocketflyer.rocketflow.ui.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.TransactionRequest
import com.tracki.data.model.response.config.Transaction
import com.tracki.data.model.response.config.TransactionData
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityWalletBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.ui.tasklisting.PagingData
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import kotlinx.android.synthetic.main.activity_wallet.*
import javax.inject.Inject

class WalletActivity : BaseActivity<ActivityWalletBinding, TransactionViewModel>(), TransactionNavigator, View.OnClickListener {
    @Inject
    lateinit var mTransactionViewModel: TransactionViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: WalletAdapter


    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var txnTypes: ArrayList<String>? = null

    private lateinit var mActivityWalletBinding: ActivityWalletBinding
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityWalletBinding.llMain, getString(R.string.please_check_your_internet_connection))
    }

    companion object {
        private val TAG = WalletActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, WalletActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityWalletBinding = viewDataBinding
        mTransactionViewModel.navigator = this
        rvTransaction.adapter = adapter
        setToolbar(mActivityWalletBinding.toolbar, "My Wallet")
        getBalance()
        llAllData.setOnClickListener(this)
        llDebitData.setOnClickListener(this)
        llCreditData.setOnClickListener(this)
        var layoutManger: LinearLayoutManager = rvTransaction!!.layoutManager!! as LinearLayoutManager
        rvTransaction.addOnScrollListener(object : PaginationListener(layoutManger) {
            override fun loadMoreItems() {
                this@WalletActivity.isLoading = true
                currentPage++
                showLoading()
                var request = TransactionRequest()
                request.txnTypes = txnTypes
                request.userId = preferencesHelper.userDetail.userId
                val pagingData = PagingData()
                pagingData.datalimit = 10
                pagingData.pageOffset = (currentPage - 1) * 10
                pagingData.pageIndex = currentPage
                request.paginationData = pagingData
                mTransactionViewModel.getTransaction(httpManager, request)

            }

            override fun isLastPage(): Boolean {
                return this@WalletActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@WalletActivity.isLoading
            }
        })

    }
    fun getBalance() {
        showLoading()
        mTransactionViewModel.getBalance(httpManager)
    }

    fun getTransactionFirst() {
        showLoading()
        var request = TransactionRequest()
        request.txnTypes = txnTypes
        request.userId = preferencesHelper.userDetail.userId
       // request.userId = "a098513d-a136-4852-b695-a6ddab90f6af"
        val pagingData = PagingData()
        pagingData.datalimit = 10
        pagingData.pageOffset = (currentPage - 1) * 10
        pagingData.pageIndex = currentPage
        request.paginationData = pagingData
        // request.categoryId = "ddddddd5e9-4495-8bc1-dokkkkd3de79aca05"
        mTransactionViewModel.getTransaction(httpManager, request)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_wallet
    }

    override fun getViewModel(): TransactionViewModel {
        return mTransactionViewModel
    }

    override fun handleTransactionResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@WalletActivity)) {
            this@WalletActivity.isLoading = false
            val jsonConverter: JSONConverter<TransactionData> = JSONConverter()
            var response: TransactionData = jsonConverter.jsonToObject(result.toString(), TransactionData::class.java) as TransactionData
//            mActivityWalletBinding.data = response
//            if (response.balance == null)
//                mActivityWalletBinding.tvTotalAmount.text = "0 INR"
            if (response.transactions != null) {
                if (response.paginationData != null) {

                    CommonUtils.showLogMessage("e", "adapter total_count =>",
                            "" + adapter.getItemCount())
                    CommonUtils.showLogMessage("e", "fetch total_count =>",
                            "" + response.paginationData!!.dataCount)
                    if (response.paginationData!!.dataCount == adapter.getAllList().size) {
                        isLastPage = true
                    }
                }
                adapter.addItems(response.transactions as ArrayList<Transaction>)
            }
        } else {
           // mActivityWalletBinding.tvTotalAmount.text = "0 INR"
        }

    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
         hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@WalletActivity)) {
            this@WalletActivity.isLoading = false
            val jsonConverter: JSONConverter<TransactionData> = JSONConverter()
            var response: TransactionData = jsonConverter.jsonToObject(result.toString(), TransactionData::class.java) as TransactionData
            mActivityWalletBinding.data = response
            if (response.balance == null)
                mActivityWalletBinding.tvTotalAmount.text = "₹ 0 "
            getTransactionFirst()
        } else {
            mActivityWalletBinding.tvTotalAmount.text = "₹ 0"
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.llAllData -> {
                tvAllData.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tvAllData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark, 0)
                tvDebitData.setTextColor(ContextCompat.getColor(this, R.color.gray))
                tvDebitData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark_uncheck, 0)
                tvCreditData.setTextColor(ContextCompat.getColor(this, R.color.gray))
                tvCreditData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark_uncheck, 0)
                adapter.clearList()
                txnTypes = null
                getTransactionFirst()
            }
            R.id.llDebitData -> {
                tvAllData.setTextColor(ContextCompat.getColor(this, R.color.gray))
                tvAllData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark_uncheck, 0)
                tvDebitData.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tvDebitData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark, 0)
                tvCreditData.setTextColor(ContextCompat.getColor(this, R.color.gray))
                tvCreditData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark_uncheck, 0)
                adapter.clearList()
                txnTypes= ArrayList()
                txnTypes!!.add("DEBIT")
                getTransactionFirst()
            }
            R.id.llCreditData -> {
                tvAllData.setTextColor(ContextCompat.getColor(this, R.color.gray))
                tvAllData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark_uncheck, 0)
                tvDebitData.setTextColor(ContextCompat.getColor(this, R.color.gray))
                tvDebitData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark_uncheck, 0)
                tvCreditData.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                tvCreditData.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tick_mark, 0)
                adapter.clearList()
                txnTypes= ArrayList()
                txnTypes!!.add("CREDIT")
                getTransactionFirst()
            }
        }
    }
}