package com.tracki.data.database


import android.content.Context
import androidx.room.Room

class DatabaseClient private constructor(  mCtx: Context) {
    private val DB_NAME = "rocketflowcontact.db"

    //our app database object
    val appDatabase: ContactDatabase

    init {

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, ContactDatabase::class.java, DB_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    companion object {
        private var mInstance: DatabaseClient? = null

        @Synchronized
        fun getInstance(mCtx: Context): DatabaseClient {
            if (mInstance == null) {
                mInstance = DatabaseClient(mCtx)
            }
            return mInstance as DatabaseClient
        }
    }
}
