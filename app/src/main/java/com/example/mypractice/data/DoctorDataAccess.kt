package com.example.mypractice.data

import com.example.mypractice.model.DoctorModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object DoctorDataAccess {
    private val firestore = FirebaseFirestore.getInstance()

    fun currentDocEmail(callback: (String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        callback(email)
    }

    fun getDoctorByEmail(email: String, onSuccess:(DoctorModel) ->Unit, onFailure:(String) -> Unit)
    {
        val doctorsCollection = FirebaseFirestore.getInstance().collection("doctors")
        doctorsCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doctorDocument = querySnapshot.documents[0]
                    val doctor = doctorDocument.toObject((DoctorModel::class.java))
                    if (doctor != null) {
                        onSuccess(doctor)
                    } else {
                        onFailure("Failed to parse doctor data")
                    }
                }
                else{
                    onFailure("No doctor found with email $email")
                }
            }
            .addOnFailureListener{ e->
                onFailure(e.message ?: "Unknown error")
            }



    }
    fun getDoctorCertIdByEmail(callback: (String?) -> Unit) {
        currentDocEmail { doctorEmail ->
            if (doctorEmail != null) {
                val doctorsCollection = FirebaseFirestore.getInstance().collection("doctors")

                doctorsCollection
                    .whereEqualTo("email", doctorEmail)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val doctorDocument = querySnapshot.documents[0]
                            val certId = doctorDocument.getString("certId")
                            callback(certId)
                        } else {
                            // No doctor found with the provided email
                            callback(null)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        callback(null)
                    }
            } else {
                // Failed to get the current user's email
                callback(null)
            }
        }
    }

    fun getDoctorNameByEmail(callback: (String?) -> Unit) {
        currentDocEmail { doctorEmail ->
            if (doctorEmail != null) {
                val doctorsCollection = FirebaseFirestore.getInstance().collection("doctors")

                doctorsCollection
                    .whereEqualTo("email", doctorEmail)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val doctorDocument = querySnapshot.documents[0]
                            val name = doctorDocument.getString("name")
                            callback(name)
                        } else {
                            // No doctor found with the provided email
                            callback(null)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        callback(null)
                    }
            } else {
                // Failed to get the current user's email
                callback(null)
            }
        }
    }

}