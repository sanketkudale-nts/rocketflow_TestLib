package com.tracki

import android.content.Context
import android.content.Intent
import android.os.Bundle

object UtilsExpose {
    fun startNewActivity(context: Context, clazz: Class<*>, extras: Bundle) {

        val intent = Intent(context, clazz)
        // To pass any data to next activity
        intent.putExtra("keyIdentifier", extras)
        // start your next activity
        context.startActivity(intent)

    }


    fun startNewActivity(context: Context, clazz: Class<*>) {

        val intent = Intent(context, clazz)
        // start your next activity
        context.startActivity(intent)

    }


    /**
     * @throws Exception
     *
     */

    fun startNewActivityUsingAny(context: Context, clazz: Class<Any>) {

        val intent = Intent(context, clazz)
        // start your next activity
        context.startActivity(intent)

    }
}