package com.mnishiguchi.criminalrecorder.ui

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.MATCH_PARENT
import com.mnishiguchi.criminalrecorder.util.getScaledBitmap
import org.jetbrains.anko.bundleOf
import java.io.File

/**
 * Created by masa on 7/7/17.
 */
class PhotoFragment : DialogFragment() {
    private val TAG = javaClass.simpleName

    companion object {
        val ARG_PHOTO_FILE = "ARG_PHOTO_FILE"

        // Define how a hosting activity should create this fragment.
        fun newInstance(photoFile: File): PhotoFragment {

            Log.d("PhotoFragment", photoFile.toString())

            return PhotoFragment().apply {
                arguments = bundleOf(ARG_PHOTO_FILE to photoFile)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val photoFile = arguments.getSerializable(ARG_PHOTO_FILE) as File
        val bitmap: Bitmap? = activity.getScaledBitmap(photoFile!!.path)

        Log.d(TAG, "onCreateDialog: $photoFile")

        val imageView = ImageView(activity).apply {
            setImageBitmap(bitmap)
        }

        return AlertDialog.Builder(activity)
                .setView(imageView)
                .create()
    }
}