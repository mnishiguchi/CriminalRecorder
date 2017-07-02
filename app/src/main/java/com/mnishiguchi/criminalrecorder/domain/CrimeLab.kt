package com.mnishiguchi.criminalrecorder.domain

import android.content.Context
import java.util.*

/**
 * A singleton central data storage that stores the list of crimes.
 */
class CrimeLab(val crimes: MutableList<Crime> = mutableListOf<Crime>()) {
    lateinit private var context: Context

//    init {
//        // Populate the list with fake items.
//        for (i in 0..99) {
//            val crime = Crime(title = "Crime #${i + 1}", isSolved = i % 2 == 0)
//            crimes.add(crime)
//        }
//    }

    companion object {
        // Store the singleton instance.
        private val instance = CrimeLab()

        // Getter for the singleton instance.
        fun get(context: Context): CrimeLab {
            instance.context = context.applicationContext
            return instance
        }
    }

    /**
     * Find a crime by id.
     */
    fun crime(id: UUID): Crime? = crimes.find { it.id == id }

    /**
     * Add a crime to CrimeLab.
     */
    fun add(crime: Crime): Crime {
        crimes.add(crime)
        return crime
    }

    /**
     * Create a new blank crime in CrimeLab.
     */
    fun newCrime(): Crime = add(Crime())

    /**
     * Remove a crime from CrimeLab.
     */
    fun remove(crime: Crime): Unit {
        crimes.remove(crime)
    }

}