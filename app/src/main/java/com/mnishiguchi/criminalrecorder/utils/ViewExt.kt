package com.mnishiguchi.criminalrecorder.utils

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