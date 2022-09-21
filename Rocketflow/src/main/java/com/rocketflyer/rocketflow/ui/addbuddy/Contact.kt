package com.rocketflyer.rocketflow.ui.addbuddy

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Parcelable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*


/**
 * Created by Vikas Kesharvani on 10/06/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
@Parcelize
@Entity(tableName = "contacts")
data class Contact(@ColumnInfo(name = "name") var name:String?=null,@ColumnInfo(name = "mobile")  var mobileNumber:String?=null ): Parcelable {


    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
        get() = field
        set(id) {
            field = id
        }
    var contact_id:Long=0
    var isColorSet:Boolean=false
    @ColumnInfo(name = "is_buddy")  var isMyBuddy:Boolean=false
    companion object {

        @JvmStatic
        @BindingAdapter("textInitials")
        fun setInitials(textView: TextView, name: String?){

            textView.text= name!!.get(0).toUpperCase().toString()

        }

        @JvmStatic
        @BindingAdapter("changebg")
        fun setBackGround(imageView: ImageView, name: String){
            val rnd = Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        }


    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null || obj.javaClass != this.javaClass) return false
        val secondObject: Contact = obj as Contact
        return secondObject.mobileNumber.equals(mobileNumber)
    }

    override fun hashCode(): Int {
        return id
    }
}