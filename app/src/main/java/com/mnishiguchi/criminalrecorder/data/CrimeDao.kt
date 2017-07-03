package com.mnishiguchi.criminalrecorder.data

import android.util.Log
import com.mnishiguchi.criminalrecorder.domain.Crime
import com.mnishiguchi.criminalrecorder.utils.byId
import com.mnishiguchi.criminalrecorder.utils.parseListWithMap
import com.mnishiguchi.criminalrecorder.utils.parseOptWithMap
import com.mnishiguchi.criminalrecorder.utils.toVarargArray
import org.jetbrains.anko.db.*
import java.util.*

/**
 * https://github.com/Kotlin/anko/wiki/Anko-SQLite
 * https://github.com/Kotlin/anko/blob/master/anko/library/static/sqlite/src/Database.kt
 */
class CrimeDao(val database: AppDatabase = AppDatabase,
               val dataMapper: CrimeDataMapper = CrimeDataMapper) {

    private val TAG = javaClass.simpleName
    private val t = AppDatabase.CrimeTable

    /* Read-only operations */

    fun all(): List<Crime> {
        val result = database.use {
            select(t.TABLE_NAME)
                    .parseListWithMap {
                        val data = CrimeEntity(HashMap(it))
                        dataMapper.toDomain(data)
                    }
        }
        Log.d(TAG, "all: $result")
        return result
    }

    fun find(id: Int): Crime? {
        val result = database.use {
            select(t.TABLE_NAME).byId(id)
                    .parseOptWithMap {
                        val data = CrimeEntity(HashMap(it))
                        dataMapper.toDomain(data)
                    }
        }
        Log.d(TAG, "all: _id: $result")
        return result
    }

    fun findByUUID(uuid: UUID): Crime? {
        val result = database.use {
            select(t.TABLE_NAME)
                    .whereArgs("${t.UUID} = {uuid}", "uuid" to uuid)
                    .parseOptWithMap {
                        val data = CrimeEntity(HashMap(it))
                        dataMapper.toDomain(data)
                    }
        }
        Log.d(TAG, "findByUUID: $result")
        return result
    }

    fun last(): Crime {
        val result = database.use {
            select(t.TABLE_NAME)
                    .orderBy(t.ID, SqlOrderDirection.DESC)
                    .limit(1)
                    .parseOptWithMap {
                        val data = CrimeEntity(HashMap(it))
                        dataMapper.toDomain(data)
                    }
        }
        Log.d(TAG, "last: $result")
        return result!!
    }

    /* Write operations */

    fun newRecord(): Crime {
        insert(Crime())
        return last()
    }

    fun insert(crime: Crime): Long {
        val vararg = dataMapper.fromDomain(crime).map
                .apply { remove("_id") } // Ignore _id because duplicate id is not allowed.
                .toVarargArray()
        val result = database.use {
            insert(t.TABLE_NAME, *vararg)
        }
        Log.d(TAG, "insert: _id: ${crime._id}, result: $result")
        return result
    }

    fun update(crime: Crime): Boolean {
        val vararg = dataMapper.fromDomain(crime).map
                .apply { remove("_id") } // Ignore _id because duplicate id is not allowed.
                .toVarargArray()
        val result = database.use {
            update(t.TABLE_NAME, *vararg)
                    .whereArgs("_id = {_id}", "_id" to crime._id)
                    .exec()
        } == 1
        Log.d(TAG, "update: _id: ${crime._id}, result: $result")
        return result
    }

    fun delete(crime: Crime): Boolean {
        val result = database.use {
            delete(t.TABLE_NAME, "_id = {_id}", "_id" to crime._id)
        } == 1
        Log.d(TAG, "delete: _id: ${crime._id}, result: $result")
        return result
    }
}