package com.tracki.ui.cart

data class ApplyCouponResponse(
    var couponId: String?,
    var discount: Int?,
    var loginToken: String?,
    var promo: String?,
    var responseCode: String?,
    var responseMsg: String?,
    var successful: Boolean
)