package com.mnishiguchi.criminalrecorder.domain

import java.util.*

/**
 * Represent a crime in the problem domain.
 */
data class Crime(
        val _id: Long = -1, // -1: not saved to db
        val uuid: UUID = UUID.randomUUID(),
        var title: String = "",
        var date: Long = Date().time,
        var isSolved: Boolean = false)
