package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mypractice.databinding.ActivityRegisterPasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPassword : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding.imBack.setOnClickListener{
            onBackPressed()
        }

        binding.btnFinish.setOnClickListener{
            var p1 = binding.etPassword1.text.toString()
            var p2 = binding.etPassword2.text.toString()

            //check if the password matches the requirements
            try {
                DataRegistration.password = p1

                if (p1 != p2){
                    binding.etPassword2.error = "Passwords don't match"
                }
                else{ //all the data given has been verified - now create the user in the firebase db

                    createUserDb()
                }
            }
            catch (e: IllegalArgumentException)
            {
                binding.etPassword1.error = "${e.message}"
            }


        }


    }

    private fun createUserDb () {
        runOnUiThread{
            Toast.makeText(
                this@RegisterPassword,
                "here",
                Toast.LENGTH_SHORT
            ).show()
        }

        val email = DataRegistration.email
        val password = DataRegistration.password

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (it.isSuccessful)
                {
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(this@RegisterPassword, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
//        val registrationThread = Thread {//starting a worker thread
//            val auth = FirebaseAuth.getInstance()
//
//
//            auth.createUserWithEmailAndPassword(DataRegistration.email, DataRegistration.password)
//                .addOnCompleteListener(this@RegisterPassword, OnCompleteListener { task ->
//                    if (task.isSuccessful) {
//
//                        //Successful registration
//                        val user = auth.currentUser
//                        if (user != null) {
//                            addUserDataDb(user) {success ->
//                                if (success){
//                                    //user successfully registered
//                                }
//                                else{
//                                    //TODO handle firestore error
//                                }
//                            }
//                        }
//
//                        runOnUiThread {
//                            //TODO change to the next activity - successful sign up-> return to log in
//                            Toast.makeText(
//                                this@RegisterPassword,
//                                "Registration successful",
//                                Toast.LENGTH_SHORT
//                            ).show()
//
//                        }
//
//                    } else {
//                        //failed to register
//
//                        val exception = task.exception
//                        runOnUiThread {
//                            // Update UI with error message
//                            Toast.makeText(
//                                this@RegisterPassword,
//                                "Registration failed: " + exception?.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            Log.e("YourTag", "An error occurred: ${exception?.message}", exception)
//                        }
//                    }
//                })
//        }
//
//        registrationThread.start()
    }
    private fun addUserDataDb(currentUser: FirebaseUser, callback: (Boolean) -> Unit){

        //val uID = currentUser.uid
        val firestore = FirebaseFirestore.getInstance()
        val userDocument =
            firestore.collection("doctors").document(currentUser?.uid ?: "")

        val userData = hashMapOf(
            "certID" to DataRegistration.certID,
            "email" to DataRegistration.email,
            "name" to DataRegistration.name,
            "number" to DataRegistration.phone,
            "practiceID" to DataRegistration.pracID

        )
        userDocument.set(userData)
            .addOnSuccessListener {
                //user profile added to firestore
                callback(true)
            }
            .addOnFailureListener{
                callback (false)
            }

    }
}