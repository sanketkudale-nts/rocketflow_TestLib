package com.rocketflyer.rocketflow.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tracki.data.database.dao.ContactsDataDao
import com.tracki.ui.addbuddy.Contact


@Database(entities = [Contact::class], version = 1 ,exportSchema = false)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactsDataDao(): ContactsDataDao

}