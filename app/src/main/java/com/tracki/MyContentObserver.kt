package com.tracki

import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.tracki.data.database.DatabaseClient
import com.tracki.ui.addbuddy.Contact
import com.tracki.utils.CommonUtils


/**
 * Created by Vikas Kesharvani on 28/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class MyContentObserver : ContentObserver(null) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        val cursor: Cursor? = TrackiApplication.instance!!.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " Desc"
        )
        if (cursor != null && cursor.moveToNext()) {
            val id: String = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID)
            )
            val name: String = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            )
            var number = getPrimaryNumber(id.toLong())
            CommonUtils.showLogMessage("e", "Contact ID", id)
            CommonUtils.showLogMessage("e", "Person Name", name)
            CommonUtils.showLogMessage("e", "Mobile Number", number)
            var apiDataDao =DatabaseClient.getInstance(TrackiApplication.instance!!).appDatabase.contactsDataDao()

            var existContact = apiDataDao.geContact(id.toLong())
            if (number != null) {
                number = phoneNumberWithOutCountryCode(number)
                number = number!!.replace("(", "").replace(")", "").replace("-", "").trim()
                if (number.startsWith("0") && number.length > 10) {
                    number = number.substring(1)
                }
            }
            if (existContact != null) {
                apiDataDao.updateContact(name, number, id.toLong())
            } else {
                val contact = Contact(name, number)
                contact.contact_id = id.toLong()
                apiDataDao.insertContacts(contact)
            }
        }
    }

    override fun deliverSelfNotifications(): Boolean {
        return true
    }

    fun phoneNumberWithOutCountryCode(phoneNumber: String): String? {
        var number = phoneNumber.replace("\\s".toRegex(), "")
        if (phoneNumber.startsWith("+")) {
            if (number.length == 13) {
                number = number.substring(3)
            } else if (number.length == 14) {
                number = number.substring(4)
            }
        }
        return number
    }

    /**
     * Get primary Number of requested  id.
     *
     * @return string value of primary number.
     */
    private fun getPrimaryNumber(_id: Long): String? {
        var primaryNumber: String? = null
        var lcursor: Cursor? = null
        try {
            val cursor: Cursor? = TrackiApplication.instance!!.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.TYPE
                    ),
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + _id,  // We need to add more selection for phone type
                    null,
                    null
            )
            lcursor = cursor
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    when (cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> primaryNumber =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> primaryNumber =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> primaryNumber =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        ContactsContract.CommonDataKinds.Phone.TYPE_OTHER -> {
                        }
                    }
                    if (primaryNumber != null) break
                }
            }
        } catch (e: Exception) {
            Log.i("test", "Exception $e")
        } finally {
            if (lcursor != null) {
                lcursor.deactivate()
                lcursor.close()
            }
        }
        return primaryNumber
    }
}