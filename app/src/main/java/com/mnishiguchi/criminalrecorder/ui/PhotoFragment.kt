package com.mnishiguchi.criminalrecorder.ui

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.ImageView
import com.mnishiguchi.criminalrecorder.util.scaleBitmapToWindow
import org.jetbrains.anko.bundleOf
import java.io.File

/**
 * A Dialog Fragment that shows a full-screen photo.
 */
class PhotoFragment : DialogFragment() {
    private val TAG = javaClass.simpleName

    companion object {
        val ARG_PHOTO_FILE = "ARG_PHOTO_FILE"

        // Define how a hosting activity should create this fragment.
        fun newInstance(photoFile: File): PhotoFragment {
            return PhotoFragment().apply {
                arguments = bundleOf(ARG_PHOTO_FILE to photoFile)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val photoFile = arguments.getSerializable(ARG_PHOTO_FILE) as File
        val scaledBitmap: Bitmap? = activity.scaleBitmapToWindow(photoFile)
        val imageView = ImageView(activity)
        if (scaledBitmap != null) imageView.setImageBitmap(scaledBitmap)

        return AlertDialog.Builder(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                .setView(imageView)
                .create()
    }
}