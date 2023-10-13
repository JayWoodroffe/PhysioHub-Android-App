package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.mypractice.databinding.ActivityAppointmentsBinding

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
}