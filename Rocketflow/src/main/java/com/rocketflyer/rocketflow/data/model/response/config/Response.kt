package com.rocketflyer.rocketflow.data.model.response.config

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Parcelable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.request.RequestOptions
import com.tracki.R
import com.tracki.data.model.BaseResponse
import com.tracki.data.model.NotificationResponse
import com.tracki.data.model.request.AddEmployeeRequest
import com.tracki.data.model.request.AddressInfo
import com.tracki.data.model.request.Contact
import com.tracki.data.model.request.Shift
import com.tracki.ui.attendance.punchInOut.PunchData
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.products.PackInfo
import com.tracki.ui.taskdetails.StageHistoryData
import com.tracki.ui.tasklisting.PagingData
import com.tracki.utils.*
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by rahul on 28/11/18
 */
class AssigneeDetail : Serializable {
    var acceptOrRejectAt = 0L
    var buddyId: String? = null
    var location: Location? = null
    var mobile: String? = null
    var email: String? = null
    var name: String? = null
    var profileImage: String? = null
    var shift: Shift? = null
    var shortCode: String? = null
    var currentSpeed: Int? = null
    var dateOfBirth: Long? = null
    var createdAt: Long? = null
    var status: String? = null

    override fun toString(): String {
        return " { " +
                "acceptOrRejectAt: " + acceptOrRejectAt +
                " buddyId: " + buddyId +
                " location: " + location +
                " mobile: " + mobile +
                " email: " + email +
                " name: " + name +
                " profileImage: " + profileImage +
                " shift: " + shift +
                " shortCode: " + shortCode +
                " status: " + status +
                " } "
    }

}

class Place : Serializable {
    var address: String? = null
    var regionId: String? = null
    var hubId: String? = null
    var stateId: String? = null
    var cityId: String? = null
    var manualLocation: Boolean = false
    var location: GeoCoordinates? = null
}

data class AttendanceStatusData(var status: String? = null, var count: Int = 0) {
    var isSelected: Boolean = false
}

class GetUserManualLocationData() {
    var data: HashMap<String, String>? = null
}

@Parcelize
class LocData() : Parcelable {
    var name: String? = null
    var id: String? = null
    var isSelected = false
    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val locData: LocData = obj as LocData
        return locData.id == this.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}

@Parcelize
class FilterCategoryData() : Parcelable {
    var name: String? = null
    var id: String? = null
    var isSelected = false
    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val locData: LocData = obj as LocData
        return locData.id == this.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}

class Location : Serializable {
    var capturedAt = 0L
    var geoCoordinates: GeoCoordinates? = null
    var speed = 0

    override fun toString(): String {
        return " { " +
                "capturedAt: " + capturedAt +
                " geoCoordinates: " + geoCoordinates +
                " speed: " + speed +
                " } "
    }
}

class GeoCoordinates : Serializable {
    var latitude = 0.0
    var longitude = 0.0
    var placeId: String? = null

    override fun toString(): String {
        return "{ " +
                "latitude: " + latitude +
                " longitude: " + longitude +
                " placeId: " + placeId +
                " }"
    }
}

class TaggedDocument : Serializable {
    var docUrl: String? = null
    var docuemntType: String? = null
}

class TripData : Serializable {
    var events: MutableList<Event>? = null
    var googleActivities: List<GoogleActivity>? = null
    var locations: List<Location>? = null
}

class Event : Serializable {
    var eventEndTime = 0L
    var eventName: String? = null
    var eventStartTime = 0L
    var eventTime = 0L
    var from: GeoCoordinates? = null
    var speed = 0
    var to: GeoCoordinates? = null
    var tripId: String? = null
}

class GoogleActivity : Serializable {
    var activityType: String? = null
    var confidence = 0
}

data class PaymentData(
        var amountBreakup: AmountBreakup? = null,
        var event: String? = null,
        var mode: Any? = null,
        var paymentId: String? = null,
        var retry: Int? = null,
        var status: String? = null,
        var taskId: String? = null
) : Serializable



data class PayoutHistoryResponse(
        var `data`: List<PayoutData>? = null,
        var successful: Boolean? = null
)

data class PayoutData(

        var dataId: String? = null,
        var executedDate: String? = null,
        var executionDate: Long? = null,
        var executiveId: String? = null,
        var merchantId: String? = null,
        var payoutBreakup: PayoutBreakup? = null,
        var payoutPlan: PayoutPlan? = null,
        var payoutRefId: String? = null,
        var status: String? = null,
        var taskDetail: Task? = null,
        var taskId: String? = null
)

data class InventoryDetails(
        var categoryId: String? = null,
        var categoryName: String? = null,
        var gourpId: String? = null,
        var groupName: String? = null,
        var inventoryId: String? = null,
        var manufacturer: Any? = null,
        var referenceId: String? = null
) : Serializable

data class PayoutBreakup(
        var baseAmountBreakup: BaseAmountBreakup? = null,
        var totalPayout: Double? = null

) : Serializable {
    var basefare = 0.0
    var nightCharge = 0.0
    var waitingCharge = 0.0
    var overtimeCharge: Double = 0.0
    var extraKmCharge: Double = 0.0
}

data class PayoutPlan(

        var name: String? = null,
        var payoutConfig: PayoutConfig? = null,
        var payoutType: String? = null,
        var planId: String? = null,
        var status: String? = null,
        var version: Int? = null
) : Serializable


data class BaseAmountBreakup(
        var amt: Double? = null,
        var taxBreakup: TaxBreakup? = null,
        var totalAmt: Double? = null
) : Serializable


data class PayoutConfig(
        var value: Double? = null
) : Serializable

data class TaxBreakup(
        var cgstAmt: Double? = null,
        var cgstPer: Double? = null,
        var igstAmt: Double? = null,
        var igstPer: Double? = null,
        var sgstAmt: Double? = null,
        var sgstPer: Double? = null,
        var totalTaxAmt: Double? = null
) : Serializable

enum class TrackingState {
    DISABLED, ENABLED
}

class Task : BaseResponse(), Serializable {
    var autoCreated = false
    var autoCancel = false
    var fleetId: String? = null
    var tcfId: String? = null
    var categoryId: String? = null
    var assigneeDetail: AssigneeDetail? = null
    var buddyDetail: AssigneeDetail? = null
    var requestedUser: AssigneeDetail? = null
    var description: String? = null
    var destination: Place? = null
    var endTime = 0L
    var source: Place? = null
    var startTime = 0L
    var status: TaskStatus? = null
    var taggedDocuments: List<TaggedDocument>? = null
    var taskName: String? = null
    var clientTaskId: String? = null
    var referenceId: String? = null
    var tripData: TripData? = null
    var createdAt = 0L
    var contact: Contact? = null
    var acceptOrRejectAt = 0L
    var tripStatistics: TripsStatistics? = null
    var fleetDetail: FleetDetail? = null
    var taskData: List<TrackiTaskData>? = null
    var taskType: String? = null
    var taskStateUpdated = false
    var payoutEligible = false
    var allowSubTask = false
    var trackingState: TrackingState? = null
    var driverPayoutBreakUps: PayoutBreakup? = null
    var taskstats: TripsStatistics? = null
    var subCategoryIds: ArrayList<String>? = null

    //    var startTaskData: List<DynamicFormData>? = null
//    var endTaskData: List<DynamicFormData>? = null
    var stageHistory: List<StageHistoryData>? = null
    var assignedToDetails: List<AssigneeDetail?>? = null
    var assignmentType: String? = null
    var currentStage: Stage? = null
    var paymentData: PaymentData? = null
    var stageUpdatedAt = 0L
    var requestCode = 0
    var userGroup: UserGroup? = null
    var trackable: Boolean = false
    var invDetails: InventoryDetails? = null
  /*  var orderDetails: HashMap<String, OrderDetails>? = null*/
    var orderDetails:ArrayList<OrderDetails>? = null
    var products:ArrayList<ProductOrder>? = null

    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val task: Task = obj as Task
        return task.taskId == this.taskId
    }

    override fun hashCode(): Int {
        return this.taskId.hashCode()
    }

    override fun toString(): String {
        return "{ " +
                "autoCreated: " + autoCreated +
                " taskId: " + taskId +
                " fleetId: " + fleetId +
                " assigneeDetail: " + assigneeDetail +
                " buddyDetail: " + buddyDetail +
                " description: " + description +
                " destination: " + destination +
                " endTime: " + endTime +
                " source: " + source +
                " startTime: " + startTime +
                " status: " + status +
                " taggedDocuments: " + taggedDocuments +
                " taskName: " + taskName +
                " tripData: " + tripData +
                " createdAt: " + createdAt +
                " contact: " + contact +
                " acceptOrRejectAt: " + acceptOrRejectAt +
                " tripStatistics: " + tripStatistics +
                " fleetDetail: " + fleetDetail +
                " taskData: " + taskData +
//                " startTaskData: " + startTaskData +
//                " endTaskData: " + endTaskData +
                " taskType: " + taskType +
                " stage: " + currentStage +
                " stageUpdatedAt: " + stageUpdatedAt +
                " requestCode: " + requestCode +
                " }"
    }


}
data class ProductMap(
    var key: String?,
    var value: String?
)

data class FeedDfDataMap(
    var key: String?,
    var value: String?,
    var type: DataType?
)
data class DfDataMain(
    var df_data: ArrayList<FeedDfDataMap>?
)
data class ProductOrder(
    var active: Boolean?,
    var attList: Any?,
    var brandId: String?,
    var brandList: Any?,
    var categoryId: String?,
    var categoryMap: Any?,
    var categoryName: String?,
    var description: Any?,
    var dfdId: String?,
    var enableMultipleVariant: Boolean?,
    var enableUnitIdentity: Boolean?,
    var flavor: Any?,
    var flavorId: String?,
    var lastUpdated: Any?,
    var mappingList: Any?,
    var maxOrderLimit: Int?,
    var merchantId: String?,
    var minStockQuantity: Int?,
    var picture: ArrayList<String>?,
    var pid: String?,
    var price: Double?,
    var productName: String?,
    var productStock: ProductStock?,
    var sellingPrice: Double?,
    var specifications: Any?,
    var unitInfo: Any?,
    var units: Any?,
    var prodInfoMap: Map<String,String>?,
    var upcNumber: String?
): Serializable
data class ProductStock(
    var minValue: Int?,
    var raiseAlert: Boolean?,
    var value: Int?
): Serializable
data class OrderDetails(
    var amountBreakup: AmountBreakup?,
    var currentStageName: String?,
    var customerAddress: String?,
    var customerEmail: String?,
    var customerMobile: String?,
    var approver: String?,
    var customerName: String?,
    var date: String?,
    var items: Int?,
    var orderId: String?,
    var status: OrderStatus?,
    var orderItemsInfo: ArrayList<OrderItemsInfo>?,
    var refId: String?,
    var storeName: String?,
    var totalAmt: Double?
): Serializable
/*data class AmountBreakup(
        var amt: Double? = null,
        var taxBreakup: TaxBreakup? = null,
        var totalAmt: Double? = null
) : Serializable*/
enum class OrderStatus{
    PENDING,CONFIRMED,CANCELLED,CLOSED
}
data class AmountBreakup(
    var amt: Double?,
    var discountInfo: String?,
    var payableAmt: Double?,
    var totalAmt: Double? = null,
    var taxBreakup: TaxBreakupOrder?
): Serializable

data class OrderItemsInfo(
    var img: String?,
    var orderItemId: String?,
    var price: Double?,
    var productName: String?,
    var quantity: Int?,
    var totalAmt: Double?,
    var unitInfo: UnitInfo?
): Serializable

data class UnitInfo(
    var packInfo: PackInfo?,
    var quantity: Int?,
    var type: String?
)

data class PackInfo(
    var from: Int?,
    var to: Int?,
    var unitType: String?
)
/*
data class OrderDetails(
        var amountBrkup: AmountBrkup? = null,
        var approver: String? = null,
        var approverGroups: Any? = null,
        var createdBy: String? = null,
        var fleetDetails: List<OrderData>? = null,
        var orderAt: String? = null,
        var orderBy: Any? = null,
        var stage: String? = null,
        var status: String? = null,
        var subTaskApproval: Boolean? = null,
        var totalItem: Int? = null,
        var totlaAmount: Int? = null
) : Serializable
*/


data class OrderData(
        var approvedBy: String? = null,
        var categoryId: String? = null,
        var dfdId: String? = null,
        var fleetId: String? = null,
        var fleetImg: String? = null,
        var groupPrice: Int? = null,
        var inventoryGroupId: String? = null,
        var inventoryId: String? = null,
        var orderId: String? = null,
        var price: Int? = null,
        var quantity: Int? = null,
        var status: String? = null,
        var year: Int? = null
) : Serializable

data class TaxBreakupOrder(
        var cgstAmt: Double? = null,
        var cgstPer: Double? = null,
        var igstAmt: Double? = null,
        var igstPer: Double? = null,
        var sgstAmt: Double? = null,
        var sgstPer: Double? = null,
        var totalTaxAmt: Double? = null
) : Serializable

data class UserGroup(
        var authorised: Boolean? = null,
        var functionalities: List<String>? = null,
        var groupId: String? = null,
        var groupName: String? = null,
        var groupType: String? = null,
        var status: String? = null,
        var users: List<String>? = null
) : Serializable

data class SubTaskConfig(
        var categories: List<String>? = null,
        var label: String? = null
)

class Stage {
    var name: String? = null
    var initial: Boolean? = null
    var terminal: Boolean? = null

    var callToActions: List<CallToActions>? = null

    override fun toString(): String {
        return "{  " +
                "name: " + name +
                ", callToActions: " + callToActions +
                " }"
    }
}

data class GeoFenceData(
        var circleData: CircleData? = null,
        var geofenceId: String? = null,
        var geofenceName: String? = null,
        var geofenceType: String? = null
)
//data class GeoFenceData(
//        var circleData: CircleData? = null,
//        var geofenceId: String? = null,
//        var geofenceName: String? = null,
//        var geofenceType: String? = null
//)

data class CircleData(
        var geoCoordinates: List<GeoCoordinate>? = null,
        var radius: Float = 0f
)

data class GeoCoordinate(
        var latitude: Double? = null,
        var locationId: String? = null,
        var longitude: Double? = null
)

class CallToActions {

    var id: String? = null
    var name: String? = null
    var nextStageId: String? = null
    var primary = false
    var conditions: List<Condition>? = null
    var actions: Any? = null
    var tracking = Tracking.NO_CHANGE //used for start tracking or stop tracking.
    var executor: Executor? = null
    var dynamicFormId: String? = null
    var properties: Map<String, String>? = null
    var isEditable: Boolean = false
    var dynamicFormEditable: Boolean = false
    var categoryId: String? = null
    var createTask: Boolean = false
    var targetInfo: TargetInfo? = null
    override fun toString(): String {
        return "{  " +
                "id: " + id +
                ", name: " + name +
                ", nextStageId: " + nextStageId +
                ", actions: " + actions +
                ", tracking: " + tracking +
                ", primary: " + primary +
                ", conditions: " + conditions +
                ", executor: " + executor +
                ", dynamicFormId: " + dynamicFormId +
                ", properties: " + properties +
                ", properties: " + dynamicFormEditable +
                " }"
    }
}

class TargetInfo {
    var target: TRAGETINFO? = null
    var category: String? = null
    var targetInfo: String? = null
    var targetValues: ArrayList<String>? = null
}


class CtaInventoryConfig(var dynamicPricing:Boolean?=null,var invAction:String?=null)

enum class TRAGETINFO {
    TAG_INVENTORY, CREATE_TASK,NONE,ASSIGN_EXECUTIVE
}

enum class Tracking {
    START, END, NO_CHANGE
}

enum class Executor {
    SYSTEM, USER
}

class Condition {
    var isPassed = false
    var level: String? = null
    var type: String? = null
    var properties: Map<String, String>? = null

    override fun toString(): String {
        return "{  " +
                "level: " + level +
                ", type: " + type +
                ", properties: " + properties +
                " }"
    }
}



class TrackiTaskData : Serializable {
    var action: TaskAction? = null
    var ctaId: String? = null
    var taskData: List<DynamicFormData>? = null
    var uploadedAt = 0L
}

enum class TaskAction {
    START, SOURCE_GEOFENCE_IN, DESTINATION_GEOFENCE_IN, END, UNKNOWN
}

class DynamicFormData : Serializable {
    var type: DataType? = null
    var key: String? = null
    var value: String? = null
    var embeddedDf: Boolean? = false
    var embeddedDfId: String? = null
    var weight: Double = 0.0
    var label: String? = null
    var includeForCalculation: Boolean = false
    var operation: String? = null
    var errorMessage: String? = null
    override fun toString(): String {
        return "{" +
                " type: " + type +
                ", key: " + key +
                ", value: " + value +
                " }"
    }
}

class FleetDetail : Serializable {
    var fleetId: String? = null
    var fleetImg: String? = null
    var fleetName: String? = null
    var regNumber: String? = null
    var inventoryId: String? = null
}

class TaskListing(var tasks: List<Task>? = null, var paginationData: PagingData? = null) : BaseResponse(), Serializable

class TripsStatistics : Serializable {
    var events: HashMap<String, Int>? = null
    var tripId: String? = null
    var tripScore = 0
    var maxSpeed = 0
    val minSpeed = 0
    val avgSpeed = 0f
    var tripDurationInMinute = 0
    var distanceInMeter = 0.0
    var distance = 0.0
    var tripDuration = 0.0
    val waitingtime = 0.0
    val bookingTime = 0L
}

class TaskResponse : BaseResponse(), Serializable {
    var taskDetail: Task? = null
}

class BuddyListResponse : BaseResponse(), Serializable {
    var buddies: List<Buddy>? = null
    var notifications = false
    var invites = false
}

class TimeBreakup : Serializable {
    var hour = 0
    var min = 0
}

class DashboardData : Serializable {
    var alerts = 0
    var avgSpeed = 0f
    var kms = 0f
    var maxSpeed = 0
    var timeBreakup: TimeBreakup? = null
    var trips = 0
}

class DashboardResponse : BaseResponse(), Serializable {
    var data: DashboardData? = null
}

class NotificationListResponse : BaseResponse(), Serializable {
    var notifications: List<NotificationResponse>? = null
}

class Buddy : Serializable {

    var isLoggedIn = false
    var buddyId: String? = null
    var name: String? = null
    var email: String? = null
    var location: Loc? = null
    var profileImage: String? = null
    var shortCode: String? = null
    var mobile: String? = null
    var createdAt = 0L
    var shift: Shift? = null

    var status: BuddyStatus? = null
    var isActive = false
    var isSelected = false
    var showSelected = false

    //fields for chat messages
    var lastMessage: String? = null
    var type: String? = null
    var roomId: String? = null
    var chatUserStatus: ChatUserStatus? = null
    var lastMsgTime = 0L
    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val buddy: Buddy = obj as Buddy
        return buddy.buddyId == this.buddyId
    }

    override fun hashCode(): Int {
        return this.buddyId.hashCode()
    }
}

class Loc : Serializable {
    var address: String? = null
    var location: GeoCoordinates? = null
}

class FleetListResponse : BaseResponse(), Serializable {
    var fleets: List<Fleet>? = null
}

class Fleet : Serializable {
    var fleetId: String? = null
    var fleetImg: String? = null

    var fleetName: String? = null
    var regNumber: String? = null

    //
    var invId: String? = null
    var categoryId: String? = null
    var invName: String? = null
    var inventoryGroupId: String? = null
    var refId: String? = null
    var groupName: String? = null
    var categoryName: String? = null
    var invImg: String? = null

    var status: String? = null
    var isActive: Boolean? = null
    var isSelected: Boolean? = null
    var showSelected: Boolean? = null
}

class ProfileResponse : BaseResponse(), Serializable {
    var profileInfo: ProfileInfo? = null
    var imageUrl: String? = null
}


class SignUpResponse : BaseResponse() {
    var draftId: String? = null
}

class OtpResponse : BaseResponse() {
    var profileDetail: ProfileInfo? = null
    var shifts: Map<Int, List<ShiftTime>>? = null
    var hubLocations: List<HubLocation>? = null
    var userTypes: List<UserType>? = null
}


class HubLocation {
    var location: GeoLocation? = null
    var radius: Int = 100
    var hubName: String? = null
    var hubId: String? = null
    var isSelected: Boolean = false
}

class GeoLocation {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var locationId: String? = null
}

class FileUrlsResponse : BaseResponse() {
    val filesUrl: HashMap<String, ArrayList<String>>? = null
}

class SettingResponse : BaseResponse() {
    var settings: Settings? = null
}

class Settings {
    var alertNotification: Boolean? = null
    var voiceAlerts: Boolean? = null
    var autoStart: Boolean? = null
    var emergencyContacts: ArrayList<EmergencyContact>? = null
    var sosMessage: String? = null
}

class EmergencyContact(name: String, mobile: String) {
    var mobile: String? = mobile
    var name: String? = name
}

//class MessageResponse : BaseResponse() {
//    var lastMessage: String? = null
//    var name: String? = null
//    var imageUrl: String? = null
//    var status: BuddyStatus? = null
//    var unReadCount = 0
//    var time: Long = 0L
//}

class ChatResponse : BaseResponse() {
    var isMe: Boolean? = null
    var isLoadMore: Boolean? = null
    var message: String? = null
    var name: String? = null
    var imageUrl: String? = null
    var status: BuddyStatus? = null
    var type: String? = null

}

class MyEarningResponse : BaseResponse() {
    var earnings: List<MyEarning>? = null
}

class MyEarning {
    var totalRide = 0
    var date = 0L
    var totalAmount = 0.0
}


class AttendanceResponse : BaseResponse() {
    var leave: List<LeaveHistoryData>? = null
    var data: List<Data>? = null
    var absent: List<Data>? = null
    var present: List<Data>? = null
    var dayOff: List<Data>? = null
    var dataCount: Int = 0

//    var dataCountMap: AttendanceCountMap? = null
//    var percentage: Float = 0F
//    var workingDays: Int = 0
}

class AttendanceCountResponse : BaseResponse() {

    var data: AttendanceCountMap? = null
    var percentage: Float = 0F
    var workingDays: Int = 0
}

data class Data(var date: Long = 0L) {
    var status: AttendanceStatus? = null
    var hours: String? = null
    var punchInAt: Long? = null
    var punchOutAt: Long? = null
    val punchInData: PunchAttendanceData? = null
    val punchOutData: PunchAttendanceData? = null
    var from = 0L
    var to = 0L
    var leaveType: LeaveType? = null
    var dayKeys: List<String>? = null
    var remarks: String? = null

}

data class PunchAttendanceData(
        val `data`: Remarks,
        val location: LocationData? = null,
        val punchedBy: String,
        val source: String
)

data class Remarks(
        val remarks: String
)

@Parcelize
data class PunchLocationData(
    val address: String?,
    val location: PunchGeoCoordinates?
):Parcelable

@Parcelize
class PunchGeoCoordinates :Parcelable {
    var latitude = 0.0
    var longitude = 0.0
    var locationId: String? = null


}


data class LocationData(
        val address: String?,
        val location: GeoCoordinates?
)

class AttendanceCountMap {
    var NOT_UPDATED: Int = 0
    var PRESENT: Int = 0
    var HOLIDAY: Int = 0
    var ON_LEAVE: Int = 0
    var DAY_OFF: Int = 0
    var ABSENT: Int = 0

}

enum class AttendanceStatus {
    ALL, PRESENT, ABSENT, ON_LEAVE, HOLIDAY, DAY_OFF, NOT_UPDATED
}

class LeaveSummaryResponse : BaseResponse() {

    var summary: LeaveSummary? = null
}

class LeaveSummary {

    var parentMerchantId: String? = null
    var userId: String? = null
    var summary: List<Summary>? = null
}

class Summary : Serializable {
    var type: LeaveType? = null
    var remaing = 0
    var allowed = 0
    var taken = 0

}


enum class LeaveType {
    PRIVILEGE_LEAVE, SICK_LEAVE, CASUAL_LEAVE
}


data class LeaveCount(
        val APPROVED: Int,
        val CANCELLED: Int,
        val PENDING: Int,
        val REJECTED: Int
)

class LeaveHistory : BaseResponse() {
    var history: List<LeaveHistoryData>? = null
    var leaveMap: LeaveCount? = null
}

class LeaveHistoryData {
    var isExpandable = false
    var lrId: String? = null
    var from = 0L
    var to = 0L
    var leaveType: LeaveType? = null
    var status: LeaveStatus? = null
    var approvedOrRejectedBy: String? = null
    var approverComment: String? = null
    var approvedOrRejectAt = 0L
    var dayKeys: List<String>? = null
    var cancelledAt = 0L
    var leaveApprovers: List<LeaveApprovers>? = null
    var approvedBy: LeaveApprovers? = null
    var remarks: String? = null
    var appliedOn = 0L
    var itemPos = 0


}

class AppliedByHistory {

    var email: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var mobile: String? = null
    var userId: String? = null
}


class ApprovedByHistory {

    var email: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var mobile: String? = null
    var userId: String? = null
}

class LeaveApproversHistory {

    var email: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var mobile: String? = null
    var userId: String? = null
}


enum class LeaveStatus {
    ALL, PENDING, APPROVED, REJECTED, CANCELLED, PRESENT, ABSENT, DAY_OFF, HOLIDAY, ON_LEAVE,
    NOT_UPDATED
}


class AppliedByApproval {

    val email: String? = null
    val firstName: String? = null
    val lastName: String? = null
    val mobile: String? = null
    val userId: String? = null
}

class ApprovedByApproval {

    val email: String? = null
    val firstName: String? = null
    val lastName: String? = null
    val mobile: String? = null
    val userId: String? = null
}

class LeaveApproval : BaseResponse() {

    var leaveRequests: List<LeaveApprovalData>? = null
}

class LeaveApprovers {

    val email: String? = null
    val firstName: String? = null
    val lastName: String? = null
    val mobile: String? = null
    val userId: String? = null
}

class LeaveApprovalData {

    var isExpandable: Boolean = false

    // var appliedBy: AppliedByApproval ?= null
    var appliedBy: AppliedBy? = null
    var approvedBy: ApprovedByApproval? = null
    var approvedOrRejectAt: Long = 0L
    var approvedOrRejectedBy: String? = null
    var approverIds: List<String>? = null
    var approvers: List<String>? = null
    var cancellationReason: String? = null
    var cancelledAt: Long? = 0L
    var from: Long? = 0L
    var dayKeys: List<String>? = null
    var leaveApprovers: List<LeaveApprovers>? = null
    var leaveType: LeaveType? = null
    var leaveNum: Int = 0
    var lrId: String? = null
    var remarks: String? = null
    var status: LeaveApprovalStatus? = null
    var to: Long = 0L
    var appliedOn: Long? = 0L
}

class AppliedBy {
    var userId: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var mobile: String? = null
    var profileImg: String? = null
}
//class LeaveApproval: BaseResponse(){
//    var data : List<LeaveApprovalData>?=null
//
//}

//class LeaveApprovalData{
//    var isExpandable: Boolean = false
//    var appliedBy: AppliedBy ?= null
//    var approvedBy: ApprovedBy?= null
//    var approvedOrRejectAt: String?= null
//    var approvedOrRejectedBy: String?= null
//    var approverIds: List<String>?= null
//    var approvers: List<String>?= null
//    var cancellationReason: String?= null
//    var cancelledAt: Long?= null
//    var from: String?= null
//    var leaveApprovers: List<LeaveApprovers>?= null
//    var leaveType: LeaveType?= null
//    var lrId: String?= null
//    var remarks: String?= null
//    var status: LeaveApprovalStatus?= null
//    var to: String?= null
//    var leaveNum: String?= null
//    var applyDate : String?=null
//    var comment : String? = null
//
//}

enum class LeaveApprovalStatus {
    ALL, PENDING, APPROVED, REJECTED, CANCELLED
}


class EditData {
    var leaveType: String? = null
    var leaveFrom = 0L
    var leaveTo = 0L
    var remarks: String? = null
    var lrId: String? = null
    var isEdit = false
    var itemPos = 0

}

data class AttendanceMap(
        var ABSENT: Int = 0,
        var ALL: Int = 0,
        var DAY_OFF: Int = 0,
        var HOLIDAY: Int = 0,
        var NOT_UPDATED: Int = 0,
        var ON_LEAVE: Int = 0,
        var PRESENT: Int = 0
)

data class DashBoardMapResponse(
        var taskCountMap: HashMap<String, Int>? = HashMap(),
        var attendanceStatusMap: AttendanceMap? = null
) : BaseResponse()

data class TeamAttendanceResponse(
        var taskCountMap: HashMap<String, Int>? = HashMap(),
        var attendanceStatusMap: AttendanceMap? = null
) : BaseResponse()

enum class Action {
    APPROVE, REJECT
}

class ExecutiveMap : BaseResponse() {
    // var executiveMap: Map<String, String>? = null
    var data: Map<String, String>? = null
}

class GetTaskDataResponse : BaseResponse() {

    var data: List<TaskData>? = null
    var dfdId: String? = null
    var dfId: String? = null


}

class TaskData {
    var key: String? = null
    var type: DataType? = null
    var value: String? = null
    var field: String? = null
    var label: String? = null
    var embeddedDf: Boolean? = false
    var embeddedDfId: String? = null
    var weight: Double = 0.0
    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val task: TaskData = obj as TaskData
        return task.key == this.key
    }

    override fun hashCode(): Int {
        return this.key.hashCode()
    }
}

class UploadTaskDataResponse : BaseResponse() {
    var id: String? = null
}

@Parcelize
data class Contact(var name: String, var mobileNumber: String?) : Parcelable {
    companion object {

        @JvmStatic
        @BindingAdapter("textInitials")
        fun setInitials(textView: TextView, name: String?) {

            textView.text = name!!.get(0).toUpperCase().toString()

        }

        @JvmStatic
        @BindingAdapter("changebg")
        fun setBackGround(imageView: ImageView, name: String?) {
            val rnd = Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }


    }


}

data class InventoryResponse(
        var carModelMap: HashMap<String, String>? = null,
        var categoryId: String? = null,
        var filterCategoryMapping: HashMap<String, String>? = null,
        var inventories: List<Inventory>? = null,
        var inventoriesRequest: InventoriesRequest? = null,
        var inventoryConfig: InventoryConfig? = null,
        var pageTitle: String? = null,
        var subTask: Boolean? = null
) : BaseResponse()

enum class InventoryStatus {
    INACTIVE, ACTIVE
}

class Inventory(
        var categoryId: String? = null,
        var dfdId: String? = null,
        var fleetId: String? = null,
        var fleetImg: String? = null,
        var groupPrice: Int? = null,
        var inventoryGroupId: String? = null,
        var inventoryGroupName: String? = null,
        var inventoryId: String? = null,
        var lastUpdatedAt: String? = null,
        var modelName: String? = null,
        var price: Double = 0.0,
        var quantity: Int = 0,
        var status: InventoryStatus? = null,
        var year: Int? = null,
        var addquantity: Int = 0,
        var isAdded: Boolean = false
) {

    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val task: Inventory = obj as Inventory
        return task.fleetId == this.fleetId
    }

    override fun hashCode(): Int {
        return this.fleetId.hashCode()
    }

    companion object {

        @JvmStatic
        @BindingAdapter("setprice")
        fun setMoney(textView: TextView, money: Double?) {
            if (money != null)
                textView.text = "INR $money"

        }

        @JvmStatic
        @BindingAdapter("prodstatus")
        fun setBackGround(textView: TextView, quantity: Int?) {
            if (quantity != null) {
                if (quantity > 0) {
                    textView.text = "In Stock"
                    textView.background = ContextCompat.getDrawable(textView.context, R.drawable.status_active)
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.green))

                } else {
                    textView.text = "Out Of Stock"
                    textView.background = ContextCompat.getDrawable(textView.context, R.drawable.staus_inactive)
                    textView.setTextColor(ContextCompat.getColor(textView.context, android.R.color.holo_red_dark))
                }
            }

        }

        @JvmStatic
        @BindingAdapter("active")
        fun setEnableButton(button: Button, quantity: Int?) {
            if (quantity != null) button.isEnabled = quantity > 0

        }


    }
}

data class InventoriesRequest(
        var at: Int? = null,
        var category: Any? = null,
        var categoryId: Any? = null,
        var categoryIds: List<String>? = null,
        var cid: Any? = null,
        var fleetname: Any? = null,
        var gid: Any? = null,
        var inventoryGroupId: Any? = null,
        var inventoryId: Any? = null,
        var modelId: Any? = null,
        var modelIds: List<String>? = null,
        var paginationData: PaginationData? = null,
        var regNumber: Any? = null
)

data class InventoryConfig(
        var approvalOn: ApprovalOn? = null,
        var approvalType: ApprovalType? = null,
        var approvers: List<String>? = null,
        var availabilityType: String? = null,
        var categoryId: List<String>? = null,
        var enableInv: Boolean? = null,
        var groupIds: List<String>? = null,
        var mappingOn: MappingOn? = null,
        var allowOrderDeletion: Boolean?,
        var allowOrderEditAfterPay: Boolean?,
        var allowOrderEditing: Boolean?,
        var editMode: EditMode?,
        var enableBom: Boolean?,
        var enablePayment: Boolean?,
        var flavorId: String?,
        var invAction: InvAction?,
        var stockUpdate: Boolean?,
        var multipleQty: Boolean?,
        var linkingType: TaggingType?,
        var linkOption: LinkOptions?,
        var dynamicPricing: Boolean?,
        var geoTagging: Boolean?
)
enum class LinkOptions {
    DIRECT,ORDER
}
enum class TaggingType {
    SINGLE, MULTIPLE
}
enum class MappingOn {
    CREATION, EXECUTION
}
enum class ApprovalType{
    MANUAL , AUTO
}
enum class InvAction {
    ADD, REMOVE, HOLD,IN,OUT,TRANSFER
}

enum class EditMode {
    ADD_REMOVE_NEW_INVENTORY,
    ADD_REMOVE_WITHIN_ORDER, REMOVE_WITHIN_ORDER
}

enum class ApprovalOn {
    SAME_TASK, SUB_TASK
}

@Parcelize
data class PaginationData(
        var dataCount: Int? = null,
        var datalimit: Int? = null,
        var email: String? = null,
        var mobile: String? = null,
        var pageIndex: Int? = null,
        var pageNumbers: Int? = null,
        var pageOffset: Int? = null,
        var retailname: String? = null
) : Parcelable

data class CategoryGroupResponse(
        var groupIds: HashMap<String, String>? = null
) : BaseResponse()

data class TransactionData(
        var balance: Double = 0.0,
        var blockedAmmount: Int? = null,
        var paginationData: PagingData? = null,
        var transactions: List<Transaction>? = null
) : BaseResponse()


data class Transaction(
        var afterTxnAmt: Double? = null,
        var amount: Int = 0,
        var beforeTxnAmt: Double? = null,
        var clientTaskId: String? = null,
        var date: String? = null,
        var fromDate: Int? = null,
        var paginationData: Any? = null,
        var refId: String? = null,
        var remark: String? = null,
        var status: String? = null,
//        var toDate: Int? = null,
        var transactionId: String? = null,
        var txnTypes: Any? = null,
        var userId: String? = null
) {
    companion object {

        @JvmStatic
        @BindingAdapter("moneystatus")
        fun setMoneyStatus(textView: TextView, data: Transaction?) {
            if (data != null) {
                var strSign = ""
                if (data.status.equals("CREDIT")) {
                    strSign = "+"
                } else if (data.status.equals("DEBIT")) {
                    strSign = "-"
                }
                textView.text = "${strSign}${data.amount}"
            }

        }

        @JvmStatic
        @BindingAdapter("debiticon")
        fun setDebitCreditIcon(imageView: ImageView, data: Transaction?) {
            if (data != null) {
                if (data.status.equals("CREDIT")) {
                    imageView.setImageResource(R.drawable.ic_money_credit)
                } else if (data.status.equals("DEBIT")) {
                    imageView.setImageResource(R.drawable.ic_money_debit)
                }
            }

        }
//        @JvmStatic
//        @BindingAdapter("dateformate")
//        fun setDateFormate(textView: TextView, date: String?) {
//            if (date != null) {
//            }
//
//        }


    }

}

data class PostResponse(
        var paginationData: PaginationData? = null,
        var postList: List<Post>? = null
) : BaseResponse()

enum class MEDIATYPE {
    VIDEO, IMAGE, AUDIO
}
enum class ContentType{
    DF_DATA,PLAIN_TEXT
}


data class PostDetailsResponse(
    var data:Post?=null
): BaseResponse()

data class Post(
        var commentCounts: Int = 0,
        var likeCounts: Int = 0,
        var message: String? = null,
        var postId: String? = null,
        var fileUrl: String? = null,
        var title: String? = null,
        var image: String? = null,
        var createdBy: String? = null,
        var postedAt: String? = null,
        var postedUserImage: String? = null,
        var mediaType: MEDIATYPE? = null,
        var contentType: ContentType,
        var loggedInUserLike: Boolean = false

) {
    companion object {

        @JvmStatic
        @BindingAdapter("like")
        fun setLike(textView: TextView, data: Post?) {
            if (data != null) {
                var likes = ""

                if (data.likeCounts > 0) {
                    likes = "${data.likeCounts} likes"
                } else {
                    likes = "${data.likeCounts} like"
                }
                textView.text = "${likes}"

            }

        }

        @JvmStatic
        @BindingAdapter("comments")
        fun setComments(textView: TextView, data: Post?) {
            if (data != null) {
                var comments = ""
                if (data.commentCounts > 0) {
                    comments = "${data.commentCounts} comments"
                } else {
                    comments = "${data.commentCounts} comment"
                }

                textView.text = "${comments} "

            }

        }

        @JvmStatic
        @BindingAdapter("loadpost")
        fun loadPost(view: ImageView, url: String?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (url != null && url.isNotEmpty()) {
                GlideApp.with(view.context)
                        .load(url)
                        .placeholder(R.drawable.ic_picture)
                        .error(R.drawable.ic_picture)
                        .into(view)
            }
        }

    }

}

data class CommentsResponse(
        var comments: List<Comments>? = null
) : BaseResponse()

data class Comments(
        var comment: String? = null,
        var commentedBy: String? = null,
        var commentedAt: String? = null,
        var userImage: String? = null
)

data class LikeResponse(
        var likes: List<Likes>? = null
) : BaseResponse()

data class Likes(
        var like: Boolean? = null,
        var likedAt: String? = null,
        var likedBy: String? = null,
        var userImage: String? = null
)

enum class BuddySelectionType() {
    SINGLE, MULTIPLE
}

@Parcelize
data class GetSavedServicesResponse(
        var services: List<Service>? = null
) : BaseResponse(), Parcelable

@Parcelize
data class InitiateSignUpResponse(
        var draftId: String,
        var merchantId: String,
        var refreshConfig: Boolean,
        var services: List<Service>,
        var userId: String? = null
) : BaseResponse(), Parcelable

@Parcelize
data class Service(
        var id: String,
        var img: String?,
        var title: String,
        var selected: Boolean = false
) : Parcelable

data class ClientDataResponse(
        var userInfo: List<ClientData>?
) : BaseResponse()

data class ClientData(
        val address: String?,
        val designation: String?,
        val email: String?,
        val firstName: String?,
        val lastName: String?,
        val mobile: String?,
        val userId: String?,
        val profileImg: String?

)

@Parcelize
data class InsightResponse(
        val `data`: Insight? = null
) : BaseResponse(), Parcelable

@Parcelize
data class Insight(
        val button: ButtonData? = null,
        val footer: String? = null,
        val heading: String? = null,
        val img: String? = null,
        val insights: List<ServiceDescr>? = null,
        val subheading: String? = null
) : Parcelable

@Parcelize
data class ServiceDescr(
        var count: Int,
        val img: String,
        val serviceDescr: String
) : Parcelable {
    companion object {
        @JvmStatic
        @BindingAdapter("serviceDescr")
        fun serviceDescrImage(view: ImageView, url: String?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (url != null && url.isNotEmpty()) {
                GlideApp.with(view.context)
                        .load(url)
                        .placeholder(R.drawable.ic_gallery_chooser)
                        .error(R.drawable.ic_gallery_chooser)
                        .into(view)
            }
        }
    }
}

@Parcelize
data class ButtonData(
        val title: String? = null,
        val url: String? = null
) : Parcelable

class HandlerObject {
    var totalSize: Int = 0
    var chunkSize: Int = 0

}

@Parcelize
data class SelectAccountListResponse(
        val accountsAndOfferings: List<AccountsAndOffering>,
        val draftId: String,
        val profileDetail: ProfileDetail

) : BaseResponse(), Parcelable

@Parcelize
data class AccountsAndOffering(
        var accDescr: String? = null,
        val accTitle: String,
        val merchantId: String? = null,
        var disclaimer: String? = null,
        val offerings: List<Offering>? = null,
        val signupAs: String? = null
) : Parcelable

@Parcelize
data class ProfileDetail(
        val email: String? = null,
        val mobile: String? = null,
        val name: String? = null,
        val profileImg: String? = null,
        val statistics: String
) : Parcelable

@Parcelize
data class Offering(
        val description: String,
        val id: String,
        val image: String,
        val title: String,
        var selected: Boolean = false
) : Parcelable {
    companion object {

        @JvmStatic
        @BindingAdapter("taskselectionimage")
        fun taskSelectionImage(view: ImageView, url: String?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (url != null && url.isNotEmpty()) {
                GlideApp.with(view.context)
                        .load(url)
                        .placeholder(R.drawable.ic_demo_image)
                        .error(R.drawable.ic_demo_image)
                        .into(view)
            }
        }

        @JvmStatic
        @BindingAdapter("selectedtask")
        fun selectedTask(view: LinearLayout, selected: Boolean?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (selected != null && selected) {
                view.background = ContextCompat.getDrawable(view.context, R.drawable.account_selection_stroke)
            } else {
                view.background = ContextCompat.getDrawable(view.context, R.drawable.account_not_selected)
            }
        }

        @JvmStatic
        @BindingAdapter("selectediconshow")
        fun selectedIconShow(view: ImageView, selected: Boolean?) { // This methods should not have any return type, = declaration would make it return that object declaration.
            if (selected != null && selected) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }
}

@Parcelize
data class UpdateUserResponse(
        var `data`: UserData?
) : Parcelable, BaseResponse()

@Parcelize
data class UserListResponse(
        var `data`: List<UserData>?,
        var count: Int = 0
) : Parcelable, BaseResponse()


@Parcelize
data class UserData(
        var dateOfBirth: String? = null,
        var dateOfJoining: String? = null,
        var email: String? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var middleName: String? = null,
        var mobile: String? = null,
        var profileImg: String? = null,
        var roleId: String? = null,
        var roleName: String? = null,
        var status: String? = null,
        var password: String? = null,
        var personId: String? = null,
        var motherName: String? = null,
        var fatherName: String? = null,
        var userId: String? = null
) : Parcelable {
    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val locData: UserData = obj as UserData
        return locData.userId == this.userId
    }

    override fun hashCode(): Int {
        return this.userId.hashCode()
    }
}

class UpdateResponse(
        var `data`: AddEmployeeRequest?
) : BaseResponse()

class UserAddressResponse(
        var `data`: List<AddressInfo>?

) : BaseResponse()

