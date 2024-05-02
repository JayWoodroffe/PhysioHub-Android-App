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


        //setting up exercises tab
        //TODO get data from firestore
        val tabToSelect = binding.toggleButton.tabLayout.getTabAt(1)
        tabToSelect?.select()

        val bundle=Bundle()
        bundle.putString("clientID", clientId)


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


//        binding.taskBar.ivExercisesMenu.setOnClickListener {
//
//            val popupMenu = PopupMenu(this, binding.taskBar.ivExercisesMenu, Gravity.END)
////            val inflater: MenuInflater = popupMenu.menuInflater
////            val customLayout = layoutInflater.inflate(R.layout.exercises_menu, null)
//
//            popupMenu.menu.add(Menu.NONE, R.id.menu_edit, Menu.NONE, "Edit")
//            popupMenu.menu.add(Menu.NONE, R.id.menu_add, Menu.NONE, "Add")
//            popupMenu.menu.add(Menu.NONE, R.id.menu_retired, Menu.NONE, "Retired")
//
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.menu_edit -> {
//                        Toast.makeText(this@ClientDetails, "Edit", Toast.LENGTH_SHORT).show()
//                        editModeOn()
//
//                        true
//                    }
//                    R.id.menu_add -> {
//                        // Handle Add option click
//                        editModeOff()
//                        showDropDownMenu()
//                        Toast.makeText(this@ClientDetails, "Add", Toast.LENGTH_SHORT).show()
//                        true
//                    }
//                    R.id.menu_retired -> {
//                        // Handle Retired option click
//                        editModeOff()
//                        displayRetiredExercises()
//                        Toast.makeText(this@ClientDetails, "Retire", Toast.LENGTH_SHORT).show()
//                        true
//                    }
//                    else -> false
//                }
//            }
//            popupMenu.show()
//        }

//        //bottom navigation handling
//        val btmView = binding.btmExerciseMenu
//        btmView.setOnItemSelectedListener { item: MenuItem -> handleNavigationItemSelected(item)}

        //changing toggle bar
        binding.toggleButton.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab selection
                tab?.let {
                    // Check which tab is selected
                    when (it.text) {
                        "Exercises" -> {
                            // Show the ExerciseFragment

                            hidePopUp()
                            showExerciseFragment()
                        }
                        // Add more cases for other tabs if needed
                    }
                    when (it.text) {
                        "Chat" -> {
                            // Show the ExerciseFragment
                            hidePopUp()
                            hideExerciseFragment()
                        }
                        // Add more cases for other tabs if needed
                    }
                    when (it.text) {
                        "Info" -> {
                            // Show the ExerciseFragment
                            hideExerciseFragment()
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

//        binding.taskBar.cbSelectAll.setOnCheckedChangeListener{ _, isChecked->
//            if(binding.taskBar.selectAllContainer.visibility == View.VISIBLE){
//                if(isChecked)
//                {
//                    fragment.selectAll()
//                }
//                else
//                {
//                    fragment.deselectAll()
//                }
//                }
//        }


        //call button
        //TODO add call feature to chat area
    }

//    private fun editModeOn()
//    {
//        fragment.setSelectionMode(true)
//        binding.taskBar.selectAllContainer.visibility=View.VISIBLE
//        binding.btmExerciseMenu.visibility=View.VISIBLE
//    }
//
//    private fun editModeOff()
//    {
//        fragment.deselectAll()
//        fragment.setSelectionMode(false)
//        binding.taskBar.selectAllContainer.visibility=View.GONE
//        binding.taskBar.cbSelectAll.isChecked=false
//        binding.btmExerciseMenu.visibility=View.GONE
//    }
//    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId){
//            R.id.nav_back ->{
//                editModeOff()
//                return true
//            }
//            R.id.nav_retire ->{
//                //TODO add code to retire exercises
//                return true}
//            R.id.nav_delete ->{
//                deleteConfirmed(this)
//                {
//                    //TODO add code to delte exercises
//                }
//                return true}
//            R.id.nav_settings ->{
//                startActivity(Intent(this, Settings::class.java))
//                return true}
//            else -> return false
//        }
//    }

    private fun showExerciseFragment() {
        val taskbar: View = findViewById(R.id.taskBar)
        taskbar.visibility = View.VISIBLE

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.show(fragment)
        fragmentTransaction.commit()
    }

    private fun hideExerciseFragment() {
        val taskbar: View = findViewById(R.id.taskBar)
        taskbar.visibility = View.GONE

        // Remove the ExerciseFragment from the container
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.hide(fragment)
        fragmentTransaction.commit()
    }

    private fun showPopup(anchorView: TabLayout.Tab)
    {
        val popupInfoLayout: View = findViewById(R.id.popupInfo)
            popupInfoLayout.visibility = View.VISIBLE
    }

    private fun hidePopUp()
    {
        val popupInfoLayout: View = findViewById(R.id.popupInfo)
        popupInfoLayout.visibility = View.GONE
    }

//    private fun displayRetiredExercises()
//    {
//        binding.taskBar.retiredContainer.visibility=View.VISIBLE
//        binding.taskBar.ivBack.setOnClickListener {
//            displayActiveExercises()
//        }
//        ExerciseDataAccess.getRetiredExercisesForClient(clientId){ exerList: List<ExerciseModel> ->
//            Log.d("Exercises", "# ${exerList.size}")
//            fragment.setExercises(exerList)
//        }
//    }
//    private fun displayActiveExercises()
//    {
//        binding.taskBar.retiredContainer.visibility=View.GONE
//        ExerciseDataAccess.getActiveExercisesForClient(clientId){ exerList: List<ExerciseModel> ->
//            Log.d("Exercises", "# ${exerList.size}")
//            fragment.setExercises(exerList)
//        }
//    }

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

//    private fun showDropDownMenu()
//    {
//        val popupMenu = PopupMenu(this, binding.taskBar.ivExercisesMenu, Gravity.END)
////            val inflater: MenuInflater = popupMenu.menuInflater
////            val customLayout = layoutInflater.inflate(R.layout.exercises_menu, null)
//
//        popupMenu.menu.add(Menu.NONE, R.id.menu_addNew, Menu.NONE, "New")
//        popupMenu.menu.add(Menu.NONE, R.id.menu_addExisting, Menu.NONE, "Pre-existing")
//
//        popupMenu.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//
//                R.id.menu_addNew -> {true}
//                R.id.menu_addExisting->{true}
//
//                else -> false
//            }
//        }
//        popupMenu.show()
//    }
//    fun deleteConfirmed(context: Context, onDeleteConfirmed: () -> Unit) {
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle("Delete Confirmation")
//            .setMessage("Are you sure you want to delete the selected exercises?")
//            .setPositiveButton("Delete") { dialog, _ ->
//                // Call the onDeleteConfirmed callback when the user confirms deletion
//                onDeleteConfirmed()
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//        val dialog = builder.create()
//        dialog.show()
//    }

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