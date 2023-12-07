package com.example.mypractice

//for firebase
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{
                        if (it.isSuccessful)
                        {
                            startActivity(Intent(this, Home::class.java))
                        }
                        else
                        {
                            Toast.makeText(this@Login, "Invalid login details, please try again", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterContact::class.java))

        }
    }



}
