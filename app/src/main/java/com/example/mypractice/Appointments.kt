package com.example.mypractice

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
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
import com.example.mypractice.utils.FirebaseUtil
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Appointments : AppCompatActivity() {

    private lateinit var binding : ActivityAppointmentsBinding
    //global variable to store the currently selected date square
    private var selectedDateSquare: LinearLayout? = null

    private var currentDateSelected: Calendar = Calendar.getInstance()

    // Define a map to store the original background colors based on the day of the week
    //this is to ensure that after a date item is deselected, it can return back to its initial color
    //that corresponds to the day of the week of that date
    private val originalDrawables = mutableMapOf<Int, Drawable?>()

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

        binding.btnAddNew.setOnClickListener {
            val intent = Intent(this, AddAppointment::class.java)
            intent.putExtra("calendarExtra", currentDateSelected as Serializable?)
            startActivity(intent)
        }

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



        for (i in 0..numberOfDays) {
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

            // Store the original background color based on the day of the week
            val originalDrawable = when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> ContextCompat.getDrawable(this, R.drawable.monday_bg)
                Calendar.TUESDAY -> ContextCompat.getDrawable(this, R.drawable.tuesday_bg)
                Calendar.WEDNESDAY -> ContextCompat.getDrawable(this, R.drawable.wednesday_bg)
                Calendar.THURSDAY -> ContextCompat.getDrawable(this, R.drawable.thursday_bg)
                Calendar.FRIDAY -> ContextCompat.getDrawable(this, R.drawable.friday_bg)
                Calendar.SATURDAY -> ContextCompat.getDrawable(this, R.drawable.saturday_bg)
                Calendar.SUNDAY -> ContextCompat.getDrawable(this, R.drawable.sunday_bg)
                else -> ContextCompat.getDrawable(this, R.drawable.round_bar_blue)
            }

            // Store the original color in the map
            originalDrawables[cal.get(Calendar.DAY_OF_WEEK)] = originalDrawable

            // Set the background color
            dateSquare.background=originalDrawable


            // Set a click listener for the date square - new date clicked, horizontal view and appts are updated
            dateSquare.setOnClickListener {
                // Reset the background color to the original color

                newDateSelected(dateSquare)
            }

            linearLayout.addView(dateSquare)
        }


        // Set the initial selected date to the current date
        val initialSelectedDate = Calendar.getInstance()
        initialSelectedDate.set(Calendar.HOUR_OF_DAY, 0)
        selectedDateSquare = binding.linLayout.getChildAt(numberOfDays) as? LinearLayout
        selectedDateSquare?.setBackgroundResource(R.drawable.selected_border)
        updateAppointmentsForDate(initialSelectedDate)
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

    private fun getAppointments(currentDay: Timestamp, nextDay: Timestamp, callback: (List<Appointment>) -> Unit) {

        val appointmentsList = mutableListOf<Appointment>()
        //querying the doctors appointments for the selected day using their certId
        var certId = FirebaseUtil.getDoctorCertIdByEmail { certId ->
            val appointmentsCollectionRef = FirebaseUtil.allAppointmentsCollectionReference()

            val query = appointmentsCollectionRef
                .whereEqualTo("doctorCertId", certId)
                .whereGreaterThanOrEqualTo("date", currentDay)
                .whereLessThan("date", nextDay)
                .orderBy("date", Query.Direction.ASCENDING)

//            Log.d("Appointments", "time " + day.timeInMillis + TimeUnit.DAYS.toMillis(1))
            // Execute the query
            query.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Parse and process each appointment document
                        val time = document.getTimestamp("date")?.toDate()
                        val clientName = document.getString("clientName")

                        // Create Appointment object and add it to the list
                        val appointment = Appointment(clientName ?: "", time ?: Date())
                        appointmentsList.add(appointment)
                    }
                    Log.d("appointments", "number of appointments after query: " + appointmentsList.size)
                    callback(appointmentsList) // Notify the callback with the result
                }
                .addOnFailureListener { exception ->
                    Log.e("Appointments", "Failed to query appointments", exception)
                    callback(emptyList()) // Notify the callback with an empty list in case of failure
                }

        }
    }

    private fun formatTimestamp(timestamp: Timestamp): String {
        // Create a SimpleDateFormat instance with the desired format
        val sdf = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())

        // Convert Timestamp to Date and format it
        val date = timestamp.toDate()
        return sdf.format(date)
    }
    private fun displayAppointments(dayStart:Timestamp, dayEnd:Timestamp)
    {
        val formattedTimestamp1 = formatTimestamp(dayStart)
        val formattedTimestamp2 = formatTimestamp(dayEnd)
        Log.d("appt", "Timestamp 1: $formattedTimestamp1")
        Log.d("appt", "Timestamp 2: $formattedTimestamp2")
        //setting up appointments for current day
        getAppointments(dayStart, dayEnd) { appointmentsList ->


            // Handle the result here
            Log.d("appointments", "number of appointments received: " + appointmentsList.size)
            val appointmentsLayout: LinearLayout = binding.appointmentsLayout

            for (appointment in appointmentsList)
            {
                val appointmentItem = LayoutInflater.from(this).inflate(R.layout.item_appt, null) as LinearLayout
                // Assuming itemAppt is the layout you want to space vertically
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 0, 0, 20)  // Adjust the vertical margin as needed
                appointmentItem.layoutParams = params

                //setting up appointment details
                val timeTv: TextView = appointmentItem.findViewById(R.id.tvTime)
                val clientNameTv: TextView = appointmentItem.findViewById(R.id.tvClientName)

                timeTv.text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(appointment.time)
                clientNameTv.text = appointment.clientName

                //add the app to linear layout
                appointmentsLayout.addView(appointmentItem)
                Log.d("Appt check", "appt added")
            }
        }
    }
    private fun getTodayTimestamp(): Timestamp {
        val currentDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return Timestamp(currentDate.time)
    }

    private fun getTomorrowTimestamp(): Timestamp {
        val tomorrowDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return Timestamp(tomorrowDate.time)
    }

    // Function to update appointments based on the selected date
    private fun updateAppointmentsForDate(selectedDate: Calendar) {

        val currentDate = Calendar.getInstance()
        if (selectedDate.get(Calendar.MONTH) < currentDate.get(Calendar.MONTH)) {
            // Increment the year for the next year
            selectedDate.add(Calendar.YEAR, 1)
        }

        // Clear the existing appointments layout
        binding.appointmentsLayout.removeAllViews()

        //setting the date view
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(selectedDate.time)
        binding.tvDayofWeek.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(selectedDate.time)


        //display appointments for the newly selected date
        displayAppointments(Timestamp(selectedDate.time), Timestamp(dayPlusOne(selectedDate).time))
    }


    private fun dayPlusOne(day: Calendar): Calendar { //get to calendar object of the day after a calendar object
        val modifiedDay = day.clone() as Calendar
        modifiedDay.add(Calendar.DAY_OF_MONTH, 1)
        return modifiedDay
    }

    private fun newDateSelected(dateSquare: LinearLayout)
    {
        //method is called when a new date is selected from the horizontal scroll

        val currentDate = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        // Deselect the previously selected date square
        selectedDateSquare?.apply {
            val dayTv: TextView = findViewById(R.id.tvDay)

            val dayVal = dayTv.text.toString().toInt()
            val monthTv: TextView = findViewById(R.id.tvMonth)
            val monthAbb = monthTv.text.toString()
            val monthTemp = monthFormat.parse(monthAbb)

            val monthVal = Calendar.getInstance().apply {
                time = monthTemp

            }.get(Calendar.MONTH)

            val prevDate = Calendar.getInstance()
            prevDate.set(Calendar.DAY_OF_MONTH, dayVal)
            prevDate.set(Calendar.MONTH, monthVal)

            if (monthVal  < currentDate.get(Calendar.MONTH)) {
                // Increment the year for the next year
                prevDate.add(Calendar.YEAR, 1)
            }
            background = originalDrawables[prevDate.get(Calendar.DAY_OF_WEEK)]
        }

        // Update the appearance of the clicked date square (add thick border)
        dateSquare.setBackgroundResource(R.drawable.selected_border)

        // Update the selected date square
        selectedDateSquare = dateSquare

        // Extract the day and month from the TextView elements
        val dayTextView: TextView = dateSquare.findViewById(R.id.tvDay)
        val monthTextView: TextView = dateSquare.findViewById(R.id.tvMonth)

        val selectedDay = dayTextView.text.toString().toInt()
        val selectedMonthAbbreviation = monthTextView.text.toString()

        // Use SimpleDateFormat to get the month index
        val monthDate = monthFormat.parse(selectedMonthAbbreviation)
        val selectedMonth = Calendar.getInstance().apply {
            time = monthDate
        }.get(Calendar.MONTH)

        // Get the date information from the selected date square
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, selectedDay)
        cal.set(Calendar.MONTH, selectedMonth)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        // Extract the month from the selected date
        val selectedDate = cal.time

        currentDateSelected = cal
        // Update information based on the selected date (e.g., fetch appointments)
        updateAppointmentsForDate(cal)
    }
}