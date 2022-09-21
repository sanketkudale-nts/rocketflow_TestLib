//package com.tracki.ui.service;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import com.tracki.utils.Log;
//
///**
// * Created by rahul on 2/5/19
// */
//public class BackgroundWorker extends Worker {
//
//    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        try {
//            loop();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return Result.success();
//    }
//
//    private void loop() throws InterruptedException {
//        for (int i = 0; i < 50; i++) {
//            Thread.sleep(1000);
//            Log.d("BackgroundWorker", "Working: " + i);
//        }
//    }
//}
