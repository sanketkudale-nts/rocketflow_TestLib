package com.rocketflyer.rocketflow.data.local.db;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.tracki.utils.Log;

/**
 * Created by rahul on 2/1/19
 */
public class BaseTable {
    protected final Gson gson = new Gson();

    static void onClearTable(final String TABLE_NAME, final SQLiteDatabase db) {
        if (db == null)
            return;

        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        } catch (Exception e) {
            Log.e(TABLE_NAME, TABLE_NAME + ".clearTable: " + e.getMessage());
        }
    }

    static void onCreate(final String TABLE_NAME, final SQLiteDatabase db, final String TABLE_CREATE_QUERY) {
        if (db == null) {
            return;
        }

        try {
//            Log.e(TABLE_NAME, TABLE_CREATE_QUERY);
            db.execSQL(TABLE_CREATE_QUERY);
        } catch (Exception e) {
            Log.e(TABLE_NAME, TABLE_NAME + ".onCreate: " + e);
        }
    }

    static void onUpgrade(final String TABLE_NAME, final SQLiteDatabase db, final String TABLE_CREATE_QUERY,
                          final int oldVersion, final int newVersion) {
        if (db == null) {
            return;
        }

        try {
            Log.i(TABLE_NAME, TABLE_NAME + ".onUpgrade called.");

            onClearTable(TABLE_NAME, db);
            onCreate(TABLE_NAME, db, TABLE_CREATE_QUERY);

        } catch (Exception e) {
            Log.e(TABLE_NAME, TABLE_NAME + ".onUpgrade: " + e);
        }
    }
}
