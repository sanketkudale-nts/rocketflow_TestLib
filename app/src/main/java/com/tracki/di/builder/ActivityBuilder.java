package com.tracki.di.builder;

import com.tracki.ui.deviceChange.DeviceChangeActivity;
import com.tracki.ui.deviceChange.DeviceChangeActivityModule;
import com.tracki.ui.scanqrcode.ProductScan;
import com.tracki.ui.scanqrcode.ProductScanActivityModule;
import com.tracki.ui.scanqrcode.QrScannerActivityModule;
import com.tracki.ui.scanqrcode.ScanQrAndBarCodeActivity;
import com.tracki.ui.transactionDetails.TransactionDetailsActivity;
import com.tracki.ui.transactionDetails.TransactionDetailsActivityModule;
import com.tracki.ui.cart.CartActivity;
import com.tracki.ui.account.MyAccountActivity;
import com.tracki.ui.account.MyAccountActivityModule;
import com.tracki.ui.accountlist.AccountListActivity;
import com.tracki.ui.accountlist.AccountListActivityModule;
import com.tracki.ui.addbuddy.AddBuddyActivity;
import com.tracki.ui.addbuddy.AddBuddyActivityModule;
import com.tracki.ui.addcontact.AddEmergencyContactActivity;
import com.tracki.ui.addcontact.AddEmergencyContactModule;
import com.tracki.ui.addcustomer.AddCustomerActivity;
import com.tracki.ui.addcustomer.AddCustomerFragmentProvider;
import com.tracki.ui.addcustomer.CustomerInfoModule;
import com.tracki.ui.addfleet.AddFleetActivity;
import com.tracki.ui.addfleet.AddFleetActivityModule;
import com.tracki.ui.addplace.AddLocationActivity;
import com.tracki.ui.addplace.AddPlaceActivityModule;
import com.tracki.ui.adduserAddress.AddAddressViewModule;
import com.tracki.ui.adduserAddress.AddUserAddressActivity;
import com.tracki.ui.adjusttime.AdjustTimeActivity;
import com.tracki.ui.adjusttime.AdjustTimeViewModule;
import com.tracki.ui.attendance.AttendanceActivity;
import com.tracki.ui.attendance.AttendanceActivityModule;
import com.tracki.ui.attendance.AttendanceBaseFragmentProvider;
import com.tracki.ui.attendance.EmployeeActivityModule;
import com.tracki.ui.attendance.EmployeeListActivity;
import com.tracki.ui.attendance.attendance_tab.AttendanceFragmentProvider;
import com.tracki.ui.attendance.punchInOut.PunchInOutFragmentProvider;
import com.tracki.ui.attendance.teamattendance.TeamAttendanceFragmentProvider;
import com.tracki.ui.buddylisting.BuddyListingActivity;
import com.tracki.ui.buddylisting.BuddyListingActivityModule;
import com.tracki.ui.buddyprofile.BuddyProfileActivity;
import com.tracki.ui.buddyprofile.BuddyProfileActivityModule;
import com.tracki.ui.buddyrequest.BuddyRequestActivity;
import com.tracki.ui.buddyrequest.BuddyRequestActivityModule;
import com.tracki.ui.cart.CartViewModule;
import com.tracki.ui.category.AddProductCategoryViewModule;
import com.tracki.ui.category.AddProductCategoryActivity;
import com.tracki.ui.category.GetProductCategoryViewModule;
import com.tracki.ui.category.ProductCategoryListActivity;
import com.tracki.ui.changemobile.ChangeMobileActivity;
import com.tracki.ui.changemobile.ChangeMobileActivityModule;
import com.tracki.ui.changepassword.ChangePasswordActivity;
import com.tracki.ui.changepassword.ChangePasswordActivityModule;
import com.tracki.ui.chat.ChatActivity;
import com.tracki.ui.chat.ChatActivityModule;
import com.tracki.ui.consent.ConsentActivity;
import com.tracki.ui.consent.ConsentActivityModule;
import com.tracki.ui.deprecation_expiration.AppBlockActivity;
import com.tracki.ui.deprecation_expiration.AppBlockActivityModule;
import com.tracki.ui.dynamicform.DynamicFormActivity;
import com.tracki.ui.dynamicform.DynamicFormActivityModule;
import com.tracki.ui.dynamicform.dynamicfragment.DynamicFragmentProvider;
import com.tracki.ui.dynamicformpreview.FormPreviewActivity;
import com.tracki.ui.dynamicformpreview.FormPreviewActivityModule;
import com.tracki.ui.earnings.MyEarningsActivity;
import com.tracki.ui.earnings.MyEarningsActivityModule;
import com.tracki.ui.emergencymessage.EmergencyMessageActivity;
import com.tracki.ui.emergencymessage.EmergencyMessageActivityModule;
import com.tracki.ui.emergencyphone.EmergencyContactActivity;
import com.tracki.ui.emergencyphone.EmergencyContactActivityModule;
import com.tracki.ui.facility.ServicesActivity;
import com.tracki.ui.facility.UpdateServiceModule;
import com.tracki.ui.feeddetails.FeedDetailsActivity;
import com.tracki.ui.feeddetails.FeedDetailsActivityModule;
import com.tracki.ui.feeds.FeedsActivity;
import com.tracki.ui.feeds.FeedsActivityModule;
import com.tracki.ui.fleetlisting.FleetListingActivity;
import com.tracki.ui.fleetlisting.FleetListingActivityModule;
import com.tracki.ui.idealtrip.IdealTripDetailActivityModule;
import com.tracki.ui.idealtrip.IdealTripDetailsActivity;
import com.tracki.ui.introscreens.IntroActivity;
import com.tracki.ui.introscreens.IntroActivityModule;
import com.tracki.ui.introscreens.introfragment.IntroScreenFragmentProvider;
import com.tracki.ui.inventory.InventoryActivity;
import com.tracki.ui.inventory.InventoryActivityModule;
import com.tracki.ui.leave.LeaveActivity;
import com.tracki.ui.leave.LeaveActivityModule;
import com.tracki.ui.leave.apply_leave.ApplyLeaveFragmentProvider;
import com.tracki.ui.leave.leave_approval.LeaveApprovalFragmentProvider;
import com.tracki.ui.leave.leave_history.LeaveHistoryFragmentProvider;
import com.tracki.ui.leave.leave_summary.LeaveSummaryFragmentProvider;
import com.tracki.ui.leavedetails.LeaveDetailsModule;
import com.tracki.ui.leavedetails.UsersLeaveDetailsActivity;
import com.tracki.ui.likeslist.LikeListActivity;
import com.tracki.ui.likeslist.LikeListActivityModule;
import com.tracki.ui.login.LoginActivity;
import com.tracki.ui.login.LoginActivityModule;
import com.tracki.ui.main.MainActivity;
import com.tracki.ui.main.MainActivityModule;
import com.tracki.ui.main.filter.BuddyFilterActivity;
import com.tracki.ui.main.filter.BuddyFilterActivityModule;
import com.tracki.ui.main.filter.TaskFilterActivity;
import com.tracki.ui.main.filter.TaskFilterActivityModule;
import com.tracki.ui.main.taskdashboard.TaskDashBoardFragmentProvider;
import com.tracki.ui.messages.MessageActivityModule;
import com.tracki.ui.messages.MessagesActivity;
import com.tracki.ui.myDocument.MyDocumentActivity;
import com.tracki.ui.myDocument.MyDocumentModule;
import com.tracki.ui.myInventory.MyInventoryActivity;
import com.tracki.ui.myInventory.MyInventoryModule;
import com.tracki.ui.newcreatetask.NewCreateTaskActivity;
import com.tracki.ui.newcreatetask.NewCreateTaskActivityModule;
import com.tracki.ui.newdynamicform.NewDynamicFragmentProvider;
import com.tracki.ui.notification.NotificationActivity;
import com.tracki.ui.notification.NotificationActivityModule;
import com.tracki.ui.otp.OtpActivity;
import com.tracki.ui.otp.OtpActivityModule;
import com.tracki.ui.payouts.AdminUserPayoutsActivity;
import com.tracki.ui.payouts.AdminUserPayoutsActivityModule;
import com.tracki.ui.placelist.MyPlaceActivityModule;
import com.tracki.ui.placelist.MyPlaceListActivity;
import com.tracki.ui.productdetails.ImageScreenFragmentProvider;
import com.tracki.ui.productdetails.ProductDetailsActivity;
import com.tracki.ui.productdetails.ProductDetailsViewModule;
import com.tracki.ui.productlist.ProductListActivity;
import com.tracki.ui.productlist.ProductListViewModule;
import com.tracki.ui.products.AddProductActivity;
import com.tracki.ui.products.AddProductViewModule;
import com.tracki.ui.profile.MyProfileActivity;
import com.tracki.ui.profile.MyProfileActivityModule;
import com.tracki.ui.referearn.ReferAndEarnActivity;
import com.tracki.ui.referearn.ReferAndEarnActivityModule;
import com.tracki.ui.register.RegisterActivity;
import com.tracki.ui.register.RegisterActivityModule;
import com.tracki.ui.rides.RideActivity;
import com.tracki.ui.rides.RideActivityModule;
import com.tracki.ui.roleselection.SelectAccountModule;
import com.tracki.ui.roleselection.SelectionActivity;
import com.tracki.ui.roleselection.TaskSelectionFragmentProvider;
import com.tracki.ui.selectorder.SelectOrderActivity;
import com.tracki.ui.selectorder.SelectOrderViewModule;
import com.tracki.ui.setting.SettingActivityModule;
import com.tracki.ui.setting.SettingsActivity;
import com.tracki.ui.sharetrip.ShareTripActivity;
import com.tracki.ui.sharetrip.ShareTripActivityModule;
import com.tracki.ui.splash.SplashActivity;
import com.tracki.ui.splash.SplashActivityModule;
import com.tracki.ui.stockhistorydetails.StockHistoryDetailsActivity;
import com.tracki.ui.stockhistorydetails.StockHistoryViewModule;
import com.tracki.ui.taskdetails.NewTaskDetailActivityModule;
import com.tracki.ui.taskdetails.NewTaskDetailsActivity;
import com.tracki.ui.taskdetails.TaskDetailActivity;
import com.tracki.ui.taskdetails.TaskDetailActivityModule;
import com.tracki.ui.taskdetails.subtask.SubTaskFragmentProvider;
import com.tracki.ui.taskdetails.timeline.TaskDetailsFragmentProvider;
import com.tracki.ui.tasklisting.TaskActivity;
import com.tracki.ui.tasklisting.TaskActivityModule;
import com.tracki.ui.tasklisting.assignedtome.AssignedtoMeFragmentProvider;
import com.tracki.ui.tasklisting.ihaveassigned.IhaveAssignedFragmentProvider;
import com.tracki.ui.trackingbuddy.TrackingBuddyActivity;
import com.tracki.ui.trackingbuddy.TrackingBuddyActivityModule;
import com.tracki.ui.trackingbuddy.buddydetail.TrackingBuddyDetailActivity;
import com.tracki.ui.trackingbuddy.buddydetail.TrackingBuddyDetailActivityModule;
import com.tracki.ui.trackingbuddy.iamtracking.IamTrackingFragmentProvider;
import com.tracki.ui.trackingbuddy.trackingme.TrackingMeFragmentProvider;
import com.tracki.ui.update.AppUpdateScreenActivity;
import com.tracki.ui.update.AppUpdateScreenActivityModule;
import com.tracki.ui.uploadDocument.UploadDocumentActivity;
import com.tracki.ui.uploadDocument.UploadDocumentModule;
import com.tracki.ui.useraddresslist.UserAddressListActivity;
import com.tracki.ui.useraddresslist.UserAddressListActivityModule;
import com.tracki.ui.userattendancedetails.UserAttendanceActivityModule;
import com.tracki.ui.userattendancedetails.UserAttendanceDetailsActivity;
import com.tracki.ui.userdetails.UserAddressFragmentProvider;
import com.tracki.ui.userdetails.UserDetailsActivity;
import com.tracki.ui.userdetails.UserDetailsModule;
import com.tracki.ui.userdetails.basicinfo.UserBasicInfoFragmentProvider;
import com.tracki.ui.userlisting.UserListNewActivity;
import com.tracki.ui.userlisting.UserListNewViewModule;
import com.tracki.ui.userlisting.UserListViewModule;
import com.tracki.ui.userlisting.UserListingActivity;
import com.tracki.ui.wallet.WalletActivity;
import com.tracki.ui.wallet.WalletActivityModule;
import com.tracki.ui.webview.WebViewActivity;
import com.tracki.ui.webview.WebViewActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = SplashActivityModule.class)
    abstract SplashActivity bindSplashActivity();

    @ContributesAndroidInjector(modules = LoginActivityModule.class)
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector(modules = RegisterActivityModule.class)
    abstract RegisterActivity bindRegisterActivity();

    @ContributesAndroidInjector(modules = OtpActivityModule.class)
    abstract OtpActivity bindOtpActivity();

    @ContributesAndroidInjector(modules = AddFleetActivityModule.class)
    abstract AddFleetActivity bindAddFleetActivity();

    @ContributesAndroidInjector(modules = {MainActivityModule.class, TaskDashBoardFragmentProvider.class, AttendanceBaseFragmentProvider.class,PunchInOutFragmentProvider.class,
            AttendanceFragmentProvider.class,TeamAttendanceFragmentProvider.class})
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = BuddyListingActivityModule.class)
    abstract BuddyListingActivity bindBuddyListingActivity();

    @ContributesAndroidInjector(modules = EmployeeActivityModule.class)
    abstract EmployeeListActivity bindEmployeeListActivity();

    @ContributesAndroidInjector(modules = FeedsActivityModule.class)
    abstract FeedsActivity bindFeedsActivity();

    @ContributesAndroidInjector(modules = LikeListActivityModule.class)
    abstract LikeListActivity bindLikeListActivity();

    @ContributesAndroidInjector(modules = AccountListActivityModule.class)
    abstract AccountListActivity bindAccountListActivity();

//    @ContributesAndroidInjector(modules = UserTypeListActivityModule.class)
//    abstract SelectUserTypeActivity bindUserTypeListActivity();

    @ContributesAndroidInjector(modules = AddBuddyActivityModule.class)
    abstract AddBuddyActivity bindAddBuddyActivity();

    @ContributesAndroidInjector(modules = MyAccountActivityModule.class)
    abstract MyAccountActivity bindMyAccountActivity();

    @ContributesAndroidInjector(modules = {MyProfileActivityModule.class,AddCustomerFragmentProvider.class})
    abstract MyProfileActivity bindMyProfileActivity();

    @ContributesAndroidInjector(modules = ChangeMobileActivityModule.class)
    abstract ChangeMobileActivity bindChangeMobileActivity();

    @ContributesAndroidInjector(modules = ChangePasswordActivityModule.class)
    abstract ChangePasswordActivity bindChangePasswordActivity();

    @ContributesAndroidInjector(modules = UpdateServiceModule.class)
    abstract ServicesActivity bindServicesActivity();



    @ContributesAndroidInjector(modules = {
            TrackingBuddyActivityModule.class,
            TrackingMeFragmentProvider.class,
            IamTrackingFragmentProvider.class})
    abstract TrackingBuddyActivity bindTrackingBuddyActivity();

    @ContributesAndroidInjector(modules = {
            TaskActivityModule.class,
            AssignedtoMeFragmentProvider.class,
            IhaveAssignedFragmentProvider.class})
    abstract TaskActivity bindTaskActivity();

    @ContributesAndroidInjector(modules = {
            SelectAccountModule.class,
            TaskSelectionFragmentProvider.class})
    abstract SelectionActivity bindSelectionActivity();

    @ContributesAndroidInjector(modules = BuddyProfileActivityModule.class)
    abstract BuddyProfileActivity bindBuddyDetailActivity();

    @ContributesAndroidInjector(modules = FleetListingActivityModule.class)
    abstract FleetListingActivity bindFleetListingActivity();

    @ContributesAndroidInjector(modules = NotificationActivityModule.class)
    abstract NotificationActivity bindNotificationActivity();

    @ContributesAndroidInjector(modules = BuddyFilterActivityModule.class)
    abstract BuddyFilterActivity bindBuddyFilterActivity();



    @ContributesAndroidInjector(modules = TaskFilterActivityModule.class)
    abstract TaskFilterActivity bindTaskFilterActivity();

    @ContributesAndroidInjector(modules = AddPlaceActivityModule.class)
    abstract AddLocationActivity bindAddLocationActivity();

    @ContributesAndroidInjector(modules = LeaveDetailsModule.class)
    abstract UsersLeaveDetailsActivity bindUsersLeaveDetailsActivity();

    @ContributesAndroidInjector(modules = MyPlaceActivityModule.class)
    abstract MyPlaceListActivity bindMyPlaceActivity();

    @ContributesAndroidInjector(modules = {NewCreateTaskActivityModule.class, NewDynamicFragmentProvider.class})
    abstract NewCreateTaskActivity bindNewCreateTaskActivity();

    @ContributesAndroidInjector(modules = TaskDetailActivityModule.class)
    abstract TaskDetailActivity bindTaskDetailActivity();

    @ContributesAndroidInjector(modules = IdealTripDetailActivityModule.class)
    abstract IdealTripDetailsActivity bindIdealTripDetailsActivity();

    @ContributesAndroidInjector(modules = InventoryActivityModule.class)
    abstract InventoryActivity bindInventoryActivity();

    @ContributesAndroidInjector(modules = WalletActivityModule.class)
    abstract WalletActivity bindWalletActivity();

    @ContributesAndroidInjector(modules = {NewTaskDetailActivityModule.class, TaskDetailsFragmentProvider.class, SubTaskFragmentProvider.class
    , AssignedtoMeFragmentProvider.class})
    abstract NewTaskDetailsActivity bindNewTaskDetailActivity();

    @ContributesAndroidInjector(modules = ReferAndEarnActivityModule.class)
    abstract ReferAndEarnActivity bindReferAndEarnActivity();

    @ContributesAndroidInjector(modules = TrackingBuddyDetailActivityModule.class)
    abstract TrackingBuddyDetailActivity bindTrackingBuddyDetailActivity();

    @ContributesAndroidInjector(modules = SettingActivityModule.class)
    abstract SettingsActivity bindSettingsActivity();

    @ContributesAndroidInjector(modules = QrScannerActivityModule.class)
    abstract ScanQrAndBarCodeActivity bindScanQrAndBarCodeActivity();

    @ContributesAndroidInjector(modules = ProductScanActivityModule.class)
    abstract ProductScan productScan();

    @ContributesAndroidInjector(modules = EmergencyContactActivityModule.class)
    abstract EmergencyContactActivity bindEmergencyContactActivity();

    @ContributesAndroidInjector(modules = BuddyRequestActivityModule.class)
    abstract BuddyRequestActivity bindBuddyRequestActivity();

    @ContributesAndroidInjector(modules = AddEmergencyContactModule.class)
    abstract AddEmergencyContactActivity bindAddEmergencyContactActivity();

    @ContributesAndroidInjector(modules = EmergencyMessageActivityModule.class)
    abstract EmergencyMessageActivity bindEmergencyMessageActivity();

    @ContributesAndroidInjector(modules = {
            IntroActivityModule.class,
            IntroScreenFragmentProvider.class})
    abstract IntroActivity bindIntroActivity();

    @ContributesAndroidInjector(modules = ShareTripActivityModule.class)
    abstract ShareTripActivity bindShareTripActivity();

    @ContributesAndroidInjector(modules = WebViewActivityModule.class)
    abstract WebViewActivity bindWebViewActivity();

    @ContributesAndroidInjector(modules = {
            DynamicFormActivityModule.class,
            DynamicFragmentProvider.class})
    abstract DynamicFormActivity bindDynamicFormActivity();

    @ContributesAndroidInjector(modules = AppBlockActivityModule.class)
    abstract AppBlockActivity bindAppBlockActivity();

    @ContributesAndroidInjector(modules = ConsentActivityModule.class)
    abstract ConsentActivity bindConsentActivity();

    @ContributesAndroidInjector(modules = MessageActivityModule.class)
    abstract MessagesActivity bindMessageActivity();

    @ContributesAndroidInjector(modules = ChatActivityModule.class)
    abstract ChatActivity bindChatActivity();

    @ContributesAndroidInjector(modules = FormPreviewActivityModule.class)
    abstract FormPreviewActivity bindFormPreviewActivity();


    @ContributesAndroidInjector(modules = MyEarningsActivityModule.class)
    abstract MyEarningsActivity bindMyEarningsActivity();

    @ContributesAndroidInjector(modules = AdminUserPayoutsActivityModule.class)
    abstract AdminUserPayoutsActivity bindAdminUserPayoutsActivity();

    @ContributesAndroidInjector(modules = RideActivityModule.class)
    abstract RideActivity bindRideActivity();

    @ContributesAndroidInjector(modules = AppUpdateScreenActivityModule.class)
    abstract AppUpdateScreenActivity bindAppUpdateScreenActivity();

    @ContributesAndroidInjector(modules = {UserAttendanceActivityModule.class,AttendanceFragmentProvider.class,LeaveHistoryFragmentProvider.class})
    abstract UserAttendanceDetailsActivity bindUserAttendanceDetailsActivity();


    @ContributesAndroidInjector(modules = {
            AttendanceActivityModule.class,
            PunchInOutFragmentProvider.class,
            AttendanceFragmentProvider.class,
            TeamAttendanceFragmentProvider.class})
    abstract AttendanceActivity bindAttendanceActivity();

    @ContributesAndroidInjector(modules = {
            LeaveActivityModule.class,
            ApplyLeaveFragmentProvider.class,
            LeaveHistoryFragmentProvider.class,
            LeaveSummaryFragmentProvider.class,
            LeaveApprovalFragmentProvider.class})
    abstract LeaveActivity bindLeaveActivity();

    @ContributesAndroidInjector(modules = {CustomerInfoModule.class, AddCustomerFragmentProvider.class})
    abstract AddCustomerActivity bindUserAddCustomerActivity();


    @ContributesAndroidInjector(modules = {UserListViewModule.class})
    abstract UserListingActivity bindUserUserListingActivity();

    @ContributesAndroidInjector(modules = {UserListNewViewModule.class})
    abstract UserListNewActivity bindUserUserListNewActivity();

    @ContributesAndroidInjector(modules = {SelectOrderViewModule.class})
    abstract SelectOrderActivity bindSelectOrderActivity();

    @ContributesAndroidInjector(modules = {CartViewModule.class})
    abstract CartActivity bindCartActivity();


    @ContributesAndroidInjector(modules = {UserDetailsModule.class,AttendanceFragmentProvider.class,LeaveHistoryFragmentProvider.class, UserAddressFragmentProvider.class, UserBasicInfoFragmentProvider.class})
    abstract UserDetailsActivity bindUserDetailsActivity();

    @ContributesAndroidInjector(modules = {UserAddressListActivityModule.class})
    abstract UserAddressListActivity bindUserAddressListActivity();


    @ContributesAndroidInjector(modules = {AddAddressViewModule.class})
    abstract AddUserAddressActivity bindAddUserAddressActivity();


    @ContributesAndroidInjector(modules = {AdjustTimeViewModule.class})
    abstract AdjustTimeActivity bindAdjustTimeActivity();


    @ContributesAndroidInjector(modules = {AddProductCategoryViewModule.class})
    abstract AddProductCategoryActivity bindAddProductCategoryActivity();


    @ContributesAndroidInjector(modules = {GetProductCategoryViewModule.class})
    abstract ProductCategoryListActivity bindProductCategoryListActivity();


    @ContributesAndroidInjector(modules = {AddProductViewModule.class,NewDynamicFragmentProvider.class})
    abstract AddProductActivity bindAddProductActivity();


    @ContributesAndroidInjector(modules = {ProductListViewModule.class})
    abstract ProductListActivity bindProductListActivity();



    @ContributesAndroidInjector(modules = {ProductDetailsViewModule.class, ImageScreenFragmentProvider.class})
    abstract ProductDetailsActivity bindProductDetailsActivity();


    @ContributesAndroidInjector(modules = {StockHistoryViewModule.class})
    abstract StockHistoryDetailsActivity bindStockHistoryDetailsActivity();

    @ContributesAndroidInjector(modules = {FeedDetailsActivityModule.class})
    abstract FeedDetailsActivity bindFeedDetailsActivity();



    @ContributesAndroidInjector(modules = {TransactionDetailsActivityModule.class})
    abstract TransactionDetailsActivity bindTransactionDetailsActivity();


    @ContributesAndroidInjector(modules = {MyDocumentModule.class})
    abstract MyDocumentActivity bindMyDocumentActivity();

    @ContributesAndroidInjector(modules = {UploadDocumentModule.class})
    abstract UploadDocumentActivity bindUploadDocumentActivity();

    @ContributesAndroidInjector(modules = {MyInventoryModule.class})
    abstract MyInventoryActivity bindMyInventoryActivity();

    @ContributesAndroidInjector(modules = {DeviceChangeActivityModule.class})
    abstract DeviceChangeActivity deviceChangeActivity();


}
