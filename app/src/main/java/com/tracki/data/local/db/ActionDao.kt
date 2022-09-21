//package com.tracki.data.local.db
//
//import android.arch.persistence.room.Dao
//import android.arch.persistence.room.Insert
//import android.arch.persistence.room.Query
//import com.tracki.utils.AppConstants
//
///**
// * Created by rahul on 4/4/19
// */
//@Dao
//interface ActionDao {
//    @Query("SELECT * FROM " + AppConstants.TABLE_NAME)
//    fun getAllActions(): List<Actions>
//
//    @Insert
//    fun insert(actions: Actions): Long
//
//    @Query("SELECT * FROM " + AppConstants.TABLE_NAME + " where action_name == :actionName")
//    fun findByName(actionName: String): Actions
//
//
//}