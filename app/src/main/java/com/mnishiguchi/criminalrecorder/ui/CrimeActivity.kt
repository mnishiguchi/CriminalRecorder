package com.mnishiguchi.criminalrecorder.ui

import android.app.Activity
import android.support.v4.app.Fragment
import org.jetbrains.anko.startActivity
import java.util.*

/**
 * Inherit FragmentActivity because we use support fragments.
 */
class CrimeActivity : SingleFragmentActivity() {
    private val TAG: String = javaClass.simpleName

    companion object {
        private val EXTRA_CRIME_ID = "${CrimeActivity::class.java.canonicalName}.EXTRA_CRIME_ID"

        // Define how a parent activity should start this activity.
        fun start(activity: Activity, crimeId: UUID) {
            activity.startActivity<CrimeActivity>(EXTRA_CRIME_ID to crimeId)
        }
    }

    override fun createFragment(): Fragment {
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        return CrimeFragment.newInstance(crimeId)
    }
}
