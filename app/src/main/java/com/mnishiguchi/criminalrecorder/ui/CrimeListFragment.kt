package com.mnishiguchi.criminalrecorder.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.domain.CrimeLab
import com.mnishiguchi.criminalrecorder.utils.inflate
import kotlinx.android.synthetic.main.fragment_crime_list.*
import kotlinx.android.synthetic.main.list_item_crime.view.*

/**
 * Use the [CrimeListFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeListFragment : Fragment() {
    private val TAG: String = javaClass.simpleName

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment CrimeListFragment.
         */
        fun newInstance(): CrimeListFragment {
            val fragment = CrimeListFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LayoutManager handles the positioning of items and defines the scrolling behavior.
        crimeList.layoutManager = LinearLayoutManager(activity)

        updateUI()
    }

    private fun updateUI() {
        val crimes = CrimeLab.get(activity).crimes
        crimeList.adapter = CrimeListAdapter(crimes)
    }

    private class CrimeListAdapter(val crimes: List<Crime>) : RecyclerView.Adapter<CrimeListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = parent.inflate(R.layout.list_item_crime)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindCrime(crimes[position])
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

        // https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindCrime(crime: Crime) {
                with(itemView) {
                    listItemCrimeTitle.text = crime.title
                    listItemCrimeDate.text = crime.date.toString()
                    listItemCrimeIsSolved.isChecked = crime.isSolved
                }
            }
        }
    }
}