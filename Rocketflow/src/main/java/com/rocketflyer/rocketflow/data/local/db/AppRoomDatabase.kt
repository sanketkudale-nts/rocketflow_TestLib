package com.rocketflyer.rocketflow.data.local.db//package com.tracki.data.local.db
//
//import android.arch.persistence.room.Database
//import android.arch.persistence.room.Room
//import android.arch.persistence.room.RoomDatabase
//import android.content.Context
//
//
///**
// * Created by rahul on 4/4/19
// */
//const val DATABASE_NAME = ""
//const val DATABASE_VERSION = 1
//
//@Database(entities = [Actions::class], version = DATABASE_VERSION/*,exportSchema = false*/)
//abstract class AppRoomDatabase : RoomDatabase() {
//    abstract fun actionDao(): ActionDao
//
//    companion object {
//
//
//        private var INSTANCE: AppRoomDatabase? = null
//
//        fun getDatabase(context: Context): AppRoomDatabase? {
//            if (INSTANCE == null) {
//                synchronized(AppRoomDatabase::class.java) {
//                    if (INSTANCE == null) {
//                        // Create database here
//                        INSTANCE = Room.databaseBuilder(context.applicationContext,
//                                AppRoomDatabase::class.java, DATABASE_NAME)
////                                .allowMainThreadQueries()
//                                .build()
//                    }
//                }
//            }
//            return INSTANCE
//        }
//
//        fun cleanUp() {
//            INSTANCE = null
//        }
//
//    }
//
//}