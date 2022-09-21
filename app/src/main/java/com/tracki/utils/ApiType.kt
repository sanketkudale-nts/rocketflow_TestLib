package com.tracki.utils

/**
 * Created by rahul on 14/11/18
 */
enum class ApiType {
    CONFIG, LOGIN, VERIFY_MOBILE, SIGNUP, HOME, MY_PROFILE, UPDATE_PROFILE, ADD_BUDDY,
    ADD_FLEET, BUDDIES, FLEETS, DASHBOARD, UPLOAD_FILE_AGAINEST_ENTITY, INVITATIONS,
    ACCEPT_INVITATION, REJECT_INVITATION, UPDATE_BUDDY, GET_BUDDY_BY_ID, UPDATE_FLEET, GET_FLEET_BY_ID,
    DELETE_BUDDY, DELETE_FLEET, CREATE_TASK, TASKS, GET_TASK_BY_ID, START_TASK, END_TASK,
    CANCEL_TASK, SHARE_TRIP, CHANGE_STATUS, FEEDBACK, LOGOUT, ACCEPT_TASK, REJECT_TASK,
    UPDATE_TASK, NOTIFICATIONS, CANCEL_REQUEST, CLEAR_NOTIFICATIONS, UPLOAD_FILE, SETTINGS, SAVE_SETTINGS,UPLOAD_MEDIA,
    UPDATE_TASK_DATA, ARRIVE_REACH_TASK, MY_EARNINGS, TASK_BY_DATE,
    PUNCH, GET_ATTENDANCE, APPLY_LEAVE, LEAVE_HISTORY, LEAVE_SUMMARY, EDIT_LEAVE,
    CANCEL_LEAVE, APPROVALS, REJECT_LEAVE, APPROVE_LEAVE, MY_LEAVES, EXECUTE_UPDATE, EXECUTIVE_MAP, GET_TASK_DATA,GET_LAST_PUNCH
    ,USER_LOCATIONS,UPDATE_USER_LOCATION,REMOVE_USER_LOCATION,FIELD_DATA_AUTO_POPULATE,DASHBOARD_TASKS,GET_SUBORDINTES,ALLOCATE_EXECUTIVE
    ,EXECUTIVE_MAP_BY_PLACE,CHANGE_FLEET_STATUS,EXECUTIVE_PAYOUTS,USER_GROUP_MAP,GET_CITIES, GET_HUBS, GET_REGIONS, GET_STATES,SYNC_DB,GET_TASK_INVENTORIES,GET_CATEGORY_BY_GROUP,LINK_INVENTORY
    ,WALLET_TRANSACTIONS,TEAM_ATTENDANCE,CALL_LOG_SYNC,TEAM_ATTENDANCES,GET_ALL_POST,GET_COMMENTS,UPDATE_REACTION,GET_LIKES,GET_SEARCH_USER,USER_ATTENDANCE_MAP,USER_ATTENDANCE_BREAKUP,CREATE_DRAFT,SIGNUP_ACCOUNTS,INITIATE_SIGNUP,SIGNUP_AND_UPDATE_PREFERENCE,
    GET_SERVICE_PREF,UPDATE_SERVICE_PREF,GET_INSIGHTS,USER_ATTENDANCE_LOCATION,
    ADD_USER,UPDATE_USER,DELETE_USER,USER_DETAIL,GET_USERS,USER_SEARCH,USER_ADDRESS_ADD,USER_ADDRESS_GET,USER_ADDRESS_UPDATE,USER_ADDRESS_DELETE,SERVER_TIME,ADD_PRODUCT_CATEGORY,UPDATE_PRODUCT_CATEGORY,
    PRODUCT_CATEGORIES,GET_PRODUCTS,GET_TASK_PRODUCTS,VIEW_CART,VALIDATE_COUPON,CREATE_ORDER,DELETE_PRODUCT_CATEGORY,UPDATE_PRODUCT_CATEGORY_STATUS,PRODUCT_DETAIL,DELETE_PRODUCT,
    UPDATE_PRODUCT_STATUS,UPDATE_PRODUCT,ADD_PRODUCT,
   SEARCH_PRODUCT,APP_ERROR,GET_UNITS,SUB_CATEGORIES,TERMINAL_CATEGORIES,DAILY_STOCK_HISTORY,STOCK_HISTORY_DETAIL,GET_POST_DETAIL,
    TASK_PRODUCT_CATEGORIES,CREATE_DOCUMENT,
    DELETE_DOCUMENT,
    GET_DOCUMENT_BY_ID,
    GET_DOCUMENT_BY_TYPE,
    UPDATE_DOCUMENT,
    GET_WALLET_BALANCE,SCANNER,ENTITY_SCANNER,INITIATE_DEVICE_CHANGE,


}