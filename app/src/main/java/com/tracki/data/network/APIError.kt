package com.tracki.data.network

/**
 * Created by rahul on 13/11/18
 */
class APIError(var errorType: ErrorType) {
    enum class ErrorType {
        TIMEOUT, NETWORK_FAIL, PARSE_FAILED, SERVER_DOWN, UNKNOWN_ERROR, SESSION_EXPIRED,INTERNET_CONNECTION,SLOW_INTERNET_CONNECTION
    }
}