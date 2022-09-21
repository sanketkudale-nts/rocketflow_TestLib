package com.rocketflyer.rocketflow.ui.userdetails

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.gms.maps.model.LatLng
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.AddEmployeeRequest
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityUserDetailsBinding
import com.tracki.ui.addcustomer.AddCustomerActivity
import com.tracki.ui.addcustomer.AddUserViewModel
import com.tracki.ui.addcustomer.CustomerInfoScreenFragment
import com.tracki.ui.attendance.attendance_tab.AttendanceFragment

import com.tracki.ui.base.BaseActivity
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.ui.custom.CircleTransform
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.leave.leave_history.LeaveHistoryFragment
import com.tracki.ui.newdynamicform.NewDynamicFormFragment
import com.tracki.ui.userdetails.basicinfo.UserBasicInfoFragment
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import com.tracki.utils.Log
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject


class UserDetailsActivity : BaseActivity<ActivityUserDetailsBinding, UserDetailsViewModel>(), UserDetailsNavigator, View.OnClickListener, UserOptionsSelectedAdapter.UserOptionsSelectedListener, HasSupportFragmentInjector, CustomerInfoScreenFragment.OnDataSubmitListener {
    private var userData: UserData? = null
    private var userId: String? = null
    private var roleId: String? = null
    private var property: String? = null
    private var from: String? = null
    private var isUpdate: Boolean = false

    lateinit var etUserDetails: LinearLayout

    private lateinit var addEmployeeRequest: AddEmployeeRequest
    lateinit var mAddUserViewModel: AddUserViewModel

    @Inject
    lateinit var mUserDetailsViewModel: UserDetailsViewModel

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var adapter: UserOptionsSelectedAdapter

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var CONST_EDIT = 3456


    lateinit var binding: ActivityUserDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mUserDetailsViewModel.navigator = this
        adapter.setListener(this)

        binding.userOptionAdapter = adapter
        etUserDetails = binding.etUserDetails
        binding.ivThreeDot.setOnClickListener(this)
        // binding.ivDelete.setOnClickListener(this)
//        binding.rvAttendance.setOnClickListener(this)
//        binding.rvLeave.setOnClickListener(this)
//        binding.rvUserTask.setOnClickListener(this)
//        binding.rvDocuments.setOnClickListener(this)
//        binding.rvHierarchy.setOnClickListener(this)
//        binding.rvPayroll.setOnClickListener(this)
//        binding.rvAudit.setOnClickListener(this)
//        binding.rvShifts.setOnClickListener(this)
        if (intent.hasExtra("from")) {
            from = intent.getStringExtra("from")
            if (from == AppConstants.EMPLOYEES) {
                setToolbar(binding.toolbar, "Employee Details")
            } else {
                setToolbar(binding.toolbar, "Customer Details")
            }

        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            property = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)

        }
        handleIntent()
    }

    private fun handleUpdateIntent(intent: Intent) {
        if (intent.hasExtra("userData")) {
            userData = intent.getParcelableExtra<UserData>("userData")
            if (userData != null && userData!!.roleId != null && userData!!.roleId!!.isNotEmpty()) {
                binding.tvName.text = returnName(userData!!)
                binding.data = userData
                roleId = userData!!.roleId!!
                if (userData!!.userId != null && userData!!.userId!!.isNotEmpty()) {
                    userId = userData!!.userId!!
                    adapter.addItems(getOptionsList())
                    userId = userData!!.userId!!
                    mUserDetailsViewModel.getUserDetail(httpManager, userData!!)
                    openBasicInfo()

                }
                if(userData!!.roleName!=null&&userData!!.roleName!!.isNotEmpty()){
                    binding.tvRoll.setText(userData!!.roleName)
                    setToolbar(binding.toolbar, "${userData!!.roleName} Details")
                    binding.llRole.visibility=View.VISIBLE
                }else{
                    binding.llRole.visibility=View.GONE
                    if (intent.hasExtra("from")) {
                        from = intent.getStringExtra("from")
                        if (from == AppConstants.EMPLOYEES) {
                            setToolbar(binding.toolbar, "Employee Details")
                        } else {
                            setToolbar(binding.toolbar, "Customer Details")
                        }

                    }
                }
                if (userData!!.profileImg != null && userData!!.profileImg!!.isNotEmpty()) {
                    GlideApp.with(this)
                            .load(userData!!.profileImg)
                            .apply(RequestOptions().circleCrop()
                                    .placeholder(R.drawable.ic_my_profile))
                            .error(R.drawable.ic_my_profile)
                            .into(binding.ivUser)
                    binding.ivUser.setOnClickListener {
                        openDialogShowImage(userData!!.profileImg!!)
                    }
                } else {
                    binding.ivUser.setImageResource(R.drawable.ic_my_profile)
                }
                if (userData!!.mobile != null && userData!!.mobile!!.isNotEmpty()) {
                    var mobile = userData!!.mobile
                    binding.tvMobile.setOnClickListener {
                        CommonUtils.openDialer(this, mobile)
                    }
                }
            }
        }


    }


    private fun handleIntent() {
        if (intent.hasExtra("userData")) {
            userData = intent.getParcelableExtra<UserData>("userData")

            if (userData != null && userData!!.roleId != null && userData!!.roleId!!.isNotEmpty()) {
                binding.tvName.text = returnName(userData!!)
                binding.data = userData
                roleId = userData!!.roleId!!
                userId = userData!!.userId!!
                mUserDetailsViewModel.getUserDetail(httpManager, userData!!)


                if (userData!!.userId != null && userData!!.userId!!.isNotEmpty()) {
                    userId = userData!!.userId!!
                    var list = getOptionsList()
                    if (list.size > 1) {
                        binding.rvUserOptions.visibility = View.VISIBLE
                    } else {
                        binding.rvUserOptions.visibility = View.GONE
                    }
                    adapter.addItems(list)
                    openBasicInfo()
                }
                if(userData!!.roleName!=null&&userData!!.roleName!!.isNotEmpty()){
                    binding.tvRoll.setText(userData!!.roleName)
                    binding.llRole.visibility=View.VISIBLE
                    setToolbar(binding.toolbar, "${userData!!.roleName} Details")
                }else{
                    if (intent.hasExtra("from")) {
                        from = intent.getStringExtra("from")
                        if (from == AppConstants.EMPLOYEES) {
                            setToolbar(binding.toolbar, "Employee Details")
                        } else {
                            setToolbar(binding.toolbar, "Customer Details")
                        }

                    }
                    binding.llRole.visibility=View.GONE
                }
                if (userData!!.profileImg != null && userData!!.profileImg!!.isNotEmpty()) {
                    GlideApp.with(this)
                            .load(userData!!.profileImg)
                            .apply(RequestOptions().circleCrop()
                                    .placeholder(R.drawable.ic_my_profile))
                            .error(R.drawable.ic_my_profile)
                            .into(binding.ivUser)
                    binding.ivUser.setOnClickListener {
                        openDialogShowImage(userData!!.profileImg!!)
                    }
                } else {
                    binding.ivUser.setImageResource(R.drawable.ic_my_profile)
                }
                if (userData!!.mobile != null && userData!!.mobile!!.isNotEmpty()) {
                    var mobile = userData!!.mobile
                    binding.tvMobile.setOnClickListener {
                        CommonUtils.openDialer(this, mobile)
                    }
                }
            }
        }


    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_details
    }

    override fun getViewModel(): UserDetailsViewModel {
        return mUserDetailsViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handleUserDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        Log.e("checkUserDetails","$result")
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<UpdateResponse> = JSONConverter()
            var response: UpdateResponse = jsonConverter.jsonToObject(
                result.toString(),
                UpdateResponse::class.java
            ) as UpdateResponse
            if (response.data != null) {
                binding.updateResponse = response
                /*if (action.equals("view")) {

                    binding.request = response.data

                    if (!data.dfData.isNullOrEmpty()) {
                        showDynamicForm(dfId, data!!.dfData!!)
                    } else {
                        showDynamicForm(dfId, ArrayList())
                    }

                } else {
                    var data = response.data
                    binding.request = data
                    if (!data?.dfData.isNullOrEmpty()) {
                        showDynamicForm(dfId, data!!.dfData!!)
                    } else {
                        showDynamicForm(dfId, ArrayList())
                    }
                }*/




            }

        }
    }

    override fun handleDeleteResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@UserDetailsActivity)) {
            onSuccess()
        } else {
            onCancel()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            /*  R.id.ivEdit -> {
                  onEditUser()
              }
              R.id.ivDelete -> {
                  if (userData != null) {
                      onDeleteUser(userData!!)
                  }

              }*/

            R.id.ivThreeDot -> {
                showPopUpMenu()
            }

        }
    }

    private fun openAttendance() {
        var empData = ProfileInfo()
        etUserDetails.visibility = View.GONE
        empData.profileImg = userData!!.profileImg
        empData.userId = userData!!.userId
        empData.mobile = userData!!.mobile
        empData.email = userData!!.email
        empData.roleId = userData!!.roleId
        empData.name = returnName(userData!!)
        addFragmentInContainer(AttendanceFragment.newInstance(empData, false,false))

    }

    private fun openLeave() {
        etUserDetails.visibility = View.GONE
        var empData = ProfileInfo()
        empData.profileImg = userData!!.profileImg
        empData.userId = userData!!.userId
        empData.mobile = userData!!.mobile
        empData.email = userData!!.email
        empData.roleId = userData!!.roleId
        empData.name = returnName(userData!!)
        addFragmentInContainer(LeaveHistoryFragment.newInstance(empData))

    }

    private fun openAddress() {
        etUserDetails.visibility = View.GONE
        var empData = ProfileInfo()
        empData.profileImg = userData!!.profileImg
        empData.userId = userData!!.userId
        empData.mobile = userData!!.mobile
        empData.email = userData!!.email
        empData.roleId = userData!!.roleId
        empData.name = returnName(userData!!)
        addFragmentInContainer(UserAddressListFragment.newInstance(empData))

    }

    private fun openBasicInfo() {
        etUserDetails.visibility = View.VISIBLE
        addFragmentInContainer(UserBasicInfoFragment.newInstance(userData = userData!!))

    }

    private fun onEditUser() {
        //Log.e("checkClick","edit CLicked")
        var intent = Intent(this, AddCustomerActivity::class.java)
        intent.putExtra("userData", userData)
        intent.putExtra("from", from)
        intent.putExtra("action", "edit")

        //    intent.putExtra(property, AppConstants.Extra.EXTRA_CATEGORIES)
        startActivityForResult(intent, CONST_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONST_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                isUpdate = true
                if (data != null)
                    handleUpdateIntent(data!!)

            } else {
                isUpdate = false
            }
        } else {
            isUpdate = false
        }
    }

    fun onDeleteUser(data: UserData) {
        val dialog = DoubleButtonDialog(this,
                true,
                null,
                getString(R.string.delete_user),
                getString(R.string.yes),
                getString(R.string.no),
                object : OnClickListener {
                    override fun onClickCancel() {}
                    override fun onClick() {
                        if (data.userId != null && data.userId!!.isNotEmpty()) {
                            showLoading()
                            mUserDetailsViewModel.deleteUser(httpManager, data.userId)
                        }
                    }
                })
        dialog.show()

    }

    override fun onSuccess() {
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(BaseActivity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onCancel() {
        val returnIntent = Intent()
        setResult(BaseActivity.RESULT_CANCELED, returnIntent)
        finish()
    }

    override fun onUpdateDataSucess(userData: UserData?) {

    }

    override fun onUpdateHeader(type: String?) {

    }

    fun returnName(data: UserData): String {
        var name = ""
        if (data.firstName != null && data.middleName != null && data.lastName != null) {
            name = data.firstName + " " + data.middleName + " " + data.lastName
        } else if (data.firstName != null && data.lastName != null) {
            name = data.firstName + " " + data.lastName
        } else if (data.firstName != null && data.middleName != null) {
            name = data.firstName + " " + data.middleName
        } else if (data.firstName != null) {
            name = data.firstName!!
        }
        return name
    }

    fun showPopUpMenu() {
        var popup = PopupMenu(this, binding.ivThreeDot);
        //Inflating the Popup using xml file
        popup.menuInflater
                .inflate(R.menu.user_edit_options, popup.menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.action_edit -> {
                    onEditUser()
                }
                R.id.action_delete -> {
                    if (userData != null) {
                        onDeleteUser(userData!!)
                    }
                }

            }

            true
        })



        popup.show();
    }

    private fun setExtraDetails(){
        /*val dynamicFragment = NewDynamicFormFragment.getInstance(
            dfId,
            roleId!!,
            false,
            formadata
        )
        dynamicFragment!!.setListener(this)
        addFragmentInContainerDetails(dynamicFragment!!, dfId)
        dynamicFormsNew = CommonUtils.getFormByFormId(dfId)*/
    }

    private fun addFragmentInContainer(fragment: Fragment) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.disallowAddToBackStack()
        ft.replace(R.id.container, fragment)
        ft.commit()
    }

    private fun addFragmentInContainerDetails(fragment: Fragment,dfId: String) {
        /*val formName = CommonUtils.getFormByFormId(dfId)
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        ft.replace(R.id.containerDetails, fragment, dfId)
        ft.commit()*/
    }

    override fun onItemSelected(data: AttendanceStatusData) {
//        var i = data.count
        if (data.status == AppConstants.BASIC_INFO) {
            openBasicInfo()
        } else if (data.status == AppConstants.ADDRESS) {
            openAddress()

        } else if (data.status == AppConstants.ATTENDANCE_Title) {
            openAttendance()
        } else if (data.status == AppConstants.LEAVE_TEXT) {
            openLeave()
        }

    }

    fun getOptionsList(): ArrayList<AttendanceStatusData> {
        var list = ArrayList<AttendanceStatusData>()
        mUserDetailsViewModel.performShowHideView(this, preferencesHelper, roleId!!)
        var isAddressShow = mUserDetailsViewModel.isAddress.get()
        if (isAddressShow!!) {
            if (from == AppConstants.EMPLOYEES) {
                for (i in 0..3) {
                    var data = AttendanceStatusData()
                    data.count = i
                    if (i == 0) {
                        data.status = AppConstants.BASIC_INFO
                        data.isSelected = true
                    } else if (i == 1) {
                        data.status = AppConstants.ADDRESS
                    } else if (i == 2) {
                        data.status = AppConstants.ATTENDANCE_Title
                    } else if (i == 3) {
                        data.status = AppConstants.LEAVE_TEXT
                    }
                    list.add(data)


                }
            } else {
                for (i in 0..1) {
                    var data = AttendanceStatusData()
                    data.count = i
                    if (i == 0) {
                        data.status = AppConstants.BASIC_INFO
                        data.isSelected = true
                    } else if (i == 1) {
                        data.status = AppConstants.ADDRESS
                    }
                    list.add(data)


                }
            }

        } else {
            if (from == AppConstants.EMPLOYEES) {
                for (i in 0..2) {
                    var data = AttendanceStatusData()
                    data.count = i
                    if (i == 0) {
                        data.status = AppConstants.BASIC_INFO
                        data.isSelected = true
                    } else if (i == 1) {
                        data.status = AppConstants.ATTENDANCE_Title
                    } else if (i == 2) {
                        data.status = AppConstants.LEAVE_TEXT
                    }
                    list.add(data)


                }
            } else {
                var data = AttendanceStatusData()
                data.count = 0
                data.status = AppConstants.BASIC_INFO
                data.isSelected = true
                list.add(data)
            }
        }

        return list
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    private fun openDialogShowImage(url: String) {

        val dialog = Dialog(this)
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

        Glide.with(this)
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

    override fun onBackPressed() {
        if (isUpdate) {
            onSuccess()
        } else {
            onCancel()
        }
    }


}