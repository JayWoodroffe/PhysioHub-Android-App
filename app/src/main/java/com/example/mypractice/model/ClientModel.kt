package com.example.mypractice.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class ClientModel (
    @DocumentId
    var clientId: String? = null,
    var email: String,
    var name: String,
    var number: String?,
    var doctorID: String?,
    var dob: Date?,
    var searchField: String?
) {
    constructor() : this("", "", "", "", "", null, "") {}
}
