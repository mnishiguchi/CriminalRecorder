package com.mnishiguchi.criminalrecorder.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import java.util.*


/**
 * Created by masa on 7/2/17.
 */
@Dao
interface CrimeDao {
    @Query("SELECT * FROM crimes")
    fun all(): List<Crime>

    @Query("SELECT * FROM crimes WHERE uuid = :arg0")
    fun findByUUID(uuid: UUID): List<Crime>

    @Insert
    fun insert(crime: Crime): Unit

    @Delete
    fun delete(crime: Crime): Unit
}