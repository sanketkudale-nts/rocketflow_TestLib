package com.rocketflyer.rocketflow.data.local.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.tracki.ui.service.transition.UserActivity
import com.tracki.utils.Log
import java.util.*


/**
 * Created by rahul on 22/5/19
 */
class TransitionTable : BaseTable() {

    companion object {
        private val TAG = TransitionTable::class.java.simpleName
        private val TABLE_NAME = "transition"


        private val _ID = "_id"
        private val COLUMN_TYPE = "type"
        private val COLUMN_RECORDED_AT = "recorded_at"
        private val COLUMN_CREATED_AT = "created_at"
        private val COLUMN_CONFIDENCE = "confidence"
        private val COLUMN_TRANSITION = "transitions"


        private val TABLE_CREATE_QUERY = ("CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME
                + " (" +
                _ID + " INTEGER PRIMARY KEY  AUTOINCREMENT, " +
                COLUMN_RECORDED_AT + " LONG," +
                COLUMN_CREATED_AT + " LONG," +
                COLUMN_CONFIDENCE + " INTEGER," +
                COLUMN_TRANSITION + " TEXT," +
                COLUMN_TYPE + " TEXT" +
                ");")

        @JvmStatic
        fun onClearTable(db: SQLiteDatabase) {
            onClearTable(TABLE_NAME, db)
        }

        @JvmStatic
        fun onCreate(db: SQLiteDatabase) {
            onCreate(TABLE_NAME, db, TABLE_CREATE_QUERY)
        }

        @JvmStatic
        fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onUpgrade(TABLE_NAME, db, TABLE_CREATE_QUERY, oldVersion, newVersion)
        }
    }

    fun addTransition(db: SQLiteDatabase?, userActivity: UserActivity?): Long {
        if (db == null || userActivity == null) {
            return -1L
        }
        // Create a new map of values, where column names are the keys
        val values = ContentValues()

        values.put(COLUMN_RECORDED_AT, userActivity.recordedAt)
        values.put(COLUMN_CREATED_AT, Date().time)
        values.put(COLUMN_CONFIDENCE, userActivity.confidence)
        values.put(COLUMN_TRANSITION, userActivity.transition)
        values.put(COLUMN_TYPE, userActivity.type.toString())

        Log.i(TAG, values.toString())

        try {
            // Insert the new row, returning the primary key value of the new row
            return db.insert(TABLE_NAME, null, values)
        } catch (e: Exception) {
            Log.e(TAG, TABLE_NAME + ".addTransition: " + e.message)
        }

        return -1L
    }

    fun getAllTransitions(db: SQLiteDatabase?): List<UserActivity> {
        val userActivityList = ArrayList<UserActivity>()

        if (db == null) {
            return userActivityList
        }
        try {
            val query = ("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_CREATED_AT DESC")
            Log.e(TAG, "-------> $query")

            val cursorEvent = db.rawQuery(query, null)
            if (cursorEvent != null && cursorEvent.moveToFirst()) {
                do {
                    val userActivity = UserActivity(
                            getActivityType(cursorEvent.getString(5)),
                            cursorEvent.getInt(3),
                            cursorEvent.getLong(1),
                            cursorEvent.getString(4)
                    )

                    Log.e(TAG, userActivity.toString())

                    userActivityList.add(userActivity)
                } while (cursorEvent.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception inside checkIfRecordExists() $e")
        }
        return userActivityList
    }

    private fun getActivityType(name: String): UserActivity.ActivityType {
        return when (name) {
            "drive" -> {
                UserActivity.ActivityType.DRIVE
            }
            "cycle" -> {
                UserActivity.ActivityType.CYCLE
            }
            "on_foot" -> {
                UserActivity.ActivityType.ON_FOOT
            }
            "stop" -> {
                UserActivity.ActivityType.STOP
            }
            "walk" -> {
                UserActivity.ActivityType.WALK
            }
            "run" -> {
                UserActivity.ActivityType.RUN
            }
            else -> {
                UserActivity.ActivityType.UNKNOWN
            }
        }
    }

}