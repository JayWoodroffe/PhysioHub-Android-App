package com.example.mypractice.data

import android.util.Log
import com.example.mypractice.model.ClientModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

object  ClientDataAccess {

    fun getClientByID (clientID: String, callback: (ClientModel?) -> Unit)
    {
        val clientReference= FirebaseFirestore.getInstance().collection("users")
            .document(clientID)

        clientReference.get()
            .addOnSuccessListener { document ->
                if(document != null && document.exists()){
                    val client = document.toObject(ClientModel::class.java)
                    if (client != null) {
                        callback(client)
                    }
                }
                else{
                    callback(null)
                }
            }
            .addOnFailureListener{exception ->
                Log.e("ClientDataAccess", "Error getting client with ID: $clientID", exception)
                callback(null)
            }
    }

    fun addClient(client: ClientModel, callback: (String?) -> Unit)
    {
        FirebaseFirestore.getInstance().collection("users")
            .add(client)
            .addOnSuccessListener { documentReference->
                callback(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.e("ClientDataAccess", "Error adding client", e)
                // Return null to indicate failure via callback
                callback(null)
            }
    }

    fun addInviteCode(code:String, docID: String)
    {
        val inviteReference= FirebaseFirestore.getInstance().collection("invitations")

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 3)
        val expirationTime = calendar.time


        val invitationData = hashMapOf(
            "code" to code,
            "doctorID" to docID,
            "expirationTime" to expirationTime
        )

        inviteReference
            .add(invitationData)
            .addOnSuccessListener { documentReference ->
                Log.d("invitation", "Invitation code stored successfully")
            }
            .addOnFailureListener { e ->
                Log.e("invitation", "Error storing invitation code: ${e.message}")
            }
    }
}