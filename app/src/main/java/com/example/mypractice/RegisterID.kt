package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mypractice.databinding.ActivityRegisterIdBinding
import com.example.mypractice.model.DocModel

class RegisterID : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterIdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding.btnNext.setOnClickListener{
            var accepted: Boolean = true
            try{
                DocModel.certID = binding.etCertID.text.toString()
            }
            catch(e:IllegalArgumentException)
            {
                binding.etCertID.setError("${e.message}")
                accepted = false
            }

            try{
                DocModel.pracID = binding.etPracID.text.toString()
            }
            catch(e:IllegalArgumentException)
            {
                binding.etPracID.setError("${e.message}")
                accepted = false
            }
            if (accepted == true)
                startActivity(Intent(this, RegisterPassword::class.java))
        }
        binding.imBack.setOnClickListener{
            onBackPressed()
        }
    }
}