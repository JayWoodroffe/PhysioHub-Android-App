package com.example.mypractice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.data.ClientDataAccess
import com.example.mypractice.data.ExerciseDataAccess
import com.example.mypractice.databinding.ActivityClientDetailsBinding
import com.example.mypractice.model.ClientModel
import com.example.mypractice.model.ExerciseModel
import com.example.mypractice.utils.ClientDetailsFragment
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ClientDetails : AppCompatActivity() {
    private lateinit var binding: ActivityClientDetailsBinding
    private lateinit var clientId:String
    private lateinit var popupWindow: PopupWindow
    private lateinit var fragment : ExerciseFragment
    private lateinit var chatFragment: ChatFragment
    private lateinit var infoFragment: ClientDetailsFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getting the id of the selected client
        clientId = intent.getStringExtra("clientId").toString()
        Log.d("ClientDetails", "Client ID: $clientId")

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


        //setting up exercises tab

        val tabToSelect = binding.toggleButton.tabLayout.getTabAt(1)
        tabToSelect?.select()

        val bundle=Bundle()
        bundle.putString("clientID", clientId)

        infoFragment = ClientDetailsFragment()
        setClientDetails()

        chatFragment = ChatFragment()
        chatFragment.setClientId(clientId)

        fragment = ExerciseFragment()
        fragment.setClientId(clientId)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.exerciseFragmentContainer.id, fragment)
        fragmentTransaction.commit()

//        displayActiveExercises()

        //TODO long click does nothing
        val fragContainer: View = findViewById(R.id.exercise_fragment_container)
        fragContainer.setOnLongClickListener {

            fragment.setSelectionMode(true)
            Log.d("Select", "container long click")
            true
        }

        //changing toggle bar
        binding.toggleButton.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab selection
                tab?.let {
                    // Check which tab is selected
                    when (it.text) {
                        "Exercises" -> {
                            // Show the ExerciseFragment

//                            hidePopUp()
                            showExerciseFragment()
                        }
                        // Add more cases for other tabs if needed
                    }
                    when (it.text) {
                        "Chat" -> {
                            // Show the ExerciseFragment
                            showChatFragment()
//                            hidePopUp()
                            //hideExerciseFragment()

                        }
                        // Add more cases for other tabs if needed
                    }
                    when (it.text) {
                        "Info" -> {
//                            hideExerciseFragment()
                            showPopup(it)
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
    }

    private fun showExerciseFragment() {
//        val taskbar: View = findViewById(R.id.taskBar)
//        taskbar.visibility = View.VISIBLE

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.exerciseFragmentContainer.id, fragment)
        fragmentTransaction.commit()
    }

    private fun hideExerciseFragment() {
//        val taskbar: View = findViewById(R.id.taskBar)
//        taskbar.visibility = View.GONE

        // Remove the ExerciseFragment from the container
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.hide(fragment)
        fragmentTransaction.commit()
    }

    private fun showChatFragment()
    {
//        val taskbar: View = findViewById(R.id.taskBar)
//        taskbar.visibility = View.GONE

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.exerciseFragmentContainer.id, chatFragment)
        fragmentTransaction.commit()
    }



    private fun showPopup(anchorView: TabLayout.Tab)
    {
//        val popupInfoLayout: View = findViewById(R.id.popupInfo)
//            popupInfoLayout.visibility = View.VISIBLE
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.exerciseFragmentContainer.id, infoFragment)
        fragmentTransaction.commit()
    }

//    private fun hidePopUp()
//    {
//        val popupInfoLayout: View = findViewById(R.id.popupInfo)
//        popupInfoLayout.visibility = View.GONE
//    }


    private fun setClientDetails()
    {
        //querying the rest of the information about the client
        ClientDataAccess.getClientByID(clientId!!) { client: ClientModel? ->
            client?.let{
                val client = ClientModel()
                client.name = it.name
                client.number = it.number?:""
                client.email = it.email?:""
                client.dob = it.dob

                binding.tvClientName?.text = client.name

                infoFragment.setClientDetails(client)
            } ?: run {
                Log.d("ClientDetails", "No client found with ID: $clientId")
            }
        }
    }


}