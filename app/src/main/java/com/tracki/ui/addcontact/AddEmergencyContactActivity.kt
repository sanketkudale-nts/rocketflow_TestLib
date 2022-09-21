package com.tracki.ui.addcontact

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.tracki.BR
import com.tracki.R
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.databinding.ActivityAddEmergencyContactBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.Log
import com.tracki.utils.TrackiToast
import com.tracki.utils.TrackiToast.Message.showShort
import java.util.*
import javax.inject.Inject


/**
 * Created by rahul on 18/2/19
 */
class AddEmergencyContactActivity : BaseActivity<ActivityAddEmergencyContactBinding, AddEmergencyContactViewModel>(),
        AddEmergencyContactNavigator {

    private var snackBar: Snackbar?=null
    private val permissions = arrayOf(Manifest.permission.READ_CONTACTS/*,
            Manifest.permission.WRITE_CONTACTS*/)

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    @Inject
    lateinit var mAddEmergencyContactViewModel: AddEmergencyContactViewModel
    private lateinit var mActivityAddEmergencyContactBinding: ActivityAddEmergencyContactBinding

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_add_emergency_contact
    override fun getViewModel() = mAddEmergencyContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddEmergencyContactBinding = viewDataBinding
        mAddEmergencyContactViewModel.navigator = this
        setUp()
    }

    private lateinit var toolbar: Toolbar

    private var position = -1

    private var name: String? = null
    private var number: String? = null

    private fun setUp() {
        toolbar = mActivityAddEmergencyContactBinding.toolbar
        setToolbar(toolbar, getString(R.string.emergency_contacts))
        if (intent != null) {
            //for edit contact
            if (intent.hasExtra(AppConstants.Extra.EXTRA_CONTACT_ID)) {
                position = intent.getIntExtra(AppConstants.Extra.EXTRA_CONTACT_ID, -1)
            }
            if (intent.hasExtra(AppConstants.Extra.EXTRA_CONTACT_NAME)) {
                name = intent.getStringExtra(AppConstants.Extra.EXTRA_CONTACT_NAME)
                mActivityAddEmergencyContactBinding.edFullName.setText(name)
                mActivityAddEmergencyContactBinding.edFullName.setSelection(name!!.length)
            }
            if (intent.hasExtra(AppConstants.Extra.EXTRA_CONTACT_NUMBER)) {
                number = intent.getStringExtra(AppConstants.Extra.EXTRA_CONTACT_NUMBER)
                mActivityAddEmergencyContactBinding.edMobileNumber.setText(number)
                mActivityAddEmergencyContactBinding.edMobileNumber.setSelection(number!!.length)
            }
        }

        val tvChooseContact = mActivityAddEmergencyContactBinding.tvChooseContact
        tvChooseContact.setOnClickListener {

            // Check if the Read Write contacts permission is granted
            if (!hasPermission(permissions)) {
                return@setOnClickListener
            }

            pickContact()
        }
    }

    private fun pickContact() {
        //fetch contacts
        val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
//            contactPickerIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        startActivityForResult(contactPickerIntent, AppConstants.REQUEST_CODE_PICK_CONTACT)
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        when (reqCode) {
            AppConstants.REQUEST_CODE_PICK_CONTACT -> if (resultCode == Activity.RESULT_OK) {
                val cursor: Cursor?
                try {
                    if (data?.data != null) {
                        val uri = data.data as Uri
                        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

                        cursor = contentResolver.query(uri, projection, null, null, null)
                        if (cursor!!.moveToFirst()) {

                            val numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val mobileNumberWithCountryCode = cursor.getString(numberColumnIndex)

                            val num = phoneNumberWithOutCountryCode(mobileNumberWithCountryCode)

                            val nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                            val name = cursor.getString(nameColumnIndex)

                            mActivityAddEmergencyContactBinding.edFullName.setText(name)
                            mActivityAddEmergencyContactBinding.edFullName.setSelection(name.length)
                            mActivityAddEmergencyContactBinding.edMobileNumber.setText(num)
                            mActivityAddEmergencyContactBinding.edMobileNumber.setSelection(num.length)

                            cursor.close()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception inside onActivityResult(): $e")
                }

                super.onActivityResult(reqCode, resultCode, data)
            }
        }
    }

    private fun phoneNumberWithOutCountryCode(phoneNumber: String): String {
        var number = phoneNumber
        if (phoneNumber.startsWith("+")) {
            if (phoneNumber.length == 13) {
                number = phoneNumber.substring(3)
            } else if (phoneNumber.length == 14) {
                number = phoneNumber.substring(4)
            }
        }

        if (phoneNumber.contains(" ")) {
            number = phoneNumber.replace("\\s".toRegex(), "")
        }
        return number
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawable = item.icon
        if (drawable is Animatable) {
            (drawable as Animatable).start()
        }
        return when (item.itemId) {
            R.id.action_submit -> {
                validateContact()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateContact() {
        val edFullName = mActivityAddEmergencyContactBinding.edFullName.text.toString().trim()
        val edMobileNumber = mActivityAddEmergencyContactBinding.edMobileNumber.text.toString().trim()

        if (mAddEmergencyContactViewModel.isViewNullOrEmpty(edFullName)) {
            showShort(this, getString(R.string.name_cannot_be_empty))
            return
        }
        if (CommonUtils.isViewNullOrEmpty(edMobileNumber)) {
            showShort(this, getString(R.string.field_cannot_be_empty))
            return
        }

        if (!mAddEmergencyContactViewModel.isMobileValid(edMobileNumber)) {
            showShort(this, getString(R.string.invalid_mobile))
            return
        }

        hideKeyboard()

        val intent = Intent()
        //position cannot be -1 if its in edit mode
        if (position != -1) {
            intent.putExtra(AppConstants.Extra.EXTRA_CONTACT_ID, position)
        }
        intent.putExtra(AppConstants.Extra.EXTRA_CONTACT_NAME, edFullName)
        intent.putExtra(AppConstants.Extra.EXTRA_CONTACT_NUMBER, edMobileNumber)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        // Initialize the map with both permissions
        // Fill with actual results from user
        if (requestCode == AppConstants.PERMISSIONS_REQUEST_CODE_MULTIPLE) {
            val perms = HashMap<String, Int>()
            perms[Manifest.permission.READ_CONTACTS] = PackageManager.PERMISSION_GRANTED
            if (grantResults.isNotEmpty()) {
                for (i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]

                val readContactPer = perms[Manifest.permission.READ_CONTACTS]

                // Check for both permissions
                if (readContactPer != null && readContactPer == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "sms & location services permission granted")
                    // process the normal flow
                    //else any one or both the permissions are not granted
                    try {
                        pickContact()
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception inside onRequestPermissionsResult(): $e")
                    }

                } else {
                    Log.d(TAG, "Some permissions are not granted ask again ")
                    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                    //                        // shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                        showDialogOK("Allow access to read your contacts from device contact") { dialog, which ->
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                hasPermission(permissions)
                                dialog.dismiss()
                            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                finish()
                            }
                        }
                    } else {
                        TrackiToast.Message
                                .showLong(this, "Go to settings and enable permissions")
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        private val TAG = "AddEmergencyContact"

        @JvmStatic
        fun newIntent(context: Context) = Intent(context, AddEmergencyContactActivity::class.java)
    }
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityAddEmergencyContactBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }

}