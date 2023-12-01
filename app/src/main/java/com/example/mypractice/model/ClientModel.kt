package com.example.mypractice.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class ClientModel (
    @DocumentId
    var clientId: String? = null,
    var email: String,
    var name: String,
    var number: String?,
    var docID: Int?,
    var dob: Date?
) {
    constructor() : this("", "", "", "", null, null) {}
}
