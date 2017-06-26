package com.mnishiguchi.criminalrecorder.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.mnishiguchi.criminalrecorder.R;

/**
 * A generic activity superclass for hosting a single fragment.
 * It subclasses FragmentActivity because we are using the support library fragments.
 */
abstract class SingleFragmentActivity : FragmentActivity() {

    /**
     * Subclasses of [SingleFragmentActivity] must implement this method.
     * @return An instance of the fragment that the activity is hosting.
     */
    abstract fun createFragment() : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)

        // Use supportFragmentManager because we are using the support library fragments.
        // Check if the fragment is already in the fragment manager's list.
        var fragment = supportFragmentManager.findFragmentById(R.id.singleFragmentContainer)

        // If it was not found, we create one and add to the fragment manager
        if (fragment == null) {
            // Instantiate the fragment that the activity is hosting.
            fragment = createFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.singleFragmentContainer, fragment)
                    .commit()
        }

        // A container view ID serves two purposes:
        // 1. Tells the FragmentManager where in the activity's view the fragment's view should appear.
        // 2. Used as a unique identifier for a fragment in the FragmentManager's list.
    }
}