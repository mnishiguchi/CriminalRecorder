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
import kotlinx.android.synthetic.main.fragment_crime.*

/**
 * Use the [CrimeFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeFragment : Fragment() {
    private val TAG: String = javaClass.simpleName

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment CrimeFragment.
         */
        fun newInstance(): CrimeFragment {
            val fragment = CrimeFragment()
            return fragment
        }
    }

    private val crime: Crime by lazy { Crime() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_crime, container, false)
    }

    // https://developer.android.com/reference/android/app/Fragment.html#onViewCreated(android.view.View, android.os.Bundle)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        crimeSolved.setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }
    }
}
