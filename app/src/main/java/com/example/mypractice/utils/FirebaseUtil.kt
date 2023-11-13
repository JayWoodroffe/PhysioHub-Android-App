package com.example.mypractice.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

//for methods related to firebase that may be used frequently
class FirebaseUtil {
    fun currentDocId(): String {
        return FirebaseAuth.getInstance().uid.toString()
    }
    fun currentDocDetails(): DocumentReference {
        return FirebaseFirestore.getInstance().collection("doctors").document(currentDocId())
    }



    companion object {
        fun allClientCollectionReference(): CollectionReference {
            return FirebaseFirestore.getInstance().collection("users")
        }
    }
}