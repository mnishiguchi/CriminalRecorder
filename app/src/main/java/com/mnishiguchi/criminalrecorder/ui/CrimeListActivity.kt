package com.mnishiguchi.criminalrecorder.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.viewmodel.CrimeVM
import kotlinx.android.synthetic.main.activity_two_pane.*

class CrimeListActivity : SingleFragmentActivity() {
    private val TAG = javaClass.simpleName

    private val vm: CrimeVM by lazy { ViewModelProviders.of(this).get(CrimeVM::class.java) }

    override fun getLayoutResId(): Int = R.layout.activity_master_detail

    override fun createFragment(): Fragment = CrimeListFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.getSelectedCrime().observe(this, Observer<Int> {
            it?.let {
                Log.d(TAG, "selected: $it")
                onCrimeSelected(it)
            }
        })
    }

    private fun onCrimeSelected(id: Int) {
        if (detail_fragment_container == null) {
            val intent = CrimePagerActivity.newIntent(this, id)
            startActivity(intent)
        } else {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, CrimeFragment.newInstance(id))
                    .commit()
        }
    }
}
