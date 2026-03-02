package com.adrianhelo.journalapp.data

import com.google.firebase.Timestamp

data class JournalModel(
    var title: String = "",
    var username: String = "",
    var userId: String = "",
    var imageUrl: String = "",
    var thoughts: String = "",
    var timeAdded: Timestamp = Timestamp.now()
) {
    constructor() : this("", "", "", "", "", Timestamp.now())
}
