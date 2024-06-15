package com.example.mypractice.data

import android.util.Log
import com.example.mypractice.model.Appointment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object AppointmentsDataAccess {
    private val db = FirebaseFirestore.getInstance()
    fun getNextAppointment(certId: String,  callback: (Appointment?) -> Unit) {
        val currentTimeStamp: Timestamp = Timestamp.now()
        val appointmentsCollectionRef = db.collection("appointments")

        val query = appointmentsCollectionRef
            .whereEqualTo("doctorCertId", certId)
            .whereGreaterThanOrEqualTo("date", currentTimeStamp)
            .orderBy("date", Query.Direction.ASCENDING)
            .limit(1) // Limit the result to the first document

        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Parse and process the first appointment document
                    val id = document.id
                    val time = document.getTimestamp("date")?.toDate()
                    val clientName = document.getString("clientName")
                    val doctorCertId = document.getString("doctorCertId") // Retrieve doctorCertId

                    if (time != null && clientName != null && doctorCertId != null) {
                        val nextAppointment = Appointment(id, clientName, time, doctorCertId)
                        callback(nextAppointment)
                    } else {
                        callback(null)
                    }
                    break
                }
                if (documents.isEmpty()) {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null)
            }
    }

    fun getAppointments( currentDay: Timestamp,
                         nextDay: Timestamp,
                         callback: (List<Appointment>) -> Unit)
    {
        val appointmentsList = mutableListOf<Appointment>()
        val docCertId = DoctorDataHolder.getLoggedInDoctor()?.certId
        if(docCertId == null){
            callback(emptyList())
            return
        }
        val appointmentsCollection = FirebaseFirestore.getInstance().collection("appointments")
        val query = appointmentsCollection
            .whereEqualTo("doctorCertId", docCertId)
            .whereGreaterThanOrEqualTo("date", currentDay)
            .whereLessThan("date", nextDay)
            .orderBy("date", Query.Direction.ASCENDING)

        query.get()//execute the query
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val time = document.getTimestamp("date")?.toDate()
                    val clientName = document.getString("clientName")
                    val doctorCertId = document.getString("doctorCertId") // Retrieve doctorCertId

                    if (time != null && clientName != null && doctorCertId != null) {
                        val appointment = Appointment(id, clientName, time, doctorCertId)
                        appointmentsList.add(appointment)
                    }
                }
                callback(appointmentsList)
            }
            .addOnFailureListener { exception ->
                Log.e("Appointments", "Failed to query appointments", exception)
                callback(emptyList()) // Notify the callback with an empty list in case of failure
            }
    }

    fun addAppointment(selectedClient: String, timestamp: Timestamp, callback: (Boolean) -> Unit)
    {
        val docCertId = DoctorDataHolder.getLoggedInDoctor()?.certId
        if (docCertId != null) {
            val db = FirebaseFirestore.getInstance()
            // Create a new appointment object
            val appointment = hashMapOf(
                "clientName" to selectedClient,
                "date" to timestamp,
                "doctorCertId" to docCertId
            )
            // Add the appointment to the "appointments" collection
            db.collection("appointments")
                .add(appointment)
                .addOnSuccessListener { documentReference ->
                    // Appointment added successfully
                    callback(true)
                }
                .addOnFailureListener { e ->
                    // Error adding appointment
                    callback(false)
                }
        } else {
            callback(false)
        }
    }
}