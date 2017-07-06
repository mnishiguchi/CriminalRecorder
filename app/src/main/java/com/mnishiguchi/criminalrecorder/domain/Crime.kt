package com.mnishiguchi.criminalrecorder.domain

import java.util.*

/**
 * Represent a crime in the problem domain.
 *
 * Make sure all the properties are given a default value.
 */
data class Crime(
        val _id: Long = -1, // -1: not saved to db
        val uuid: UUID = UUID.randomUUID(),
        var title: String = "",
        var date: Long = Date().time,
        var isSolved: Boolean = false,
        var suspect: String = ""
) {
    /**
     * A unique file name for this crime.
     */
    fun photoFileName(): String = "IMG_$uuid.jpg"
}
