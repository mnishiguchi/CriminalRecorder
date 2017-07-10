package com.mnishiguchi.criminalrecorder.ui

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.data.Crime
import com.mnishiguchi.criminalrecorder.util.inflate
import com.mnishiguchi.criminalrecorder.viewmodel.CrimeVM
import kotlinx.android.synthetic.main.fragment_crime_list.*
import kotlinx.android.synthetic.main.list_item_crime.view.*
import org.jetbrains.anko.support.v4.toast

/**
 * Use the [CrimeListFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeListFragment : Fragment() {
    private val TAG = javaClass.simpleName

    private val vm: CrimeVM by lazy { ViewModelProviders.of(activity).get(CrimeVM::class.java) }
    private lateinit var adapter: CrimeListAdapter
    private var isSubtitleVisible = false

    companion object {
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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.adapter = CrimeListAdapter(
                onClick = { clickedCrime ->
                    val intent = CrimePagerActivity.newIntent(activity, clickedCrime.id)
                    startActivity(intent)
                },
                onLongClick = { clickedCrime ->
                    AlertDialog.Builder(context)
                            .setTitle("Deleting ${clickedCrime.title} (id: ${clickedCrime.id})")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes") { _, _ ->
                                vm.destroy(clickedCrime) { count ->
                                    toast("Deleted ${clickedCrime.title}")
                                }
                            }
                            .setNegativeButton("No") { _, _ -> }
                            .show()
                    true
                },
                onCheckedChange = { changedCrime -> vm.update(changedCrime) }
        )

        crimeList.adapter = adapter
        crimeList.layoutManager = LinearLayoutManager(activity)

        vm.crimes.observe(activity as LifecycleOwner, Observer<List<Crime>> {
            // Set up the pager when data is available.
            it?.let {
                adapter.replaceDataSet(it)
                updateListVisibility()
                updateSubtitle()
            }
        })

        emptyListButton.setOnClickListener { startBlankCrime() }
        setDivider()
    }

    private fun setDivider() {
        DividerItemDecoration(crimeList.context, LinearLayoutManager(activity).orientation).apply {
            crimeList.addItemDecoration(this)
        }
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

    // In general, onResume is the safest place to take actions to update a fragment view.
    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_IS_SUBTITLE_VISIBLE, isSubtitleVisible)
    }

    /**
     * Create a blank crime and open an editor (CrimeFragment).
     */
    private fun startBlankCrime() {
        // Maybe in the future, use shared vm for current id.
        vm.create { id ->
            Log.d(TAG, "id: $id")

            val intent = CrimePagerActivity.newIntent(activity, id.toInt())
            startActivity(intent)
        }
    }

    /**
     * Show the placeholder view if the list is empty.
     */
    private fun updateListVisibility() {
        if (vm.isEmpty()) {
            crimeList.visibility = View.GONE
            emptyList.visibility = View.VISIBLE
        } else {
            crimeList.visibility = View.VISIBLE
            emptyList.visibility = View.GONE
        }
    }

    /**
     * Update subtitle based on its visibility status and current crime counts.
     */
    private fun updateSubtitle() {
        // Toggle the subtitle.
        // https://medium.com/google-developer-experts/how-to-add-toolbar-to-an-activity-which-doesn-t-extend-appcompatactivity-a07c026717b3
        (activity as AppCompatActivity).supportActionBar?.subtitle =
                if (isSubtitleVisible) {
                    resources.getQuantityString(R.plurals.quantity_crime_count, vm.size(), vm.size())
                } else null
    }

    /**
     * An adapter for CrimeListFragment.
     */
    class CrimeListAdapter(var crimes: List<Crime> = emptyList(),
                           val onClick: (crime: Crime) -> Unit,
                           val onLongClick: (crime: Crime) -> Boolean,
                           val onCheckedChange: (crime: Crime) -> Unit
    ) : RecyclerView.Adapter<CrimeListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = parent.inflate(R.layout.list_item_crime)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(crimes[position], onClick, onLongClick, onCheckedChange)
        }

        override fun getItemCount(): Int = crimes.size

        fun replaceDataSet(crimes: List<Crime>) {
            this.crimes = crimes
            this.notifyDataSetChanged()
        }

        /**
         * A view holder for CrimeListAdapter.
         * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ViewHolder.html
         */
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            fun bind(crime: Crime,
                     onClick: (crime: Crime) -> Unit,
                     onLongClick: (crime: Crime) -> Boolean,
                     onCheckedChange: (crime: Crime) -> Unit
            ) = with(itemView) {
                listItemCrimeTitle.text =
                        if (crime.title.trim().isEmpty())
                            resources.getString(android.R.string.unknownName)
                        else crime.title
                listItemCrimeDate.text = App.mediumDateFormat.format(crime.date)
                listItemCrimeIsSolved.isChecked = crime.isSolved
                listItemCrimeIsSolved.setOnCheckedChangeListener { _, isChecked ->
                    crime.isSolved = isChecked
                    onCheckedChange(crime)
                }
                setOnClickListener { onClick(crime) }
                setOnLongClickListener { onLongClick(crime) }
            }
        }
    }
}
