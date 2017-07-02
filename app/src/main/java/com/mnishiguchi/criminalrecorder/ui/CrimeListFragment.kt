package com.mnishiguchi.criminalrecorder.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.domain.CrimeLab
import com.mnishiguchi.criminalrecorder.utils.inflate
import com.mnishiguchi.criminalrecorder.utils.mediumDateFormat
import kotlinx.android.synthetic.main.fragment_crime_list.*
import kotlinx.android.synthetic.main.list_item_crime.view.*
import org.jetbrains.anko.toast


/**
 * Use the [CrimeListFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeListFragment : Fragment() {
    private val TAG = javaClass.simpleName

    lateinit private var dateFormat: java.text.DateFormat
    private var clickedPosition = -1
    private var isSubtitleVisible = false

    companion object {
        private val REQUEST_CRIME = 1
        private val SAVED_IS_SUBTITLE_VISIBLE = "SAVED_IS_SUBTITLE_VISIBLE"

        // Define how a hosting activity should create this fragment.
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            isSubtitleVisible = savedInstanceState.getBoolean(SAVED_IS_SUBTITLE_VISIBLE, false)
        }

        // Tell the FragmentManager that this fragment need its onCreateOptionsMenu to be called.
        setHasOptionsMenu(true)

        // Create a DateFormat instance.
        dateFormat = this.context.mediumDateFormat()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LayoutManager handles the positioning of items and defines the scrolling behavior.
        crimeList.layoutManager = LinearLayoutManager(activity)

        updateUI()
    }

    // Inflate the menu view. Make sure that we specify setHasOptionsMenu(true) in onCreate.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)

        // Switch the "toggle subtitle" menu item.
        val menuItem: MenuItem = menu.findItem(R.id.menu_item_toggle_subtitle)
        when (isSubtitleVisible) {
            true -> menuItem.setTitle(R.string.hide_subtitle)
            else -> menuItem.setTitle(R.string.show_subtitle)
        }
    }

    // Called when the user clicks on a menu item.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_new_crime -> {
                startBlankCrime()
                return true // Indicate that no further processing is necessary.
            }
            R.id.menu_item_toggle_subtitle -> {
                isSubtitleVisible = !isSubtitleVisible

                // Redraw the options menu because we want to toggle show/hide subtitle menu item.
                activity.invalidateOptionsMenu()

                updateSubtitle()
                return true // Indicate that no further processing is necessary.
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Called before onResume if a child activity set results.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d(TAG, "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_IS_SUBTITLE_VISIBLE, isSubtitleVisible)
    }

    // Create a blank crime and open an editor (CrimeFragment).
    private fun startBlankCrime() {
        val newCrime = CrimeLab.get(activity).newCrime()
        val intent = CrimePagerActivity.newIntent(activity, newCrime.id)
        startActivity(intent)
    }

    // Update subtitle based on its visibility status and current crime counts.
    private fun updateSubtitle() {
        // Toggle the subtitle.
        (activity as AppCompatActivity).supportActionBar?.subtitle =
                if (isSubtitleVisible) {
                    val crimeCount = CrimeLab.get(activity).crimes.size
                    resources.getQuantityString(R.plurals.quantity_crime_count, crimeCount, crimeCount)
                } else null
    }

    private fun updateUI() {
        val crimes = CrimeLab.get(activity).crimes

        if (crimeList.adapter == null) {
            crimeList.adapter = CrimeListAdapter(crimes, dateFormat) {
                // on-click callback
                (id), position ->
                val intent = CrimePagerActivity.newIntent(activity, id)
                startActivityForResult(intent, REQUEST_CRIME)

                // Remember the position for later use.
                clickedPosition = position
            }
        } else {
            // Reload the list.
            // FIXME - Update data more efficiently
            // - Maybe we can use notifyItemChanged(Int) or google's new architecture components.
            crimeList.adapter.notifyDataSetChanged()
        }

        updateSubtitle()
    }

    /**
     * An adapter for CrimeListFragment.
     */
    private class CrimeListAdapter(val crimes: List<Crime>,
                                   val dateFormat: java.text.DateFormat,
                                   val itemClick: (crime: Crime, position: Int) -> Unit)
        : RecyclerView.Adapter<CrimeListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = parent.inflate(R.layout.list_item_crime)
            return ViewHolder(view, dateFormat, itemClick)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindCrime(crimes[position], position)
        }

        override fun getItemCount(): Int = crimes.size

        /**
         * A view holder for CrimeListAdapter.
         * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html
         */
        class ViewHolder(view: View,
                         val dateFormat: java.text.DateFormat,
                         val itemClick: (crime: Crime, position: Int) -> Unit)
            : RecyclerView.ViewHolder(view) {

            fun bindCrime(crime: Crime, position: Int) = with(itemView) {
                listItemCrimeTitle.text = crime.title
                listItemCrimeDate.text = dateFormat.format(crime.date)
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
