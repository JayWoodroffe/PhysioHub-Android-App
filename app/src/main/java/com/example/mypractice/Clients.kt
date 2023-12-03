package com.example.mypractice

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.Query

class Clients : AppCompatActivity() {
    private lateinit var binding: ActivityClientsBinding
    private lateinit var  adapter: SearchClientRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private var query: Query? = null
    private var certId: Int? = null
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

        setupDoctorCertId {
            // This block is executed once the certId is obtained
            setupSearchRecyclerView("")
        }


        binding.btnSearchClient.setOnClickListener {
            val searchTerm = binding.etSearchClient.text.toString()
            setupSearchRecyclerView(searchTerm)

        }

        binding.btnAddNew.setOnClickListener {
            val intent = Intent(this, AddClient::class.java)
            Log.d("Tag", "doc id to send to new act: " + certId)
            intent.putExtra("docId", certId)
            startActivity(intent)
        }

    }
    private fun setupDoctorCertId(callback: () -> Unit) {
        FirebaseUtil.getDoctorCertIdByEmail { obtainedCertId ->
            if (obtainedCertId != null) {
                certId = obtainedCertId.toInt()
            }
            callback.invoke() // Call the callback once the certId is obtained
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

    private fun setupSearchRecyclerView(searchTerm: String) {
        //adds the clients to the recycler view
        Log.d("filter", "docId: $certId")

        val query = FirebaseUtil.getClientQuery(certId, searchTerm)

        val options = FirestoreRecyclerOptions.Builder<ClientModel>()
            .setQuery(query, ClientModel::class.java)
            .build()

        adapter = SearchClientRecyclerAdapter(options, applicationContext)
        binding.clientRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.clientRecyclerView.adapter = adapter

        // Set onItemClick listener
        adapter.onItemClick = { position, clientId ->
            val selectedClientModel = adapter.getItem(position)

            //  pass the selected client information to the ClientDetails activity
            val intent = Intent(this, ClientDetails::class.java).apply {
                putExtra("clientId", clientId)
                // Add other details as needed
            }
            startActivity(intent)
        }

        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (::adapter.isInitialized) {
            adapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::adapter.isInitialized) {
            adapter.stopListening()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.startListening()
        }
    }




}

