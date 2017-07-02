package com.mnishiguchi.criminalrecorder.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
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
import org.jetbrains.anko.intentFor
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
        private val EXTRA_CRIME_ID = "${CrimeFragment::class.java.canonicalName}.EXTRA_CRIME_ID"
        private val DIALOG_DATE = "DIALOG_DATE"
        private val REQUEST_DATE = 0

        // Define how a hosting activity should create this fragment.
        fun newInstance(crimeId: UUID): CrimeFragment {
            return CrimeFragment().apply {
                arguments = bundleOf(ARG_CRIME_ID to crimeId)
            }
        }

        // Define how the previous activity should get result.
        fun crimeIdResult(data: Intent): UUID {
            return data.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        }
    }

    // Tell the hosting activity to set result values because only activity can have results.
    private fun setResult(crimeId: UUID) {
        with(activity) {
            setResult(Activity.RESULT_OK, intentFor<CrimeFragment>(EXTRA_CRIME_ID to crimeId))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tell the FragmentManager that this fragment need its onCreateOptionsMenu to be called.
        setHasOptionsMenu(true)

        // Find a crime in CrimeLab and store the ref.
        val crimeId = arguments.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.get(activity).crime(crimeId) ?: throw Exception("Could not find a crime with the specified uuid.")

        // Create a DateFormat instance.
        df = this.context.mediumDateFormat()

        setResult(crimeId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    // https://developer.android.com/reference/android/app/Fragment.html#onViewCreated(android.view.View, android.os.Bundle)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
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
            val dialog = DatePickerFragment.newInstance(crime.date)
            dialog.setTargetFragment(this, REQUEST_DATE) // Similar to startActivityForResult
            dialog.show(fm, DIALOG_DATE)
        }
        updateDateText()

        crimeSolved.isChecked = crime.isSolved
        crimeSolved.setOnCheckedChangeListener {
            _, isChecked ->
            crime.isSolved = isChecked
        }
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
                CrimeLab.get(activity).remove(crime)
                activity.finish()
                return true // Indicate that no further processing is necessary.
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")

        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "Result was not OK")
            return
        }

        when (requestCode) {
            REQUEST_DATE -> {
                data?.let {
                    // Update the crime date to CrimeLab.
                    crime.date = DatePickerFragment.dateResult(data)

                    updateDateText()
                }
            }
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume - currentCrimeId: ${crime.id}")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    /**
     * Update the date text based on a crime stored in the CrimeLab.
     */
    private fun updateDateText() {
        crimeDate.text = df.format(crime.date)
    }
}
