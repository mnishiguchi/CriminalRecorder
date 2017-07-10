package com.mnishiguchi.criminalrecorder.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface CrimeDao {

    /* Read operations */

    @Query("SELECT * FROM crimes")
    fun all(): LiveData<List<Crime>>

    @Query("SELECT * FROM crimes WHERE id = :arg0")
    fun find(id: Int): LiveData<List<Crime>>

    @Query("SELECT COUNT(*) FROM crimes")
    fun count(): Long

    /* Write operations */

    @Insert(onConflict = REPLACE)
    fun insert(crime: Crime): Long // Id

    @Delete
    fun delete(crime: Crime): Int // Number of deleted rows

    @Query("DELETE FROM crimes WHERE id = :arg0")
    fun delete(id: Int): Int // Number of deleted rows
}
