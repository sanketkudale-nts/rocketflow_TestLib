package com.rocketflyer.rocketflow.utils.geofence;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tracki.R;
import com.tracki.ui.main.MainActivity;

import static com.tracki.utils.geofence.GeoFenceBroadCastReceiver.CHANNEL_ID;

/**
 * Created by Vikas Kesharvani on 14/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
public class GeofenceLocationService extends Service {

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        String message = "";
        if (intent.hasExtra("message")) {
            message = intent.getStringExtra("message");
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Alarm")
                .setContentText("You " + message + " the location.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, notification);
        // startForeground(2, notification);
        //do heavy work on a background thread
        //stopSelf();
//        Toast.makeText(this
//                , "Geofencing get", Toast.LENGTH_SHORT).show();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
