package com.tracki.ui.likeslist

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.GetCommentsOfPosts
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityLikeListBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import kotlinx.android.synthetic.main.activity_employee_list.*
import javax.inject.Inject

class LikeListActivity : BaseActivity<ActivityLikeListBinding, LikeListViewModel>(), LikesListNavigator {
    @Inject
    lateinit var mLikeListViewModel: LikeListViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: LikeListAdapter


    lateinit var binding: ActivityLikeListBinding
    var postId: String? = null
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(binding.rlMain, getString(R.string.please_check_your_internet_connection))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mLikeListViewModel.navigator = this
        binding.adapter = adapter
        setToolbar(toolbar, "People Who React")
        if (intent.hasExtra("postId")) {
            postId = intent.getStringExtra("postId")
            getAllLikes(postId)
        }

    }

    private fun getAllLikes(postId: String?) {
        showLoading()
        var request = GetCommentsOfPosts()
        request.postId = postId
        mLikeListViewModel.getAllLikes(httpManager, request)

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_like_list
    }

    override fun getViewModel(): LikeListViewModel {
        return mLikeListViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@LikeListActivity)) {
            val jsonConverter: JSONConverter<LikeResponse> = JSONConverter()
            var response: LikeResponse = jsonConverter.jsonToObject(result.toString(), LikeResponse::class.java) as LikeResponse
            if (response.likes != null) {
                adapter.addItems(response.likes as ArrayList<Likes>)
            }
        } else {
        }
    }
}