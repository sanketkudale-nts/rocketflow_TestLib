package com.rocketflyer.rocketflow.data.local.db//package com.tracki.data.local.db
//
//import android.arch.persistence.room.ColumnInfo
//import android.arch.persistence.room.Entity
//import android.arch.persistence.room.PrimaryKey
//import com.tracki.utils.AppConstants
//import com.tracki.utils.DateTimeUtil
//
///**
// * Class is used as an entity to store actions like start,end,cancel,reject
// * accept task & reject invitation.
// *
// * Created by rahul on 4/4/19
// */
//const val TABLE_NAME="task_sync"
//@Entity(tableName = TABLE_NAME)
//data class Actions(
//        @ColumnInfo(name = "action_name") val actionName: Action,
//        @ColumnInfo(name = "action_time") val actionTime: Long,
//        @ColumnInfo(name = "request_param") val requestParam: Any?,
//        @ColumnInfo(name = "is_synced") val isSynced: Int?
//) {
//    @PrimaryKey(autoGenerate = true)
//    var id: Int = 0
//    @ColumnInfo(name = "created_at")
//    val createdAt: Long = DateTimeUtil.getCurrentDateInMillis()
//
//}