package com.rocketflyer.rocketflow.ui.products

data class UnitsResponse(
    var `data`: Data?,
    var responseCode: String?,
    var responseMsg: String?,
    var successful: Boolean
)

data class Data(
    var PACK_UNIT: LinkedHashMap<String,String>?,
    var UNIT: LinkedHashMap<String,String>?
)

data class PACKUNIT(
    var GM: String?,
    var KG: String?,
    var MG: String?,
    var POUND: String?,
    var QUINTAL: String?,
    var TON: String?
)

data class UNIT(
    var CAPSULE: String?,
    var CM: String?,
    var DAY: String?,
    var DOZEN: String?,
    var FEET: String?,
    var GM: String?,
    var GUNTA: String?,
    var HOUR: String?,
    var INCH: String?,
    var KG: String?,
    var KM: String?,
    var LTR: String?,
    var MG: String?,
    var MINUTE: String?,
    var ML: String?,
    var MM: String?,
    var MTR: String?,
    var PACK: String?,
    var PAIR: String?,
    var PCS: String?,
    var PLATE: String?,
    var POUND: String?,
    var QUINTAL: String?,
    var SQ_METER: String?,
    var SQ_YARD: String?,
    var TABLET: String?,
    var TON: String?,
    var YEAR: String?
)