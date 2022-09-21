package com.rocketflyer.rocketflow.ui.myInventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.tracki.BR
import com.tracki.R
import com.tracki.databinding.ActivityMyInventoryBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.myDocument.DocumentsAdapter
import com.tracki.ui.myDocument.MyDocumentViewModel
import kotlinx.android.synthetic.main.activity_my_inventory.*
import javax.inject.Inject

class MyInventoryActivity : BaseActivity<ActivityMyInventoryBinding,MyInventoryViewModel>() {

    @Inject
    lateinit var mInventoryNewViewModel: MyInventoryViewModel

    @Inject
    lateinit var adapter: MyInventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rvInventory.adapter = adapter

        iv_back_arrow.setOnClickListener{
            onBackPressed()
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_inventory
    }

    override fun getViewModel(): MyInventoryViewModel {
        return mInventoryNewViewModel
    }
}