package com.example.mypractice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.data.ClientDataAccess
import com.example.mypractice.databinding.ActivityClientDetailsBinding
import com.example.mypractice.model.ClientModel
import com.example.mypractice.utils.ExerciseFragment
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ClientDetails : AppCompatActivity() {
    private lateinit var binding: ActivityClientDetailsBinding
    private lateinit var clientId:String
    private lateinit var popupWindow: PopupWindow
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getting the id of the selected client
        clientId = intent.getStringExtra("clientId").toString()
        Log.d("ClientDetails", "Client ID: $clientId")
        setClientDetails()
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

        binding.btnInfo.setOnClickListener {
            showPopup(it)
        }

        //setting up exercises tab
        //TODO get data from firestore
        val tabToSelect = binding.toggleButton.tabLayout.getTabAt(1)
        tabToSelect?.select()

        val fragment = ExerciseFragment()

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.exerciseFragmentContainer.id, fragment)
        fragmentTransaction.commit()

        binding.toggleButton.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab selection
                tab?.let {
                    // Check which tab is selected
                    when (it.text) {
                        "Exercises" -> {
                            // Show the ExerciseFragment
                            showExerciseFragment()
                        }
                        // Add more cases for other tabs if needed
                    }
                    when (it.text) {
                        "Chat" -> {
                            // Show the ExerciseFragment
                            hideExerciseFragment()
                        }
                        // Add more cases for other tabs if needed
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselection (if needed)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselection (if needed)
            }

        })







//        FirebaseUtil.firestore.collection("users").document(clientId!!)
//            .get()
//            .addOnSuccessListener { document ->
//                // Check if the document exists
//                if (document != null && document.exists()) {
//                    val name = document.getString("name")
//                    number = document.getString("number").toString()
//                    val email = document.getString("email")
//                    val dobTS = document.getTimestamp("dob")
//                    val dob: Date? = dobTS?.toDate()
//
//                    // Check if dob is not null before formatting
//                    dob?.let {
//                        val formattedDate =
//                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
//
//                        // Check if tvDobData is not null before setting the text
//                        val age = dob.calculateAge()
//                        binding.tvDobData?.text = formattedDate
//                        binding.tvAgeData?.text = age.toString()
//                    } ?: run {
//                        // Handle the case when dob is null
//                        binding.tvDobData?.text = "N/A"
//                    }
//
//                    // Update the UI with client information
//                    binding.tvClientName?.text = name
//                    binding.tvNumberData?.text = number
//                    binding.tvEmailData?.text = email
//                } else {
//                    Log.d("ClientDetails", "Document with ID $clientId does not exist.")
//                }
//            }
//            .addOnFailureListener { exception ->
//                binding.tvClientName.text = "not found"
//            }


        //call button
        //TODO add call feature to chat area
//        binding.tvNumberData.setOnClickListener {
//
//            val dialIntent = Intent (Intent.ACTION_DIAL, Uri.parse("tel:$number"))
//            // Check if there is an app that can handle the Intent before starting
//            if (dialIntent.resolveActivity(packageManager) != null) {
//                startActivity(dialIntent)
//            } else {
//                // Handle the case where no app can handle the dial Intent
//                Toast.makeText(this, "No app can handle the dial action", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun showExerciseFragment() {
        val fragment = ExerciseFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.exercise_fragment_container, fragment)
            .commit()
    }

    private fun hideExerciseFragment() {
        // Remove the ExerciseFragment from the container
        val fragment = supportFragmentManager.findFragmentById(R.id.exercise_fragment_container)
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
    }

    private fun showPopup(anchorView: View)
    {
        val popupInfoLayout: View = findViewById(R.id.popupInfo)
        if (popupInfoLayout.visibility == View.VISIBLE) {
            popupInfoLayout.visibility = View.GONE
        } else {
            popupInfoLayout.visibility = View.VISIBLE

            binding.popupInfo.imClose.setOnClickListener {
                popupInfoLayout.visibility = View.GONE
            }
        }

    }

    private fun setClientDetails()
    {
        //querying the rest of the information about the client
        ClientDataAccess.getClientByID(clientId!!) { client: ClientModel? ->
            client?.let{
                val name = it.name
                val number = it.number?:""
                val email = it.email?:""
                val dob = it.dob

                dob?.let{
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                    val age = it.calculateAge()
                    binding.popupInfo.tvDobData.text = formattedDate
                    binding.popupInfo.tvAgeData.text = age.toString()
                }?:run{
                    binding.popupInfo.tvDobData.text = "N/A"
                }

                binding.tvClientName?.text = name

                binding.popupInfo.tvNumberData.text = number
                binding.popupInfo.tvEmailData.text =  email
            } ?: run {
                Log.d("ClientDetails", "No client found with ID: $clientId")
            }
        }
    }

    private fun Date.calculateAge(): Int {
        val currentDate = Calendar.getInstance().time
        val dobCalendar = Calendar.getInstance().apply { time = this@calculateAge }

        var age = Calendar.getInstance().get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

        if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}