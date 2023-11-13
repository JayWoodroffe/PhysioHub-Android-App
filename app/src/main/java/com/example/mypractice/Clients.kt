package com.example.mypractice

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypractice.adapter.SearchClientRecyclerAdapter
import com.example.mypractice.databinding.ActivityClientsBinding
import com.example.mypractice.model.ClientModel
import com.example.mypractice.utils.FirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class Clients : AppCompatActivity() {
    private lateinit var binding: ActivityClientsBinding
    private lateinit var  adapter: SearchClientRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hidings status and navigation bar
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        val navigationView = binding.btmNavMenu
        navigationView.selectedItemId = R.id.nav_clients
        navigationView.setOnItemSelectedListener { item: MenuItem -> handleNavigationItemSelected(item)}

        setupSearchRecyclerView("");

        binding.btnSearchClient.setOnClickListener {
            val searchTerm = binding.etSearchClient.text.toString()
            setupSearchRecyclerView(searchTerm)
        }

    }

    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_home ->{
                startActivity(Intent(this, Home::class.java))
                return true
            }
            R.id.nav_clients ->{

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

    fun setupSearchRecyclerView(searchTerm: String) {
        //adds the clients to the recycler view
        //TODO filter clients by doctorID
        val query = if (searchTerm.isNotEmpty()) {
            val searchTermLowerCase = searchTerm.lowercase()

            FirebaseUtil.allClientCollectionReference()
                .orderBy("name")
                .startAt(searchTermLowerCase)
                .endAt(searchTermLowerCase + "\uF8FF")
        } else {
            FirebaseUtil.allClientCollectionReference()
        }

        val options = FirestoreRecyclerOptions.Builder<ClientModel>()
            .setQuery(query, ClientModel::class.java)
            .build()

        adapter = SearchClientRecyclerAdapter(options, applicationContext)
        binding.clientRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.clientRecyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }




}