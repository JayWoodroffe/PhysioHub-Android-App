package com.example.mypractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mypractice.data.DoctorDataHolder
import com.example.mypractice.data.ExerciseDataAccess
import com.example.mypractice.databinding.ActivityNewExerciseBinding
import com.example.mypractice.model.ExerciseModel

class NewExercise : AppCompatActivity() {
    private lateinit var binding: ActivityNewExerciseBinding
    private lateinit var clientId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clientId = intent.getStringExtra("ClientId").toString()
        setUpNumberPickers()
        binding.btnCreate.setOnClickListener {
            createExercise()
        }

        binding.imBack.setOnClickListener {
            openPreviousActivity()
        }
    }

    private fun createExercise()
    {
        var name = binding.etName.text.toString()
        var description = binding.etDescription.text.toString()
        var sets = binding.npSets.value
        var reps = binding.npReps.value
        var doctorId = DoctorDataHolder.getLoggedInDoctor()?.certId



        var largest = 0
        ExerciseDataAccess.getMaxExerciseId(
            { largestId ->
                largest = largestId

                if(name != null && description != null&& clientId!=null&&doctorId!=null)
                {
                    val newExercise = ExerciseModel(
                        id = largest+1, // Placeholder ID, it will be assigned by Firestore
                        name = name,
                        description = description,
                        sets = sets,
                        reps = reps,
                        clientId = clientId,
                        doctorId = doctorId, // Your doctor ID
                        retired = false
                    )

                    ExerciseDataAccess.createNewExercise(
                        newExercise,
                        onSuccess = {
                            // Success callback
                            Log.d("Exercise", "Exercise added successfully")
                            openPreviousActivity()
                        },
                        onFailure = { e ->
                            // Failure callback
                            Log.d("Exercise", "Error adding exercise: $e")
                        })
                }
            },
            {
                Log.e("Exercise", "Failed to get largest ID")
            }
        )


    }

    private fun setUpNumberPickers()
    {
        binding.npSets.minValue= 1
        binding.npSets.maxValue= 10
        binding.npSets.value=3

        binding.npReps.minValue= 1
        binding.npReps.maxValue= 20
        binding.npReps.value= 10
    }

    private fun openPreviousActivity()
    {
        val intent = Intent(this, ClientDetails::class.java).apply {
            putExtra("clientId", clientId)
        }
        startActivity(intent)
    }
}