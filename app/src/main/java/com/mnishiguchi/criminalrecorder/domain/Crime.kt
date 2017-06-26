package com.mnishiguchi.criminalrecorder.domain

import java.util.*

data class Crime(val id: UUID = UUID.randomUUID(), var title: String)
