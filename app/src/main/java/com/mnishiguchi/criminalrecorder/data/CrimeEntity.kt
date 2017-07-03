package com.mnishiguchi.criminalrecorder.data

import java.util.*

/**
 * Represent a crime as a database entity.
 *
 * The two constructors work as converters between data objects and SQLite records in both directions.
 * The map delegation is used to map the fields to the database and vice versa.
 *
 * The property names must be the same as the column names in the database.
 *
 * The default constructor
 *   + For retrieving a record from database.
 *   + The map in the first arg must be filled with the values of the properties.
 *
 * The second constructor
 *   + For saving a record to database.
 *   + An empty map will be filled with passed-in properties automatically by delegation.
 */
class CrimeEntity(val map: MutableMap<String, Any?>) {
    // For converting database to domain
    var _id: Long by map
    var uuid: String by map
    var title: String by map
    var date: Long by map
    var isSolved: Int by map

    // For converting domain to database
    constructor(_id: Long, uuid: String, title: String, date: Long, isSolved: Int) : this(HashMap()) {
        this._id = _id
        this.uuid = uuid
        this.title = title
        this.date = date
        this.isSolved = isSolved
    }
}