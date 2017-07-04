package com.mnishiguchi.criminalrecorder.data

import android.database.sqlite.SQLiteDatabase
import com.mnishiguchi.criminalrecorder.ui.App
import org.jetbrains.anko.db.*

/**
 * A db helper singleton that handles all the low-level database operations for us.
 *
 * Usage:
 *     val database: DbHelper = AppDatabase
 *     val result = database.use {
 *         val queriedObject = ...
 *         queriedObject
 *     }
 */
object AppDatabase : ManagedSQLiteOpenHelper(App.instance, "app_database.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // android.database.sqlite.SQLiteException: near “org”: syntax error (code 1)
        // https://stackoverflow.com/q/44491674/3837223
        db.createTable(CrimeTable.TABLE_NAME, true,
                CrimeTable.ID to SqlType.create("INTEGER PRIMARY KEY AUTOINCREMENT"),
                CrimeTable.UUID to TEXT + UNIQUE,
                CrimeTable.TITLE to TEXT,
                CrimeTable.DATE to INTEGER,
                CrimeTable.IS_SOLVED to INTEGER,
                CrimeTable.SUSPECT to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(CrimeTable.TABLE_NAME, true)
        onCreate(db)
    }

    // All the column names must match the entity's property names so that we can take advantage of
    // the map delegate in converting between database records and data objects.

    object CrimeTable {
        val TABLE_NAME = "crimes"
        val ID = "_id"
        val UUID = "uuid"
        val TITLE = "title"
        val DATE = "date"
        val IS_SOLVED = "isSolved"
        val SUSPECT = "suspect"
    }
}