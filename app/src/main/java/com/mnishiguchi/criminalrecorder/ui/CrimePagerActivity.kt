package com.mnishiguchi.criminalrecorder.ui;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.domain.CrimeLab
import kotlinx.android.synthetic.main.activity_crime_pager.*
import java.util.*


/**
 * We subclass android.support.v7.app.AppCompatActivity, which is a subclass of
 * android.support.v4.app.FragmentActivity, so that we can use:
 *   + ViewPager
 *   + support-library fragments
 *   + cross-api-version toolbar
 * https://developer.android.com/reference/android/support/v7/app/AppCompatActivity.html
 *
 * A ViewPager is only available in the support library and requires a PagerAdapter, such as
 * FragmentStatePagerAdapter and FragmentPagerAdapter.
 * https://developer.android.com/reference/android/support/v4/view/ViewPager.html
 */
class CrimePagerActivity : AppCompatActivity(), CrimeFragment.Callback {
    private val TAG = javaClass.simpleName

    companion object {
        private val EXTRA_CRIME_ID = "${CrimePagerActivity::class.java.canonicalName}.EXTRA_CRIME_ID"

        // Define an extra intent for starting this activity.
        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            return Intent(packageContext, CrimePagerActivity::class.java).apply {
                putExtra(EXTRA_CRIME_ID, crimeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        // Get a crime uuid from the intent so that we can determine the initial item.
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID

        val crimes = CrimeLab.crimes()

        crimePager.adapter = CrimePagerAdapter(supportFragmentManager, crimes)
        // Set initial pager item based on the uuid provided by the previous activity.
        crimePager.currentItem = crimes.indexOfFirst { it.uuid == crimeId }
    }

    override fun onCrimeUpdated(crime: Crime) {
        Log.d(TAG, "onCrimeUpdated")
        // Do nothing.
    }

    override fun onCrimeDeleted(crime: Crime) {
        Log.d(TAG, "onCrimeDeleted")

        // Just finishing the view pager is the easiest option for what we do after deleting an item.
        // Updating the view pager seems tricky.
        finish()
    }
}

class CrimePagerAdapter(fm: FragmentManager, val crimes: MutableList<Crime>) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = CrimeFragment.newInstance(crimes[position].uuid)
    override fun getCount(): Int = crimes.size

    // This is called when notifyDataSetChanged() is called.
    override fun getItemPosition(item: Any): Int {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE
    }
}