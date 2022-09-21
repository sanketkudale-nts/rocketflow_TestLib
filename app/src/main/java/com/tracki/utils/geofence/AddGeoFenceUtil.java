package com.tracki.utils.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.ui.base.BaseActivity;
import com.tracki.utils.TrackiToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vikas Kesharvani on 15/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
public class AddGeoFenceUtil {
    private List<Geofence> geofenceList = new ArrayList<>();
    private GeofencingClient geofencingClient;
    private BaseActivity context;
    private static final String TAG = AddGeoFenceUtil.class.getSimpleName();
    private PendingIntent geofencePendingIntent;
    private PreferencesHelper preferencesHelper;

    public AddGeoFenceUtil(BaseActivity context, PreferencesHelper preferencesHelper) {
        this.context = context;
        this.preferencesHelper = preferencesHelper;
        geofenceList = new ArrayList<>();
        geofencingClient = LocationServices.getGeofencingClient(context);
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
      //  Toast.makeText(context, "starting broadcast", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, GeoFenceBroadCastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    public void addGeofence(String taskId, GeoCoordinates geoCoordinates, float radius) {
        if (!preferencesHelper.getLocationReminderFlag()) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (preferencesHelper.getGeoFence() == null || preferencesHelper.getGeoFence().isEmpty()) {
            Map<String, GeoCoordinates> map = new HashMap<>();
            map.put(taskId, geoCoordinates);
            preferencesHelper.setGeoFence(map);
            geofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(taskId)
                    .setCircularRegion(geoCoordinates.getLatitude(), geoCoordinates.getLongitude()
                            ,// lng
                            radius)// add the radius in float.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .build());
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(context, aVoid -> {
                        TrackiToast.Message.showShort(context
                                , "Geofencing has started");
                   })
                    .addOnFailureListener(context, e -> {
                        TrackiToast.Message.showShort(context
                                , "Geofencing failed");

                    });
        } else if (!preferencesHelper.getGeoFence().containsKey(taskId)) {

            Map<String, GeoCoordinates> map = preferencesHelper.getGeoFence();
            map.put(taskId, geoCoordinates);
            preferencesHelper.setGeoFence(map);
            geofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(taskId)
                    .setCircularRegion(geoCoordinates.getLatitude(), geoCoordinates.getLongitude()
                            ,// lng
                            radius)// add the radius in float.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .build());
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(context, aVoid -> {
                        TrackiToast.Message.showShort(context
                                , "Geofencing has started");
                    })
                    .addOnFailureListener(context, e -> {
                        TrackiToast.Message.showShort(context
                                , "Geofencing failed");
                    });
        }


    }

    public void removeAllGeofence() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        preferencesHelper.setGeoFence(null);
        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(context, aVoid -> {
                    TrackiToast.Message.showShort(context
                            , "Geofencing has been removed");
                })
                .addOnFailureListener(context, e -> {
                    TrackiToast.Message.showShort(context
                            , "Geofencing could not be removed");
                });
    }

    public void removeGeoFenceById(List<String> geofenceId) {

        geofencingClient.removeGeofences(geofenceId)
                .addOnSuccessListener(context, aVoid -> {
                    if (!preferencesHelper.getGeoFence().isEmpty()) {
                        for (int i = 0; i < geofenceId.size(); i++) {
                            preferencesHelper.remove(geofenceId.get(i));
                        }
                    }
                    TrackiToast.Message.showShort(context
                            , "Geofencing has been removed");
                })
                .addOnFailureListener(context, e -> {
                    TrackiToast.Message.showShort(context
                            , "Geofencing could not be removed");

                });

    }
}
