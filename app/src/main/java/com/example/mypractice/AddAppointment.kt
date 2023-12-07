package com.example.mypractice

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.databinding.ActivityAddAppointmentBinding
import com.example.mypractice.utils.FirebaseUtil
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddAppointment : AppCompatActivity() {
    private lateinit var binding:ActivityAddAppointmentBinding

    private var apptDate: Date? = null
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0
    private var apptTime: Calendar? = null
    private var  docId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("calendarExtra")) {
            // Retrieve the Calendar object from the Intent
            val apptDateCal = intent.getSerializableExtra("calendarExtra") as Calendar
            apptDate = apptDateCal.time
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvSelectDate.text = dateFormat.format(apptDate!!.time)

        }

        FirebaseUtil.getDoctorCertIdByEmail{certId ->
            if (certId != null) {
                docId = certId.toInt()
                Log.d ("Tag" , "doc id received by new appt activity; " + certId)
                val query = FirebaseUtil.getAllClientsForDoctorQuery(certId)
                query.get().addOnSuccessListener { documents ->
                    val clientNames = mutableListOf<String>()

                    for (document in documents)
                    {
                        Log.d ("Tag" , "in for loop")
                        val clientName = document.getString("name")
                        clientName?.let{
                            clientNames.add(it)
                        }
                    }
                    Log.d ("Tag" , "number of clients " + clientNames.size)
                    //set up the spinner w client names
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clientNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinClients.adapter = adapter
                }.addOnFailureListener{exception ->
                    Log.e("tag", "Error getting clients " + exception)
                }
            }
        }

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        binding.imBack.setOnClickListener {
            showDiscardConfirmationDialog()

        }
        binding.btnSelDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSelTime.setOnClickListener {
            showTimePickerDialog()
        }

        binding.btnCreateApp.setOnClickListener {
            if (validateData()) {
                val selectedClient: String = binding.spinClients.selectedItem as String
                val timestamp: Timestamp = getCombinedDateTime()
                docId?.let { it1 -> FirebaseUtil.addAppointment(selectedClient, it1, timestamp) }
                startActivity(Intent(this, Appointments::class.java))
            }
            else
            {
                Toast.makeText(this, "Please fill in all the required data", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getCombinedDateTime(): Timestamp {
        // Create a Calendar instance for apptDate
        val combinedDateTime = Calendar.getInstance()
        combinedDateTime.time = apptDate

        // Extract hour and minute from apptTime
        val hour = apptTime!!.get(Calendar.HOUR_OF_DAY)
        val minute = apptTime!!.get(Calendar.MINUTE)

        // Set the hour and minute to the combinedDateTime
        combinedDateTime.set(Calendar.HOUR_OF_DAY, hour)
        combinedDateTime.set(Calendar.MINUTE, minute)

        // Convert the combined DateTime to a Timestamp
        return Timestamp(combinedDateTime.time)
    }

    private fun validateData(): Boolean
    {
        return apptDate != null && apptTime != null && selectedHour != null && selectedMinute != null
    }
    private fun showTimePickerDialog(){
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Create a TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            R.style.TimePickerTheme,
            { _, hourOfDay, minute ->
                // Update the selectedHour and selectedMinute variables
                selectedHour = hourOfDay
                selectedMinute = minute

                // Update the TextView with the selected time
                updateSelectedTimeTextView()
            },
            currentHour,
            currentMinute,
            false
        )

        // Show the TimePickerDialog
        timePickerDialog.show()
    }

    private fun updateSelectedTimeTextView() {
        // Format the selected time and update the TextView
        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
            .format(getSelectedTimeCalendar().time)
        binding.tvSelectTime.text = formattedTime
    }

    private fun getSelectedTimeCalendar(): Calendar {
        // Create a Calendar instance with the selected hour and minute
        val selectedTimeCalendar = Calendar.getInstance()
        selectedTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        selectedTimeCalendar.set(Calendar.MINUTE, selectedMinute)
        apptTime = selectedTimeCalendar
        return selectedTimeCalendar
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DatePickerTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDateCalendar = Calendar.getInstance()
                selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay)
                val selectedDate = selectedDateCalendar.time
                updateSelectedDate(selectedDate)
                apptDate = selectedDate
            },
            year,
            month,
            day
        )

        // Set the DatePickerDialog to show a spinner (dropdown)
        val datePicker = datePickerDialog.datePicker
        datePicker.calendarViewShown = false
        datePicker.spinnersShown = true

        // Show the DatePickerDialog
        datePickerDialog.show()
    }
    private fun updateSelectedDate(date: Date?) {
        // Update your TextView with the selected date in the desired format
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.tvSelectDate.text = dateFormat.format(date)
    }


    private fun showDiscardConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Discard Appointment")
            .setMessage("Appointment has not been saved yet. Do you wish to discard this information?")
            .setPositiveButton("Yes") { dialog, which ->
                // User clicked Yes, navigate to the shopping list activity
                startActivity(Intent(this, Appointments::class.java))
            }
            .setNegativeButton("No") { dialog, which ->
                // User clicked No, close the dialog (do nothing)
                dialog.dismiss()
            }
            .show()
    }


}