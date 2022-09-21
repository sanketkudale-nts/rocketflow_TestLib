package com.rocketflyer.rocketflow.ui.feeds

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tracki.data.model.response.config.DataType
import com.tracki.data.model.response.config.FeedDfDataMap
import com.tracki.databinding.*
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.chat.PlayVideoVerticallyActivity
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.ui.payouts.AdminUserPayoutsActivity

class DynamicFormDataFeedAdapter(private var mList: ArrayList<FeedDfDataMap>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var context: Context? = null

    companion object {
        private const val VIEW_EMPTY = 0
        private const val VIEW_DISPLAY_TEXT = 1
        private const val VIEW_DISPLAY_SINGLE_PIC = 3
        private const val VIEW_PLAY_VIDEO = 4
        private const val TAG = "DynamicFormDataFeedAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_DISPLAY_TEXT -> {
                val simpleViewItemBinding: ItemRowFeedDfTextBinding =
                    ItemRowFeedDfTextBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                TextTypeViewHolder(simpleViewItemBinding)
            }
            VIEW_DISPLAY_SINGLE_PIC -> {
                val simpleViewItemBinding: ItemRowFeedDfImageBinding =
                    ItemRowFeedDfImageBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                ImageTypeViewHolder(simpleViewItemBinding)
            }
            VIEW_PLAY_VIDEO -> {
                val simpleViewItemBinding: ItemRowFeedDfVideoBinding =
                    ItemRowFeedDfVideoBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                VideoTypeViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyEarningEmptyBinding =
                    ItemMyEarningEmptyBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.isNotEmpty()) {
            if (mList[position].type != null) {
                when (mList[position].type) {
                    DataType.IP_ADDRESS, DataType.CALCULATE, DataType.NUMBER, DataType.RATING, DataType.DATE_RANGE, DataType.TEXT,
                    DataType.DATE, DataType.DATE_TIME, DataType.DAY, DataType.TIME, DataType.EMAIL,
                    DataType.TEXT_AREA, DataType.GEO, DataType.MULTI_SELECT, DataType.TOGGLE, DataType.BUTTON,
                    DataType.DROPDOWN, DataType.CONDITIONAL_DROPDOWN_STATIC, DataType.CONDITIONAL_DROPDOWN_API, DataType.SELECT_EXECUTIVE,
                    DataType.SELECT_EXECUTIVE_BY_PLACE, DataType.SELECT_NEAR_BY_EXECUTIVE,
                    DataType.SELECT_GROUP, DataType.ASSIGN_SUBORDINATE,
                    DataType.RADIO, DataType.CHECKBOX, DataType.LABLE, DataType.LABEL, DataType.DROPDOWN_API, DataType.ASSIGN_SUBORDINATE, DataType.PASSWORD, DataType.USER_NAME, DataType.PORT, DataType.RTSP
                    -> VIEW_DISPLAY_TEXT
                    DataType.FILE, DataType.CAMERA -> VIEW_DISPLAY_SINGLE_PIC
                    DataType.IMAGE, DataType.SIGNATURE -> VIEW_DISPLAY_SINGLE_PIC
                    DataType.VIDEO -> VIEW_PLAY_VIDEO
                    else -> VIEW_EMPTY
                }
            } else {
                VIEW_EMPTY
            }
        } else {
            VIEW_EMPTY
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun addItems(list: ArrayList<FeedDfDataMap>) {
        mList = list
    }


    inner class TextTypeViewHolder(private val mBinding: ItemRowFeedDfTextBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            mBinding.executePendingBindings()
        }


    }

    inner class VideoTypeViewHolder(private val mBinding: ItemRowFeedDfVideoBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            mBinding.data = lists
            mBinding.ivPlay.setOnClickListener(View.OnClickListener {
                if (lists.value != null&&lists.value!!.isNotEmpty()) {
                    val intent = Intent(context, PlayVideoVerticallyActivity::class.java)
                    intent.putExtra("url", lists.value)
                    context!!.startActivity(intent)
                }
            })
            mBinding.executePendingBindings()
        }


    }

    inner class ImageTypeViewHolder(private val mBinding: ItemRowFeedDfImageBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val lists = mList!![position]
            if (lists.value!!.contains(",")) {
                val lstValues: List<String> = lists.value!!.split(",").map { it -> it.trim() }

                if (lstValues.isNotEmpty())
                    lists.value = lstValues[0]

            }
            mBinding.data = lists
            mBinding.executePendingBindings()
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptyBinding) :
        BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            if (context is AdminUserPayoutsActivity) {
                mBinding.tvMessage.text = "No Orders"
            }

            mBinding.executePendingBindings()
        }
    }
}