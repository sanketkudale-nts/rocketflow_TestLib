package com.tracki.ui.custom.socket

import com.tracki.utils.DateTimeUtil


open class BaseModel {
    var s: Boolean? = null
    var m: String? = null
}

class OpenCreateRoomModel : BaseModel() {
    var messages: List<Messages>? = null
    var connections: List<String>? = null
    var roomId: String? = null
    var roomName: String? = null
    var newRoom: Boolean? = null
    var hm: Boolean? = null
    var connectionsStatus: Map<String,String>? = null


    override fun toString(): String {
        return "{" +
                " messages ${messages.toString()}, " +
                "connections $connections, " +
                "roomId $roomId, " +
                "roomName $roomName, " +
                "newRoom $newRoom, " +
                "hm $hm " +
                "}"
    }
}

class ConnectionResponse : BaseModel() {
    var connections: Map<String, ConnectionInfo>? = null
    var connectionGroups: Map<String, ConnectionGroupInfo>? = null

    override fun toString(): String {
        return "{ " +
                "connections $connections, " +
                "connectionGroups $connectionGroups " +
                "}"
    }
}

class ConnectionInfo {
    var connectionId: String? = null
    var roomId: String? = null
    var status: String? = null
    var lastMessage: Messages? = null
    var time = 0L

    override fun toString(): String {
        return "{ " +
                "connectionId $connectionId, " +
                "roomId $roomId, " +
                "status $status, " +
                "lastMessage $lastMessage," +
                "time $time " +
                "}"
    }
}

class ConnectionDetail : BaseModel() {
    var connectionId: String? = null
    var state: String? = null
    var disconnectedAt = 0L // for disconnect

    override fun toString(): String {
        return "{ " +
                "connectionId $connectionId, " +
                "state $state, " +
                "disconnectedAt ${DateTimeUtil.getParsedDate(disconnectedAt)} " +
                "}"
    }
}

class ConnectionGroupInfo

class Messages : BaseModel() {
    var message: Message? = null
    var roomId: String? = null
    var sender: String? = null
    var time = 0L

    override fun toString(): String {
        return "{ " +
                "message ${message.toString()}, " +
                "roomId $roomId, " +
                "sender $sender, " +
                "time $time " +
                "}"
    }
}

class Message {
    var data: String? = null
    var type: String? = null
    var self: Boolean? = null

    override fun toString(): String {
        return "{ " +
                "data $data, " +
                "type $type, " +
                "self $self " +
                "}"
    }
}