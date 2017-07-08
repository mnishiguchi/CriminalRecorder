package com.mnishiguchi.criminalrecorder.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.domain.CrimeLab
import com.mnishiguchi.criminalrecorder.util.inflate
import com.mnishiguchi.criminalrecorder.util.mediumDateFormat
import kotlinx.android.synthetic.main.fragment_crime_list.*
import kotlinx.android.synthetic.main.list_item_crime.view.*

// Global state for this file.
var currentPosition = -1

/**
 * Use the [CrimeListFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeListFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private var callback: CrimeListFragment.Callback? = null

    /**
     * Required interface for hosting activities.
     */
    interface Callback {
        fun onCrimeListItemSelected(crime: Crime)
    }

    private val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(activity) }
    private val dateFormat: java.text.DateFormat by lazy { this.context.mediumDateFormat() }
    private var isSubtitleVisible = false

    companion object {
        private val SAVED_IS_SUBTITLE_VISIBLE = "SAVED_IS_SUBTITLE_VISIBLE"

        // Define how a hosting activity should create this fragment.
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = activity as Callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            isSubtitleVisible = savedInstanceState.getBoolean(SAVED_IS_SUBTITLE_VISIBLE, false)
        }

        // Tell the FragmentManager that this fragment need its onCreateOptionsMenu to be called.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LayoutManager handles the positioning of items and defines the scrolling behavior.
        crimeList.layoutManager = layoutManager

        setEmptyView()
        setDivider()
        updateUI()
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

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    // Inflate the menu view. Make sure that we specify setHasOptionsMenu(true) in onCreate.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)

        // Switch the "toggle subtitle" menu item.
        val menuItem: MenuItem = menu.findItem(R.id.menu_item_toggle_subtitle)
        if (isSubtitleVisible) {
            menuItem.setTitle(R.string.hide_subtitle)
        } else {
            menuItem.setTitle(R.string.show_subtitle)
        }
    }

    // Called when the user clicks on a menu item.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_new_crime -> {
                val crime = CrimeLab.create()
                callback?.onCrimeListItemSelected(crime)
                updateUI()
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

    /**
     * Update the uI based on the latest data set.
     */
    fun updateUI() {
        val newDataSet = CrimeLab.crimes()

        if (crimeList.adapter == null) {
            val itemClick: (Crime, Int) -> Unit = { crime: Crime, position: Int ->
                callback?.onCrimeListItemSelected(crime)
                currentPosition = position
            }
            crimeList.adapter = CrimeListAdapter(newDataSet, dateFormat, itemClick)
        } else {
            // Reload the list.
            crimeList.adapter.notifyDataSetChanged()
        }

        // Show the placeholder view if the list is empty.
        if (newDataSet.isEmpty()) {
            crimeList.visibility = View.GONE
            emptyList.visibility = View.VISIBLE
        } else {
            crimeList.visibility = View.VISIBLE
            emptyList.visibility = View.GONE
        }

        updateSubtitle()
    }

    private fun setEmptyView() {
        emptyListButton.setOnClickListener {
            val crime = CrimeLab.create()
            callback?.onCrimeListItemSelected(crime)
        }
    }

    private fun setDivider() {
        val dividerItemDecoration = DividerItemDecoration(crimeList.context, layoutManager.orientation)
        crimeList.addItemDecoration(dividerItemDecoration)
    }

    // Update subtitle based on its visibility status and current crime counts.
    private fun updateSubtitle() {
        // Toggle the subtitle.
        (activity as AppCompatActivity).supportActionBar?.subtitle =
                if (isSubtitleVisible) {
                    val crimeCount = CrimeLab.size
                    resources.getQuantityString(R.plurals.quantity_crime_count, crimeCount, crimeCount)
                } else null
    }
}

/**
 * An adapter for CrimeListFragment.
 */
class CrimeListAdapter(val crimes: List<Crime>,
                       val dateFormat: java.text.DateFormat,
                       val itemClick: (crime: Crime, position: Int) -> Unit)
    : RecyclerView.Adapter<CrimeListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeListViewHolder {
        val view = parent.inflate(R.layout.list_item_crime)
        return CrimeListViewHolder(view, dateFormat, itemClick)
    }

    override fun onBindViewHolder(holder: CrimeListViewHolder, position: Int) {
        holder.bindCrime(crimes[position], position)
    }

    override fun getItemCount(): Int = crimes.size
}

/**
 * A view holder for CrimeListAdapter.
 * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html
 */
class CrimeListViewHolder(view: View,
                          val dateFormat: java.text.DateFormat,
                          val itemClick: (crime: Crime, position: Int) -> Unit)
    : RecyclerView.ViewHolder(view) {

    fun bindCrime(crime: Crime, position: Int) {
        with(itemView) {
            val backgroundColor =
                    if (position == currentPosition)
                        ResourcesCompat.getColor(resources, android.R.color.holo_blue_bright, App.instance.theme)
                    else
                        ResourcesCompat.getColor(resources, android.R.color.transparent, App.instance.theme)
            setBackgroundColor(backgroundColor)

            listItemCrimeTitle.text =
                    if (crime.title.isBlank())
                        resources.getString(android.R.string.unknownName)
                    else
                        crime.title

            listItemCrimeDate.text = dateFormat.format(crime.date)

            // Show / hide the check icon according to the crime's state.
            with(listItemCrimeIsSolved) {
                if (crime.isSolved) {
                    setBackgroundResource(R.drawable.ic_check_black_24dp)
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }

            setOnClickListener {
                itemClick(crime, position)
            }
        }
    }
}
