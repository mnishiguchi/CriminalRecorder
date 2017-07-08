package com.mnishiguchi.criminalrecorder.ui;

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
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
    companion object {
        val DEFAULT_LAYOUT_RES: Int = R.layout.activity_one_pane
    }

    /**
     * A subclass can choose to overrider this function to return a layout other than the default.
     */
    @LayoutRes
    protected open fun getLayoutResId(): Int = DEFAULT_LAYOUT_RES

    /**
     * Subclasses of [SingleFragmentActivity] must implement this method.
     * @return An instance of the fragment that the activity is hosting.
     */
    abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())

        // Use supportFragmentManager because we are using the support library fragments.
        val fm = supportFragmentManager

        // Register a fragment to the fragment manager if not already.
        // NOTE: Never add the same fragment twice!
        if (findFragment(fm) == null) registerFragment(fm)
    }

    // A container view ID serves two purposes:
    // 1. Tells the FragmentManager where in the activity's view the fragment's view should appear.
    // 2. Used as a unique identifier for a fragment in the FragmentManager's list.

    // Find a fragment instance in a fragment manager's list.
    private fun findFragment(fm: FragmentManager): Fragment? {
        return fm.findFragmentById(R.id.fragment_container)
    }

    // Create a new fragment instance and register it to a fragment manager.
    private fun registerFragment(fm: FragmentManager) {
        fm.beginTransaction()
                .add(R.id.fragment_container, createFragment())
                .commit()
    }
}