package com.example.mypractice

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.databinding.ActivityAddClientBinding
import com.example.mypractice.utils.FirebaseUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class AddClient : AppCompatActivity() {


    private lateinit var binding: ActivityAddClientBinding
    private var dob: Date? = null
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var number: String
    private var docId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedIntent = intent
        docId  = receivedIntent.getIntExtra("docId", 0)


        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )


        binding.btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.imBack.setOnClickListener {
            //check if the doc wants to discard this client
            showDiscardConfirmationDialog()
        }

        binding.btnCreate.setOnClickListener {

            if (verifyInputs()== true)
            {
                createUser()
            }
        }

    }

    private fun createUser(){
        //creating the new client
        val client = hashMapOf(
            "dob" to dob?.let { com.google.firebase.Timestamp(it) },
            "doctorID" to docId,
            "email" to email,
            "name" to name,
            "number" to number,
            "searchField" to name.toLowerCase(),
            "userType" to "client"
        )

        FirebaseUtil.addClientToDb(client)
        startActivity(Intent(this, Clients::class.java))
    }
    private fun verifyInputs(): Boolean{
        var validData = true
         name = binding.etClientName.text.toString()
         email = binding.etClientEmail.text.toString()
         number = binding.etClientNumber.text.toString()

        //check name
        if(name.trim() == "")
        {
            binding.etClientName.setError("Name cannot be null")
            validData = false
        }

        //check email
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,4})+$")
        if (!email.matches(emailRegex))
        {
            validData = false
            binding.etClientEmail.setError("Invalid emaila address")
            //TODO Next sem: check if client is already registered in system
        }
        if(dob == null)
        {
            validData= false
            binding.tvDobData.setError("Date of birth cannot be null")
        }

        //check cell number
        var temp: String = number.replace("\\s+".toRegex(), "")//removing all the spaces in the string
        var length = temp.length
        var alldig = temp.all{it.isDigit()}
        if(!alldig || length !=10)
        {
            binding.etClientNumber.setError("Invalid cell number")
            validData=false
        }

        return validData
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
                dob = selectedDate
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
        binding.tvDobData.text = dateFormat.format(date)
    }

    private fun showDiscardConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Discard Client")
            .setMessage("Client has not been saved yet. Returning to your client list will" +
                    " discard these detials.")
            .setPositiveButton("Discard client") { dialog, which ->
                // User clicked Yes, navigate to the shopping list activity
                startActivity(Intent(this, Clients::class.java))
            }
            .setNegativeButton("Keep editing") { dialog, which ->
                // User clicked No, close the dialog (do nothing)
                dialog.dismiss()
            }
            .show()
    }
}