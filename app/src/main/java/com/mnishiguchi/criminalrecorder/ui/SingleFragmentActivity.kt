package com.mnishiguchi.criminalrecorder.ui;

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.mnishiguchi.criminalrecorder.R

/**
 * A generic activity superclass for hosting a single fragment.
 * We subclass android.support.v7.app.AppCompatActivity, which is a subclass of
 * android.support.v4.app.FragmentActivity, so that we can use:
 *   + support-library fragments
 *   + cross-api-version toolbar
 * https://developer.android.com/reference/android/support/v7/app/AppCompatActivity.html
 */
abstract class SingleFragmentActivity : AppCompatActivity() {

    /**
     * Subclasses of [SingleFragmentActivity] must implement this method.
     * @return An instance of the fragment that the activity is hosting.
     */
    abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)

        // Use supportFragmentManager because we are using the support library fragments.
        with(supportFragmentManager) {
            // Find a fragment in the fragment manager's list or create a new instance.
            val fragment = findFragmentById(R.id.singleFragmentContainer) ?: createFragment()
            beginTransaction()
                    .add(R.id.singleFragmentContainer, fragment)
                    .commit()
        }

        // A container view ID serves two purposes:
        // 1. Tells the FragmentManager where in the activity's view the fragment's view should appear.
        // 2. Used as a unique identifier for a fragment in the FragmentManager's list.
    }
}