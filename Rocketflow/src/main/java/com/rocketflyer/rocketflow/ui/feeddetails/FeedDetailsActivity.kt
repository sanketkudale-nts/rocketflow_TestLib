package com.rocketflyer.rocketflow.ui.feeddetails

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.material.snackbar.Snackbar
import com.tracki.R
import com.tracki.BR
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityFeedDetailsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.dynamicform.dynamicfragment.PlayVideoActivity
import com.tracki.ui.feeds.DynamicFormDataFeedAdapter
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import java.lang.Exception
import javax.inject.Inject

class FeedDetailsActivity : BaseActivity<ActivityFeedDetailsBinding, FeedDetailsViewModel>(),
    FeedDetailsNavigator {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var mFeedDetailsViewModel: FeedDetailsViewModel
    private var snackBar: Snackbar? = null
    lateinit var binding: ActivityFeedDetailsBinding
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            binding.llMain,
            getString(R.string.please_check_your_internet_connection)
        )
    }


    companion object {
        fun getInstance(context: Context, feedId: String): Intent {
            var detailsIntent = Intent(context, FeedDetailsActivity::class.java)
            detailsIntent.putExtra("feedId", feedId)
            return detailsIntent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mFeedDetailsViewModel.navigator = this
        var titile = "Feed Details"
        setToolbar(binding.toolbar, titile)
        if (intent.hasExtra("feedId")) {
            var feedId = intent.getStringExtra("feedId")
            getFeedDetails(feedId)

        }

    }

    private fun getFeedDetails(feedId: String?) {
        showLoading()
        mFeedDetailsViewModel.getFeedsDetails(httpManager, feedId!!)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_feed_details
    }

    override fun getViewModel(): FeedDetailsViewModel {
        return mFeedDetailsViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@FeedDetailsActivity)) {
            val jsonConverter: JSONConverter<PostDetailsResponse> = JSONConverter()
            var response: PostDetailsResponse = jsonConverter.jsonToObject(
                result.toString(),
                PostDetailsResponse::class.java
            ) as PostDetailsResponse
            var post = response.data
            if (post != null) {
                if (post?.contentType == ContentType.PLAIN_TEXT) {
                    if (post?.mediaType != null) {
                        if (post?.mediaType == MEDIATYPE.IMAGE) {
                            showNoMediaTypeData(post)
                            showImageTypeData(post)
                        } else if (post?.mediaType == MEDIATYPE.VIDEO) {
                            showNoMediaTypeData(post)
                            showVideoTypeData(post)
                        }
                    } else {
                        showNoMediaTypeData(post)
                    }

                } else {
                    showDfDataCode(post!!)
                }

            }
        }
    }

    private fun showNoMediaTypeData(post: Post) {
        binding.data = post

    }

    private fun showVideoTypeData(post: Post) {
        if (post.fileUrl != null && post.fileUrl!!.isNotEmpty()) {
            binding.progressBarVideo.visibility = View.VISIBLE
            binding.rlVideo.visibility = View.VISIBLE
            binding.llLikes.visibility = View.VISIBLE
            GlideApp.with(binding.ivVideo).load(post.fileUrl)
                .error(R.drawable.ic_picture)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBarVideo.visibility = View.GONE
                        binding.ivPlay.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(binding.ivVideo)
            binding.rlVideo.setOnClickListener {
                if (post.fileUrl != null) {
                    var intent = Intent(this, PlayVideoActivity::class.java)
                    intent.putExtra("url", post.fileUrl)
                    startActivity(intent)
                }
            }
        }

    }

    private fun showImageTypeData(post: Post) {
        if (post.fileUrl != null && post.fileUrl!!.isNotEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
            binding.llImage.visibility = View.VISIBLE
            binding.llLikes.visibility = View.VISIBLE
            GlideApp.with(binding.ivImageMain).load(post.fileUrl)
                .error(R.drawable.ic_picture)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.ivImageMain)
        }

    }

    private fun showDfDataCode(post: Post) {
        try {

            binding.llLikes.visibility = View.GONE
            binding.tvDescription.visibility = View.GONE
            val jsonConverter: JSONConverter<DfDataMain> = JSONConverter()
            var response: DfDataMain = jsonConverter.jsonToObject(
                post.message.toString(),
                DfDataMain::class.java
            ) as DfDataMain
            if (response.df_data != null && response.df_data!!.isNotEmpty()) {
                binding.rlDfData.visibility = View.VISIBLE
                var adapter = DynamicFormDataFeedAdapter(response.df_data!!)
                binding.rvDfData.adapter = adapter


            }
            post.message=""
            binding.data = post
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}