package com.rocketflyer.rocketflow.ui.taskdetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tracki.R
import com.tracki.utils.CommonUtils


/**
 * Created by Vikas Kesharvani on 04/08/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class TaskAlterEventAdapter(var context:Context,var list:ArrayList<AlertEvent>): RecyclerView.Adapter<TaskAlterEventAdapter.MyAlertViewHolder>() {
 var mListener:OnAlertClick?=null
    var cellwidthWillbe=0
    init {
        mListener=context as OnAlertClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAlertViewHolder {
        var view:View=LayoutInflater.from(parent.context).inflate(R.layout.item_my_alter,parent,false)
        return  MyAlertViewHolder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyAlertViewHolder, position: Int) {
        if(cellwidthWillbe!=0){
            holder.cardMain.setLayoutParams(FrameLayout.LayoutParams(
                    cellwidthWillbe, CommonUtils.dpToPixel(context,110)))
        }
        /**
         * Method used to get the events and show their count on screen.
         *
         *@param tripStats stats model from sdk
         *
         * Events are:
         * ACTION_AIRPLANE_MODE,
         * ACTION_MANUAL_BATTERY_REMOVAL,
         * ACTION_MANUAL_MOBILE_DATA_OFF,
         * PHONE_USAGE,
         * ACTION_SHUTDOWN_GRACEFULLY,
         * ACTION_DATE_TIME_CHANGE,
         * OVER_SPEEDING,
         * OVER_STOPPING,
         * HARSH_ACCELERATION,
         * HARSH_BREAKING,
         * HARSH_CORNERING,
         * ACTION_SLOW_DRIVING,
         * GEOFENCE_IN,
         * GEOFENCE_OUT,
         * UNKNOWN,
         * OUT_OF_NETWORK,
         * NONE,
         * ACTION_LOCATION_CHANGE,
         * ACTION_CLEAR_DATA,
         * LOGOUT
         * FORCE_PUNCH_OUT
         */
        if(list[position].name!=null)
        {
            when(list[position].name){
                "ACTION_AIRPLANE_MODE"->{
                    holder.textName.text=" Airplane Mode"
                }
                "ACTION_MANUAL_MOBILE_DATA_OFF"->{
                    holder.textName.text="  Data Off "
                }
                "PHONE_USAGE"->{
                    holder.textName.text="   Call      "
                }
                "ACTION_SHUTDOWN_GRACEFULLY"->{
                    holder.textName.text="   Shutdown  "
                }
                "ACTION_DATE_TIME_CHANGE"->{
                    holder.textName.text="  Time Change "
                }
                "OVER_SPEEDING"->{
                    holder.textName.text="  Over Speed  "
                }
                "OVER_STOPPING"->{
                    holder.textName.text="Over Stopping"
                }
                "HARSH_ACCELERATION"->{
                    holder.textName.text="Harsh Acceleration"
                }
                "HARSH_BREAKING"->{
                    holder.textName.text="Harsh Breaking"
                }
                "HARSH_CORNERING"->{
                    holder.textName.text="Harsh Cornering"
                }
                "ACTION_SLOW_DRIVING"->{
                    holder.textName.text="Slow Driving"
                }
                "GEOFENCE_IN"->{
                    holder.textName.text="Geofence In"
                }
                "GEOFENCE_OUT"->{
                    holder.textName.text="Geofence Out"
                }
                "OUT_OF_NETWORK"->{
                    holder.textName.text="Out Of\nNetwork"
                }
                "FORCE_PUNCH_OUT"->{
                    holder.textName.text="Force Punch Out"
                }
                "ACTION_LOCATION_CHANGE"->{
                    holder.textName.text="Location Off"
                }


                "LOGOUT"->{
                    holder.textName.text="  Logout  "
                }else->{
                holder.textName.text = list[position].name
            }

            }


        }
        if(list[position].icon!=null)
        {
            holder.iconAlert.setImageDrawable(list[position].icon)
        }
        if(list[position].eventData!=null){
            holder.textNumber.text="("+list[position].eventNUmber+")"
        }

        holder.cardMain.setOnClickListener {
            if(mListener!=null){
                mListener!!.alert(list[position])
            }


        }
    }

    public fun setWidthWillBe(cellwidthWillbe: Int) {
        this.cellwidthWillbe=cellwidthWillbe
    }

    fun addList(list: java.util.ArrayList<AlertEvent>) {
       this.list=list
        notifyDataSetChanged()
    }

    inner class MyAlertViewHolder(view: View):RecyclerView.ViewHolder(view){
        var textName=view.findViewById<TextView>(R.id.tvName)
        var textNumber=view.findViewById<TextView>(R.id.tvEventNumber)
        var iconAlert=view.findViewById<ImageView>(R.id.ivAlertIcon)
        var cardMain=view.findViewById<CardView>(R.id.cardMain)

    }
     interface OnAlertClick{
        fun alert(alertEvent: AlertEvent)
    }
}