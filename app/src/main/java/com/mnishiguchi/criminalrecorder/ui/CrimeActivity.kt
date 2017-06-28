package com.mnishiguchi.criminalrecorder.ui

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import java.util.*

/**
 * Inherit FragmentActivity because we use support fragments.
 */
class CrimeActivity : SingleFragmentActivity() {
    private val TAG = javaClass.simpleName

    companion object {
        private val EXTRA_CRIME_ID = "${CrimeActivity::class.java.canonicalName}.EXTRA_CRIME_ID"

        // Define an extra intent for starting this activity.
        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            return Intent(packageContext, CrimeActivity::class.java).apply {
                putExtra(EXTRA_CRIME_ID, crimeId)
            }
        }
    }

    override fun createFragment(): Fragment {
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        return CrimeFragment.newInstance(crimeId)
    }
}
