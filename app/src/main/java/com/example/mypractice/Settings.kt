package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.mypractice.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        val navigationView = binding.btmNavMenu
        navigationView.selectedItemId = R.id.nav_settings
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
                startActivity(Intent(this, Appointments::class.java))
                return true}
            R.id.nav_settings ->{

                return true}
            else -> return false
        }
    }
}