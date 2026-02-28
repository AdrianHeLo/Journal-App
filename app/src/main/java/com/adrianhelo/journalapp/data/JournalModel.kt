package com.adrianhelo.journalapp.data

import com.google.firebase.Timestamp

data class JournalModel(
    val title: String,
    val username: String,
    val userId: String,
    val imageUrl: Int,
    val thoughts: String,
    val timeAdded: Timestamp)
