package com.mnishiguchi.criminalrecorder.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.domain.CrimeLab
import com.mnishiguchi.criminalrecorder.utils.mediumDateFormat
import kotlinx.android.synthetic.main.fragment_crime.*
import org.jetbrains.anko.bundleOf
import java.util.*

/**
 * Use the [CrimeFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeFragment : Fragment() {
    private val TAG = javaClass.simpleName

    lateinit private var crime: Crime
    lateinit private var df: java.text.DateFormat
    private val fm: FragmentManager by lazy { activity.supportFragmentManager }

    companion object {
        private val ARG_CRIME_ID = "${CrimeFragment::class.java.canonicalName}.ARG_CRIME_ID"
        private val DIALOG_DATE = "DIALOG_DATE"
        private val REQUEST_DATE = 0

        // Define how a hosting activity should create this fragment.
        fun newInstance(crimeId: UUID): CrimeFragment {
            return CrimeFragment().apply {
                arguments = bundleOf(ARG_CRIME_ID to crimeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tell the FragmentManager that this fragment need its onCreateOptionsMenu to be called.
        setHasOptionsMenu(true)

        // Find a crime in CrimeLab and store the ref.
        val uuid = arguments.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.crime(uuid) ?: throw Exception("Could not find a crime (uuid: $uuid)")

        // Create a DateFormat instance.
        df = this.context.mediumDateFormat()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: _id: ${crime._id}")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    // https://developer.android.com/reference/android/app/Fragment.html#onViewCreated(android.view.View, android.os.Bundle)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: _id: ${crime._id}")
        super.onViewCreated(view, savedInstanceState)

        crimeTitle.setText(crime.title)
        crimeTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        crimeDate.setOnClickListener {
            val dialog = DatePickerFragment.newInstance(Date(crime.date))
            dialog.setTargetFragment(this, REQUEST_DATE) // Similar to startActivityForResult
            dialog.show(fm, DIALOG_DATE)
        }
        updateDateText()

        crimeSolved.isChecked = crime.isSolved
        crimeSolved.setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }

        crimeReport.setOnClickListener { sendCrimeReport() }
    }

    // Inflate the menu view. Make sure that we specify setHasOptionsMenu(true) in onCreate.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime, menu)
    }

    // Called when the user clicks on a menu item.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete_crime -> {
                AlertDialog.Builder(context)
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes") { _, _ ->
                            CrimeLab.remove(crime)
                            activity.finish()
                        }
                        .setNegativeButton("No") { _, _ -> }
                        .show()
                return true // Indicate that no further processing is necessary.
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: _id: ${crime._id}")

        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "Result was not OK")
            return
        }

        when (requestCode) {
            REQUEST_DATE -> {
                data?.let {
                    // Update the crime date to CrimeLab.
                    crime.date = DatePickerFragment.dateResult(data).time

                    updateDateText()
                }
            }
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume: _id: ${crime._id}")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onResume: _id: ${crime._id}")
        super.onPause()

        Log.d(TAG, "_id: ${crime._id}")
        CrimeLab.save(crime)
    }

    /**
     * Update the date text based on a crime stored in the CrimeLab.
     */
    private fun updateDateText(): Unit {
        crimeDate.text = df.format(crime.date)
    }

    /**
     * Start an application that can send a report.
     */
    private fun sendCrimeReport(): Unit {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
        }
        val intentWithChooser = Intent.createChooser(intent, getString(R.string.send_report))
        startActivity(intentWithChooser)
    }

    /**
     * Generate a text for a crime report.
     */
    private fun getCrimeReport(): String {
        val dateString = getString(R.string.crime_report_date, df.format(crime.date))

        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val suspectString = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect)
        }

        return getString(R.string.crime_report,
                crime.title, dateString, solvedString, suspectString).trim()
    }
}


