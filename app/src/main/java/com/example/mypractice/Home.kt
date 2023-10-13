package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.mypractice.databinding.ActivityHomeBinding
import com.example.mypractice.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

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
        binding.tvName.setText("Dr. Jay Woodroffe")


        //changing the activity when menu item is selected
        val navigationView = binding.btmNavMenu
        navigationView.selectedItemId = R.id.nav_home
        navigationView.setOnItemSelectedListener { item: MenuItem-> handleNavigationItemSelected(item)}

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
}