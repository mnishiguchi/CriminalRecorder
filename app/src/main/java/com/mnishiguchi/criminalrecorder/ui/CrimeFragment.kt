package com.mnishiguchi.criminalrecorder.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.ShareCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import com.mnishiguchi.criminalrecorder.R
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.domain.CrimeLab
import com.mnishiguchi.criminalrecorder.util.getScaledBitmap
import com.mnishiguchi.criminalrecorder.util.mediumDateFormat
import kotlinx.android.synthetic.main.fragment_crime.*
import kotlinx.android.synthetic.main.view_camera_and_title.*
import org.jetbrains.anko.bundleOf
import java.io.File
import java.util.*

/**
 * Use the [CrimeFragment.newInstance] factory method to create an instance of this fragment.
 */
class CrimeFragment : Fragment() {
    private val TAG = javaClass.simpleName

    lateinit private var crime: Crime
    lateinit private var df: java.text.DateFormat

    private val photoFile: File? by lazy { CrimeLab.photoFile(crime) } // Null if no external storage was found!!
    private val canTakePhoto: Boolean by lazy { photoFile != null && isCameraAvailable() }
    private val canUseContactList: Boolean by lazy { isContactListAvailable() }
    private val fm: FragmentManager by lazy { activity.supportFragmentManager }

    companion object {
        private val ARG_CRIME_ID = "${CrimeFragment::class.java.canonicalName}.ARG_CRIME_ID"
        private val DIALOG_DATE = "DIALOG_DATE"
        private val DIALOG_PHOTO = "DIALOG_PHOTO"
        private val REQUEST_DATE = 0
        private val REQUEST_CONTACT = 1
        private val REQUEST_PHOTO = 2

        // Define how a hosting activity should create this fragment.
        fun newInstance(crimeId: UUID): CrimeFragment {
            return CrimeFragment().apply {
                arguments = bundleOf(ARG_CRIME_ID to crimeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tell the FragmentManager that this fragment need its onCreateOptionsMenu to be called.
        setHasOptionsMenu(true)

        // Find a crime in CrimeLab and store the ref.
        val uuid = arguments.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.crime(uuid) ?: throw Exception("Could not find a crime (uuid: $uuid)")

        // Create a DateFormat instance.
        df = this.context.mediumDateFormat()

        Log.d(TAG, "photoFile: $photoFile")
        Log.d(TAG, "canTakePhoto: $canTakePhoto")
        Log.d(TAG, "canUseContactList: $canUseContactList")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView: _id: ${crime._id}")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    // https://developer.android.com/reference/android/app/Fragment.html#onViewCreated(android.view.View, android.os.Bundle)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: _id: ${crime._id}")
        super.onViewCreated(view, savedInstanceState)

        crimePhoto.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout(): Unit {
                // Update the photo view only once, taking into consideration the view size.
                updatePhotoView()
                crimePhoto.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        crimePhoto.setOnClickListener { showZoomedInPhoto() }

        crimeCameraButton.isEnabled = canTakePhoto
        crimeCameraButton.setOnClickListener { startCameraForResult() }

        crimeTitle.setText(crime.title)
        crimeTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        crimeTitle.clearFocus()

        crimeDate.setOnClickListener { startDatePickerForResult() }
        updateDateText()

        crimeSolvedButton.isChecked = crime.isSolved
        crimeSolvedButton.setOnCheckedChangeListener { _, isChecked -> crime.isSolved = isChecked }

        crimeReportButton.setOnClickListener { sendCrimeReport() }

        crimeSuspectButton.setOnClickListener { startContactListForSuspect() }
        if (!crime.suspect.isBlank()) {
            crimeSuspectButton.text = crime.suspect
        }
        if (!canUseContactList) {
            crimeSuspectButton.isEnabled = false
        }
    }

    // Inflate the menu view. Make sure that we specify setHasOptionsMenu(true) in onCreate.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime, menu)
    }

    // Called when the user clicks on a menu item.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete_crime -> {
                AlertDialog.Builder(context)
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes") { _, _ ->
                            CrimeLab.remove(crime)
                            activity.finish()
                        }
                        .setNegativeButton("No") { _, _ -> }
                        .show()
                return true // Indicate that no further processing is necessary.
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: _id: ${crime._id}")

        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "Result was not OK")
            return
        }

        // Make sure that we update both in-memory representaion and UI.
        when (requestCode) {
            REQUEST_DATE -> {
                data?.let {
                    crime.date = DatePickerFragment.dateResult(data).time // In-memory
                    updateDateText() // UI
                }
            }
            REQUEST_CONTACT -> {
                data?.let {
                    crime.suspect = getSuspectNameFromContactList(data) // In-memory
                    crimeSuspectButton.text = crime.suspect // UI
                }
            }
            REQUEST_PHOTO -> {
                updatePhotoView()
            }
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume: _id: ${crime._id}")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onResume: _id: ${crime._id}")
        super.onPause()
        CrimeLab.save(crime)
    }

    /**
     * Update the date text based on a crime stored in the CrimeLab.
     */
    private fun updateDateText(): Unit {
        crimeDate.text = df.format(crime.date)
    }

    /**
     * Update the photo view after resizing it to the view size.
     */
    private fun updatePhotoView() {
        Log.d(TAG, "updatePhotoView: ${crimePhoto.width} x ${crimePhoto.height}")

        if (photoFile != null && photoFile!!.exists()) {
            val bitmap: Bitmap? = crimePhoto.getScaledBitmap(photoFile!!.path)
            crimePhoto.setImageBitmap(bitmap)
        } else {
            crimePhoto.setImageDrawable(null)
        }
    }

    /**
     * Start the contact list app.
     */
    private fun startContactListForSuspect(): Unit {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, REQUEST_CONTACT)
    }

    /**
     * Get a contact name from the result intent.
     */
    private fun getSuspectNameFromContactList(result: Intent): String {
        val contactUri = result.getData()

        // Specify which fields we want our query to return.
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        // Perform query like SQL where.
        val cursor: Cursor = activity.contentResolver.query(contactUri, queryFields, null, null, null)

        cursor.use { c ->
            // Pull out the first column of the first row.
            return if (c.count == 0) "" else with(c) { moveToFirst(); getString(0) }
        }
    }

    /**
     * Start an application that can send a report.
     */
    private fun sendCrimeReport(): Unit {
        val intent: Intent = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setSubject(getString(R.string.crime_report_subject))
                .setText(getCrimeReportText())
                .setChooserTitle(getString(R.string.send_report))
                .createChooserIntent()
        startActivity(intent)
    }

    /**
     * Generate a text for a crime report.
     */
    private fun getCrimeReportText(): String {
        val dateString = getString(R.string.crime_report_date, df.format(crime.date))

        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val suspectString = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report,
                crime.title, dateString, solvedString, suspectString).trim()
    }

    private fun startDatePickerForResult(): Unit {
        DatePickerFragment.newInstance(Date(crime.date)).apply {
            setTargetFragment(this, REQUEST_DATE) // Similar to startActivityForResult
        }.show(fm, DIALOG_DATE)
    }

    private fun startCameraForResult(): Unit {
        if (!canTakePhoto) return
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
        }
        startActivityForResult(intent, REQUEST_PHOTO)
    }

    private fun showZoomedInPhoto() {
        Log.d(TAG, "startZoomedInPhoto: $photoFile")

        // If the photo file exists, start the photo fragment.
        photoFile?.let {
            PhotoFragment.newInstance(photoFile!!)
                    .show(activity.supportFragmentManager, DIALOG_PHOTO)
        }
    }

    /**
     * @return true if a contact list app is available on the device.
     */
    private fun isContactListAvailable(): Boolean {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        // The packageManager knows all the components installed on a device and finds an activity
        // that matches the specified intent. Restricts the search to activities with the CATEGORY_DEFAULT.
        return activity.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null
    }

    /**
     * @return true if a user can take a photo on the device.
     */
    private fun isCameraAvailable(): Boolean {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(activity.packageManager) != null
    }
}