package com.example.mypractice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.databinding.ActivityClientDetailsBinding
import com.example.mypractice.utils.FirebaseUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ClientDetails : AppCompatActivity() {
    private lateinit var binding: ActivityClientDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        //closing the activity
        binding.ivClose.setOnClickListener{
            startActivity(Intent(this, Clients::class.java))
        }

        //number needs to be more widely shared
        var number = ""


        //getting the id of the selected client
        val clientId = intent.getStringExtra("clientId")
        Log.d("ClientDetails", "Client ID: $clientId")

        //querying the rest of the information about the client
        FirebaseUtil.firestore.collection("users").document(clientId!!)
            .get()
            .addOnSuccessListener { document ->
                // Check if the document exists
                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    number = document.getString("number").toString()
                    val email = document.getString("email")
                    val dobTS = document.getTimestamp("dob")
                    val dob: Date? = dobTS?.toDate()

                    // Check if dob is not null before formatting
                    dob?.let {
                        val formattedDate =
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)

                        // Check if tvDobData is not null before setting the text
                        val age = dob.calculateAge()
                        binding.tvDobData?.text = formattedDate
                        binding.tvAgeData?.text = age.toString()
                    } ?: run {
                        // Handle the case when dob is null
                        binding.tvDobData?.text = "N/A"
                    }

                    // Update the UI with client information
                    binding.tvClientName?.text = name
                    binding.tvNumberData?.text = number
                    binding.tvEmailData?.text = email
                } else {
                    Log.d("ClientDetails", "Document with ID $clientId does not exist.")
                }
            }
            .addOnFailureListener { exception ->
                binding.tvClientName.text = "not found"
            }

        //call button
        binding.tvNumberData.setOnClickListener {

            val dialIntent = Intent (Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            // Check if there is an app that can handle the Intent before starting
            if (dialIntent.resolveActivity(packageManager) != null) {
                startActivity(dialIntent)
            } else {
                // Handle the case where no app can handle the dial Intent
                Toast.makeText(this, "No app can handle the dial action", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun Date.calculateAge(): Int {
        val currentDate = Calendar.getInstance().time
        val dobCalendar = Calendar.getInstance().apply { time = this@calculateAge }

        var age = Calendar.getInstance().get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

        if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}