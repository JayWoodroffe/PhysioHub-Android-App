package com.example.mypractice.utils

import android.util.Log
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

        fun allAppointmentsCollectionReference(): CollectionReference{
            return FirebaseFirestore.getInstance().collection("appointments")
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

        fun addClientToDb(clientData: HashMap<String, Comparable<*>?>){
                //add client to the firestore
                val db = FirebaseFirestore.getInstance()

                //adding the new document with a generated ID
                db.collection("users")
                    .add(clientData)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Tag", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Tag", "Error adding document", e)
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