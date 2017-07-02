package com.mnishiguchi.criminalrecorder.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * The line that inflates the view is the same on any adapters most of the time.
 * We might as well give ViewGroup the ability to inflate views.
 * https://antonioleiva.com/extension-functions-kotlin/
 */
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

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