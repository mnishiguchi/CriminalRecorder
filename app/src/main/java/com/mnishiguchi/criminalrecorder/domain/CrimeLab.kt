package com.mnishiguchi.criminalrecorder.domain

import android.content.Context
import java.util.*

/**
 * A singleton central data storage that stores the list of crimes.
 */
class CrimeLab(val crimes: MutableList<Crime> = mutableListOf<Crime>()) {
    lateinit private var context: Context

    init {
        // Populate the list with fake items.
        for (i in 0..99) {
            val crime = Crime(title = "Crime #${i + 1}", isSolved = i % 2 == 0)
            crimes.add(crime)
        }
    }

    companion object {
        // Store the singleton instance.
        private val instance = CrimeLab()

        // Getter for the singleton instance.
        fun get(context: Context): CrimeLab {
            instance.context = context.applicationContext
            return instance
        }
    }

    fun crime(id: UUID) : Crime? = crimes.find { it.id == id }
}