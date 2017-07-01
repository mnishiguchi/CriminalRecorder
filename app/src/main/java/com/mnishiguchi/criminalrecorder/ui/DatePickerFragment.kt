package com.mnishiguchi.criminalrecorder.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
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
    private val TAG = javaClass.simpleName

    companion object {
        val ARG_DATE = "ARG_DATE"
        val EXTRA_DATE = "${DatePickerFragment::class.java.canonicalName}.EXTRA_DATE"

        // Define how a hosting activity should create this fragment.
        fun newInstance(date: Date): DatePickerFragment {
            return DatePickerFragment().apply {
                arguments = bundleOf(ARG_DATE to date)
            }
        }

        // Define how the previous activity should get result.
        fun dateResult(data: Intent): Date {
            return data.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val initialDate = arguments.getSerializable(ARG_DATE) as Date

        val calendar = Calendar.getInstance().apply {
            time = initialDate
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePicker(activity).apply {
            init(year, month, day, null)
        }

        return AlertDialog.Builder(activity)
                .setTitle(R.string.date_picker_title)
                .setView(datePicker)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) {
                    _, _ ->
                    val date = GregorianCalendar(datePicker.year, datePicker.month, datePicker.dayOfMonth).time
                    Log.d(TAG, date.toString())
                    sendResult(Activity.RESULT_OK, date)
                }
                .create()
    }

    /**
     * Send a result back to a target fragment (requester).
     */
    private fun sendResult(resultCode: Int, date: Date) {
        Log.d(TAG, "sendResult")

        if (targetFragment == null) {
            Log.d(TAG, "sendResult: targetFragment: null")
            return
        }

        val intent = Intent().apply {
            putExtra(EXTRA_DATE, date)
        }

        // We need to directly call onActivityResult because the ActivityManager is not present in
        // the process of communicating between a fragment and a dialog.
        targetFragment.onActivityResult(targetRequestCode, resultCode, intent)
    }
}
