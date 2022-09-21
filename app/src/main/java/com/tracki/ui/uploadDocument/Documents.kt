package com.tracki.ui.uploadDocument

import com.tracki.data.model.response.config.RoleConfigData

data class Documents ( var name:String="",
                  var type:DocType?=null){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Documents

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        return type!!.name?.hashCode() ?: 0
    }

}
