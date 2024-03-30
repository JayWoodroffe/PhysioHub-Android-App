package com.example.mypractice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.data.DoctorDataAccess
import com.example.mypractice.databinding.ActivityRegisterPasswordBinding
import com.example.mypractice.model.DoctorModel
import com.google.firebase.auth.FirebaseAuth
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
            if(validPassword(p1) && passwordMatch(p1, p2))
            {
                addDoctorToDatabase(p1)
                startActivity(Intent(this, Login::class.java))
            }
        }
    }

    private fun addDoctorToDatabase(pw:String)
    {
        val name = intent.getStringExtra("Name")
        val email = intent.getStringExtra("Email")
        val number = intent.getStringExtra("Number")
        val certID = intent.getStringExtra("CertID")
        val pracID = intent.getStringExtra("PracID")
        val newDoctor = DoctorModel(name, email, number, certID, pracID)
        DoctorDataAccess.addNewDoctor(newDoctor, pw)
    }
    private fun validPassword(pw: String): Boolean
    {
        val specialCharRegex = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
         val digitRegex = Regex("\\d")

         //checking if the password matches the requirements for a safe password
         var valid: Boolean = true
         if( pw.length < 8 )
         {
             binding.etPassword1.error = "Password too short. Must be 8 characters or more."
             valid = false
         }
         if (!specialCharRegex.containsMatchIn(pw))
         {
             binding.etPassword1.error = "Password should contain a special character"
             valid = false
         }
         if(!digitRegex.containsMatchIn(pw)) {
             binding.etPassword1.error = "Password should contain a digit"
             valid = false
         }
        if(valid)
        {
            binding.etPassword1.error= null
        }
         return valid
    }

    private fun passwordMatch(pw1: String, pw2: String): Boolean
    {
        return if (pw1!= pw2) {
            binding.etPassword2.error = "Passwords do not match"
            false
        } else{
            binding.etPassword2.error = null
            true
        }
    }

}