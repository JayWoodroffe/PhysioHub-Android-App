package com.example.mypractice.model

import java.util.Date

data class ClientModel (
    var email: String,
    var name: String,
    var number: String,
    var docID: Int?,
    var dob: Date?
) {
    constructor() : this("", "", "", null, null) {}
}
