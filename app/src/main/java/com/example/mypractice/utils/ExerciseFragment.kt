package com.example.mypractice.utils

import android.os.Bundle
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

        initializeGridView()

        return view
    }

    // Populate the grid view with exercises
    private fun initializeGridView() {
        // Populate exercises (replace with your actual data)
        populateExercises()

        // Set up the grid adapter
        val gridAdapter = ExerciseAdapter(requireContext(), exercises)
        gridView.adapter = gridAdapter

        // Set item click listener
        gridView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                // Handle item click here
                val selectedExercise = exercises[position]
                // Do something with the selected exercise
            }
    }

    // Populate the exercises list with sample data (replace with your actual data retrieval logic)
    private fun populateExercises() {
        for (i in 1..10) {
            val exercise = ExerciseModel(
                id = i,
                name = "Exercise $i",
                description = "Description of Exercise $i",
                sets = 3,
                reps = 10,
                clientId = "client_$i"
            )
            exercises.add(exercise)
        }
    }


}