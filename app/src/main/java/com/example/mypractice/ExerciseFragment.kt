package com.example.mypractice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import com.example.mypractice.R
import com.example.mypractice.adapter.ExerciseAdapter
import com.example.mypractice.model.ExerciseModel

class ExerciseFragment : Fragment() {
    private lateinit var gridView: GridView

    //list of exercise data
    private val exercises = mutableListOf<ExerciseModel>()
    private lateinit var exerciseAdapter: ExerciseAdapter

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)
        gridView = view.findViewById(R.id.gridView)

        exerciseAdapter = ExerciseAdapter(requireContext(), exercises)
        gridView.adapter = exerciseAdapter
//        initializeGridView()

//        gridView.setOnItemLongClickListener { _, view, position, _ ->
//            exerciseAdapter.toggleSelectionMode()
//            exerciseAdapter.toggleSelection(position)
//            true
//        }

        return view
    }

    // Populate the grid view with exercises
//    private fun initializeGridView() {
//
//        // Set up the grid adapter
//        val gridAdapter = ExerciseAdapter(requireContext(), exercises)
//        gridView.adapter = gridAdapter
//
//        // Set item click listener
//        gridView.onItemClickListener =
//            AdapterView.OnItemClickListener { _, _, position, _ ->
//                // Handle item click here
//                val selectedExercise = exercises[position]
//                // Do something with the selected exercise
//            }
//    }


    fun setExercises(exercises: List<ExerciseModel>)
    {
        this.exercises.clear()
        this.exercises.addAll(exercises)
        exerciseAdapter.notifyDataSetChanged()
    }

    fun toggleSelectionMode() {
        Log.d("Selection", "Fragment reached")
        exerciseAdapter.toggleSelectionMode()
    }

    fun setSelectionMode(mode: Boolean)
    {
        exerciseAdapter.setSelectionMode(mode)
    }

    fun selectAll()
    {
        exerciseAdapter.selectAll()
    }

    fun deselectAll()
    {
        exerciseAdapter.deselectAll()
    }

}