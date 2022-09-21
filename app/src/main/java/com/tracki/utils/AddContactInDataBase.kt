package com.tracki.utils

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tracki.data.database.DatabaseClient
import com.tracki.ui.addbuddy.Contact




/**
 * Created by Vikas Kesharvani on 30/07/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class AddContactInDataBase(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    var context:Context?=null
    init {
        this.context=context
    }
    override fun doWork(): Result {
        DatabaseClient.getInstance(context!!).appDatabase.contactsDataDao().deleteContact()
        var displayName: String
        var number: String?
        var _id: Long
        val columns = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        val cursor: Cursor?= context!!.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            columns,
            null,
            null,
            ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        )

        if (cursor!=null&&cursor.moveToFirst()) {
            do {
                _id =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)).toLong()
                displayName =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        .trim { it <= ' ' }
                number = getPrimaryNumber(_id)
                if(number!=null) {
                    number=phoneNumberWithOutCountryCode(number)
                    number = number!!.replace("(", "").replace(")", "").replace("-", "").trim()
                   if(number.startsWith("0")&&number.length>10){
                       number=number.substring(1)
                   }
                }

                val contact = Contact(displayName, number)
                contact.contact_id=_id
                DatabaseClient.getInstance(context!!).appDatabase.contactsDataDao().insertContacts(contact)
            } while (cursor.moveToNext())
        } else {
        }
       return Result.success()
    }
    private fun phoneNumberWithOutCountryCode(phoneNumber: String): String? {
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
            val cursor: Cursor? = context!!.contentResolver.query(
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