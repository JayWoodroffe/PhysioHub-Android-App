package com.example.mypractice

//for firebase
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.data.AuthServices
import com.example.mypractice.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


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

        binding.btnLogin.setOnClickListener {
            //TODO add user authentication
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email?.isNullOrEmpty() == true || password?.isNullOrEmpty() == true)
            {
                Toast.makeText(this@Login, "Invalid login details, please try again", Toast.LENGTH_SHORT).show()
            }
            else{
                try {

                    AuthServices.login(email, password,
                        onSuccess = {
                            //login successful
                            startActivity(Intent(this, Home::class.java))
                            finish()
                        },
                        onFailure = {
                            Toast.makeText(
                                this@Login,
                                "Invalid login details, please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("LoginActivity", "Login failed")
                        })
                }catch (e: Exception) {
                    // Handle any unexpected exceptions
                    Toast.makeText(
                        this@Login,
                        "An unexpected error occurred, please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("LoginActivity", "Unexpected error during login", e)
                }
            }


        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterContact::class.java))

        }
    }



}
