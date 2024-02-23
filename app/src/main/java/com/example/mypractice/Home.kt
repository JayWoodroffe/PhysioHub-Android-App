package com.example.mypractice

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.data.AppointmentsDataAccess
import com.example.mypractice.data.DoctorDataHolder
import com.example.mypractice.databinding.ActivityHomeBinding
import com.example.mypractice.model.Appointment
import com.example.mypractice.model.DoctorModel
import com.example.mypractice.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var loggedInDoctor: DoctorModel? = null

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

        loggedInDoctor = DoctorDataHolder.getLoggedInDoctor()
        if(loggedInDoctor!=null)
        {
            binding.tvName.setText("Dr. " + loggedInDoctor!!.name)
            //setting up the appt display
            AppointmentsDataAccess.getNextAppointment(loggedInDoctor!!.certId){
                    appointment ->
                if(appointment!= null){
                    updateUIWithAppointment(appointment)
                }
                else{
                    showNoAppointmentsMessage()
                }
            }
        }
        else {
            Toast.makeText(this, "No doctor logged in", Toast.LENGTH_SHORT).show()
        }
        binding.gif.setImageResource(getGif())





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

    private fun updateUIWithAppointment(appointment: Appointment) {
        binding.tvClientName.text =appointment.clientName
        binding.tvTime.text = SimpleDateFormat("dd MMM h:mm a", Locale.getDefault()).format(appointment.time)
    }

    private fun showNoAppointmentsMessage() {
        // Display a message indicating no appointments found
        binding.tvClientName.text = "No future appointments created"
        binding.tvTime.text = ""
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

    
}