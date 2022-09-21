package com.rocketflyer.rocketflow.ui.transactionDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.tracki.BR
import com.tracki.R
import com.tracki.databinding.ActivityTransactionDetailsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.wallet.TransactionViewModel
import javax.inject.Inject

class TransactionDetailsActivity : BaseActivity<ActivityTransactionDetailsBinding, TransactionViewModel>() {

    @Inject
    lateinit var mTransactionNewViewModel: TransactionViewModel

    companion object {
        private val TAG = TransactionDetailsActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, TransactionDetailsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_transaction_details
    }

    override fun getViewModel(): TransactionViewModel {
        return mTransactionNewViewModel
    }
}