package com.ezycart.presentation.common.data

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import okhttp3.MediaType.Companion.toMediaType
import java.text.DecimalFormat
import java.util.Date

object Constants {

    var BASE_URL = "https://uat-api-retailetics-ops-mini-03.retailetics.com"
    //var BASE_URL = "https://api-retailetics-ops-mini-03.retailetics.com"


    private var LOGS_BASE_URL = "https://uat-logs.retailetics.com"

    const val EZY_LIST_BASE_URL = "https://staging.ezyretail.retailetics.com"

    const val CLOUD_BASE_URL = "https://api-staging-ops01.retailetics.com"

    const val OTP_BASE_URL = "https://otp-retailetics-ops01.retailetics.com"

    val DCM_URL = "https://ezycart-dcm.retailetics.com/ "
    //val DCM_URL = "https://192.168.10.2:3366/"
const val EZY_LITE_TRANSACTION_URL="https://cms-vgo-retailetics-ops-mini-03.retailetics.com/SalesReport/customertransaction"
    val LOG_URL = "${LOGS_BASE_URL}/api/v1/log"
    val MONITOR_URL = "${LOGS_BASE_URL}/api/v1/cart-monitor"
    var REJECT_LOG_URL = "${BASE_URL}/v1/ezyCart/ai/cart/reject"

    const val GET_USER_LIST_API = "${EZY_LIST_BASE_URL}/ezyList/"
    const val GET_USER_QR_LOGIN_API = "${EZY_LIST_BASE_URL}/user?"
    const val GET_USER_SHOPPING_LIST_API = "${EZY_LIST_BASE_URL}/ezyList/mylists"

    const val REVIEW_PAYMENT_API = "/v1/ezyCart/cart/review/"



    const val CONTENT_TYPE = "Content-Type"
    const val ACCEPT = "Accept"
    const val APPLICATION_JSON = "application/json"
    const val CONTENT_TYPE_JSON = "application/x-www-form-urlencoded"
    val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    const val API_KEY = "apiKey"
    const val DEVICE_TYPE = "deviceType"
    const val DEVICE_ID = "deviceId"
    const val DEVICE_ID_VALUE = "device_id_value"
    const val DEVICE_OS_VERSION = "deviceOsVersion"
    const val DEVICE_BRAND = "deviceBrand"
    const val APP_VERSION = "appVersion"
    const val BUILD_NO = "buildNo"
    const val TBM = "TBM"
    const val API_KEY_VALUE = "cfg202106110012"
    const val DEVICE_TYPE_VALUE = "1"
    const val AUTHORIZATION = "Authorization"
    const val X_AUTHORIZATION = "x-authorization"
    const val ACCEPTED_LANGUAGE = "accepted-language"

    //RemoteConfig Key
    const val KEY_MID = "mid"
    const val KEY_TID = "tid"
    const val KEY_TERMINAL_ID = "terminal_id"

    private val decimalFormat = DecimalFormat("0.00")
    fun getDecimalFormater(): DecimalFormat {
        decimalFormat.minimumFractionDigits = 2
        decimalFormat.maximumFractionDigits = 2

        return decimalFormat
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(): String {
        return SimpleDateFormat("dd-MMM-yyyy").format(Date())
    }

    //BMM
    // val allowedDevices = listOf("samsung/sm-t505/samsung","zebra/et45/zebra","aava/inari8/aava")
    val allowedDevices = listOf("samsung", "zebra", "aava")


    var deviceId = ""
    var employeeToken=""
    var jwtToken=""
}