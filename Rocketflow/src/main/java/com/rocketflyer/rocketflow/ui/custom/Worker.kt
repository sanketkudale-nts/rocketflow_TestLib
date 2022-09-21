package com.rocketflyer.rocketflow.ui.custom

import android.net.ParseException
import android.os.Handler
import android.os.Message
import com.bumptech.glide.load.HttpException
import com.google.gson.Gson
import com.tracki.data.model.response.config.Api
import com.tracki.data.model.response.config.FileUrlsResponse
import com.tracki.data.network.APIError
import com.tracki.data.network.HttpManager
import com.tracki.utils.CommonUtils
import com.tracki.utils.Log
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.Callable


/**
 * Created by Rahul Abrol on 26/7/19.
 *
 * Class works as a thread with custom implementation to hit
 * api and get the response.
 */
internal class Worker(private val key: String,
                      private val files: List<File>,
                      private val httpManager: HttpManager,
                      private val api: Api,
                      private val mHandler: Handler?
) : Callable<FileUrlsResponse> {

    override fun call(): FileUrlsResponse {
        var message: Message? = null
        var fileResponse: FileUrlsResponse? = FileUrlsResponse()
        try {
            val hashMapFileRequest = HashMap<String, List<File>>()
            hashMapFileRequest[key] = files
            val response = httpManager.uploadMultipleFiles(api, hashMapFileRequest)
            //add the response
            fileResponse = Gson().fromJson(response, FileUrlsResponse::class.java)
            if (fileResponse.successful) {
                //update the data after getting response
                CommonUtils.updateData(fileResponse)
            } else {
                Log.e("Worker", "call(): response not success")
                message = mHandler?.obtainMessage(1, APIError.ErrorType.SERVER_DOWN.name)
            }
        } catch (var5: SocketTimeoutException) {
            Log.e("Worker", "call(): $var5")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.TIMEOUT.name)
        } catch (var6: SocketException) {
            Log.e("Worker", "call(): $var6")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.NETWORK_FAIL.name)
        } catch (var6: UnknownHostException) {
            Log.e("Worker", "call(): $var6")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.NETWORK_FAIL.name)
        } catch (var6: UnsupportedEncodingException) {
            Log.e("Worker", "call(): $var6")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.NETWORK_FAIL.name)
        } catch (e: ParseException) {
            Log.e("Worker", "call(): $e")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.PARSE_FAILED.name)
        } catch (ex: InterruptedException) {
            Log.e("Worker", "call():Thread got Interrupted: $ex")
        } catch (e: Exception) {
            Log.e("Worker", "call(): $e")
            message = mHandler?.obtainMessage(1, APIError.ErrorType.UNKNOWN_ERROR.name)
        }
        message?.sendToTarget()
        return fileResponse!!
    }
}