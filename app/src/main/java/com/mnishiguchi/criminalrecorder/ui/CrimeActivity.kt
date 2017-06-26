package com.mnishiguchi.criminalrecorder.ui

import android.support.v4.app.Fragment

/**
 * Inherit FragmentActivity because we use support fragments.
 */
class CrimeActivity : SingleFragmentActivity() {
    private val TAG: String = javaClass.simpleName

    override fun createFragment(): Fragment {
        return CrimeFragment.newInstance()
    }
}
