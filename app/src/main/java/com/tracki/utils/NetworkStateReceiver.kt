package com.tracki.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent



class NetworkStateReceiver : BroadcastReceiver {
    var listeners: MutableSet<NetworkStateReceiverListener> = HashSet()
    var connected: Boolean? = null
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent == null || intent.extras == null)
            return
//        val manager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val ni = manager.activeNetworkInfo
//
//        if (ni != null && ni.isConnected) {
//            connected = true
//        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, java.lang.Boolean.FALSE)) {
//            connected = false
//        }
        connected=NetworkUtils.isNetworkConnected(context!!)
//        if(connected==true){
//            connected=NetworkUtils.isOnline()
//        }
        notifyStateToAll()

    }

//    interface NetworkStateReceiverListener {
//        @JvmDefault
//        fun networkAvailable()
//
//        @JvmDefault
//        fun networkUnavailable()
//    }

    private fun notifyStateToAll() {
        for (listener in listeners)
            notifyState(listener)
    }

    private fun notifyState(listener: NetworkStateReceiverListener?) {
        if (connected == null || listener == null)
            return

        if (connected == true)
            listener.networkAvailable()
        else
            listener.networkUnavailable()
    }

    fun addListener(l: NetworkStateReceiverListener) {
        listeners.add(l)
        notifyState(l)
    }

    fun removeListener(l: NetworkStateReceiverListener) {
        listeners.remove(l)
    }

    constructor() {
        connected = null
    }
}