package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.mypractice.databinding.ActivityMainBinding
//for firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.messaging.FirebaseMessaging


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialising firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        

            //initialising firebase sdk
            FirebaseApp.initializeApp(this)
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val messaging = FirebaseMessaging.getInstance()

            binding.btnLogin.setOnClickListener {
                //TODO add user authentication
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        if (it.isSuccessful)
                        {
                            startActivity(Intent(this, Home::class.java))
                        }
                        else
                        {
                            Toast.makeText(this@Login, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

            }

            binding.tvRegister.setOnClickListener {
                startActivity(Intent(this, RegisterContact::class.java))

            }
        }



    }
