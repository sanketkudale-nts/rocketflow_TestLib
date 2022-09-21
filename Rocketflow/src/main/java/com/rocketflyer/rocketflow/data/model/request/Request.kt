package com.rocketflyer.rocketflow.data.model.request

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.tracki.data.model.response.config.*
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.ui.tasklisting.PagingData
import com.tracki.ui.uploadDocument.DocType
import com.tracki.utils.*
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.Serializable

/**
 * Created by rahul on 16/11/18
 *
 * This class contains all the request data models.
 */


class LoginRequest(var actionType: NextScreen, var mobile: String)

class OtpRequest(val mobile: String, val otp: String)

class RegisterRequest(private var name: String, private var email: String, private var mobile: String,
                      private var otp: String){
    private var verificationId: String?=null
    private var password: String?=null
}
class AccountListRequest(val draftId: String)

data class InitiateSignUpRequest(
        var draftId: String?=null,
        var merchantId: String?=null,
        var selectionIds: ArrayList<String>?=null,
        var signupAs: String?=null
)
data class UpdateServiceRequest(
        var draftId: String?=null,
        var merchantId: String?=null,
        var roleId: String?=null,
        var serviceIds: List<String>?=null
)

class BuddiesRequest(var status: List<BuddyStatus>, var loadBy: BuddyInfo, var includeSelfAsBuddy: Boolean)

class HomeRequest(var status: List<BuddyStatus>)

data class DashboardRequest(var buddies: List<String>, var from: Long)

class FleetRequest(var status: String)

class Shift(var from: String, var to: String) : Serializable

class AddBuddyRequest(var name: String, var mobile: String, var shift: Shift)

class AddFleetRequest(var fleetImg: String?, var fleetName: String, var regNumber: String, var fleetId: String?)

class UpdateProfileRequest(private val name: String, private val email: String,
                           private val mobile: String, private val profileImg: String?)

class TaskRequest(var status: MutableList<out TaskStatus>, var loadBy: BuddyInfo?, var properties: Map<String, String>?) {
    var categoryId: String? = null
    var stageId: String? = null
    var parentTaskId: String? = null
    var referenceId: String? = null
    var from: Long? = null
    var to: Long? = null
    var paginationData: PagingData? = null
    var regionId: String? = null
    var hubIds: List<String>? = null
    var stateId: String? = null
    var cityId: String? = null
    var placeId: String? = null
    var userGeoReq: Boolean = false

    init {
        if (properties != null && properties!!.containsKey("categoryId"))
            categoryId = properties!!.get("categoryId")
        if (properties != null && properties!!.containsKey("stageId"))
            stageId = properties!!.get("stageId")
    }
}

data class RegionRequest(
        var regionId: String? = null,
        var userGeoReq: Boolean? = null
)

class CreateTaskRequest(
        var autoCreated: Boolean,
        var buddyId: String,
        var fleetId: String,
        var createdBy: String,
        var description: String,//req
        var destination: Place?,//req
        var source: Place?,//req
        var startTime: Long,//req
        var taskName: String,//req
        var taskId: String,
        var endTime: Long,
        var contact: Contact?,
        var taskType: String?) : Serializable {
    var taskData: TaskData? = null
    var categoryId: String? = null
    var dfId: String? = null
    var referenceId: String? = null
    var parentTaskId: String? = null
    var requestBy: String? = null
    var invIds: List<String>?=null
    var  directMapping:Boolean=false

    // var location:GeoLocation?=null
}

class GetManualLocationRequest {
    var regionId: String? = null
    var stateId: String? = null
    var cityId: String? = null
    var userGeoReq: Boolean? = null
}

class OnlineOffLineRequest() {
    var status: String? = null
}

data class TeamAttendanceRequest(var date: Long):Serializable {
    var regionId: String? = null
    var stateId: String? = null
    var cityId: List<String>? = null
    var hubId: List<String>? = null
    var geoFilter: Boolean = false
}
/*
"contact": {
    "mobile": "string",
    "name": "string"
  },
 */

class Contact(
        var name: String?,
        var mobile: String?) : Serializable

data class ExecuteUpdateRequest(var taskId: String, var ctaId: String, var timestamp: Long,
                                var taskData: TaskData?): Serializable {
    var dfdId: String? = null
    var dfId: String? = null
    var location: CtaLocation? = null


}

class CtaLocation:Serializable {
    var location: GeoCoordinates? = null
    var address: String? = null
}

class AcceptRejectRequest(var taskId: String)

class CancelRequest(var taskId: String, var autoCancel: Boolean)

class ArriveReachRequest(var time: Long, var taskId: String, var formData: HashMap<String, String>?)

class StartTaskRequest(var taskId: String, var formData: HashMap<String, String>?, var startTime: Long)

class EndAutoTaskRequest(var taskId: String, var destination: Place, var endTime: Long)

class EndTaskRequest(var taskId: String, var formData: HashMap<String, String>?,
                     var endTime: Long, var destination: Place?)

class AcceptRejectBuddyRequest(var action: String, var inviteId: String)

class DeleteBuddyRequest(var buddyId: String)

class DeleteFleetRequest(var fleetId: String)

class ChangeFleetStatusApiRequest() {
    var status: EntityStatus? = null
    var fleetId: String? = null
}

enum class EntityStatus {
    ACTIVE, INACTIVE
}

class RejectTaskRequest(var taskId: String, var rejectionReason: String, var comment: String)

class FileListRequest(val imageName: String, val file: File, val url: String?, val position: Int)

class AnalyticRequest(var eventName: String, var pageName: String, var data: HashMap<String, Any>)

class UpdateFileRequest(var file: File, var type: FileType, var id: String)

class ShareTrip(var expiredBy: Long, var trackingId: String)

@Parcelize
data class DynamicFormMainData(var taskData: TaskData, var taskId: String, var ctaId: String, var dfId: String,
                               var dfversion: Int) : Parcelable {
    override fun toString(): String {
        return "{ " +
                " taskData: " + taskData +
                " taskId: " + taskId +
                " }"
    }
}

@Parcelize
data class DynamicFormDataCreateTask(var taskData: ArrayList<DynamicFormData>? = null, var ctaId: String, var uploadedAt: Long = 0L) : Parcelable {
    override fun toString(): String {
        return "{ "
        "taskData: " + taskData +
                " ctaId: " + ctaId +
                " }"
    }
}

@Parcelize
data class TaskData(var ctaId: String? = null, var taskData: List<DynamicFormData>? = null, var uploadedAt: Long = 0L) : Parcelable {


    override fun toString(): String {
        return "{ " +
                "ctaId: " + ctaId +
                " taskData: " + taskData +
                " uploadedAt: " + uploadedAt +
                " }"
    }
}

@Parcelize
data class CalculateFormData(var taskData: List<DynamicFormData>? = null) : Parcelable {

}

@Parcelize
data class TaskDataCreateTask(var taskData: ArrayList<DynamicFormData>? = null, var uploadedAt: Long = 0L) : Parcelable {

    override fun toString(): String {
        return "{ " +
                "taskData: " + taskData +
                " uploadedAt: " + uploadedAt +
                " }"
    }
}

class MyEarningRequest {
    var from = 0L
    var to = 0L
    var userId: String? = null
}

class GetTaskByDateRequest {
    var date = 0L
    var from = 0L
    var to = 0L
    var userId: String? = null
}

class AttendanceReq {
    var from: Long = 0L
    var to: Long = 0L
    var status: LeaveStatus? = null
    var userId: String? = null
    var limit: Int? = null
    var offset: Int? = null
    var dataCount: Int? = null

}

class ApplyLeaveReq {
    var from: Long = 0L
    var to: Long = 0L
    var leaveType: LeaveType? = null
    var remarks: String? = null
    var lrId: String? = null
}


enum class PunchInOut {
    PUNCH_IN, PUNCH_OUT
}


class Data {
    var remarks: String? = null
    var selfie: String? = null
}

class PunchRequest {
    var data: Data? = null
    var date = System.currentTimeMillis()
    var event: PunchInOut? = null
    var location: Addrloc? = null
    var userId: String? = null


}

class Addrloc {
    var address: String? = null
    var location: Location? = null
}

class Location {

    var latitude: Double? = null
    var locationId: String? = null
    var longitude: Double? = null

}

class LeaveHistoryRequest {
    var from = 0L
    var to = 0L
    var status: LeaveStatus? = null
    var userId: String? = null
}

class CancelLeaveRequest {
    var lrId: String? = null
    var remarks: String? = null
    var updatedAt = 0L
}

class ApproveRejectLeaveRequest {
    var lrId: String? = null
    var comments: String? = null
    var remarks: String? = null
    var updatedAt = 0L
}


class LeaveApprovalRequest {
    var from: Long = 0
    var to: Long = 0
    var status: LeaveStatus? = null
}

class ApproveRejectLeave {
    var lrId: String? = null
    var remarks: String? = null
    var updatedAt: Long = 0L

}

class GetTaskDataRequest {
    var dfId: String? = null
    var dfdId: String? = null
    var dfVersion: Int? = 0
    var taskId: String? = null
}

class ExecuteUpdateReq {
    var dfId: String? = null
    var ctaId: String? = null
    var taskData: ArrayList<DynamicFormData>? = null
    var uploadedAt: Long = 0L
}


data class InventoryRequest(
        var at: Int? = null,
        var category: Any? = null,
        var categoryId: String? = null,
        var inventoryCategoryId: String? = null,
        var categoryIds: List<String>? = null,
        var cid: String? = null,
        var fleetname: String? = null,
        var gid: String? = null,
        var inventoryGroupId: String? = null,
        var inventoryId: String? = null,
        var modelId: String? = null,
        var modelIds: List<String>? = null,
        var paginationData: PagingData? = null,
        var regNumber: String? = null
)

data class LinkInventoryRequest(
        var autoApprove: Boolean? = null,
        var categoryId: String? = null,
        var createSubTask: Boolean? = null,
        var ctaId: String? = null,
        var executeWorkflow: Boolean? = null,
        var inventoryId: String? = null,
        var inventoryIds: List<String>? = null,
        var merchantId: String? = null,
        var parentTaskId: String? = null,
        var flavorId: String? = null,
        var quantity: Int? = null,
        var quantitys: List<Int>? = null,
        var subTaskCategory: String? = null,
        var subTaskId: String? = null,
        var subCategoryId: String? = null,
        var taskId: String? = null,
        var type: String? = null,
        var action: String? = null,
/*
        var products:Map<String,Int>?=null
*/
        var products:List<SelectedProduct>?=null
)
data class SelectedProduct(
    var price:Float?=0f,
    var productId:String?=null,
    var quantity:Int?=0,
    var dynamicPricing: Boolean = false,

)

data class TransactionRequest(
        var userId: String? = null,
        var paginationData: PagingData? = null,
        var txnTypes: ArrayList<String>? = null
)

data class EmployeeListAttendanceRequest(
        var status: String? = null,
        var regionId: String? = null,
        var stateId: String? = null,
        var cityId: List<String>? = null,
        var hubId: List<String>? = null,
        var geoFilter: Boolean? = null,
        var paginationData: PagingData? = null,
        var date: Long=0,
        var from: Long? = null,
        var mobile: String? = null,
        var name: String? = null,
        var to: Long? = null
)

data class GetCommentsOfPosts(
        var postId: String? = null
)

class CommentsPostRequest {
    var postId: String? = null
    var comment: String? = null
    var like: Boolean? = null
}

 class ClientSearchRequest(
        val name: String?,
        val roleIds: List<String>?
)
/*class GeoPunchRequest{
    var coordinates: List<Int>?=null
    var locationId: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0


}*/

@Parcelize
 class AddEmployeeRequest():Parcelable{
     var address: Boolean?=null
     var addressInfo: AddressInfo?=null
     var dateOfBirth: String?=null
     var dateOfJoining: String?=null
     var dfData: ArrayList<DynamicFormData>?=null
     var email: String?=null
     var fatherName: String?=null
     var motherName: String?=null
     var password: String?=null
     var firstName: String?=null
     var lastName: String?=null
     var middleName: String?=null
     var mobile: String?=null
     var profileImg: String?=null
     var roleId: String?=null
     var roleName: String?=null
     var personId: String?=null
     var userId: String?=null
 }

@Parcelize
data class AddressInfo(
        var address: String?=null,
        var location: Address?=null,
        var pincode: String?=null,
        var placeId: String?=null,
        var userId: String?=null
):Parcelable{
    override fun equals(obj: Any?): Boolean {

        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val locData: AddressInfo = obj as AddressInfo
        return locData.placeId == this.placeId
    }

    override fun hashCode(): Int {
        return this.placeId.hashCode()
    }
}

data class DfData(
        var embeddedDf: Boolean?=null,
        var embeddedDfId: String?=null,
        var key: String?=null,
        var label: String?=null,
        var type: String?=null,
        var value: String?=null,
        var weight: Int?=null
)

@Parcelize
data class Address(
        var address: String?=null,
        var location: LocationX?=null,
        var radius: Int?=null
):Parcelable

@Parcelize
data class LocationX(
        var coordinates: List<Double>?=null,
        var latitude: Double?=null,
        var locationId: String?=null,
        var longitude: Double?=null
):Parcelable

class PlaceRequest{
    var placeId: String?=null
}


data class getDocsRequest(
    var pn: Long?,
    var rc: Long?,
    var type: DocType?
)

