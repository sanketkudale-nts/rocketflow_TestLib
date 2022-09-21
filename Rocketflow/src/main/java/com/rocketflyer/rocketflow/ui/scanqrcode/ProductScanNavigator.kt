package com.rocketflyer.rocketflow.ui.scanqrcode

import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.ui.base.BaseNavigator

interface ProductScanNavigator : BaseNavigator {

    fun handleQrCodeResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleProductDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?,pid:String?)
}