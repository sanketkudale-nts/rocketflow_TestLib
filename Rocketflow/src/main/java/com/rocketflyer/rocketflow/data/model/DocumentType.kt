package com.rocketflyer.rocketflow.data.model


/**
 * Created by Vikas Kesharvani on 13/01/21.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class DocumentType {
    var type:DocType?=null
    var icon:Int?=null
}

enum class DocType(var type: String) {
    DOC("Documents"),
    CAMERA("Camera"),
    GALLERY("Gallery"),
    AUDIO("Audio")
    ,VIDEO("Video"),
    PDF("Pdf"),
    OTHER("Other"),
    LOCATION("Location"),
    PAYMENT("Payment"),

}
enum class SupportedMediaType{
    TYPING, TEXT, IMAGE, AUDIO, VIDEO, DOCUMENT, LOCATION
}