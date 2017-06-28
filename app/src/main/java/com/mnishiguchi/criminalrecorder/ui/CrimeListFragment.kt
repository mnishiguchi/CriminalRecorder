package com.mnishiguchi.criminalrecorder.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.domain.CrimeLab
import com.mnishiguchi.criminalrecorder.utils.inflate
import kotlinx.android.synthetic.main.fragment_crime_list.*
import kotlinx.android.synthetic.main.list_item_crime.view.*
import org.jetbrains.anko.toast

/**
 * Use the [CrimeListFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeListFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private val REQUEST_CRIME = 1
    private var clickedPosition = -1

    companion object {
        // Define how a hosting activity should create this fragment.
        fun newInstance(): CrimeListFragment {
            val fragment = CrimeListFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")

        super.onViewCreated(view, savedInstanceState)

        // LayoutManager handles the positioning of items and defines the scrolling behavior.
        crimeList.layoutManager = LinearLayoutManager(activity)

        updateUI()
    }

    // Called before onResume if a child activity set results.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d(TAG, "onActivityResult")

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CRIME -> {
                val crimeId = CrimeFragment.crimeIdResult(data)
                activity.toast("crime id: $crimeId")
            }
        }
    }

    // In general, onResume is the safest place to take actions to update a fragment view.
    override fun onResume() {
        Log.d(TAG, "onResume")

        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val crimes = CrimeLab.get(activity).crimes

        if (crimeList.adapter == null) {
            crimeList.adapter = CrimeListAdapter(crimes) {
                // on-click callback
                (id), position ->
                val intent = CrimeActivity.newIntent(context, id)
                startActivityForResult(intent, REQUEST_CRIME)

                // Remember the position so that we can update that item later.
                clickedPosition = position
            }
        } else {
            Log.d(TAG, "clickedPosition: $clickedPosition")

            // Reload the list.
            when (clickedPosition) {
                -1 -> crimeList.adapter.notifyDataSetChanged()
                else -> crimeList.adapter.notifyItemChanged(clickedPosition)
            }


        }
    }

    private class CrimeListAdapter(val crimes: List<Crime>, val itemClick: (crime: Crime, position: Int) -> Unit)
        : RecyclerView.Adapter<CrimeListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = parent.inflate(R.layout.list_item_crime)
            return ViewHolder(view, itemClick)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindCrime(crimes[position], position)
        }

        override fun getItemCount(): Int = crimes.size

        // https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html
        class ViewHolder(view: View, val itemClick: (crime: Crime, position: Int) -> Unit)
            : RecyclerView.ViewHolder(view) {

            fun bindCrime(crime: Crime, position: Int) = with(itemView) {
                listItemCrimeTitle.text = crime.title
                listItemCrimeDate.text = crime.date.toString()
                listItemCrimeIsSolved.isChecked = crime.isSolved
                listItemCrimeIsSolved.setOnCheckedChangeListener {
                    _, isChecked ->
                    crime.isSolved = isChecked
                }
                setOnClickListener { itemClick(crime, position) }
            }
        }
    }
}