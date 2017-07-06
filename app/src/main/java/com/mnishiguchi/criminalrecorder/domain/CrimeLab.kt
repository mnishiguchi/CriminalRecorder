package com.mnishiguchi.criminalrecorder.domain

import android.os.Environment
import com.mnishiguchi.criminalrecorder.data.CrimeDao
import com.mnishiguchi.criminalrecorder.ui.App
import java.io.File
import java.util.*

/**
 * A singleton central repository.
 */
object CrimeLab {
    private val dao: CrimeDao = CrimeDao()

    // In-memory storage for the crime data.
    private var inMemoryCrimes = mutableListOf<Crime>()

    init {
        reload()
    }

    val size: Int
        get() = inMemoryCrimes.size

    operator fun get(position: Int) = inMemoryCrimes[position]

    /**
     * Sync the in-memory store with database.
     */
    fun reload(): Unit {
        inMemoryCrimes = dao.all() as MutableList<Crime>
    }

    /* Read-only operations
       - Read from in-memory store, assuming that it is always up-to-date.
     */

    /**
     * Get all the crimes stored in the in-memory store.
     */
    fun crimes(): MutableList<Crime> = inMemoryCrimes

    /**
     * Find a crime by uuid.
     */
    fun crime(uuid: UUID): Crime? = inMemoryCrimes.find { it.uuid == uuid }

    /* Write operations
       - Whenever we make a change to database, we must update the in-memory store.
     */

    /**
     * Create a new blank crime.
     */
    fun new(): Crime = dao.newRecord().apply { inMemoryCrimes.add(this) }

    /**
     * Save the changes of an existing record.
     */
    fun save(crime: Crime): Unit {
        inMemoryCrimes.forEachIndexed { index, it ->
            if (it._id == crime._id) {
                inMemoryCrimes[index] = crime
            }
        }
        dao.update(crime)
    }

    /**
     * Remove a crime.
     */
    fun remove(crime: Crime): Unit {
        inMemoryCrimes.remove(crime)
        dao.delete(crime)
    }

    /* Others */

    /**
     * Find the right location for a photo file (a subdirectory of the primary location root).
     * @return A file object that points to the file location if it exists, else null.
     */
    fun photoFile(crime: Crime): File? {
        val externalFilesDir = App.instance.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return externalFilesDir?.let { File(externalFilesDir, crime.photoFileName()) }
    }
}