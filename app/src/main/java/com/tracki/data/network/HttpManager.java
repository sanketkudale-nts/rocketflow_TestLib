package com.tracki.data.network;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.gson.Gson;
import com.tracki.BuildConfig;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.local.prefs.PostFileErrorToServerWork;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.local.prefs.StartIdealTrackWork;
import com.tracki.data.model.request.FileListRequest;
import com.tracki.data.model.request.UpdateFileRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.ApiErrorRequest;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Rahul Abrol on 4/6/18.
 */
public class HttpManager {

    private static final String TAG = HttpManager.class.getSimpleName();
    private static final MediaType JSON = MediaType.parse("application/json; charset=UTF-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private static HttpManager instance;
    private final PreferencesHelper preferencesHelper;
    private OkHttpClient client;
    private Map<String, String> commonHeaders;
    private Context context;
    private String app_key, cert;

    /**
     * constructor of this class.
     */
    private HttpManager(Context context, PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
        this.context = context;
        client = new OkHttpClient.Builder()
                .build();
        app_key = context.getString(R.string.app_key);
        cert = context.getString(R.string.certificate_hash);
        commonHeaders = CommonUtils.buildDeviceHeader(context);

    }

    /**
     * @return instance of this class.
     */
    public static HttpManager getInstance(Context context, PreferencesHelper preferencesHelper) {
        if (instance == null) {
            instance = new HttpManager(context, preferencesHelper);
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    /**
     * POST URL.
     *
     * @param data the data
     * @return the string
     */
    String postURL(String data, Api api) throws IOException {
        String responseS;
        Log.d(api.getName().name(), api.getUrl());
        Log.d(api.getName().name(), data);
        RequestBody body = RequestBody.create(JSON, data);
        Request.Builder requestBuilder = new Request.Builder()
                .url(Objects.requireNonNull(api.getUrl()))
                .post(body);
        requestBuilder.addHeader("Content-Type", "application/json");
        buildHeaders(requestBuilder);

        OkHttpClient timeOut = getOkHttpClient(800);

        Response response = timeOut.newCall(requestBuilder.build()).execute();
        responseS = response.body().string();
        Log.d(api.getName().name(), responseS);
        return responseS;
    }

    /**
     * POST URL.
     *
     * @param data the data
     * @return the string
     */
    String deletePostURL(String data, Api api) throws IOException {
        String responseS;
        Log.d(api.getName().name(), api.getUrl());
        Log.d(api.getName().name(), data);
        RequestBody body = RequestBody.create(JSON, data);
        Request.Builder requestBuilder = new Request.Builder()
                .url(Objects.requireNonNull(api.getUrl()))
                .delete(body);
        requestBuilder.addHeader("Content-Type", "application/json");
        buildHeaders(requestBuilder);

        OkHttpClient timeOut = getOkHttpClient(800);

        Response response = timeOut.newCall(requestBuilder.build()).execute();
        responseS = response.body().string();
        Log.d(api.getName().name(), responseS);
        return responseS;
    }

    /**
     * PUT URL for update request.
     *
     * @param data the data
     * @return the string
     */
    String putURL(String data, Api api) throws IOException {
        String responseS;
        Log.d(api.getName().name(), api.getUrl());
        Log.d(api.getName().name(), data);
        RequestBody body = RequestBody.create(JSON, data);
        Request.Builder requestBuilder = new Request.Builder()
                .url(Objects.requireNonNull(api.getUrl()))
//                .header("Connection", "close") //close the connection
                .put(body);
        requestBuilder.addHeader("Content-Type", "application/json");
        buildHeaders(requestBuilder);

        OkHttpClient timeOut = getOkHttpClient(api.getTimeOut());
        Response response = timeOut.newCall(requestBuilder.build()).execute();
        responseS = response.body().string();
        Log.d(api.getName().name(), responseS);
        return responseS;
    }

    /**
     * Method used to set up the okhttp client to request with all required params
     *
     * @param timeOutInSec time in sec for setting request timeout
     * @return @{@link OkHttpClient} instance
     */
    private OkHttpClient getOkHttpClient(int timeOutInSec) {
        return client.newBuilder()
                .readTimeout(timeOutInSec * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOutInSec * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(timeOutInSec * 1000, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggingInterceptor())
                .retryOnConnectionFailure(true)
//                .cache(new Cache(context.getCacheDir(), cacheSize)
                .build();
    }


    String deleteUrl(Api api) throws IOException {
        String responseS;
        Log.d(api.getName().name(), api.getUrl());
        Request.Builder requestBuilder = new Request.Builder()
                .url(Objects.requireNonNull(api.getUrl()))
                .delete();
//                .header("Connection", "close"); //close the connection;


        requestBuilder.addHeader("Content-Type", "application/json");
        buildHeaders(requestBuilder);

        OkHttpClient timeOut = getOkHttpClient(api.getTimeOut());
        Response response = timeOut.newCall(requestBuilder.build()).execute();
        responseS = response.body().string();
        Log.i(api.getName().name(), responseS);
        return responseS;
    }

    /**
     * Get method URL.
     *
     * @return the url
     * @throws IOException Signals that an I/O exception has occurred.
     */
    String getURL(Api api) throws IOException {
        String responseS;
        Log.d(api.getName().name(), api.getUrl());
        Request.Builder requestBuilder = new Request.Builder()
                .url(Objects.requireNonNull(api.getUrl()));
//                .header("Connection", "close"); //close the connection;


        requestBuilder.addHeader("Content-Type", "application/json");
        buildHeaders(requestBuilder);

        OkHttpClient timeOut = getOkHttpClient(api.getTimeOut());
        Response response = timeOut.newCall(requestBuilder.build()).execute();
        responseS = response.body().string();
        Log.i(api.getName().name(), responseS);
        return responseS;
    }

    public String getRequest(Api api) throws IOException {
        String responseS;
        Log.d(api.getName().name(), api.getUrl());
        Request.Builder requestBuilder = new Request.Builder()
                .url(Objects.requireNonNull(api.getUrl()));
//                .header("Connection", "close"); //close the connection;


        requestBuilder.addHeader("Content-Type", "application/json");
        buildHeaders(requestBuilder);

        OkHttpClient timeOut = getOkHttpClient(api.getTimeOut());
        Response response = timeOut.newCall(requestBuilder.build()).execute();
        responseS = response.body().string().toString();
        Log.e("response string", "Value " + responseS);
        Log.i(api.getName().name(), responseS);
        return responseS;
    }

    /**
     * Method used to post a single image file  to server.
     *
     * @param updateFileRequest model for request
     * @param api               api of model
     * @return response in string
     * @throws IOException exception
     */
    public String postURLBAB(UpdateFileRequest updateFileRequest, Api api) throws IOException {
        String responseS;
        OkHttpClient timeOut = getOkHttpClient(api.getTimeOut());
        timeOut.retryOnConnectionFailure();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .addFormDataPart("id", updateFileRequest.getId())
                .addFormDataPart("type", updateFileRequest.getType().name())
                .addFormDataPart("file", "file.png", RequestBody.create(MEDIA_TYPE_PNG, updateFileRequest.getFile()))
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(Objects.requireNonNull(api.getUrl()))
                .header("Connection", "close") //close the connection
                .post(formBody);
        buildHeaders(requestBuilder);
        Response response = timeOut.newCall(requestBuilder.build()).execute();
        responseS = response.body().string();
        Log.i(api.getName().name(), responseS);
        return responseS;
    }

    /**
     * Here I am uploading MultipleImages from List of @{@link FileListRequest}
     * Sending FileListRequest with URL and Caption of Photo...
     *
     * @param api         api we need to hit
     * @param fileListMap hashMap of files in map
     * @return Sting
     */
    public String uploadMultipleFiles(Api api, HashMap<String, List<File>> fileListMap)
            throws IOException {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "map_size: ---------->" + fileListMap.size());
        }
        String responseS;
        OkHttpClient timeOut = getOkHttpClient(api.getTimeOut());

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        int j = 0;
        List<File> fileList;
        for (Map.Entry<String, List<File>> entry : fileListMap.entrySet()) {
            String key = entry.getKey();
            fileList = entry.getValue();

            Log.d(TAG, "uploadMultipleFiles: ---------->" + key);
            multipartBuilder.addFormDataPart("data[" + j + "].name", key);

            if (fileList != null) {
                for (int i = 0; i < fileList.size(); i++) {

                    File fileListRequest = fileList.get(i);

                    if (fileListRequest.exists()) {
                        String ext = CommonUtils.getExtension(fileListRequest.getName());
                        /* Changing Media Type whether JPEG or PNG **/
                        final MediaType MEDIA_TYPE = ext.endsWith("png") ? MEDIA_TYPE_PNG :
                                ext.endsWith("jpg") ? MEDIA_TYPE_JPG : MEDIA_TYPE_JPEG;
                        Log.e(TAG, "File Name:----> " + "data[" + j + "].name" + "<---->" + "data[" + j + "].files[" + i + "]");

                        multipartBuilder.addFormDataPart("data[" + j + "].files[" + i + "]",
                                fileListRequest.getName(),
                                RequestBody.create(MEDIA_TYPE, fileListRequest));

                    }
                }
            }
            j++;
        }
        RequestBody requestBody = multipartBuilder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(Objects.requireNonNull(api.getUrl()))
                .post(requestBody);
        buildHeaders(requestBuilder);
        // requestBuilder.addHeader("Cache-Control","no-cache");
//        Log.i(api.getName().name(), requestBuilder.build().body().);


        Response response = timeOut.newCall(requestBuilder.build()).execute();
        responseS = response.body().string();
        if (BuildConfig.DEBUG) {
            Log.d(api.getName().name(), fileListMap.toString());
            Log.i(api.getName().name(), "" + response.code());
            Log.i(api.getName().name(), responseS);

        }

        return responseS;
    }

    /**
     * Used to build token for api security.
     *
     * @param t timestamp
     * @return token string
     */
    private String build(String t) {
//        Api Token = (Base 64 of ( SHA 256 of ( App_Key+ Certificate Hash+Access Id + Login Token + Timestamp)))
        String accessId = null;
        String loginToken = null;

        String token = app_key + cert;
        if (preferencesHelper.getAccessId() != null) {
            //accessId = AppConstants.Extra.ACCESS_ID;
            accessId = preferencesHelper.getAccessId();
            token = token + accessId + t;
        }
        if (preferencesHelper.getLoginToken() != null) {
            loginToken = preferencesHelper.getLoginToken();
            token = token + loginToken;
        }

//        Log.e(TAG, "Token variables are: app_key " + app_key + " cert " + cert +
//                " accessId " + accessId + " login_token " + loginToken + " timestamp " + t);
//        Log.e(TAG, "Token generated is: " + token);
        return CommonUtils.sig(token);
    }

    /**
     * Method used to build headers with required params.
     *
     * @param requestBuilder builder
     */
    private void buildHeaders(Request.Builder requestBuilder) {
        String t = String.valueOf(System.currentTimeMillis());

        /*
            Content-Type:application/json
            tracki-ai:AIoB31j9yI
            tracki-at:243234
            tracki-ts:23424
            device-id:4234
            login-token:240179d3-d515-4a31-8a2a-821238c3c1a9
            app-id:3213123
         */
        String loginToken = preferencesHelper.getLoginToken();
        String deviceId = preferencesHelper.getDeviceId();
        String fcmToken = preferencesHelper.getFcmToken();
        String accessId = preferencesHelper.getAccessId();

        boolean firstInstall = preferencesHelper.isFirstTimeInstallFlag();
        int s;
        //if user is hitting api for the first time
        // after installation or logout then it returns
        // false we mark it as fresh install and save with us
        // after that we always get true until user logout
        // or uninstall the app.
        if (firstInstall) {
            s = 0;
        } else {
            s = 1;
            preferencesHelper.setFirstTimeInstallFlag(true);
        }
        requestBuilder.addHeader("first-install", "" + s);
        requestBuilder.addHeader("device-id", deviceId);

        requestBuilder.addHeader("tracki-at", build(t));
        requestBuilder.addHeader("tracki-ts", "" + t);
        //if (accessId != null) {
        requestBuilder.addHeader("tracki-ai", accessId);
        //}
        //requestBuilder.addHeader("Connection", "close");

//        if(!preferencesHelper.getUserType().isEmpty()) {
//            //UserType userType= preferencesHelper.getUserType();
//            //requestBuilder.addHeader("app-type", AppConstants.Extra.APP_TYPE_DRIVER);
//            requestBuilder.addHeader("app-type", preferencesHelper.getUserType());
//        }
        requestBuilder.addHeader("app-type", AppConstants.Extra.APP_TYPE_ADMIN);
        if (loginToken != null) {
            requestBuilder.addHeader("login-token", loginToken);
        }
        if (fcmToken != null) {
            requestBuilder.addHeader("app-id", fcmToken);

        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "FCM Token:- " + fcmToken);
            Log.d(TAG, "login Token:- " + loginToken);
            Log.e(TAG, "login Token:- " + loginToken);
            Log.d(TAG, "device id:- " + deviceId);
            Log.d(TAG, "access id:- " + accessId);
        }

        for (Map.Entry<String, String> header : commonHeaders.entrySet()) {
            if (header.getValue() != null)
                requestBuilder.addHeader(header.getKey(), header.getValue());
        }

    }

    private class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            if (BuildConfig.DEBUG) {
                Log.e("Sending request on",
                        " Url: " + request.url()
                                + " Connection: " + chain.connectTimeoutMillis()
                                + " Headers " + request.headers().toString());
            }
            okhttp3.Response response = chain.proceed(request);

            if (BuildConfig.DEBUG) {
                Log.e("error_code", "" + response.code());
//                Log.e("error", "" +  response.body().string());
            }

            if (response.code() != 404 && response.code() != 200) {
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                Data.Builder data = new Data.Builder();
                try {
                    if (response.body() != null) {
                        data.putString("error", response.body().string());
                    }
                } catch (Exception e) {

                }

                data.putInt("code", response.code());
                WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(PostFileErrorToServerWork.class)
                        .setConstraints(constraints)
                        .setInputData(data.build()).build();

                WorkManager workManager = WorkManager.getInstance();
                workManager.enqueue(myWorkRequest);
//                CommonUtils.errorString=response.body().string();
            }


            return response;
        }
    }

}
