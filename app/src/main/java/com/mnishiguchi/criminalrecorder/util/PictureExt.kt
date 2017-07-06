package com.mnishiguchi.criminalrecorder.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

/**
 * Get an image data from a file and scale it down to fit the current Window size.
 * The orientation is not considered.
 */
fun Activity.getScaledBitmap(path: String): Bitmap {
    val size: Point = Point()
    windowManager.defaultDisplay.getSize(size)
    return getScaledBitmap(path, size.x, size.y)
}

/**
 * Get an image data from a file and scale it down to fit a given Window size.
 * The orientation is not considered.
 */
fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
    // Read in the dimensions of the image on disk.
    var srcWidth: Float = 0.0F
    var srcHeight: Float = 0.0F
    BitmapFactory.Options().apply {
        this.inJustDecodeBounds = true // No pixel data needed.
        BitmapFactory.decodeFile(path, this)
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
    return BitmapFactory.decodeFile(path, options)
}