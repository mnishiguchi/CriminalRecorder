package com.mnishiguchi.criminalrecorder.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.domain.CrimeLab
import kotlinx.android.synthetic.main.fragment_crime.*
import org.jetbrains.anko.bundleOf
import java.util.*

/**
 * Use the [CrimeFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeFragment : Fragment() {
    private val TAG: String = javaClass.simpleName
    lateinit private var crime: Crime

    companion object {
        val ARG_CRIME_ID = "${CrimeFragment::class.java.canonicalName}.ARG_CRIME_ID"

        // Define how a hosting activity should create this fragment.
        fun newInstance(crimeId: UUID): CrimeFragment {
            val fragment = CrimeFragment()
            fragment.arguments = bundleOf(ARG_CRIME_ID to crimeId)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crimeId = arguments.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.get(activity).crime(crimeId) ?: throw Exception("Could not find a crime with the specified uuid.")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    // https://developer.android.com/reference/android/app/Fragment.html#onViewCreated(android.view.View, android.os.Bundle)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeTitle.setText(crime.title)
        crimeTitle.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crimeTitle.setText(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val df = android.text.format.DateFormat.getLongDateFormat(this.context)
        crimeDate.text = df.format(crime.date)
        crimeDate.isEnabled = false

        crimeSolved.isChecked = crime.isSolved
        crimeSolved.setOnCheckedChangeListener {
            _, isChecked -> crime.isSolved = isChecked
        }
    }
}
