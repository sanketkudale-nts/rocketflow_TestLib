package com.rocketflyer.rocketflow.ui.attendance.punchInOut

data class PunchInPunchOutData(
    var `data`: Data?,
    var responseCode: String,
    var responseMsg: String,
    var successful: Boolean,
    var time:Long
)

data class Data(
    var date: Int,
    var hours: Any?,
    var location: Location,
    var punchData: PunchData?,
    var punchInAt: Long,
    var punchOutAt: Long,
    var status: String?,
    var punchId: String?
)

data class Location(
    var punchInLocation: PunchInLocation?,
    var punchOutLocation: PunchOutLocation?
)

data class PunchData(
    var punchInData: PunchInData?,
    var punchOutData: PunchOutData?
)

data class PunchInLocation(
    var address: String?,
    var location: LocationX
)

data class PunchOutLocation(
    var address: String?,
    var location: LocationXX
)

data class LocationX(
    var latitude: Double,
    var locationId: String,
    var longitude: Double
)

data class LocationXX(
    var latitude: Double,
    var locationId: String,
    var longitude: Double
)

data class PunchInData(
    var remarks: String,
    var selfie: String?
)

data class PunchOutData(
    var remarks: String,
    var selfie: String
)