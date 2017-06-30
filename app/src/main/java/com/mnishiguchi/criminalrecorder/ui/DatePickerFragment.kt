package com.mnishiguchi.criminalrecorder.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.DatePicker
import com.mnishiguchi.criminalrecorder.R
import org.jetbrains.anko.bundleOf
import java.util.*


/**
 * A wrapper of an AlertDialog. Although we could display an AlertDialog standalone,
 * having the dialog managed by the FragmentManager gives us more options for presenting the dialog.
 *
 * Usage:
 *   DatePickerFragment().show(activity.supportFragmentManager, DIALOG_DATE)
 */
class DatePickerFragment : DialogFragment() {

    companion object {
        val ARG_DATE = "ARG_DATE"

        // Define how a hosting activity should create this fragment.
        fun newInstance(date: Date): DatePickerFragment {
            return DatePickerFragment().apply {
                arguments = bundleOf(ARG_DATE to date)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(R.string.date_picker_title)
                .setView(DatePicker(activity) as View)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create()
    }
}
