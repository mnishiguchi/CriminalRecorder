package com.mnishiguchi.criminalrecorder.ui;

import android.arch.lifecycle.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.data.Crime
import com.mnishiguchi.criminalrecorder.viewmodel.CrimeVM
import kotlinx.android.synthetic.main.activity_crime_pager.*

/**
 * We need to subclass a subclass of android.support.v4.app.FragmentActivity, so that we can use:
 *   + ViewPager
 *   + support-library fragments
 *   + cross-api-version toolbar
 * https://developer.android.com/reference/android/support/v7/app/AppCompatActivity.html
 *
 * A ViewPager is only available in the support library and requires a PagerAdapter, such as
 * FragmentStatePagerAdapter and FragmentPagerAdapter.
 * https://developer.android.com/reference/android/support/v4/view/ViewPager.html
 */
class CrimePagerActivity : AppCompatActivity(), LifecycleRegistryOwner {
    private val TAG = javaClass.simpleName

    // LifeCycle - This will be unnecessary in the future.
    // https://developer.android.com/reference/android/arch/lifecycle/LifecycleRegistryOwner.html
    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    // ViewModel
    private val vm: CrimeVM by lazy { ViewModelProviders.of(this).get(CrimeVM::class.java) }

    companion object {
        private val EXTRA_CRIME_ID = "${CrimePagerActivity::class.java.canonicalName}.EXTRA_CRIME_ID"

        // Define an extra intent for starting this activity.
        fun newIntent(packageContext: Context, crimeId: Int): Intent {
            return Intent(packageContext, CrimePagerActivity::class.java).apply {
                putExtra(EXTRA_CRIME_ID, crimeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        setupPager()
    }

    private fun setupPager() {
        // Get a crime id from the intent so that we can determine the initial item.
        val crimeId: Int = intent.getSerializableExtra(EXTRA_CRIME_ID) as Int

        val adapter: CrimePageAdapter = CrimePageAdapter(supportFragmentManager)
        crimePager.adapter = adapter

        vm.crimes.observe(this as LifecycleOwner, Observer<List<Crime>> { crimes ->
            // Set up the pager when data is available.
            crimes?.let {
                adapter.replaceDataSet(crimes)

                // Set initial pager item based on the id provided by the previous activity.
                crimePager.currentItem = vm.indexById(crimeId)

                Log.d(TAG, "crimePager.currentItem : ${crimePager.currentItem}")

                // Unsubscribe the data since we are done with setting up the pager.
                vm.crimes.removeObservers(this as LifecycleOwner)
            }
        })
    }

    class CrimePageAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private var crimes: List<Crime> = emptyList()

        override fun getItem(position: Int): Fragment = CrimeFragment.newInstance(crimes[position].id)
        override fun getCount(): Int = crimes.size

        /**
         * Replace the data set with the new one and refresh the pager.
         */
        fun replaceDataSet(crimes: List<Crime>) {
            this.crimes = crimes
            this.notifyDataSetChanged()
        }
    }
}

