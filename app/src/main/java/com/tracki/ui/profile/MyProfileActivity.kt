package com.tracki.ui.profile

import android.Manifest
import com.tracki.utils.TrackiToast.Message.showShort

import com.tracki.ui.base.BaseActivity
import com.tracki.ui.profile.MyProfileViewModel
import com.tracki.ui.profile.MyProfileNavigator
import javax.inject.Inject
import com.tracki.data.network.HttpManager
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.ProfileInfo
import com.google.android.material.snackbar.Snackbar
import com.tracki.R
import android.os.Bundle
import com.tracki.ui.custom.GlideApp
import com.bumptech.glide.request.RequestOptions
import com.tracki.ui.custom.CircleTransform
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.graphics.drawable.Animatable
import com.tracki.data.model.response.config.Api
import com.tracki.TrackiApplication
import com.tracki.data.model.request.UpdateProfileRequest
import com.tracki.ui.changemobile.ChangeMobileActivity
import android.os.Build
import android.content.pm.PackageManager
import com.tracki.ui.profile.MyProfileActivity
import com.tracki.utils.image_utility.ImagePicker
import android.app.Activity
import android.annotation.SuppressLint
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.tracki.utils.image_utility.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import com.tracki.data.model.request.UpdateFileRequest
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.APIError
import com.tracki.data.model.response.config.ProfileResponse
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.data.model.response.config.UserData
import com.tracki.databinding.ActivityMyProfileBinding
import com.tracki.ui.addcustomer.CustomerInfoScreenFragment
import com.tracki.utils.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_create_task.*
import java.io.File

/**
 * Created by rahul on 4/10/18
 */
class MyProfileActivity : BaseActivity<ActivityMyProfileBinding?, MyProfileViewModel?>(),
    MyProfileNavigator, HasSupportFragmentInjector, CustomerInfoScreenFragment.OnDataSubmitListener {
    @JvmField
    @Inject
    var mMyProfileViewModel: MyProfileViewModel? = null

    @JvmField
    @Inject
    var httpManager: HttpManager? = null
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @JvmField
    @Inject
    var preferencesHelper: PreferencesHelper? = null
    private var actualImage: File? = null
    private var compressedImage: File? = null
    private var mActivityMyProfileBinding: ActivityMyProfileBinding? = null
    private var hasEditMode =
        false //This field indicates that whether the fields are editable are not.
    private var isProfileDataChanged =
        false //This field indicates that whether the fields are editable are not.
    private var profileInfo: ProfileInfo? = null
    private var profileImg: String? = null
    private var snackBar: Snackbar? = null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            mActivityMyProfileBinding!!.coordinatorLayout,
            getString(R.string.please_check_your_internet_connection)
        )
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_profile
    }

    override fun getViewModel(): MyProfileViewModel {
        return mMyProfileViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMyProfileBinding = viewDataBinding
        mMyProfileViewModel!!.navigator = this
        setUp()
    }

    private fun replaceFragment(fragment: Fragment) {
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        ft.replace(R.id.container, fragment, null)
        container.visibility = View.VISIBLE
        ft.commit()
    }


    private fun setUp() {
        setToolbar(mActivityMyProfileBinding!!.toolbar, getString(R.string.my_profile))
        profileInfo = preferencesHelper!!.userDetail
        if (profileInfo != null) {
//            if (profileInfo!!.name != null) {
//                mActivityMyProfileBinding!!.edFullName.setText(profileInfo!!.name)
//                mActivityMyProfileBinding!!.edFullName.setSelection(profileInfo!!.name!!.length)
//            }
//            mActivityMyProfileBinding!!.edMobileNumber.setText(profileInfo!!.mobile)
//            mActivityMyProfileBinding!!.edEmailId.setText(profileInfo!!.email)
            profileImg = profileInfo!!.profileImg
//            GlideApp.with(this)
//                .asBitmap()
//                .load(profileInfo!!.profileImg)
//                .apply(
//                    RequestOptions()
//                        .transform(CircleTransform())
//                        .placeholder(R.drawable.ic_placeholder_pic)
//                )
//                .error(R.drawable.ic_placeholder_pic)
//                .into(mActivityMyProfileBinding!!.ivProfile)
            var action="add"
            var userData=UserData()
            userData.firstName=profileInfo!!.name
            userData.userId=profileInfo!!.userId
            userData.email=profileInfo!!.email
            userData.roleId=profileInfo!!.roleId
            userData.profileImg=profileInfo!!.profileImg
            var map=HashMap<String,String>()
            map["RoleId"]=profileInfo!!.roleId!!
            var jsonConverter=JSONConverter<HashMap<String,String>>()
            var catgory=jsonConverter.objectToJson(map)
            replaceFragment(CustomerInfoScreenFragment.newInstance(AppConstants.OWNER, catgory, userData,action,null, this)!!)
        }
        val intent = intent
        if (intent.hasExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE)) {
            //This field indicates that whether the fields are editable are not.
            hasEditMode = true
            invalidateOptionsMenu()
        }
    }

   /* override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val settingsItem = menu.findItem(R.id.action_submit)
        enableViews(hasEditMode)
        val drawable: Drawable?
        drawable = if (hasEditMode) {
            ContextCompat.getDrawable(this, R.drawable.ic_tick_black_24dp)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_edit_black_24dp)
        }
        settingsItem.icon = drawable
        return super.onPrepareOptionsMenu(menu)
    }*/

    private fun enableViews(enable: Boolean) {
//        mActivityMyProfileBinding!!.edFullName.isEnabled = enable
//        mActivityMyProfileBinding!!.edEmailId.isEnabled = enable
//        mActivityMyProfileBinding!!.edMobileNumber.isClickable = enable
//        mActivityMyProfileBinding!!.tvEditPic.isEnabled = enable
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        super.onCreateOptionsMenu(menu)
//        menuInflater.inflate(R.menu.profile, menu)
//        return true
//    }

/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawable = item.icon
        if (drawable is Animatable) {
            (drawable as Animatable).start()
        }
        if (item.itemId == R.id.action_submit) {
            if (hasEditMode) {
                val name = mActivityMyProfileBinding!!.edFullName.text.toString()
                val email = mActivityMyProfileBinding!!.edEmailId.text.toString()
                val mobile = mActivityMyProfileBinding!!.edMobileNumber.text.toString()
                if (name == "" || mobile == "" || email == "") {
                    showShort(this, getString(R.string.field_cannot_be_empty))
                    return super.onOptionsItemSelected(item)
                }
                if (!CommonUtils.isEmailValid(email)) {
                    showShort(this, getString(R.string.invalid_email))
                    return super.onOptionsItemSelected(item)
                }
                if (!CommonUtils.isMobileValid(mobile)) {
                    showShort(this, getString(R.string.invalid_mobile))
                    return super.onOptionsItemSelected(item)
                }
                hideKeyboard()
                showLoading()
                val api = TrackiApplication.getApiMap()[ApiType.UPDATE_PROFILE]
                val updateProfileRequest = UpdateProfileRequest(name, email, mobile, profileImg)
                mMyProfileViewModel!!.updateProfile(updateProfileRequest, httpManager, api)
            } else {
                hasEditMode = true
                invalidateOptionsMenu()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
*/

    override fun editMobileNumber(view: View) {
        if (view.id == R.id.edMobileNumber) {
            if (view.isClickable) {
                startActivityForResult(
                    ChangeMobileActivity.newIntent(this)
                        .putExtra(AppConstants.Extra.NUMBER_EXTRA, profileInfo!!.mobile),
                    AppConstants.REQUEST_CODE_CHANGE_MOBILE
                )
            }
        }
    }

    override fun uploadProfilePic() {
        onPickImage()
    }

    fun onPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                proceedToImagePicking()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), REQUEST_READ_STORAGE
                )
            }
        } else {
            proceedToImagePicking()
        }
    }

    private fun proceedToImagePicking() {
        val chooseImageIntent = ImagePicker.getPickImageIntent(this)
        startActivityForResult(chooseImageIntent, PICK_IMAGE_FILE_ID)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.size > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    proceedToImagePicking()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_IMAGE_FILE_ID -> {
                actualImage = ImagePicker.getImageFileToUpload(this, resultCode, data)
                compressImage()
            }
            AppConstants.REQUEST_CODE_CHANGE_MOBILE -> if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(AppConstants.Extra.NUMBER_EXTRA)) {
//                    mActivityMyProfileBinding!!.edMobileNumber.setText(
//                        data.getStringExtra(AppConstants.Extra.NUMBER_EXTRA)
//                    )
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("CheckResult")
    private fun compressImage() {
        if (actualImage == null) {
            showShort(this, "Please choose an image!")
        } else {

            // Compress image in main thread
            //compressedImage = new Compressor(this).compressToFile(actualImage);
            //setCompressedImage();

            // Compress image to bitmap in main thread
            //compressedImageView.setImageBitmap(new Compressor(this).compressToBitmap(actualImage));

            // Compress image using RxJava in background thread
            Compressor(this)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file: File? ->
                    compressedImage = file
                    showLoading()
                    val updateFileRequest =
                        UpdateFileRequest(compressedImage!!, FileType.USER_PROFILE, "")
                    val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE_AGAINEST_ENTITY]
                    mMyProfileViewModel!!.uploadProfilePic(updateFileRequest, httpManager, api)
                }) { throwable: Throwable ->
                    throwable.printStackTrace()
                    showShort(this@MyProfileActivity, throwable.message)
                }
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            isProfileDataChanged = true
            val profileResponse = Gson().fromJson(result.toString(), ProfileResponse::class.java)
            if (profileResponse != null) {
                val profileInfo = profileResponse.profileInfo
                if (profileInfo != null) {
                    val info = preferencesHelper!!.userDetail
                    info.name = profileInfo.name
                    info.mobile = profileInfo.mobile
                    info.email = profileInfo.email
                    info.profileImg = info.profileImg
                    //save data into the preferences
                    preferencesHelper!!.userDetail = info
                }
            }
            if (!hasEditMode) {
                hasEditMode = false
                invalidateOptionsMenu()
            }
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun handleProfilePicResponse(callback: ApiCallback, result: Any, error: APIError) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            isProfileDataChanged = true
            val profileResponse = Gson().fromJson(result.toString(), ProfileResponse::class.java)
            if (profileResponse != null) {
                val imageUrl = profileResponse.imageUrl
                profileImg = imageUrl
                val info = preferencesHelper!!.userDetail
                info.profileImg = imageUrl
                preferencesHelper!!.userDetail = profileInfo!!
//                GlideApp.with(this)
//                    .asBitmap()
//                    .load(imageUrl)
//                    .apply(
//                        RequestOptions()
//                            .transform(CircleTransform())
//                            .placeholder(R.drawable.ic_placeholder_pic)
//                    )
//                    .error(R.drawable.ic_placeholder_pic)
//                    .into(mActivityMyProfileBinding!!.ivProfile)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isProfileDataChanged) {
            setResult(RESULT_OK)
            finish()
        } else {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    companion object {
        private const val REQUEST_READ_STORAGE = 3
        private const val PICK_IMAGE_FILE_ID = 235
        @JvmStatic
        fun newIntent(context: Context?): Intent {
            return Intent(context, MyProfileActivity::class.java)
        }
    }

    override fun onSuccess() {
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onCancel() {
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    override fun onUpdateDataSucess(userData: UserData?) {
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()

    }

    override fun onUpdateHeader(type: String?) {

    }
}