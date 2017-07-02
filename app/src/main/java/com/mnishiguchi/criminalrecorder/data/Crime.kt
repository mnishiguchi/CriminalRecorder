package com.mnishiguchi.criminalrecorder.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by masa on 7/2/17.
 */
@Entity(tableName = "crimes")
data class Crime(
        @ColumnInfo(name = "uuid") @PrimaryKey
        var uuid: UUID = UUID.randomUUID(),

        @ColumnInfo(name = "title")
        var title: String = "",

        @ColumnInfo(name = "date")
        var date: Date = Date(),

        @ColumnInfo(name = "is_solved")
        var isSolved: Boolean = false
)