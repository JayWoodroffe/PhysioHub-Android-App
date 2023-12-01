package com.example.mypractice.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Appointment(
    @DocumentId
    var clientName: String,
    var time: Date
)