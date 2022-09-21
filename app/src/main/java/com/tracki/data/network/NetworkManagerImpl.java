package com.tracki.data.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.tracki.BuildConfig;
import com.tracki.data.model.BaseResponse;
import com.tracki.data.model.request.AddEmployeeRequest;
import com.tracki.data.model.request.AddressInfo;
import com.tracki.data.model.request.PlaceRequest;
import com.tracki.data.model.request.UpdateFileRequest;
import com.tracki.data.model.request.UserGetRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.ui.addplace.AddPlaceRequest;
import com.tracki.ui.category.AddCategoryRequest;
import com.tracki.ui.productdetails.StockEntryRequest;
import com.tracki.ui.products.AddProductRequest;
import com.tracki.ui.selectorder.CataLogProductCategory;
import com.tracki.ui.selectorder.CatalogProduct;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Singleton;

/**
 * Created by rahul on 29/11/18
 */
@Singleton
public class NetworkManagerImpl implements NetworkManager {

    /**
     * Method used to hit post api.
     *
     * @param apiCallback callback
     * @param httpManager http manager
     * @param api         aipUrl
     * @param data        data
     */
    private void postApiCall(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        Log.e("urlCheckPost",""+api);
        new NetworkAsync(httpManager, apiCallback, data, api, MethodType.POST)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Method used to hit post api.
     *
     * @param apiCallback callback
     * @param httpManager http manager
     * @param api         aipUrl
     * @param data        data
     */
    private void putApiCall(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        new NetworkAsync(httpManager, apiCallback, data, api, MethodType.PUT)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    private void deletePostApiCall(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        new NetworkAsync(httpManager, apiCallback, data, api, MethodType.DELETE_POST)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void deleteApiCall(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        new NetworkAsync(httpManager, apiCallback, null, api, MethodType.DELETE)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initiateDeviceChangeCall(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        new NetworkAsync(httpManager,apiCallback,null,api, MethodType.GET).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Method used for get api hit.
     *
     * @param apiCallback callback
     * @param httpManager manager
     * @param api         apiUrls
     */
    private void getApiCall(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        new NetworkAsync(httpManager, apiCallback, null, api, MethodType.GET)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void getConfig(HttpManager httpManager, ApiCallback callBack) {
        Api url = new Api();
        url.setName(ApiType.CONFIG);
        url.setUrl(BuildConfig.BASE_URL + "config");
        url.setTimeOut(12);
        url.setCacheable(true);
        url.setVersion("1.0");
        getApiCall(callBack, httpManager, url);
    }

    @Override
    public void login(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void verifyOtp(HttpManager httpManager, ApiCallback apiCallback, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void signUp(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void buddyListing(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void fleetListing(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void createTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getUserListForSuggestion(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getRegionList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getStateList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getCityList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getHubList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void logout(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void acceptTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getTaskById(ApiCallback apiCallBack, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallBack, httpManager, api, data);
    }

    @Override
    public void rejectTask(@NotNull ApiCallback apiCallBack, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        postApiCall(apiCallBack, httpManager, api, data);
    }

    @Override
    public void deleteBuddy(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void deleteFleet(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void changeFleetStatus(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getInvites(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void acceptRejectRequest(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object request, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, request);
    }

    @Override
    public void startTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void cancelTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void endTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void callHomeApi(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateTask(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getNotifications(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void clearNotifications(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void cancelTrackingRequest(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void callDashboardApi(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void uploadFiles(@NotNull ApiCallback apiCallback, @Nullable HttpManager httpManager, @NotNull Object data, @Nullable Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getSettings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getBalance(@NonNull ApiCallback apiCallback, @NonNull HttpManager httpManager, @NonNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void saveSettings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getSharableLink(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void uploadFormList(@NotNull ApiCallback apiCallback, @Nullable HttpManager httpManager, @NotNull Object data, @Nullable Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void arriveReachTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getMyEarnings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void checkInventory(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void linkInventory(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void employeeListInEvents(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getAllPost(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getFeedDetails(@NonNull ApiCallback apiCallback, @NonNull HttpManager httpManager, @NonNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getQrCodeValue(@NonNull ApiCallback apiCallback, @NonNull HttpManager httpManager, @NonNull Api api, @NonNull Object data) {
        postApiCall(apiCallback,httpManager,api,data);
        //getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getComments(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void updateReaction(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getAllLikes(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void teamAttendance(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getCategoryGroup(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getTaskByDate(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void addBuddyInvitation(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void addFleet(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateBuddy(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getProfile(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void updateProfile(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        putApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateProfilePic(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateDocuments(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        putApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void addDocuments(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getTasksList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override
    public void punch(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override
    public void markedOnlineOffLine(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void punchInPunchOutData(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void validateGeoPunchIn(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getUserLocation(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void deleteUserLocation(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateUserLocation(ApiCallback apiCallback, AddPlaceRequest addPlaceRequest, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, addPlaceRequest);
    }

    @Override
    public void getAttendance(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }


    @Override
    public void applyLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override
    public void applyLeaveEdit(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override
    public void getLeaveSummary(ApiCallback apiCallback, HttpManager httpManager, Api apiUrl) {
        getApiCall(apiCallback, httpManager, apiUrl);
    }

    @Override
    public void getServerTime(ApiCallback apiCallback, HttpManager httpManager, Api apiUrl) {
        getApiCall(apiCallback, httpManager, apiUrl);
    }


    @Override
    public void getMyLeaves(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override
    public void cancelLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override
    public void rejectLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override
    public void approveLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override
    public void getLeaveRequests(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);
    }

    @Override

    public void executeUpdateTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void executeMap(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @Nullable Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    public void heartBeat(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);

    }

    public void taskData(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl) {
        postApiCall(apiCallback, httpManager, apiUrl, data);

    }

    @Override
    public void getAccountList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void initiateSignUp(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateService(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getSavedServices(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void updateSavedServices(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getCart(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void applyCoupon(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void createOrder(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void postFileErrorToServer(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getInsights(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void addUser(ApiCallback apiCallback, AddEmployeeRequest data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateUser(ApiCallback apiCallback, AddEmployeeRequest data, HttpManager httpManager, Api api) {
        putApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getUserList(ApiCallback apiCallback, HttpManager httpManager, Api api,UserGetRequest userGetRquest) {
        postApiCall(apiCallback, httpManager, api,userGetRquest);
    }

    @Override
    public void getProductsCategory(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getProductsSubCategory(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getProductTerminalCatgeory(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getProductDetails(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getStockEntry(ApiCallback apiCallback, StockEntryRequest data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getStockHistoryDetails(ApiCallback apiCallback, StockEntryRequest data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getProducts(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getUnits(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void searchUser(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getUserDetails(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void deleteUser(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        deleteApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getUserAddress(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void deleteAddress(ApiCallback apiCallback, PlaceRequest data, HttpManager httpManager, Api api) {
        deletePostApiCall(apiCallback, httpManager, api,data);
    }

    @Override
    public void updateAddress(ApiCallback apiCallback, AddressInfo data, HttpManager httpManager, Api api) {
        putApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void addAddress(ApiCallback apiCallback, AddressInfo data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void addProductCategory(ApiCallback apiCallback, AddCategoryRequest data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateProductCategory(ApiCallback apiCallback, AddCategoryRequest data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void addProduct(ApiCallback apiCallback, AddProductRequest data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateProduct(ApiCallback apiCallback, AddProductRequest data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void deleteProductCategory(ApiCallback apiCallback, CataLogProductCategory data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void deleteProduct(ApiCallback apiCallback, CatalogProduct data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void updateStatusProductCategory(ApiCallback apiCallback, CataLogProductCategory data, HttpManager httpManager, Api api) {
        postApiCall(apiCallback, httpManager, api, data);
    }

    @Override
    public void getDocumentById(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void getDocumentByType(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        getApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void deleteDocument(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        deleteApiCall(apiCallback, httpManager, api);
    }

    @Override
    public void initiateDeviceChange(ApiCallback apiCallback, HttpManager httpManager, Api api) {
        initiateDeviceChangeCall(apiCallback,httpManager,api);
    }


    private enum MethodType {
        GET, POST, DELETE, PUT,DELETE_POST
    }

    /**
     * Class used to call apis in background thread.
     */
    private static class NetworkAsync extends AsyncTask<Void, Void, Object> {
        private static final String TAG = NetworkAsync.class.getSimpleName();
        @SuppressLint("StaticFieldLeak")
        private final Context context;
        private HttpManager httpManager;
        private Object postData;
        private Api api;
        private MethodType methodType;
        private ApiCallback callBack;
        private SoftReference<ApiCallback> apiCallbackSoftReference;
        private APIError apiError;

        public String getBaseResponse(String message) {
            BaseResponse response = new BaseResponse();
            response.setSuccessful(false);
            response.setResponseMsg(message);
            JSONConverter jsonConverter = new JSONConverter();
            return jsonConverter.objectToJson(response);
        }

        NetworkAsync(HttpManager httpManager, ApiCallback callBack,
                     Object postData, Api url, MethodType methodType) {
            this.httpManager = httpManager;
            context = httpManager.getContext();
            Log.e("CALLBACK", "----------->>" + callBack);
            this.callBack = callBack;
            this.apiCallbackSoftReference = new SoftReference<>(callBack);
            this.postData = postData;
            this.api = url;
            if (url != null && url.getUrl() != null)
                Log.e("url=>", url.getUrl());
            JSONConverter jsonConverter = new JSONConverter();
            String json = jsonConverter.objectToJson(postData);
            Log.e("request", "----------->>" + json);
            CommonUtils.showLogMessage("e", "methodType", "----------->>" + methodType.name());
            this.methodType = methodType;
        }

        @Override
        protected Object doInBackground(Void... voids) {
            String response = null;
            apiError = null;
            Gson gson = new Gson();
            try {
                switch (methodType) {
                    case DELETE: {
                        switch (Objects.requireNonNull(api.getName())) {
                            case DELETE_USER:
                            case DELETE_DOCUMENT:
                            case USER_ADDRESS_DELETE:
                                if (NetworkUtils.isNetworkConnected(context))
                                    response = httpManager.deleteUrl(api);
                                else {
                                    response = getBaseResponse(AppConstants.ALERT_NO_CONNECTION);
                                    apiError = new APIError(APIError.ErrorType.INTERNET_CONNECTION);
                                }
                                break;
                            default:
                                Log.e(TAG, "doInBackground() DELETE REQUEST: ApiType not handled here " + api.getName());
                                break;
                        }
                    }
                    break;
                    case GET: {
                        switch (Objects.requireNonNull(api.getName())) {
                            case MY_PROFILE:
                            case INVITATIONS:
//                                response = httpManager.getURL(api);
                                response = handleApiCache(true);
                                break;
                            case CONFIG:
                            case LOGOUT:
                            case NOTIFICATIONS:
                            case CLEAR_NOTIFICATIONS:
                            case SETTINGS:
                            case LEAVE_SUMMARY:
                            case EXECUTIVE_MAP:
                            case GET_LAST_PUNCH:
                            case USER_LOCATIONS:
                            case GET_SUBORDINTES:
                            case USER_GROUP_MAP:
                            case GET_CATEGORY_BY_GROUP:
                            case GET_LIKES:
                            case GET_COMMENTS:
                            case GET_SERVICE_PREF:
                            case GET_INSIGHTS:
                            case USER_DETAIL:
                            case GET_SEARCH_USER:
                            case USER_ADDRESS_GET:
                            case SERVER_TIME:
                            case PRODUCT_CATEGORIES:
                            case GET_PRODUCTS:
                            case GET_TASK_PRODUCTS:
                            case SEARCH_PRODUCT:
                            case GET_UNITS:
                            case SUB_CATEGORIES:
                            case TERMINAL_CATEGORIES:
                            case PRODUCT_DETAIL:
                            case INITIATE_DEVICE_CHANGE:
                            case GET_POST_DETAIL:
                            case USER_SEARCH:
                            case GET_DOCUMENT_BY_TYPE:
                            case GET_DOCUMENT_BY_ID:
                            case TASK_PRODUCT_CATEGORIES:
                            case GET_WALLET_BALANCE:
                            case SCANNER:
                                if (NetworkUtils.isNetworkConnected(context))
                                    response = httpManager.getURL(api);
                                else {
                                    response = getBaseResponse(AppConstants.ALERT_NO_CONNECTION);
                                    apiError = new APIError(APIError.ErrorType.INTERNET_CONNECTION);
                                }
                                break;
                            default:
                                Log.e(TAG, "doInBackground() GET REQUEST: ApiType not handled here " + api.getName());
                                break;
                        }
                    }
                    break;
                    case DELETE_POST: {
                        switch (Objects.requireNonNull(api.getName())) {
                            case USER_ADDRESS_DELETE:
                                if (NetworkUtils.isNetworkConnected(context))
                                    response = httpManager.deletePostURL(gson.toJson(postData), api);
                                else {
                                    response = getBaseResponse(AppConstants.ALERT_NO_CONNECTION);
                                    apiError = new APIError(APIError.ErrorType.INTERNET_CONNECTION);
                                }
                                break;
                            default:
                                Log.e(TAG, "doInBackground() DELETE_POST REQUEST: ApiType not handled here " + api.getName());
                                break;
                        }
                    }
                    break;

                    case POST: {
                        switch (Objects.requireNonNull(api.getName())) {
                            case LOGIN:
                            case VERIFY_MOBILE:
                            case SIGNUP:
                            case ADD_BUDDY:
                            case ADD_FLEET:
                            case CREATE_TASK:
                            case UPDATE_TASK:
                            case UPDATE_BUDDY:
                            case UPDATE_FLEET:
                            case ACCEPT_TASK:
                            case REJECT_TASK:
                            case GET_TASK_BY_ID:
                            case DELETE_BUDDY:
                            case DELETE_FLEET:
                            case ACCEPT_INVITATION:
                            case REJECT_INVITATION:
                            case START_TASK:
                            case CANCEL_TASK:
                            case END_TASK:
                            case CANCEL_REQUEST:
                            case SAVE_SETTINGS:
                            case SHARE_TRIP:
                            case UPDATE_TASK_DATA:
                            case ARRIVE_REACH_TASK:
                            case MY_EARNINGS:
                            case TASK_BY_DATE:
                            case PUNCH:
                            case GET_ATTENDANCE:
                            case APPLY_LEAVE:
                            case LEAVE_HISTORY:
                            case EDIT_LEAVE:
                            case CANCEL_LEAVE:
                            case APPROVE_LEAVE:
                            case REJECT_LEAVE:
                            case APPROVALS:
                            case EXECUTE_UPDATE:
                            case GET_TASK_DATA:
                            case UPDATE_USER_LOCATION:
                            case REMOVE_USER_LOCATION:
                            case DASHBOARD_TASKS:
                            case CHANGE_STATUS:
                            case CHANGE_FLEET_STATUS:
                            case EXECUTIVE_PAYOUTS:
                            case GET_REGIONS:
                            case GET_CITIES:
                            case GET_HUBS:
                            case GET_USERS:
                            case GET_STATES:
                            case GET_TASK_INVENTORIES:
                            case LINK_INVENTORY:
                            case WALLET_TRANSACTIONS:
                            case TEAM_ATTENDANCE:
                            case TEAM_ATTENDANCES:
                            case GET_ALL_POST:
                            case UPDATE_REACTION:
                            case GET_SEARCH_USER:
                            case USER_ATTENDANCE_BREAKUP:
                            case USER_ATTENDANCE_MAP:
                            case CREATE_DRAFT:
                            case SIGNUP_ACCOUNTS:
                            case INITIATE_SIGNUP:
                            case SIGNUP_AND_UPDATE_PREFERENCE:
                            case UPDATE_SERVICE_PREF:
                            case USER_ATTENDANCE_LOCATION:
                            case ADD_USER:
                            case USER_ADDRESS_ADD:
                            case VIEW_CART:
                            case VALIDATE_COUPON:
                            case CREATE_ORDER:
                            case ADD_PRODUCT_CATEGORY:
                            case UPDATE_PRODUCT_CATEGORY:
                            case DELETE_PRODUCT_CATEGORY:
                            case UPDATE_PRODUCT_CATEGORY_STATUS:
                            case APP_ERROR:
                            case ADD_PRODUCT:
                            case UPDATE_PRODUCT:
                            case DELETE_PRODUCT:
                            case UPDATE_PRODUCT_STATUS:
                            case DAILY_STOCK_HISTORY:
                            case STOCK_HISTORY_DETAIL:
                            case ENTITY_SCANNER:
                            case CREATE_DOCUMENT:
                                if (NetworkUtils.isNetworkConnected(context))
                                    response = httpManager.postURL(gson.toJson(postData), api);
                                else {
                                    response = getBaseResponse(AppConstants.ALERT_NO_CONNECTION);
                                    apiError = new APIError(APIError.ErrorType.INTERNET_CONNECTION);
                                }
                                break;
                            case BUDDIES:
                            case FLEETS:
                            case TASKS:
                            case HOME:
                            case DASHBOARD:
//                                response = httpManager.postURL(gson.toJson(postData), api);
                                response = handleApiCache(false);
                                break;
                            case UPLOAD_FILE_AGAINEST_ENTITY:
                                if (NetworkUtils.isNetworkConnected(context) && NetworkUtils.isConnectedFast(context))
                                    response = httpManager.postURLBAB((UpdateFileRequest) postData, api);
                                else {
                                    response = getBaseResponse(AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE);
                                    apiError = new APIError(APIError.ErrorType.SLOW_INTERNET_CONNECTION);
                                }
                                break;
                            case UPLOAD_MEDIA:
                            case UPLOAD_FILE:
                                if (NetworkUtils.isNetworkConnected(context) && NetworkUtils.isConnectedFast(context)) {
                                    response = httpManager.uploadMultipleFiles(api, (HashMap<String, List<File>>) postData);
                                    for (Map.Entry<String, List<File>> entry : ((HashMap<String, List<File>>) postData).entrySet()) {
                                        List<File> list = entry.getValue();
                                        for (File file : list) {
                                            if (file.exists()) {
                                                try {
                                                    file.delete();
                                                    Log.e(TAG, "File Delete Successfully ");
                                                } catch (Exception e) {

                                                }

                                            }
                                        }
                                    }

                                    // Do things with the list
                                } else {
                                    response = getBaseResponse(AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE);
                                    apiError = new APIError(APIError.ErrorType.SLOW_INTERNET_CONNECTION);
                                }
                                break;
                            default:
                                Log.e(TAG, "doInBackground POST REQUEST: ApiType not found " + api.getName());
                                break;
                        }
                    }
                    break;
                    case PUT: {
                        switch (Objects.requireNonNull(api.getName())) {
                            case UPDATE_USER:
                                if (NetworkUtils.isNetworkConnected(context))
                                    response = httpManager.putURL(gson.toJson(postData), api);
                                else {
                                    response = getBaseResponse(AppConstants.ALERT_NO_CONNECTION);
                                    apiError = new APIError(APIError.ErrorType.INTERNET_CONNECTION);
                                }
                                break;
                            case UPDATE_PROFILE:
                                if (NetworkUtils.isNetworkConnected(context)) {
                                    if (Objects.requireNonNull(api.getName()) == ApiType.UPDATE_PROFILE) {
                                        response = httpManager.putURL(gson.toJson(postData), api);
                                    } else {
                                        Log.e(TAG, "doInBackground PUT REQUEST: ApiType not found " + api.getName());
                                    }
                                } else {
                                    response = getBaseResponse(AppConstants.ALERT_NO_CONNECTION);
                                    apiError = new APIError(APIError.ErrorType.INTERNET_CONNECTION);
                                }
                                break;
                            case UPDATE_DOCUMENT:
                                if (NetworkUtils.isNetworkConnected(context)) {
                                    if (Objects.requireNonNull(api.getName()) == ApiType.UPDATE_DOCUMENT) {
                                        response = httpManager.putURL(gson.toJson(postData), api);
                                    } else {
                                        Log.e(TAG, "doInBackground PUT REQUEST: ApiType not found " + api.getName());
                                    }
                                } else {
                                    response = getBaseResponse(AppConstants.ALERT_NO_CONNECTION);
                                    apiError = new APIError(APIError.ErrorType.INTERNET_CONNECTION);
                                }
                                break;

                            case USER_ADDRESS_UPDATE:
                                if (NetworkUtils.isNetworkConnected(context)) {
                                    if (Objects.requireNonNull(api.getName()) == ApiType.USER_ADDRESS_UPDATE) {
                                        response = httpManager.putURL(gson.toJson(postData), api);
                                    } else {
                                        Log.e(TAG, "doInBackground PUT REQUEST: ApiType not found " + api.getName());
                                    }
                                } else {
                                    response = getBaseResponse(AppConstants.ALERT_NO_CONNECTION);
                                    apiError = new APIError(APIError.ErrorType.INTERNET_CONNECTION);
                                }
                                break;
                            default:
                                Log.e(TAG, "doInBackground() PUT REQUEST: ApiType not handled here " + api.getName());
                                break;

                        }
                        break;
                    }
                }
//                if(api.getName()==ApiType.EXECUTE_UPDATE){
//                    String url=api.getUrl();
//                    String request=gson.toJson(postData);
//                    ScreenUtils.writeToFile("EXECUTE_UPDATE","Url=> "+url+"\n"+"Request=>"+request+"\n"+"Response=>"+response.toString());
//                }
//
//                if(api.getName()==ApiType.UPDATE_TASK_DATA){
//                    String url=api.getUrl();
//                    String request=gson.toJson(postData);
//                    ScreenUtils.writeToFile("UPDATE_TASK","Url=> "+url+"\n"+"Request=> "+request+"\n"+"Response=> "+response.toString());
//                    //ScreenUtils.writeToFile("UPDATE_TASK",response.toString());
//                }
//                if(api.getName()==ApiType.CONFIG)
//                   ScreenUtils.writeToFile("ConfigJson",response.toString());
                CommonUtils.showLogMessage("e", "response=>", response);
                // Log.e("response=>",response);
                // Log.d("response=>",response);
            } catch (SocketTimeoutException var5) {
                Log.e(TAG, "doInBackground: " + var5);
                apiError = new APIError(APIError.ErrorType.TIMEOUT);
            } catch (SocketException | UnknownHostException | UnsupportedEncodingException var6) {
                Log.e(TAG, "doInBackground: " + var6);
                apiError = new APIError(APIError.ErrorType.NETWORK_FAIL);
            } catch (ParseException e) {
                Log.e(TAG, "doInBackground: " + e);
                apiError = new APIError(APIError.ErrorType.SERVER_DOWN);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: " + e);
                apiError = new APIError(APIError.ErrorType.UNKNOWN_ERROR);
            }
            return response;
        }

        /**
         * Method used to handle whether we need to handle the cache for this api or not
         *
         * @return string response.
         * @throws IOException exception if any.
         */
        private String handleApiCache(boolean isGetRequest) throws IOException {
            String data;

            if (api.getCacheable()) {
                //if internet is connected then get from server else get from cache.
                if (NetworkUtils.isNetworkConnected(context)) {
                    data = hitApi(isGetRequest);
                } else {
                    data = CacheManager.Companion.getInstance(context)
                            .getFromCache(api.getName().name() + "_" + api.getAppendWithKey(), api.getVersion());
                }
                if (data != null) {
                    CacheManager.Companion.getInstance(context)
                            .putIntoCache(api.getName().name() + "_" + api.getAppendWithKey(), data, api.getVersion());
                } else {
                    //In case if data is not null then store into the cache.
                    data = hitApi(isGetRequest);
                    if (data != null) {
                        CacheManager.Companion.getInstance(context)
                                .putIntoCache(api.getName().name() + "_" + api.getAppendWithKey(), data, api.getVersion());
                    }
                }
            } else {
                data = hitApi(isGetRequest);
            }
            return data;
        }

        private String hitApi(boolean isGetRequest) throws IOException {
            if (isGetRequest) {
                return httpManager.getURL(api);
            } else {
                return httpManager.postURL(new Gson().toJson(postData), api);
            }
        }

        protected void onPostExecute(Object result) {
            ApiCallback apiCallback = apiCallbackSoftReference.get();
            if (apiCallback != null) {
                Log.e("SOFT CALLBACK", "----------->>" + callBack);
                apiCallback.onResponse(result, apiError);
            } else {
                if (callBack != null) {
                    Log.e("Normal CALLBACK", "----------->>" + callBack);
                    callBack.onResponse(result, apiError);
                }
            }
        }
    }
}
