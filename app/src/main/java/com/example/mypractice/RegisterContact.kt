package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mypractice.data.DoctorDataAccess
import com.example.mypractice.databinding.ActivityRegisterContactBinding
import com.example.mypractice.model.DoctorModel

class RegisterContact : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterContactBinding
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var phoneNumber: String
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

        val receivedIntent = intent
        email = receivedIntent.getStringExtra("Email").toString()

        binding.btnNext.setOnClickListener{
            accepted = true
            name = binding.etName.text.toString().trim()
            phoneNumber = binding.etNumber.text.toString().trim()

            //validating to the name and email address supplied


            if (validName()&&validNumber()) //basic data validation
            {
                //checks if email is already in the system
                checkEmailRegistered()//business logic data validation
            }
        }

        //TODO add proper intent for value saving for all back buttons 
        binding.imBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun validName():Boolean
    {
        val regex = Regex("^[a-zA-Z ]+\$")
        return if (name == "") {
            binding.etName.error = "Name cannot be empty"
            false
        } else if (!regex.matches(name)) {
            binding.etName.error = "Name can only contain letters and spaces"
            false
        } else {
            binding.etName.error = null
            true
        }
    }



    private fun validNumber(): Boolean
    {
        phoneNumber = phoneNumber.replace("\\s+".toRegex(), "")//removing all the spaces in the string
        var length = phoneNumber.length
        var alldig = phoneNumber.all{it.isDigit()}
        return if (alldig&& length == 10) {
            binding.etNumber.error =null
            true
        } else {
            binding.etNumber.error = "Invalid Number"
            false
        }
    }

    private fun checkEmailRegistered()
    {
        DoctorDataAccess.isEmailRegistered(email,
            onSuccess = { isRegistered ->
                if (isRegistered) {
                    //email is already registered as a doctor
                    showConfirmationDialog()
                } else {
                    //email is not registered yet
                    navigateToNextPage()
                }
            },
            onFailure = {error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            })
    }
    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Email Already Registered")
            .setMessage("This email is already registered as a doctor in our system, would you like to " +
                    "proceed to login")
            .setPositiveButton("Yes") { dialog, which ->
                // User clicked Yes, navigate to the shopping list activity
                startActivity(Intent(this, Login::class.java))
            }
            .setNegativeButton("No") { dialog, which ->
                // User clicked No, close the dialog (do nothing)
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToNextPage()
    {
        Log.d("RegisterContact", "Name: $name")
        Log.d("RegisterContact", "Email: $email")
        Log.d("RegisterContact","Number: $phoneNumber")



        val intent = Intent(this, RegisterID::class.java)
        intent.putExtra("Name", name)
        intent.putExtra("Email", email)
        intent.putExtra("Number", phoneNumber)
        startActivity(intent)
    }
}