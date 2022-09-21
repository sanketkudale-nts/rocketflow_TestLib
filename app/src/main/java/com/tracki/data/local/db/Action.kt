package com.tracki.data.local.db

/**
 * Created by rahul on 4/4/19
 */
enum class Action {
    START_TASK, END_TASK, CANCEL_TASK,
    REJECT_TASK, ACCEPT_TASK,
    REJECT_INVITATION, UPLOAD_FILE,APP_ERROR,
    CREATE_TASK, ARRIVE_TASK, EXECUTE_UPDATE/*, REACH_TASK*/
}