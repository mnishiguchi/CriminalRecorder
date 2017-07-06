package com.mnishiguchi.criminalrecorder.util

import android.content.Context

/**
 * Create an appropriate data format object for a context.
 * https://developer.android.com/reference/android/text/format/DateFormat.html
 */
fun Context.dateFormat(): java.text.DateFormat {
    return android.text.format.DateFormat.getDateFormat(this)
}

fun Context.mediumDateFormat(): java.text.DateFormat {
    return android.text.format.DateFormat.getMediumDateFormat(this)
}

fun Context.longDateFormat(): java.text.DateFormat {
    return android.text.format.DateFormat.getLongDateFormat(this)
}
