package com.mnishiguchi.criminalrecorder.domain

import android.content.Context
import java.util.*

/**
 * A singleton central data storage that stores the list of crimes.
 */
class CrimeLab(val context: Context) {

    companion object {
        private lateinit var instance: CrimeLab

        // Returns a singleton instance for the context.
        fun get(context: Context) : CrimeLab {
            if (instance == null ) {
                instance = CrimeLab(context)
                createFakeCrimes()
            }
            return instance
        }

        private fun createFakeCrimes() {
            for (i in 0..99) {
                instance.crimes.add(Crime(title = "Crime #" + i, isSolved = i % 2 == 0))
            }
        }
    }

    // Initially an empty list.
    var crimes = mutableListOf<Crime>()
        private set

    fun crime(id: UUID) : Crime? {
        crimes?.forEach {
            if (it.id == id) {
                return it
            }
        }
        return null
    }
}