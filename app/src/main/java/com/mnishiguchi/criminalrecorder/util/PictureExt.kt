package com.mnishiguchi.criminalrecorder.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.widget.ImageView
import java.io.File

/**
 * Get an image data from a file and scale it down to fit the current window size.
 */
fun Activity.scaleBitmapToWindow(file: File): Bitmap? {
    with(Point()) {
        windowManager.defaultDisplay.getSize(this)
        return getScaledBitmap(file, this.x, this.y)
    }
}

/**
 * Get an image data from a file, scale it down to fit the specified dimensions, and set that
 * scaled image on the image view.
 */
fun ImageView.setScaledImageBitmap(file: File, width: Int, height: Int): Bitmap? {
    val scaledBitmap = getScaledBitmap(file, width, height)
    setImageBitmap(scaledBitmap)
    return scaledBitmap
}

/**
 * Get an image data from a file and scale it down to fit given dimensions.
 */
private fun getScaledBitmap(file: File, destWidth: Int, destHeight: Int): Bitmap? {
    // Read in the dimensions of the image on disk.
    var srcWidth: Float = 0.0F
    var srcHeight: Float = 0.0F
    BitmapFactory.Options().apply {
        this.inJustDecodeBounds = true // No pixel data needed.
        BitmapFactory.decodeFile(file.path, this)
        srcWidth = this.outWidth.toFloat()
        srcHeight = this.outHeight.toFloat()
    }

    // Figure out how much to scale down by and set it on options.
    val options = BitmapFactory.Options().apply {
        val isLargerThanDisplay = srcHeight > destHeight || srcWidth > destWidth
        this.inSampleSize =
                if (isLargerThanDisplay) {
                    if (srcWidth > srcHeight) { // Landscape - Let the height match.
                        Math.round(srcHeight / destHeight)
                    } else { // Portrait - Let the width match.
                        Math.round(srcWidth / destWidth)
                    }
                } else 1
    }

    // Scale down the bitmap data based on the inSampleSize.
    return BitmapFactory.decodeFile(file.path, options)
}