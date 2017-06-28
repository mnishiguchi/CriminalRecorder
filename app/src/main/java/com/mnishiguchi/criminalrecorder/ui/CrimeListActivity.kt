package com.mnishiguchi.criminalrecorder.ui

import android.support.v4.app.Fragment

class CrimeListActivity : SingleFragmentActivity() {
    private val TAG = javaClass.simpleName

    override fun createFragment(): Fragment {
        return CrimeListFragment.newInstance()
    }
}
