package com.tracki.utils

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by rahul on 20/2/19
 */
class DateTimeUtil private constructor() {


    internal companion object {

        const val TIME_FORMAT = "HH:mm"
        const val TIME_FORMAT_2 = "hh:mm a"
        const val TIME_FORMAT_3 = "kk:mm:ss"
        const val DATE_FORMAT = "d MMM yyyy"
        const val DATE_FORMAT_2 = "dd/MM/yyyy"
        const val DATE_FORMAT2 = "dd/MM/yyyy"
        const val DATE_FORMAT3 = "dd MMM"
        const val DATE_FORMAT4 = "dd MMM yyyy"
        const val DATE_FORMAT5 = "dd MMM yyyy; EEEE"


        const val DATE_TIME_FORMAT_2 = "d MMM yyyy hh:mm aaa"
        const val DATE_TIME_FORMAT_3 = "MMMM dd yyyy, hh:mm a"
        const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        const val TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"

        const val TIME_FORMAT_4 = "h:mm aa"
        const val DATE_TIME_FORMAT_4 = "dd/MM/yyyy h:mm aa"
        const val DATE_TIME_FORMAT_5 = "MM/dd/yyyy HH:mm"


        @JvmStatic
        fun getParsedTime(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(TIME_FORMAT_2, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
        @JvmStatic
        fun getParsedTime1(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(TIME_FORMAT, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }


        @JvmStatic
        fun getParsedDate(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(DATE_FORMAT, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }


        @JvmStatic
        fun getParsedDateApply(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(DATE_TIME_FORMAT_2, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
        @JvmStatic
        fun getParsedDate(milliSeconds: Long, format: String): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(format, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        @JvmStatic
        fun getParsedDate2(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(DATE_FORMAT2, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        @JvmStatic
        fun getParsedDate3(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                var sdf=SimpleDateFormat(DATE_FORMAT3,Locale.getDefault())
                sdf.timeZone = Calendar.getInstance().timeZone
                val dateString = SimpleDateFormat(DATE_FORMAT3, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        @JvmStatic
        fun getParsedDate4(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(DATE_FORMAT4, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
        @JvmStatic
        fun getParsedDateTime5(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(DATE_TIME_FORMAT_5, Locale.US)
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
        @JvmStatic
        fun getFormattedTime(milliSeconds: Long?, format: String): String {
            if (milliSeconds == null || milliSeconds == 0L) {
                return ""
            }
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(format, Locale.US)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        @JvmStatic
        fun getFormattedTime(date: Date?): String {
            if (date == null) {
                return ""
            }
            val sdf = SimpleDateFormat(TIME_FORMAT, Locale.US)
//            sdf.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
            return sdf.format(date)
        }

        @JvmStatic
        fun getCurrentDateInMillis(): Long {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            return c.timeInMillis
        }

        fun getCurrentDate(): String {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            return getParsedDate(c.timeInMillis)
        }

        fun getCurrentDate(format: String): String {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            return getParsedDate(c.timeInMillis, format)
        }

        fun getCurrentTime(): String {
            // Get Current Time
            val c: Calendar = Calendar.getInstance()
            return getParsedTime(c.timeInMillis)
        }

        @JvmStatic
        fun getChatDateFormat(msgTimeMillis: Long): String {

            val messageTime = Calendar.getInstance()
            messageTime.timeInMillis = msgTimeMillis
            // get Current time
            val now = Calendar.getInstance()

            return if (now.get(Calendar.DATE) == messageTime.get(Calendar.DATE)
                    &&
                    now.get(Calendar.MONTH) == messageTime.get(Calendar.MONTH)
                    &&
                    now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR)) {

                "" + DateFormat.format(TIME_FORMAT_4, messageTime)

            } else if (now.get(Calendar.DATE) - messageTime.get(Calendar.DATE) == 1
                    &&
                    now.get(Calendar.MONTH) == messageTime.get(Calendar.MONTH)
                    &&
                    now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR)) {
                "Yesterday at " + DateFormat.format(TIME_FORMAT_4, messageTime)
            } else {
                "" + DateFormat.format(DATE_TIME_FORMAT_4, messageTime)
            }
        }

        @JvmStatic
        fun getCurrentDay(): String? {
            val calendar = Calendar.getInstance()
            when (calendar[Calendar.DAY_OF_WEEK]) {
                1 -> return "Sunday"
                2 -> return "Monday"
                3 -> return "Tuesday"
                4 -> return "Wednesday"
                5 -> return "Thursday"
                6 -> return "Friday"
                7 -> return "Saturday"
            }
            return ""
        }

        @JvmStatic
        fun millisToHoursMin(milliseconds: Long): String? {

            var seconds = milliseconds / 1000
            var minutes = seconds / 60
            seconds %= 60
            val hours = minutes / 60
            minutes %= 60
            return "$hours hrs $minutes min"
        }

        @JvmStatic
        fun getParsedDate5(milliSeconds: Long): String {
            if (milliSeconds == 0L) {
                return ""
            }
            return try {
                val dateString = SimpleDateFormat(
                    DATE_FORMAT5,
                    Locale.US
                )
                dateString.format(Date(milliSeconds))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }
}