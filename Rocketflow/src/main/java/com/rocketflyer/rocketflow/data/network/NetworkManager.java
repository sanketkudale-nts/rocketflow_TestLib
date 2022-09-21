package com.rocketflyer.rocketflow.data.network;


import com.tracki.data.model.request.AddEmployeeRequest;
import com.tracki.data.model.request.AddressInfo;
import com.tracki.data.model.request.PlaceRequest;
import com.tracki.data.model.request.UserGetRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.Place;
import com.tracki.ui.addplace.AddPlaceRequest;
import com.tracki.ui.category.AddCategoryRequest;
import com.tracki.ui.productdetails.StockEntryRequest;
import com.tracki.ui.products.AddProductRequest;
import com.tracki.ui.selectorder.CataLogProductCategory;
import com.tracki.ui.selectorder.CatalogProduct;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Rahul Abrol on 4/6/18.
 * <p>
 * Class uesd to connect.
 */
public interface NetworkManager {

    void getConfig(HttpManager httpManager, ApiCallback callBack);

    void login(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void verifyOtp(HttpManager httpManager, ApiCallback apiCallback, Object data, Api api);

    void signUp(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void buddyListing(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data);

    void addBuddyInvitation(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void addFleet(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void updateBuddy(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getProfile(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void updateProfile(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void updateProfilePic(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void updateDocuments(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void addDocuments(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getTasksList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl);

    void fleetListing(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void createTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getUserListForSuggestion(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getRegionList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getStateList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getCityList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getHubList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);
    void logout(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void acceptTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getTaskById(ApiCallback apiCallBack, HttpManager httpManager, Object data, Api api);

    void rejectTask(@NotNull ApiCallback apiCallBack, @NotNull HttpManager httpManager, @NotNull Object request, @NotNull Api api);

    void deleteBuddy(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data);

    void deleteFleet(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void changeFleetStatus(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getInvites(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api);

    void acceptRejectRequest(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object request, @NotNull Api api);

    void startTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void cancelTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void endTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void callHomeApi(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data);

    void updateTask(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api);

    void getNotifications(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api);

    void clearNotifications(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api);

    void cancelTrackingRequest(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void callDashboardApi(ApiCallback apiCallback, HttpManager httpManager, Api api, Object data);

    void uploadFiles(@NotNull ApiCallback apiCallback, @Nullable HttpManager httpManager, @NotNull Object list, @Nullable Api api);

    void getSettings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api);

    void getBalance(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api);

    void saveSettings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api);

    void getSharableLink(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api);

    void uploadFormList(@NotNull ApiCallback apiCallback, @Nullable HttpManager httpManager, @NotNull Object data, @Nullable Api api);

    void arriveReachTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getMyEarnings(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);

    void checkInventory(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);

    void linkInventory(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);

    void employeeListInEvents(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);

    void getAllPost(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);

    void getFeedDetails(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api);

    void getQrCodeValue(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Api api, @NotNull Object data);

    void getComments(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);

    void updateReaction(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);

    void getAllLikes(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);


    void teamAttendance(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);

    void getCategoryGroup(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, Object data, @NotNull Api api);


    void getTaskByDate(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @NotNull Object data, @NotNull Api api);

    void punch(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void markedOnlineOffLine(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void punchInPunchOutData(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void validateGeoPunchIn(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getUserLocation(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void deleteUserLocation(ApiCallback apiCallback, HttpManager httpManager, Api api, Object hub);

    void updateUserLocation(ApiCallback apiCallback, AddPlaceRequest addPlaceRequest, HttpManager httpManager, Api api);

    void getAttendance(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void applyLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void applyLeaveEdit(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getLeaveSummary(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getServerTime(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getMyLeaves(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void cancelLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void approveLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void rejectLeave(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getLeaveRequests(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);


    void executeUpdateTask(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void executeMap(@NotNull ApiCallback apiCallback, @NotNull HttpManager httpManager, @Nullable Api api);

    void heartBeat(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl);


    void taskData(ApiCallback apiCallback, HttpManager httpManager, Object data, Api apiUrl);

    void getAccountList(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void initiateSignUp(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void updateService(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getSavedServices(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void updateSavedServices(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getCart(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void applyCoupon(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void createOrder(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void postFileErrorToServer(ApiCallback apiCallback, HttpManager httpManager, Object data, Api api);

    void getInsights(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void addUser(ApiCallback apiCallback, AddEmployeeRequest addEmployeeRequest, HttpManager httpManager, Api api);

    void updateUser(ApiCallback apiCallback, AddEmployeeRequest addEmployeeRequest, HttpManager httpManager, Api api);

    void getUserList(ApiCallback apiCallback, HttpManager httpManager, Api api, UserGetRequest userGetRequest);

    void getProductsCategory(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getProductsSubCategory(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getProductTerminalCatgeory(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getProductDetails(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getStockEntry(ApiCallback apiCallback, StockEntryRequest data, HttpManager httpManager, Api api);

    void getStockHistoryDetails(ApiCallback apiCallback, StockEntryRequest data, HttpManager httpManager, Api api);

    void getProducts(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getUnits(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void searchUser(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getUserDetails(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void deleteUser(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getUserAddress(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void deleteAddress(ApiCallback apiCallback, PlaceRequest data, HttpManager httpManager, Api api);

    void updateAddress(ApiCallback apiCallback, AddressInfo data, HttpManager httpManager, Api api);

    void addAddress(ApiCallback apiCallback, AddressInfo data, HttpManager httpManager, Api api);

    void addProductCategory(ApiCallback apiCallback, AddCategoryRequest data, HttpManager httpManager, Api api);

    void updateProductCategory(ApiCallback apiCallback, AddCategoryRequest data, HttpManager httpManager, Api api);

    void addProduct(ApiCallback apiCallback, AddProductRequest data, HttpManager httpManager, Api api);

    void updateProduct(ApiCallback apiCallback, AddProductRequest data, HttpManager httpManager, Api api);

    void deleteProductCategory(ApiCallback apiCallback, CataLogProductCategory data, HttpManager httpManager, Api api);

    void deleteProduct(ApiCallback apiCallback, CatalogProduct data, HttpManager httpManager, Api api);

    void updateStatusProductCategory(ApiCallback apiCallback, CataLogProductCategory data, HttpManager httpManager, Api api);

    void getDocumentById(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void getDocumentByType(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void deleteDocument(ApiCallback apiCallback, HttpManager httpManager, Api api);

    void initiateDeviceChange(ApiCallback apiCallback, HttpManager httpManager, Api api);

}
