package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mypractice.data.DoctorDataAccess
import com.example.mypractice.databinding.ActivityRegisterIdBinding
import com.example.mypractice.model.DoctorModel

class RegisterID : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterIdBinding
    private lateinit var name:String
    private lateinit var email:String
    private lateinit var number:String

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
        // Retrieve previous intents
        val receivedIntent = intent
        name = receivedIntent.getStringExtra("Name").toString()
         email = receivedIntent.getStringExtra("Email").toString()
         number= receivedIntent.getStringExtra("Number").toString()

        binding.btnNext.setOnClickListener {
            var certID = binding.etCertID.text.toString().trim()
            var pracID = binding.etPracID.text.toString().trim()

            if (validCertID(certID) && validPracID(pracID)) {
                checkCertificateRegistered(certID, pracID)
            }
        }
        binding.imBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun validCertID(certID: String): Boolean
    {
        return if(certID == "") {
            binding.etCertID.error = "Certificate ID cannot be empty"
            false
        } else
            true
    }
    private fun validPracID(pracID: String): Boolean
    {
        return if(pracID == "") {
            binding.etPracID.error = "Practice ID cannot be empty"
            false
        } else
            true
    }

    private fun checkCertificateRegistered(certID:String, pracID: String)
    {
        DoctorDataAccess.isCertificateRegistered(certID,
            onSuccess = { isRegistered ->
                if (isRegistered)
                    showConfirmationDialog()
                else
                    navigateToNextPage(certID, pracID)
            },
            onFailure = {error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            })
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Certificate ID Already Registered")
            .setMessage("This certificate ID appears to be registered in the system already, " +
            "would you like to proceed to log in?")
            .setPositiveButton("Login") { dialog, which ->
                // User clicked Yes, navigate to the shopping list activity
                startActivity(Intent(this, Login::class.java))
            }
            .setNegativeButton("Continue sign in") { dialog, which ->
                // User clicked No, close the dialog (do nothing)
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToNextPage(certID: String, pracID: String)
    {
        val intent = Intent(this, RegisterPassword::class.java)

        //adding intents to next screen
        intent.putExtra("Name", name)
        intent.putExtra("Email", email)
        intent.putExtra("Number", number)

        Log.d("RegisterID", "Received Name: $name")
        Log.d("RegisterID", "Received Email: $email")
        Log.d("RegisterID", "Received Number: $number")
        //intents from this screen
        intent.putExtra("CertID", certID)
        intent.putExtra("PracID", pracID)

        startActivity(intent)
    }
}