package com.rocketflyer.rocketflow.ui.userlisting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.tracki.R
import com.tracki.data.model.response.config.EmpData
import com.tracki.data.model.response.config.LeaveStatus
import com.tracki.data.model.response.config.UserData
import com.tracki.databinding.ItemEmployeeListBinding
import com.tracki.databinding.ItemMyEarningEmptyBinding
import com.tracki.databinding.ItemUserListBinding
import com.tracki.ui.addcustomer.AddCustomerActivity
import com.tracki.ui.attendance.EmployeeListActivity
import com.tracki.ui.base.BaseViewHolder
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.earnings.MyEarningsEmptyItemViewModel
import com.tracki.utils.CommonUtils

class UserListAdapter : RecyclerView.Adapter<BaseViewHolder> {

    private var isHideRoll: Boolean=false
    private var context: Context? = null

    var mList: ArrayList<UserData>? = null
    private var copyList: ArrayList<UserData>? = null

    constructor(mList: ArrayList<UserData>?) : super() {
        this.mList = mList
        // copyList = mList

    }
    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_USERS = 1
    }
    private var listener:OnUserSelected?=null

    fun setListener(context: Context){
        listener=context as OnUserSelected
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_USERS -> {
                val simpleViewItemBinding: ItemUserListBinding =
                        ItemUserListBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                EmployeeListViewHolder(simpleViewItemBinding)
            }
            else -> {
                val emptyViewBinding: ItemMyEarningEmptyBinding =
                        ItemMyEarningEmptyBinding.inflate(
                                LayoutInflater.from(parent.context), parent, false)
                EmptyViewHolder(emptyViewBinding)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList != null && mList!!.isNotEmpty()) {
            mList!!.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int) = if (mList != null && mList!!.isNotEmpty()) {
        VIEW_TYPE_USERS
    } else {
        VIEW_TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.onBind(position)

    fun rollStatus(isHideRoll:Boolean){
        this.isHideRoll=isHideRoll
    }

    fun addItems(list: ArrayList<UserData>) {
        mList!!.addAll(list)
        if (copyList == null) {
            copyList = ArrayList()
        } else {
            copyList!!.clear()
        }
        if (mList != null)
            copyList!!.addAll(mList!!)
//        val set: MutableSet<EmpData> = HashSet()
//        set.addAll(mList!!)
//        mList!!.clear()
//        mList!!.addAll(set)
        notifyDataSetChanged()
    }

    fun getAllList(): ArrayList<UserData> {
        return mList!!
    }

    fun clearList() {
        if (mList!!.isNotEmpty()) {
            mList!!.clear()
            notifyDataSetChanged()
        }

    }
    fun addFilter(newText: String) {
        mList?.clear()
        if (newText.isEmpty()) {
            mList?.addAll(copyList!!)
        } else {
            for (name in copyList!!) {
                if (name.firstName != null && name.firstName!!.toLowerCase().contains(newText.toLowerCase())
                        ||name.mobile != null && name.mobile!!.contains(newText.toLowerCase())) {
                    mList?.add(name)
                }
            }
        }
        notifyDataSetChanged()
    }
    fun populateList() {
        if(mList!=null)
            mList!!.clear()
        mList?.addAll(copyList!!)
        notifyDataSetChanged()
    }




    inner class EmployeeListViewHolder(private val mBinding: ItemUserListBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val data = mList!![position]
            var name=""
            if(data.firstName!=null&&data.middleName!=null&&data.lastName!=null){
                name =data.firstName+" "+data.middleName+" "+data.lastName
            }else  if(data.firstName!=null&&data.lastName!=null){
                name =data.firstName+" "+data.lastName
            }else  if(data.firstName!=null&&data.middleName!=null){
                name =data.firstName+" "+data.middleName
            }else if(data.firstName!=null){
                name=data.firstName!!
            }
            mBinding.tvName.text=name
            mBinding.data = data
            if(isHideRoll){
                mBinding.tvRollName.visibility=View.GONE
            }
            if(data.email!=null&&data.email!!.isNotEmpty()){
                mBinding.tvEmail.setText(data.email)
                mBinding.tvEmail.visibility=View.VISIBLE
            }else{
                mBinding.tvEmail.visibility=View.GONE
            }
            if(data.mobile!=null&&data.mobile!!.isNotEmpty()){
                mBinding.tvMobile.setText(data.mobile)
                mBinding.tvMobile.visibility=View.VISIBLE
            }
            else{
                mBinding.tvMobile.visibility=View.GONE
            }


            if(data.profileImg!=null && data.profileImg!!.isNotEmpty()) {
                GlideApp.with(context!!)
                        .load(data.profileImg)
                        .apply(RequestOptions().circleCrop()
                                .placeholder(R.drawable.ic_my_profile))
                        .error(R.drawable.ic_my_profile)
                        .into(mBinding.ivUser)
                mBinding.ivUser.setOnClickListener {
                    openDialogShowImage(data.profileImg!!)
                }
            }else{
                mBinding.ivUser.setImageResource(R.drawable.ic_my_profile)
            }
//            if(data.userId!=null){
//                mBinding.ivMessage.visibility=View.VISIBLE
//            }
            if(data.mobile!=null&&data.mobile!!.isNotEmpty()){
                mBinding.ivCall.visibility=View.VISIBLE
            }else{
                mBinding.ivCall.visibility=View.GONE
            }
            mBinding.ivNext.setOnClickListener {
                if(listener!=null)
                    listener!!.showDetails(data)
            }
            mBinding.cardMain.setOnClickListener {
                if(listener!=null)
                    listener!!.showDetails(data)
            }
            mBinding.ivMessage.setOnClickListener {
                if(listener!=null&&data.userId!=null)
                    listener!!.onChatStart(data.userId,name)
            }



            mBinding.llOptions.setOnClickListener {
                if (data.mobile != null && data.mobile!!.isNotEmpty())
                    CommonUtils.openDialer(context, data.mobile)
            }
            mBinding.executePendingBindings()
        }


    }

    inner class EmptyViewHolder(private val mBinding: ItemMyEarningEmptyBinding) :
            BaseViewHolder(mBinding.root) {

        override fun onBind(position: Int) {
            val emptyViewModel = MyEarningsEmptyItemViewModel()
            mBinding.viewModel = emptyViewModel
            mBinding.imageViewEmpty.setImageResource(R.drawable.ic_empty_buddy)
            if (context is EmployeeListActivity) {
                mBinding.tvMessage.text = "No Customer"
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings()
        }
    }


    interface OnUserSelected{
        fun onChatStart(buddyId: String?, buddyName: String?)
        fun onDeleteUser(data:UserData)
        fun showDetails(data:UserData)
    }
    private fun openDialogShowImage(url: String) {

        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_show_image_big)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        val imageView = dialog.findViewById<View>(R.id.ivImages) as ImageView

        Glide.with(context!!)
                .asBitmap()
                .load(url)
                .error(R.drawable.ic_picture)
                .placeholder(R.drawable.ic_picture)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                    }

                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?) {
                        imageView.setImageBitmap(resource)
                    }
                })
        /*  Glide.with(context!!)
                  .load(url)
                  .error(R.drawable.ic_picture)
                  .placeholder(R.drawable.ic_picture)
                  .into(imageView)*/
        dialog.window!!.attributes = lp
        // imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }
}