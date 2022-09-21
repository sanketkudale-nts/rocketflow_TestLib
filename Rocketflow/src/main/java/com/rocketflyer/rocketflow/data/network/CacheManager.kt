package com.rocketflyer.rocketflow.data.network

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by rahul on 3/4/19
 */
class CacheManager private constructor(context: Context) {

    companion object {
        const val PREFS_CACHE = "tracki-cache"
        private var instance: CacheManager? = null

        fun getInstance(context: Context): CacheManager {
            if (instance == null) {
                synchronized(CacheManager::class.java) {
                    if (instance == null)
                        instance = CacheManager(context.applicationContext)
                }
            }
            return instance!!
        }
    }

    private var cachePref: SharedPreferences? = null

    init {
        cachePref = context.getSharedPreferences(PREFS_CACHE, Context.MODE_PRIVATE)
    }

    fun getFromCache(key: String, version: String): String? {
        var data = cachePref?.getString(key, null)
        if (data != null && data.indexOf(",") != -1) {
            if (version != data.substring(0, data.indexOf(","))) {
                data = null
            } else {
                data = data.substring(data.indexOf(",") + 1, data.length)
            }
        } else {
            data = null
        }
        return data

    }

    fun putIntoCache(key: String, value: String?, version: String) {
        if (value != null)
            cachePref?.edit()?.putString(key, "$version,$value")?.apply()
    }
}