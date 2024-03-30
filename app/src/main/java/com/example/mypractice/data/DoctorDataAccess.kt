package com.example.mypractice.data

import android.util.Log
import com.example.mypractice.model.DoctorModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

object DoctorDataAccess {

    fun currentDocEmail(callback: (String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        callback(email)
    }

    //function to check if an email is already registered as a doctor
    fun isEmailRegistered(email:String, onSuccess:(Boolean)-> Unit, onFailure: (String) -> Unit)
    {
        val doctorsCollection = FirebaseFirestore.getInstance().collection("doctors")
        doctorsCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents->
                onSuccess(!documents.isEmpty)
            }
            .addOnFailureListener{exception ->
                onFailure(exception.message?: "Failed to query doctors")
            }
    }

    fun isCertificateRegistered(certID:String, onSuccess: (Boolean) -> Unit, onFailure: (String) -> Unit)
    {
        val doctorsCollection = FirebaseFirestore.getInstance().collection("doctors")
        doctorsCollection.whereEqualTo("certId", certID)
            .get()
            .addOnSuccessListener { documents->
                onSuccess(!documents.isEmpty)
            }
            .addOnFailureListener{exception ->
                onFailure(exception.message?: "Failed to query doctors")
            }
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
                        Log.d("DoctorDataAccess", "Doctor retrieved: $doctor")
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

    fun addNewDoctor(newDoctor:DoctorModel, password:String)
    {
        val auth = FirebaseAuth.getInstance()
        val firestore=FirebaseFirestore.getInstance()

        //add the doctors data to firestore
        firestore.collection("doctors")
            .add(newDoctor)
            .addOnSuccessListener { documentReference ->
                //create a new user account for doctor in firebase auth
                newDoctor.email?.let {
                    auth.createUserWithEmailAndPassword(it, password)
                        .addOnSuccessListener { authResult ->
                            //link the new user accounts UID w doctor accounts UID
                            val uid = authResult.user?.uid
                            if(uid != null) {
                                documentReference.update("uid", uid)
                                    .addOnSuccessListener {
                                        Log.d("Tag", "DocumentSnapshot added with ID: $uid")
                                    }
                                    .addOnFailureListener{e->
                                        Log.e("Tag", "Failed to update doctor document with UID", e)
                                    }
                            }
                        }
                }
            }
            .addOnFailureListener{e->
                Log.w("Tag", "Error adding document", e)
            }
    }

    fun getClientsForDoctor(callback: (List<String>?) -> Unit)
    {
        val clientList = mutableListOf<String>()

        val docCertId = DoctorDataHolder.getLoggedInDoctor()?.certId
        if(docCertId != null)
        {
            val clientsCollection = FirebaseFirestore.getInstance().collection("users")
            val query = clientsCollection.whereEqualTo("doctorID", docCertId)

            query.get()
                .addOnSuccessListener { documents->
                    for(document in documents)
                    {
                        val clientName = document.getString("name")
                        clientName?.let{clientList.add(it)}
                    }
                    callback(clientList)
                }
                .addOnFailureListener{exception ->
                    // Handle any errors and invoke the callback with null
                    Log.e("DoctorDataAccess", "Error getting clients for doctor", exception)
                    callback(null)
                }
        }
    }

    fun getClientQuery(certId: String?, searchTerm: String): Query {
        val baseQuery = FirebaseFirestore.getInstance().collection("users")
            .orderBy("searchField")
            .whereEqualTo("doctorID", certId)

        return if (searchTerm.isNotEmpty()) {
            val lowerCaseSearchTerm = searchTerm.toLowerCase(Locale.getDefault())
            baseQuery
                .whereGreaterThanOrEqualTo("searchField", lowerCaseSearchTerm)
                .whereLessThanOrEqualTo("searchField", lowerCaseSearchTerm + "\uF8FF")
        } else {
            baseQuery
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