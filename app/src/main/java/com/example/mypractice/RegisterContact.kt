package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mypractice.databinding.ActivityRegisterContactBinding
import com.example.mypractice.model.DocModel

class RegisterContact : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        var accepted: Boolean
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding.btnNext.setOnClickListener{
            accepted = true
            //sending the name value to the singleton DataRegistration
            try {
                    DocModel.name = binding.etName.text.toString()
                    binding.etName.setError(null)
                }
            catch (e: IllegalArgumentException )
            {
                accepted=false
                binding.etName.setError("${e.message}")
            }


            //sending email address
            try{
                DocModel.email = binding.etEmail.text.toString()
                binding.etEmail.setError(null)
            }
            catch (e: IllegalArgumentException)
            {
                accepted=false
                binding.etEmail.setError("${e.message}")
            }

            //sending phone number
            try{
                DocModel.phone = binding.etNumber.text.toString()
                binding.etNumber.setError(null)
            }
            catch (e: IllegalArgumentException)
            {
                accepted = false
                binding.etNumber.setError("${e.message}")
            }
            if (accepted == true)
            {startActivity(Intent(this, RegisterID::class.java))}
        }

        //TODO add proper intent for value saving for all back buttons 
        binding.imBack.setOnClickListener{
            onBackPressed()
        }
    }
}