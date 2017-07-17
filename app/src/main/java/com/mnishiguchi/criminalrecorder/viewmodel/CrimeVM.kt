package com.mnishiguchi.criminalrecorder.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Environment
import com.mnishiguchi.criminalrecorder.data.Crime
import com.mnishiguchi.criminalrecorder.data.CrimeDao
import com.mnishiguchi.criminalrecorder.ui.App
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

/**
 * A ViewModel for Crime-related data.
 * https://developer.android.com/topic/libraries/architecture/viewmodel.html
 */
class CrimeVM(val dao: CrimeDao = App.database.crimeDao()) : ViewModel() {

    /* ==> Data store */

    val crimes: LiveData<List<Crime>> = dao.all()
    private val selectedCrimeId = MutableLiveData<Int>()

    /* ==> Selected crime */

    fun selectCrime(id: Int) {
        selectedCrimeId.value = id
    }

    fun getSelectedCrime(): LiveData<Int> = selectedCrimeId

    /* ==> Read operations */

    // If a crime does not exist in the data store, return a non-persisted blank crime.
    fun crimeById(id: Int): Crime = crimes.value?.find { it.id == id } ?: Crime()

    fun indexById(crimeId: Int): Int {
        return crimes.value?.indexOfFirst { it.id == crimeId } ?: -1
    }

    fun size(): Int = crimes.value?.size ?: 0

    fun isEmpty(): Boolean = size() == 0

    /* ==> Write operations */

    /**
     * Create a create blank crime.
     */
    fun create(): Crime = Crime().apply {
        val crime = this
        doAsync {
            dao.insert(crime)
        }
    }

    fun create(onCreate: (id: Int) -> Unit): Crime = Crime().apply {
        val crime = this
        doAsync {
            val id: Long = dao.insert(crime)
            uiThread { onCreate(id.toInt()) }
        }
    }

    /**
     * Save the changes of an existing record.
     */
    fun update(crime: Crime): Unit {
        doAsync {
            dao.insert(crime)
        }
    }

    /**
     * Remove a crime.
     */
    fun destroy(crime: Crime): Unit {
        doAsync {
            crimes.value?.let { dao.delete(crime) }
        }
    }

    fun destroy(crime: Crime, onDestroy: (Int) -> Unit): Unit {
        doAsync {
            val count = dao.delete(crime)
            uiThread { onDestroy(count) }
        }
    }

    fun destroy(id: Int): Unit {
        doAsync {
            dao.delete(id)
        }
    }

    fun destroy(id: Int, onDestroy: (Int) -> Unit): Unit {
        doAsync {
            val count = dao.delete(id)
            uiThread { onDestroy(count) }
        }
    }

    /* ==> Others */

    /**
     * Find the right location for a photo file (a subdirectory of the primary location root).
     * @return A file object that points to the file location if it exists, else null.
     */
    fun photoFile(crime: Crime): File? {
        val externalFilesDir = App.instance.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return externalFilesDir?.let { File(externalFilesDir, crime.photoFileName()) }
    }

    fun photoFileById(id: Int): File? = photoFile(crimeById(id))
}