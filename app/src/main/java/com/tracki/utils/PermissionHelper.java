//package com.tracki.utils;
//
//import android.Manifest;
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.os.Build;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by actolap on 20/04/17.
// */
//
//public class PermissionHelper {
//
//    public static final int WRITE_EXTERNAL_STORAGE = 1;
//    public static final int WAKE_LOCK = 2;
//    public static final int INTERNET = 3;
//    public static final int ACCESS_NETWORK_STATE = 4;
//    public static final int ACCESS_WIFI_STATE = 5;
//    public static final int READ_PHONE_STATE = 6;
//    public static final int CAMERA = 8;
//    public static String permission = null;
//    private static boolean mFlag = false;
//
//    public static Boolean CheckAndroidVersion() {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    private static Boolean CheckPermission(int mPermission, Context mContext) {
//
//        switch (mPermission) {
//            case WRITE_EXTERNAL_STORAGE:
//                permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
//                break;
//            case WAKE_LOCK:
//                permission = android.Manifest.permission.WAKE_LOCK;
//                break;
//            case INTERNET:
//                permission = android.Manifest.permission.INTERNET;
//                break;
//            case ACCESS_NETWORK_STATE:
//                permission = android.Manifest.permission.ACCESS_NETWORK_STATE;
//                break;
//            case ACCESS_WIFI_STATE:
//                permission = android.Manifest.permission.ACCESS_WIFI_STATE;
//                break;
//            case READ_PHONE_STATE:
//                permission = android.Manifest.permission.READ_PHONE_STATE;
//                break;
//            case CAMERA:
//                permission = Manifest.permission.CAMERA;
//                break;
//            default:
//                permission = null;
//                break;
//        }
//        if (permission != null) {
//            int res = mContext.checkSelfPermission(permission);
//            mFlag = res == PackageManager.PERMISSION_GRANTED;
//        }
//        return mFlag;
//    }
//
//    public static String[] CheckMultiplePermission(List<Integer> arr, Context mContext) {
//        List<String> permissionslist = new ArrayList<>();
//        for (Integer integer : arr) {
//            if (!CheckPermission(integer, mContext))
//                permissionslist.add(permission);
//        }
//        return permissionslist.toArray(new String[permissionslist.size()]);
//    }
//}