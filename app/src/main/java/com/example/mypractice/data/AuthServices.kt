package com.example.mypractice.data

import android.util.Log
import com.example.mypractice.model.DoctorModel
import com.google.firebase.auth.FirebaseAuth

object AuthServices {
    fun login(email:String, password: String, onSuccess:() -> Unit, onFailure:(String) -> Unit)
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            //successful login -> retrieve all doctors details and add them to the DoctorDataHolder
            .addOnSuccessListener { authResult->

                //if log in is successful, return users's email
                val userEmail = authResult.user?.email
                if(userEmail != null)
                {
                    DoctorDataAccess.getDoctorByEmail(
                        email,
                        onSuccess = { doctor: DoctorModel ->
                            // Store the doctor object
                            Log.d("logIn", "here")
                            DoctorDataHolder.setLoggedInDoctor(doctor)
                            onSuccess()
                        },
                        onFailure = { error ->
                            onFailure(error)
                        }
                    )
                }
                else{
                    onFailure("User email not available")
                }
            }
            .addOnFailureListener{e->
                //handle log in failure
                onFailure(e.message?:"Unknown error")
            }
    }

}