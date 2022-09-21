package com.rocketflyer.rocketflow.utils;

import android.content.Context;
import android.widget.Toast;

import com.tracki.utils.TrackiToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class SaveFileInDevice {

    public void backup(Context mContext, String outFileName) {

        //database path
        final String inFileName = mContext.getDatabasePath(CommonUtils.TRACK_THAT_DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);
//            File dbShm = new File(dbFile.getParent(), DatabaseClient.DB_NAME+"-shm");
//            File dbWal = new File(dbFile.getParent(), DatabaseClient.DB_NAME+"-wal");
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
//              saveFile(dbShm,DatabaseClient.DB_NAME+"-shm");
//              saveFile(dbWal,DatabaseClient.DB_NAME+"-wal");
           // Toast.makeText(mContext, "Backup Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            TrackiToast.Message.showShort(mContext, "Unable to backup database. Retry");
            e.printStackTrace();
        }
    }

    private void saveFile(File dbFile, String outFileName) {
        try {


            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void importDB(Context mContext, String inFileName) {

        final String outFileName = mContext.getDatabasePath(CommonUtils.TRACK_THAT_DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
            //   ((AppCompatActivity)mContext).onResume();
            TrackiToast.Message.showShort(mContext, "Import Completed");
        } catch (Exception e) {
            TrackiToast.Message.showShort(mContext, "Unable to import database. Retry");
            e.printStackTrace();
        }
    }
}
