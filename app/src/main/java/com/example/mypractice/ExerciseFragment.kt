package com.example.mypractice

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mypractice.adapter.ExerciseAdapter
import com.example.mypractice.adapter.ExerciseAdapterListener
import com.example.mypractice.data.ExerciseDataAccess
import com.example.mypractice.databinding.FragmentExerciseBinding
import com.example.mypractice.model.ExerciseModel

class ExerciseFragment : Fragment() , ExerciseAdapterListener {
    private lateinit var gridView: GridView
    private lateinit var binding: FragmentExerciseBinding
    private lateinit var clientId:String
    private var retiredMode: Boolean = false
    private var editMode: Boolean = false

    //list of exercise data
    private val exercises = mutableListOf<ExerciseModel>()
    private lateinit var exerciseAdapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentExerciseBinding.inflate(inflater, container, false)

        val rootView = binding.root
        // Inflate the layout for this fragment
        gridView =binding.gridView

        exerciseAdapter = ExerciseAdapter(requireContext(), exercises, this)
        gridView.adapter = exerciseAdapter

        displayActiveExercises()

        //bottom navigation handling
        val btmView = binding.btmExerciseMenu
        btmView.setOnItemSelectedListener { item: MenuItem -> handleNavigationItemSelected(item)}

        val btmRetiredMenu = binding.btmRetiredExerciseMenu
        btmRetiredMenu.setOnItemSelectedListener { item:MenuItem-> handleRetiredNavigationItemSelected(item) }

        //task bar binding for menu
        binding.taskBar.ivExercisesMenu.setOnClickListener {

            val popupMenu = PopupMenu(requireActivity(),  binding.taskBar.ivExercisesMenu, Gravity.END)

            popupMenu.menu.add(Menu.NONE, R.id.menu_edit, Menu.NONE, "Edit")
            popupMenu.menu.add(Menu.NONE, R.id.menu_add, Menu.NONE, "Add")
            popupMenu.menu.add(Menu.NONE, R.id.menu_retired, Menu.NONE, "Retired")

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        Toast.makeText(requireActivity(),  "Edit", Toast.LENGTH_SHORT).show()
                        selectActiveExercises()
                        true
                    }
                    R.id.menu_add -> {
                        // Handle Add option click
                        editModeOff()
                        startNewExerciseActivity()
//                        showDropDownMenu()
                        Toast.makeText(requireActivity(),  "Add", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.menu_retired -> {
                        // Handle Retired option click
                        retiredMode= true
                        editModeOff()
                        displayRetiredExercises()
                        Toast.makeText(requireActivity(),  "Retired", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        //binding for select all cb
        binding.taskBar.cbSelectAll.setOnCheckedChangeListener{ _, isChecked->
            if(binding.taskBar.selectAllContainer.visibility == View.VISIBLE){
                if(isChecked)
                {
                    selectAll()
                }
                else
                {
                    deselectAll()
                }
            }
        }


        return rootView
    }


    private fun fadeInOutAnimation(view: View) {

        val initialY = view.translationY +400
        val newY = initialY - 400 // Adjust the value as needed

        val flyUp = ObjectAnimator.ofFloat(view, "translationY", initialY, newY)
        flyUp.duration = 300

        val animSet = AnimatorSet()
        animSet.play(flyUp)
        animSet.start()
    }


    private fun setExercises(exercises: List<ExerciseModel>)
    {
        this.exercises.clear()
        this.exercises.addAll(exercises)
        fadeInOutAnimation(gridView)
        exerciseAdapter.notifyDataSetChanged()
    }

    fun toggleSelectionMode() {
        Log.d("Selection", "Fragment reached")
        exerciseAdapter.toggleSelectionMode()
    }


    private fun showDropDownMenu()
    {
        val popupMenu = PopupMenu(requireActivity(), binding.taskBar.ivExercisesMenu, Gravity.END)
//            val inflater: MenuInflater = popupMenu.menuInflater
//            val customLayout = layoutInflater.inflate(R.layout.exercises_menu, null)

        popupMenu.menu.add(Menu.NONE, R.id.menu_addNew, Menu.NONE, "New")
        popupMenu.menu.add(Menu.NONE, R.id.menu_addExisting, Menu.NONE, "Pre-existing")

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                R.id.menu_addNew -> {true}
                R.id.menu_addExisting->{true}

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun handleNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_back ->{
                editModeOff()
                return true
            }
            R.id.nav_retire ->{
                retireSelectedExercises()
                //TODO add code to retire exercises
                return true}
            R.id.nav_delete ->{
                deleteConfirmed(requireActivity())
                {
                    deleteSelectedExercises()
                    //TODO add code to delte exercises
                }
                return true}
//            R.id.nav_settings ->{
//                startActivity(Intent(this, Settings::class.java))
//                return true}
            else -> return false
        }
    }

    private fun handleRetiredNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_back ->{
                editModeOff()
                binding.taskBar.retiredContainer.visibility= View.VISIBLE
                return true
            }
            R.id.nav_retire ->{
                unretireSelectedExercises()
                //TODO add code to retire exercises
                Log.d("Retired", "unretire")
                return true}
            R.id.nav_delete ->{
                deleteConfirmed(requireActivity())
                {
                    deleteSelectedExercises()
                    //TODO add code to delte exercises
                }
                return true}
//            R.id.nav_settings ->{
//                startActivity(Intent(this, Settings::class.java))
//                return true}
            else -> return false
        }
    }



    private fun displayRetiredExercises()
    {
        retiredMode = true
        deselectAll()
        
        binding.taskBar.ivExercisesMenu.visibility= View.GONE
        binding.taskBar.retiredContainer.visibility=View.VISIBLE
        var backButton = binding.taskBar.retiredContainer.findViewById<ImageView>(R.id.ivBack)
        backButton.setOnClickListener {

            displayActiveExercises()
        }
        ExerciseDataAccess.getRetiredExercisesForClient(clientId){ exerList: List<ExerciseModel> ->
            Log.d("Exercises", "# ${exerList.size}")
            setExercises(exerList)
            exerciseAdapter.notifyDataSetChanged()
        }
    }
    private fun displayActiveExercises()
    {
        editModeOff()
        retiredMode = false
        binding.taskBar.ivExercisesMenu.visibility= View.VISIBLE
        binding.taskBar.retiredContainer.visibility=View.GONE
        ExerciseDataAccess.getActiveExercisesForClient(clientId){ exerList: List<ExerciseModel> ->
            Log.d("Exercises", "# ${exerList.size}")
            setExercises(exerList)
            exerciseAdapter.notifyDataSetChanged()
        }
    }

    private fun deleteConfirmed(context: Context, onDeleteConfirmed: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete the selected exercises?")
            .setPositiveButton("Delete") { dialog, _ ->
                // Call the onDeleteConfirmed callback when the user confirms deletion
                onDeleteConfirmed()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    fun setSelectionMode(mode: Boolean)
    {
        exerciseAdapter.setSelectionMode(mode)
    }

    //gridview click events
    override fun onItemSelected(position: Int) {
        exerciseAdapter.toggleSelection(position)
    }

    override fun onItemLongClick(position: Int) {
        if(!retiredMode){
            selectActiveExercises()
        }
        else{
            selectRetiredExercises()
        }
        exerciseAdapter.toggleSelection(position)
    }

    override fun onAllItemsSelected() {
        binding.taskBar.cbSelectAll.isChecked= true
    }

    override fun onItemDeselected() {
        binding.taskBar.cbSelectAll.isChecked=false
    }

    override fun onEditClick(position:Int) {
        val exercise = exercises[position]
        val intent = Intent(requireContext(), NewExercise::class.java).apply{
            putExtra("ClientId", clientId)
            putExtra("EditMode", true)
            putExtra("ExerciseModel", exercise)
        }
        startActivity(intent)
    }

    fun setClientId(id: String)
    {
        clientId=id
    }

    fun selectAll()
    {
        exerciseAdapter.selectAll()
    }

    fun deselectAll()
    {
        exerciseAdapter.deselectAll()
    }


    private fun selectActiveExercises()
    {
        setSelectionMode(true)
        binding.taskBar.selectAllContainer.visibility=View.VISIBLE
        binding.btmExerciseMenu.visibility=View.VISIBLE
        editMode= true
    }



    private fun editModeOff()
    {
        deselectAll()
        setSelectionMode(false)
        binding.taskBar.selectAllContainer.visibility=View.GONE
        binding.taskBar.cbSelectAll.isChecked=false
        binding.btmExerciseMenu.visibility=View.GONE
        binding.btmRetiredExerciseMenu.visibility= View.GONE
        editMode = false
    }
    fun selectRetiredExercises(){
        setSelectionMode(true)
        binding.taskBar.retiredContainer.visibility= View.GONE
        binding.btmRetiredExerciseMenu.visibility= View.VISIBLE

    }

    private fun startNewExerciseActivity()
    {
        val intent = Intent(requireContext(), NewExercise::class.java)
        intent.putExtra("ClientId", clientId)
        startActivity(intent)
    }

    private fun retireSelectedExercises()
    {
        val selected = exerciseAdapter.getSelectedExercises()
        ExerciseDataAccess.updateRetiredField(selected, true,
            onSuccess = {
                exerciseAdapter.deselectAll()
                setSelectionMode(false)
                displayActiveExercises()
            },
            onFailure = {e->
                Log.d("Exercise", "retire failed:  $e")
            })
    }

    private fun unretireSelectedExercises()
    {
        val selected =exerciseAdapter.getSelectedExercises()
        ExerciseDataAccess.updateRetiredField(selected, false,
            onSuccess = {exerciseAdapter.deselectAll()
                setSelectionMode(false)

                displayRetiredExercises()
            },
            onFailure = {e->
                Log.d("Exercise", "unretire failed:  $e")
            })
    }

    private fun deleteSelectedExercises()
    {
        val selected = exerciseAdapter.getSelectedExercises()
        ExerciseDataAccess.deleteExercises(selected,
            onSuccess = {
                exerciseAdapter.deselectAll()
                setSelectionMode(false)
                if(retiredMode)
                {
                    displayRetiredExercises()
                }
                else{
                    displayActiveExercises()
                }
            },
            onFailure = {e->
                Log.d("Exercise", "unretire failed:  $e")
            })
    }

}