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

class ExerciseFragment : Fragment(), ExerciseAdapterListener {

    private lateinit var binding: FragmentExerciseBinding
    private lateinit var exerciseAdapter: ExerciseAdapter

    private lateinit var clientId: String
    private var isRetiredMode = false
    private var isEditMode = false

    private val exercises = mutableListOf<ExerciseModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseBinding.inflate(inflater, container, false)
        setupGridView()
        setupBottomNavigation()
        setupTaskBarMenu()
        setupSelectAllCheckbox()

        displayActiveExercises()
        return binding.root
    }

    private fun setupGridView() {
        exerciseAdapter = ExerciseAdapter(requireContext(), exercises, this)
        binding.gridView.adapter = exerciseAdapter
    }

    private fun setupBottomNavigation() {
        binding.btmExerciseMenu.setOnItemSelectedListener { handleBottomNavigationItemSelected(it) }
        binding.btmRetiredExerciseMenu.setOnItemSelectedListener { handleRetiredBottomNavigationItemSelected(it) }
    }

    private fun setupTaskBarMenu() {
        binding.taskBar.ivExercisesMenu.setOnClickListener { showPopupMenu() }
    }

    private fun setupSelectAllCheckbox() {
        binding.taskBar.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (binding.taskBar.selectAllContainer.visibility == View.VISIBLE) {
                if (isChecked) selectAllExercises() else deselectAllExercises()
            }
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireActivity(), binding.taskBar.ivExercisesMenu, Gravity.END)
        popupMenu.menu.add(Menu.NONE, R.id.menu_edit, Menu.NONE, "Edit")
        popupMenu.menu.add(Menu.NONE, R.id.menu_add, Menu.NONE, "Add")
        popupMenu.menu.add(Menu.NONE, R.id.menu_retired, Menu.NONE, "Retired")
        popupMenu.setOnMenuItemClickListener { handlePopupMenuItemClick(it) }
        popupMenu.show()
    }

    private fun handlePopupMenuItemClick(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_edit -> {
                Toast.makeText(requireActivity(), "Edit", Toast.LENGTH_SHORT).show()
                enableEditMode()
                true
            }
            R.id.menu_add -> {
                disableEditMode()
                startNewExerciseActivity()
                true
            }
            R.id.menu_retired -> {
                isRetiredMode = true
                disableEditMode()
                displayRetiredExercises()
                true
            }
            else -> false
        }
    }

    private fun handleBottomNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_back -> {
                disableEditMode()
                true
            }
            R.id.nav_retire -> {
                retireSelectedExercises()
                true
            }
            R.id.nav_delete -> {
                showDeleteConfirmationDialog { deleteSelectedExercises() }
                true
            }
            else -> false
        }
    }

    private fun handleRetiredBottomNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_back -> {
                disableEditMode()
                binding.taskBar.retiredContainer.visibility = View.VISIBLE
                true
            }
            R.id.nav_retire -> {
                unretireSelectedExercises()
                true
            }
            R.id.nav_delete -> {
                showDeleteConfirmationDialog { deleteSelectedExercises() }
                true
            }
            else -> false
        }
    }

    private fun displayRetiredExercises() {
        isRetiredMode = true
        deselectAllExercises()
        binding.taskBar.ivExercisesMenu.visibility = View.GONE
        binding.taskBar.retiredContainer.visibility = View.VISIBLE
        binding.taskBar.retiredContainer.findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            displayActiveExercises()
        }
        ExerciseDataAccess.getRetiredExercisesForClient(clientId) { exerciseList ->
            updateExerciseList(exerciseList)
        }
    }

    private fun displayActiveExercises() {
        disableEditMode()
        isRetiredMode = false
        binding.taskBar.ivExercisesMenu.visibility = View.VISIBLE
        binding.taskBar.retiredContainer.visibility = View.GONE
        ExerciseDataAccess.getActiveExercisesForClient(clientId) { exerciseList ->
            updateExerciseList(exerciseList)
        }
    }

    private fun updateExerciseList(exerciseList: List<ExerciseModel>) {
        exercises.clear()
        exercises.addAll(exerciseList)
        animateGridView()
        exerciseAdapter.notifyDataSetChanged()
    }

    private fun animateGridView() {
        val initialY = binding.gridView.translationY + 400
        val newY = initialY - 400
        val flyUp = ObjectAnimator.ofFloat(binding.gridView, "translationY", initialY, newY)
        flyUp.duration = 300
        val animSet = AnimatorSet()
        animSet.play(flyUp)
        animSet.start()
    }

    private fun showDeleteConfirmationDialog(onConfirm: () -> Unit) {
        AlertDialog.Builder(requireActivity())
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete the selected exercises?")
            .setPositiveButton("Delete") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun startNewExerciseActivity() {
        val intent = Intent(requireContext(), NewExercise::class.java).apply {
            putExtra("ClientId", clientId)
        }
        startActivity(intent)
    }

    private fun retireSelectedExercises() {
        val selectedExercises = exerciseAdapter.getSelectedExercises() // Get the list of selected exercises
        binding.progressBar.visibility = View.VISIBLE // Show the progress bar

        ExerciseDataAccess.updateRetiredField(selectedExercises, true, onSuccess = {
            binding.progressBar.visibility = View.GONE // Hide the progress bar on success
            deselectAllExercises() // Deselect all exercises
            disableEditMode() // Disable edit mode
            displayActiveExercises() // Display the active exercises
        }, onFailure = { e ->
            binding.progressBar.visibility = View.GONE // Hide the progress bar on failure
            Log.d("Exercise", "Retire failed: $e") // Log the error
            // Optionally, show an error message to the user
        })
    }

    private fun unretireSelectedExercises() {
        val selectedExercises = exerciseAdapter.getSelectedExercises() // Get the list of selected exercises
        binding.progressBar.visibility = View.VISIBLE // Show the progress bar

        ExerciseDataAccess.updateRetiredField(selectedExercises, false, onSuccess = {
            binding.progressBar.visibility = View.GONE // Hide the progress bar on success
            deselectAllExercises() // Deselect all exercises
            disableEditMode() // Disable edit mode
            displayRetiredExercises() // Display the retired exercises
        }, onFailure = { e ->
            binding.progressBar.visibility = View.GONE // Hide the progress bar on failure
            Log.d("Exercise", "Unretire failed: $e") // Log the error
            // Optionally, show an error message to the user
        })
    }


    private fun deleteSelectedExercises() {
        val selectedExercises = exerciseAdapter.getSelectedExercises()
        binding.progressBar.visibility = View.VISIBLE
        ExerciseDataAccess.deleteExercises(selectedExercises, onSuccess = {
            binding.progressBar.visibility = View.GONE
            deselectAllExercises()
            disableEditMode()
            if (isRetiredMode) {
                displayRetiredExercises()
            } else {
                displayActiveExercises()
            }
        }, onFailure = { e ->
            Log.d("Exercise", "Delete failed: $e")
        })
    }

    override fun onItemSelected(position: Int) {
        exerciseAdapter.toggleSelection(position)
    }

    override fun onItemLongClick(position: Int) {
        if (isRetiredMode) {
            enableRetiredEditMode()
        } else {
            enableEditMode()
        }
        exerciseAdapter.toggleSelection(position)
    }

    override fun onAllItemsSelected() {
        binding.taskBar.cbSelectAll.isChecked = true
    }

    override fun onItemDeselected() {
        binding.taskBar.cbSelectAll.isChecked = false
    }

    override fun onEditClick(position: Int) {
        val exercise = exercises[position]
        val intent = Intent(requireContext(), NewExercise::class.java).apply {
            putExtra("ClientId", clientId)
            putExtra("EditMode", true)
            putExtra("ExerciseModel", exercise)
        }
        startActivity(intent)
    }

    fun setClientId(id: String) {
        clientId = id
    }

    fun selectAllExercises() {
        exerciseAdapter.selectAll()
    }

    fun deselectAllExercises() {
        exerciseAdapter.deselectAll()
    }

    private fun enableEditMode() {
        setSelectionMode(true)
        binding.taskBar.selectAllContainer.visibility = View.VISIBLE
        binding.btmExerciseMenu.visibility = View.VISIBLE
        isEditMode = true
    }

    private fun enableRetiredEditMode() {
        setSelectionMode(true)
        binding.taskBar.retiredContainer.visibility = View.GONE
        binding.btmRetiredExerciseMenu.visibility = View.VISIBLE
    }

    private fun disableEditMode() {
        deselectAllExercises()
        setSelectionMode(false)
        binding.taskBar.selectAllContainer.visibility = View.GONE
        binding.taskBar.cbSelectAll.isChecked = false
        binding.btmExerciseMenu.visibility = View.GONE
        binding.btmRetiredExerciseMenu.visibility = View.GONE
        isEditMode = false
    }

    fun setSelectionMode(mode: Boolean) {
        exerciseAdapter.setSelectionMode(mode)
    }
}