package com.mnishiguchi.criminalrecorder.ui;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mnishiguchi.criminalrecorder.R
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
class CrimePagerActivity : AppCompatActivity() {
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

        // Get a crime id from the intent so that we can determine the initial item.
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID

        val crimes = CrimeLab.get(this).crimes

        crimePager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment = CrimeFragment.newInstance(crimes[position].id)
            override fun getCount(): Int = crimes.size
        }

        // Set initial pager item based on the id provided by the previous activity.
        crimePager.currentItem = crimes.indexOfFirst { it.id == crimeId }
    }
}