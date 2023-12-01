package com.example.mypractice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mypractice.databinding.ActivityAppointmentsBinding
import com.example.mypractice.model.Appointment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Appointments : AppCompatActivity() {

    private lateinit var binding : ActivityAppointmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        val navigationView = binding.btmNavMenu
        navigationView.selectedItemId = R.id.nav_appointments
        navigationView.setOnItemSelectedListener { item: MenuItem -> handleNavigationItemSelected(item)}

        //setting up the horizontal scroll view of dates
        val inflater = LayoutInflater.from(this)
        val scrollView: HorizontalScrollView = binding.horScroll
        val linearLayout:LinearLayout= binding.linLayout

        //displaying 50 days before and after the current date
        val numberOfDays = 50
        val screenWidth = resources.displayMetrics.widthPixels

        // Width and margin for each date square
        val dateSquareWidth = 130 // Adjust the width as needed
        val dateSquareMargin = 10 // Adjust the margin as needed

        // Calculate total width of all date squares
        val totalWidth = (dateSquareWidth + dateSquareMargin * 2) * (numberOfDays * 2)

        for (i in -numberOfDays..numberOfDays) {
            val dateSquare = inflater.inflate(R.layout.item_date_sqr, null) as LinearLayout
            val params = LinearLayout.LayoutParams(dateSquareWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(dateSquareMargin, 0, dateSquareMargin, 0)
            dateSquare.layoutParams = params

            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, i)

            val dayTextView: TextView = dateSquare.findViewById(R.id.tvDay)
            val monthTextView: TextView = dateSquare.findViewById(R.id.tvMonth)

            dayTextView.text = SimpleDateFormat("dd", Locale.getDefault()).format(cal.time)
            monthTextView.text = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)

            when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> dateSquare.setBackgroundColor(ContextCompat.getColor(this, R.color.monday))
                Calendar.TUESDAY -> dateSquare.setBackgroundColor(ContextCompat.getColor(this, R.color.tuesday))
                Calendar.WEDNESDAY -> dateSquare.setBackgroundColor(ContextCompat.getColor(this, R.color.wednesday))
                Calendar.THURSDAY -> dateSquare.setBackgroundColor(ContextCompat.getColor(this, R.color.thursday))
                Calendar.FRIDAY -> dateSquare.setBackgroundColor(ContextCompat.getColor(this, R.color.friday))
                Calendar.SATURDAY -> dateSquare.setBackgroundColor(ContextCompat.getColor(this, R.color.saturday))
                Calendar.SUNDAY -> dateSquare.setBackgroundColor(ContextCompat.getColor(this, R.color.sunday))
            }

            linearLayout.addView(dateSquare)
        }

        // Calculate the scroll position to center the dates
        val scrollToX = totalWidth / 2 - screenWidth / 2 + 100

        scrollView.post {
            scrollView.scrollTo(scrollToX, 0)
        }

    }

    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_home ->{
                startActivity(Intent(this, Home::class.java))
                return true
            }
            R.id.nav_clients ->{
                startActivity(Intent(this, Clients::class.java))
                return true}
            R.id.nav_appointments ->{
                return true}
            R.id.nav_settings ->{
                startActivity(Intent(this, Settings::class.java))
                return true}
            else -> return false
        }
    }

    private fun getAppointments(day: Date): List<Appointment>
    {

    }
}