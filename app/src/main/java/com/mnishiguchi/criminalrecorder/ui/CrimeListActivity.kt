package com.mnishiguchi.criminalrecorder.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime

/**
 * This activity displays a list of all the crimes. If it is in two-pane mode, it also shows the
 * details of the selected item.
 *
 * The layout file is determined by the value for activity_master_detail in res/values/refs.xml.
 * It can be one-pane or two-pane layout depending on the device configurations.
 *
 * This activity will check whether it is currently in one-pane mode or two-pane, by the existence
 * of the detail pane in the layout file.
 */
class CrimeListActivity : SingleFragmentActivity(), CrimeListFragment.Callback, CrimeFragment.Callback {
    private val TAG = javaClass.simpleName

    private val isSinglePane: Boolean by lazy { findViewById(R.id.detail_fragment_container) == null }
    private val fm: FragmentManager by lazy { supportFragmentManager }

    override fun getLayoutResId(): Int = R.layout.activity_master_detail
    override fun createFragment(): Fragment = CrimeListFragment.newInstance()

    override fun onCrimeListItemSelected(crime: Crime) {
        Log.d(TAG, "onCrimeListItemSelected: isSinglePane: $isSinglePane")

        if (isSinglePane) {
            val intent = CrimePagerActivity.newIntent(this, crime.uuid)
            startActivity(intent)
        } else {
            val crimeFragment = CrimeFragment.newInstance(crime.uuid)
            fm.beginTransaction()
                    .replace(R.id.detail_fragment_container, crimeFragment)
                    .commit()
        }
    }

    override fun onCrimeUpdated(crime: Crime) {
        updateListUI()
    }

    override fun onCrimeDeleted(crime: Crime) {
        removeDetailFragment()
        updateListUI()
    }

    private fun updateListUI() {
        val crimeListFragment = fm.findFragmentById(R.id.fragment_container) as CrimeListFragment
        crimeListFragment.updateUI()
    }

    private fun removeDetailFragment() {
        val crimeFragment = fm.findFragmentById(R.id.detail_fragment_container) as CrimeFragment
        fm.beginTransaction()
                .remove(crimeFragment)
                .commitNow()
    }
}
