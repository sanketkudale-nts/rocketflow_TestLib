package com.tracki.data.dbsync;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.tracki.R;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.Log;
import com.tracki.utils.SaveFileInDevice;
import com.tracki.utils.ZipManager;

import java.io.File;
import java.io.IOException;


/**
 * Created by Vikas Kesharvani on 08/10/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class CopyZipDataBaseAndSendToServerWork extends Worker {
    private Context mContext;
    public CopyZipDataBaseAndSendToServerWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext=context;
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
    private void performBackUp() {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + mContext.getResources().getString(R.string.app_name));

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {

            String outFileName =
                    Environment.getExternalStorageDirectory().toString() + File.separator + mContext.getResources().getString(R.string.app_name) + File.separator;
            String out = outFileName + CommonUtils.TRACK_THAT_DATABASE_NAME;
            SaveFileInDevice saveFileInDevice = new SaveFileInDevice();
            saveFileInDevice.backup(mContext, out);

            final File backupDB = new File(out);
            String[] s = new String[1];
            s[0] = backupDB.getAbsolutePath();
            try {
                ZipManager.zip(s, outFileName +CommonUtils.TRACK_THAT_DATABASE_NAME_ONLY +".zip");
                File file=new File(out);
                file.deleteOnExit();

            } catch (IOException e) {
             //   Toast.makeText(this, "Unable to zip database. Retry", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.e("MainActivity", e.getMessage());
            }

        }

    }
}
