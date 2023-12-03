package com.example.mypractice

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.databinding.ActivityHomeBinding
import com.example.mypractice.model.Appointment
import com.example.mypractice.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )


        //setting the greeting
        binding.tvGreeting.setText(getGreeting())
        FirebaseUtil.getDoctorNameByEmail { name ->
            binding.tvName.setText("Dr. " + name)
        }
        binding.gif.setImageResource(getGif())


        //setting up the appt display
        getAppt()


        //changing the activity when menu item is selected
        val navigationView = binding.btmNavMenu
        navigationView.selectedItemId = R.id.nav_home
        navigationView.setOnItemSelectedListener { item: MenuItem-> handleNavigationItemSelected(item)}

        //setting up the notepad
        // Check if the fragment container exists in the layout
        val container = binding.notepadContainer

        if (container != null) {
            // Create an instance of the NotepadFragment
            val notepadFragment = NotepadFragment()

            // Add the fragment to the container
            supportFragmentManager.beginTransaction()
                .replace(R.id.notepadContainer, notepadFragment)
                .commit()
        }

    }

    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_home ->{
                return true
            }
            R.id.nav_clients ->{
                startActivity(Intent(this, Clients::class.java))
                return true}
            R.id.nav_appointments ->{
                startActivity(Intent(this, Appointments::class.java))
                return true}
            R.id.nav_settings ->{
                startActivity(Intent(this, Settings::class.java))
                return true}
            else -> return false
        }
    }
    fun getGreeting():String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when {
            hour in 0..11 -> "good morning"
            hour in 12..16 -> "good afternoon"
            hour in 17..23 -> "good evening"
            else -> ""
        }

    }

    fun getGif(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 0..11 -> R.drawable.ic_day
            in 12..16 -> R.drawable.ic_aft
            in 17..23 -> R.drawable.ic_evening
            else -> R.drawable.ic_day
        }
    }

    private fun getAppt()
    {
        val currentTimeStamp: Timestamp = Timestamp.now()
        var nextAppointment: Appointment

// Querying the doctor's appointments for the selected day using their certId
        var certId = FirebaseUtil.getDoctorCertIdByEmail { certId ->
            val appointmentsCollectionRef = FirebaseUtil.allAppointmentsCollectionReference()

            val query = appointmentsCollectionRef
                .whereEqualTo("doctorCertId", certId)
                .whereGreaterThanOrEqualTo("date", currentTimeStamp)
                .orderBy("date", Query.Direction.ASCENDING)
                .limit(1) // Limit the result to the first document

            // Execute the query
            query.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Parse and process the first appointment document
                        val time = document.getTimestamp("date")?.toDate()
                        val clientName = document.getString("clientName")

                        // Create Appointment object and assign it to nextAppointment
                        nextAppointment = Appointment(clientName ?: "", time ?: Date())


                        binding.tvClientName.text = nextAppointment.clientName
                        binding.tvTime.text = SimpleDateFormat("dd MMM h:mm a", Locale.getDefault()).format(nextAppointment.time)
                        // using a break to get out of the loop since i only need the first appointment
                        break
                    }
                    if (documents.size() == 0)
                    {
                        //display that there are no upcoming appts
                        binding.tvClientName.text = "No future appointments created"
                        binding.tvTime.text = ""
                    }
                    //Log.d("appointments", "Number of appointments after query: " + appointmentsList.size)
                }
                .addOnFailureListener { exception ->
                    //Log.e("Appointments", "Failed to query appointments", exception)
                    //(emptyList()) // Notify the callback with an empty list in case of failure

                }
        }
    }
}