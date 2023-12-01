package com.example.mypractice.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

//for methods related to firebase that may be used frequently
class FirebaseUtil {

    fun currentDocDetails(): DocumentReference {
        return FirebaseFirestore.getInstance().collection("doctors").document(currentDocId())
    }



    companion object {
        val firestore: FirebaseFirestore
            get() = FirebaseFirestore.getInstance()
        fun allClientCollectionReference(): CollectionReference {
            return FirebaseFirestore.getInstance().collection("users")
        }

        fun currentDocId(): String {
            return FirebaseAuth.getInstance().toString()
        }


        fun currentDocEmail(callback: (String?) -> Unit) {
            val user = FirebaseAuth.getInstance().currentUser
            val email = user?.email
            callback(email)
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
    }
}