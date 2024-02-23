package com.example.mypractice.data

import com.example.mypractice.model.Appointment
import com.example.mypractice.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppointmentsDataAccess {
    fun getNextAppointment(certId: String,  callback: (Appointment?) -> Unit) {
        val currentTimeStamp: Timestamp = Timestamp.now()
        val appointmentsCollectionRef = FirebaseUtil.allAppointmentsCollectionReference()

        val query = appointmentsCollectionRef
            .whereEqualTo("doctorCertId", certId)
            .whereGreaterThanOrEqualTo("date", currentTimeStamp)
            .orderBy("date", Query.Direction.ASCENDING)
            .limit(1) // Limit the result to the first document

        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Parse and process the first appointment document
                    val time = document.getTimestamp("date")?.toDate()
                    val clientName = document.getString("clientName")

                    // Create Appointment object and assign it to nextAppointment
                    val nextAppointment = Appointment(clientName ?: "", time ?: Date())
                    callback(nextAppointment)
                    break
                }
                if (documents.size() == 0)
                {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null)
            }
    }
}