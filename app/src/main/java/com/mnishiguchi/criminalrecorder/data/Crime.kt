package com.mnishiguchi.criminalrecorder.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "crimes")
data class Crime(
        // https://developer.android.com/reference/android/arch/persistence/room/PrimaryKey.html#autoGenerate()
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0, // Insert methods treat 0 as not-set while inserting the item.

        @ColumnInfo(name = "title")
        var title: String = "",

        @ColumnInfo(name = "date")
        var date: Long = Date().time,

        @ColumnInfo(name = "is_solved")
        var isSolved: Boolean = false,

        @ColumnInfo(name = "suspect")
        var suspect: String = ""
) {
    /**
     * A unique file name for this crime.
     */
    fun photoFileName(): String = "IMG_$id.jpg"
}
