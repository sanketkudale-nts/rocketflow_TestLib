package com.rocketflyer.rocketflow.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tracki.ui.addbuddy.Contact


@Dao
interface ContactsDataDao {





    @Query("SELECT * FROM contacts ORDER BY LOWER(name) ASC")
    abstract fun getAllContact(): LiveData<List<Contact>>?

    @Query("UPDATE contacts SET name = :name, mobile = :number WHERE contact_id =:contact_id")
    fun updateContact(name: String?, number: String?, contact_id: Long)

    @Query("SELECT * FROM contacts WHERE contact_id =:contact_id")
    fun geContact(contact_id: Long):Contact?

    @Insert
    abstract fun insertContacts(vararg contact: Contact)

    @Delete
    abstract fun deleteContact(contact: Contact)

    @Query("DELETE FROM contacts")
    abstract fun deleteContact()




}