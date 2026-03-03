package com.adrianhelo.journalapp.data

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class JournalModel(
    var title: String = "",
    var username: String = "",
    var userId: String = "",
    var imageUrl: String = "",
    var thoughts: String = "",
    var timeAdded: Timestamp = Timestamp.now()
){
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(timeAdded.toDate())
    }
}


