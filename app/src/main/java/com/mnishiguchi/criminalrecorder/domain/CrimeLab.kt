package com.mnishiguchi.criminalrecorder.domain

import android.content.Context
import com.mnishiguchi.criminalrecorder.data.AppDatabase
import com.mnishiguchi.criminalrecorder.data.CrimeDataMapper
import java.util.*

/**
 * A singleton central data storage that stores the list of crimes.
 */
class CrimeLab(context: Context) {
    private val database: AppDatabase = AppDatabase.createPersistentDatabase(context.applicationContext)

    companion object {
        // Store the singleton instance.
        private var instance: CrimeLab? = null

        // Getter for the singleton instance.
        fun get(context: Context): CrimeLab {
            // Instantiate only once and store the ref.
            if (instance == null) {
                instance = CrimeLab(context)
            }

            return instance!!
        }
    }

    fun crimes() : List<Crime> {
        return database.crimeDao().all().map {
            CrimeDataMapper.toDomain(it)
        }
    }

    /**
     * Find a crime by uuid.
     */
    fun crime(uuid: UUID): Crime? {
        with(database.crimeDao().findByUUID(uuid)) {
            return if (isEmpty()) null else CrimeDataMapper.toDomain(first())
        }
    }

    /**
     * Add a crime to CrimeLab.
     */
    fun add(crime: Crime): Unit {
        database.crimeDao().insert(CrimeDataMapper.fromDomain(crime))
    }

    /**
     * Create a new blank crime in CrimeLab.
     */
    fun newCrime(): Crime {
        val crime = Crime()
        add(crime)
        return crime
    }

    /**
     * Remove a crime from CrimeLab.
     */
    fun remove(crime: Crime): Unit {
        database.crimeDao().delete(CrimeDataMapper.fromDomain(crime))
    }
}