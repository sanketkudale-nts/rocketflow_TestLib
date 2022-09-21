package com.tracki.utils.image_utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.tracki.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ImagePicker {

    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final String TEMP_IMG_PREFIX = "TEMP_IMG";
    private static final String TEMP_IMG_SUFFIX = ".JPG";
    private static String lastTempImg = "";
    private static boolean strictModeBypassed = false;
    private static Uri imageUri;
    private static int limitSdkVersion = Build.VERSION_CODES.R;

    public static Intent getPickImageIntent(Context context, String imagePath) {
        lastTempImg = imagePath;
        deleteExistingFile(context, lastTempImg);
        return constructImagePickerIntent(context);
    }

    public static Intent getPickImageIntent(Context context) {
        generateNextImgFile(context);
        return constructImagePickerIntent(context);
    }

    public static Intent getCaptureImageIntent(Context context) {
        generateNextImgFile(context);
        return openCamera(context);
    }


    public static File convertBitmapToFile(Context context, Bitmap bitmap, String fileName) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80/*Ignore for PNGs*/, bytes);
        // context.getCacheDir() + File.separator + fileName
        // Environment.getExternalStorageDirectory(), fileName
        File file = new File(context.getCacheDir() + File.separator + fileName);
        try {
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Intent constructImagePickerIntent(Context context) {
        if (!strictModeBypassed) {
            bypassStrictMode();
        }
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);

        if (Build.VERSION.SDK_INT >= limitSdkVersion) {

            File file = null;
            try {
                file = createImageFile();
                imageUri = FileProvider.getUriForFile(context,
                        "com.rocketflyer.rocketflow" + ".provider", file);

                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(prepareNextImgFile(context)));
        }

        intentList = addIntentsToList(context, intentList, takePhotoIntent);
        intentList = addIntentsToList(context, intentList, pickIntent);
        if (intentList.size() > 0) {
            Intent remove=intentList.remove(intentList.size() - 1);
            chooserIntent = Intent.createChooser(remove,
                    "Pick image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private static Intent openCamera(Context context) {
        if (!strictModeBypassed) {
            bypassStrictMode();
        }
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(prepareNextImgFile(context)));
        intentList = addIntentsToList(context, intentList, takePhotoIntent);
        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Pick image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static void bypassStrictMode() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
                strictModeBypassed = true;
            } catch (Exception ignored) {
            }
        }
    }

    private static File prepareNextImgFile(Context context) {
        File imageFile = new File(context.getExternalFilesDir(null), lastTempImg);
        imageFile.setWritable(true);
        return imageFile;
    }

    public static File createImageFile() throws IOException {


        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                TEMP_IMG_PREFIX, /*prefix*/
                TEMP_IMG_SUFFIX, /*suffix*/
                storageDir      /*directory*/
        );
        lastTempImg = image.getAbsolutePath();
        Log.e("lastTempImg=>", lastTempImg);
        return image;
    }

    private static void generateNextImgFile(Context context) {
        if (!lastTempImg.isEmpty() && lastTempImg.contains(TEMP_IMG_PREFIX)) {
            deleteExistingFile(context, lastTempImg);
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        lastTempImg = TEMP_IMG_PREFIX + timeStamp + TEMP_IMG_SUFFIX;
    }

    private static void deleteExistingFile(Context context, String fileName) {
        if (Build.VERSION.SDK_INT >= limitSdkVersion) {
            File file = new File(fileName);
            if (file.exists()) {
                Log.e("file deleted=>", lastTempImg);
                file.delete();
            }
        } else {
            File file = new File(context.getExternalFilesDir(null), fileName);
            if (file.exists()) {
                Log.e("file deleted=>", lastTempImg);
                file.delete();
            }
        }

    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 we need to add specific camera apps
            // due them are not added during ACTION_IMAGE_CAPTURE scanning...
            resInfo.addAll(getCameraSpecificAppsInfo(context));
        }
//        StringBuilder sb=new StringBuilder();

        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);

        }
//        WriteFile.Companion.writeToFile("all_package",sb.toString());

        return list;
    }

    static final String[] CAMERA_SPECIFIC_APPS = new String[]{
            "best.camera",
            "net.sourceforge.opencamera",
            "com.google.android.GoogleCamera",
            "tools.photo.hd.camera",
    };

    private static List<ResolveInfo> getCameraSpecificAppsInfo(Context context) {
        List<ResolveInfo> resolveInfo = new ArrayList<>();
        if (context == null) {
            return resolveInfo;
        }
        PackageManager pm = context.getPackageManager();
        for (String packageName : CAMERA_SPECIFIC_APPS) {
            resolveInfo.addAll(getCameraSpecificAppInfo(packageName, pm));
        }
        return resolveInfo;
    }

    private static List<ResolveInfo> getCameraSpecificAppInfo(String packageName, PackageManager pm) {
        Intent specificCameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        specificCameraApp.setPackage(packageName);
        return pm.queryIntentActivities(specificCameraApp, 0);
    }

    public static Bitmap getImageFromResult(Context context, int resultCode,
                                            Intent imageReturnedIntent) throws Exception {
        Bitmap bm = null;
        if (resultCode == Activity.RESULT_OK) {
            boolean isCamera = isFromCamera(imageReturnedIntent);
            Uri selectedImage = isCamera ? Uri.fromFile(getTempFile(context)) : imageReturnedIntent.getData();
            bm = getImageResized(context, selectedImage);
            int rotation = getRotation(context, selectedImage, isCamera);
            bm = rotate(bm, rotation);
        }
        return bm;
    }

    public static File getImageFileToUpload(Context context, int resultCode, Intent imageReturnedIntent) {
        if (resultCode == Activity.RESULT_OK) {
            if (isFromCamera(imageReturnedIntent))
                if (Build.VERSION.SDK_INT >= limitSdkVersion) {
                    Bitmap bm = null;
                    try {
                        bm = ImagePicker.getImageResized(context, imageUri);
                        if(bm!=null){
                            int rotation = getRotation(context, imageUri, true);
                            bm = rotate(bm, rotation);
                        }
                        else {
                            if(imageReturnedIntent.getData()==null){
                                bm = (Bitmap)imageReturnedIntent.getExtras().get("data");
                            }else{
                                try {
                                    bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageReturnedIntent.getData());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return convertBitmapToFile(context, bm,
                                "upload_" + Calendar.getInstance().getTimeInMillis() + ".jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                } else {
                    return getTempFile(context);
                }
            return new File(getAbsolutePathFromUri(imageReturnedIntent.getData(), context));
        }
        return null;
    }

    private static boolean isFromCamera(Intent imageReturnedIntent) {
        return !lastTempImg.isEmpty() && (imageReturnedIntent == null || imageReturnedIntent.getData() == null
                || imageReturnedIntent.getData().toString().contains(lastTempImg));
    }

    private static File getTempFile(Context context) {
        return new File(context.getExternalFilesDir(null), lastTempImg);
    }

    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) throws Exception{
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }

        return BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
    }

    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    public static Bitmap getImageResized(Context context, Uri selectedImage) throws Exception{
        Bitmap bm;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm.getWidth() < DEFAULT_MIN_WIDTH_QUALITY && i < sampleSizes.length);
        return bm;
    }


    public static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        return isCamera ? getRotationFromCamera(context, imageUri) : getRotationFromGallery(context, imageUri);
    }

    private static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageFile, null);
            ExifInterface exif = new ExifInterface(lastTempImg);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        try (Cursor cursor = context.getContentResolver().query(imageUri, columns, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    public static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        }
        return bm;
    }

    private static String getAbsolutePathFromUri(Uri uri, Context context) {
        String result = "";
        String documentID;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String[] pathParts = uri.getPath().split("/");
            documentID = pathParts[pathParts.length - 1];
        } else {
            String[] pathSegments = uri.getLastPathSegment().split(":");
            documentID = pathSegments[pathSegments.length - 1];
        }
        String mediaPath = MediaStore.Images.Media.DATA;
        Cursor imageCursor = context.getContentResolver().query(uri, new String[]{mediaPath}, MediaStore.Images.Media._ID + "=" + documentID, null, null);
        if (imageCursor != null) {
            if (imageCursor.moveToFirst()) {
                result = imageCursor.getString(imageCursor.getColumnIndex(mediaPath));
            }
            imageCursor.close();
        }
        return result;
    }
}