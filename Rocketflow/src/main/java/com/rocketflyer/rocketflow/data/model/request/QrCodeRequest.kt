package com.rocketflyer.rocketflow.data.model.request


/**
 * Created by Vikas Kesharvani on 20/10/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class QrCodeRequest {
    var taskId: String? = null
    var from: String? = null
    var categoryId: String? = null
    var allowSubTask: Boolean = false
    var subTaskCategoryId: ArrayList<String>?=null
    var referenceId: String? = null
    var fromDate: Long = 0
    var toDate: Long = 0
}