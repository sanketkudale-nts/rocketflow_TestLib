package com.tracki.ui.custom

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import com.tracki.TrackiApplication
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.FileUrlsResponse
import com.tracki.data.model.response.config.HandlerObject
import com.tracki.data.network.HttpManager
import com.tracki.utils.ApiType
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import java.io.File
import java.util.*
import java.util.concurrent.*

class ExecutorThread : HandlerThread(ExecutorThread::class.java.name) {
    private var hashMapFileRequest: HashMap<String, ArrayList<File>>? = null
    private var httpManager: HttpManager? = null
    private var isException = false
    private var api: Api? = null


    override fun run() {
        //start executor service
        val executorService = Executors.newFixedThreadPool(5)
        try {
            //create a thread for communication with main thread
            val mHandler = WorkerHandler(executorService)

            if (api == null) {
                api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
            }

            val futureList = ArrayList<Future<FileUrlsResponse>>()
           var numberOfFiles=0
            for(entries in hashMapFileRequest?.entries!!){
                numberOfFiles += entries.value.size
            }
            for (entryMap in hashMapFileRequest?.entries!!) {
                val key = entryMap.key
                //create a batch list
                //change batch size 2 to 1
                val chunkedList = CommonUtils.chunkArrayList(entryMap.value, 1)
                Log.e("ExecutorThread","File Size =>"+chunkedList.size)
                var jsonConverter=JSONConverter<ArrayList<List<File>>>()
                var fileStr=jsonConverter.objectToJson(chunkedList)
                  Log.e("ExecutorThread",fileStr)
                for (j in chunkedList.indices) {
                    val worker = Worker(key, chunkedList[j], httpManager!!, api!!, mHandler)
                    //start the service by submit callable
                    val future = executorService.submit(worker)
                    // add a future instance of each callable
                    // response to wait for thread to complete
                    futureList.add(future)
                    val msg = mDynamicHandler.obtainMessage()
                    msg?.what = 2
                    var objects=HandlerObject()
                    objects.chunkSize=chunkedList[j].size
                    objects.totalSize=numberOfFiles
                    msg?.obj = objects
                    mDynamicHandler.sendMessage(msg)
                    sleep(2000)
                }
            }
            isException = false
            //wait here until all the threads got completed
            while (futureList.size > 0) {
                val iterateValue = futureList.iterator()
                //if list contains the next item
                while (iterateValue.hasNext()) {
                    try {
                        val item = iterateValue.next()
                        //check if task is done
                        if (item.isDone) {
                            //remove after task completion
                            iterateValue.remove()
                        }
                    } catch (e: NoSuchElementException) {
                        e.printStackTrace()
                    }
                }
                sleep(500)
            }
            //stop executor after completion
            executorService.shutdown()
            val msg = mDynamicHandler.obtainMessage()
            msg?.what = 0
            msg?.obj = "Please try again !!"
            //return if there is any error in api
            if (isException) {
                msg?.what = 1
                mDynamicHandler.sendMessage(msg)
                return
            }
            mDynamicHandler.sendMessage(msg)
        } catch (e: InterruptedException) {
            //stop
            executorService.shutdown()
            executorService.awaitTermination(1, TimeUnit.NANOSECONDS)
            try {
                //stop handler
                mDynamicHandler.removeMessages(1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            e.printStackTrace()
        }
    }

    private lateinit var mDynamicHandler: Handler

    fun setRequestParams(mDynamicHandler: Handler,
                         hashMapFileRequest: HashMap<String, ArrayList<File>>,
                         httpManager: HttpManager,
                         api: Api?) {
        this.mDynamicHandler = mDynamicHandler
        this.hashMapFileRequest = hashMapFileRequest
        this.httpManager = httpManager
        this.api = api
    }

    inner class WorkerHandler(private val executorService: ExecutorService) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            isException = true
            //stop execution if there is any error in the api
            executorService.shutdownNow()
            executorService.shutdown()
            //make map empty again
            CommonUtils.stringListHashMap = ConcurrentHashMap()
            val message = obtainMessage()
            message.what = msg?.what!!
            message.obj = msg.obj
            //notify main thread about the error
            mDynamicHandler.sendMessage(message)
            //return from the function
            return
        }
    }
}
