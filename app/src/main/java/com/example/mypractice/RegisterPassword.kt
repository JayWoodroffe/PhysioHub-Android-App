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
import com.google.firebase.ktx.Firebase

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

        val email = DataRegistration.email
        val password = DataRegistration.password

        //creates a user in the firebase authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    addDocToFirestore()

                }
                //TODO add proper on fail popup
                else {
                    Toast.makeText(
                        this@RegisterPassword,
                        it.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun addDocToFirestore() {
        //add user to the firestore
        val db = FirebaseFirestore.getInstance()
        //creating the new user
        val user = hashMapOf(
            "certId" to DataRegistration.certID,
            "email" to DataRegistration.email,
            "name" to DataRegistration.name,
            "number" to DataRegistration.phone,
            "practiceID" to DataRegistration.pracID,
        )


        //adding the new document with a generated ID
        db.collection("doctors")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("Tag", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Tag", "Error adding document", e)
            }

        //checking if the data is added
        db.collection("doctors")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userData = document.data
                    val username = userData["name"] as String
                    val email = userData["email"] as String
                    Toast.makeText(
                        this@RegisterPassword,
                        "welcome " + username + "!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            .addOnFailureListener { exception ->
                runOnUiThread {
                    Toast.makeText(
                        this@RegisterPassword,
                        "didnt add right",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        val intent = Intent(this, Login::class.java)
        startActivity(intent)

    }

}