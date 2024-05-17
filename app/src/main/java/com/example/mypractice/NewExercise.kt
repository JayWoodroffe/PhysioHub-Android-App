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
    private  var editMode: Boolean = false
    private lateinit var exerciseModel: ExerciseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clientId = intent.getStringExtra("ClientId").toString()
        editMode = intent.getBooleanExtra("EditMode", false)
        Log.d("Exercise", "editmode: $editMode")
        setUpNumberPickers()
        if (editMode)
        {
            exerciseModel = intent.getSerializableExtra("ExerciseModel") as ExerciseModel
            populateFields(exerciseModel)
        }

        binding.btnCreate.setOnClickListener {
            if(editMode){
                updateExercise()
            }
            else
            {
                createExercise()
            }

        }

        binding.imBack.setOnClickListener {
            openPreviousActivity()
        }
    }

    private fun populateFields(exerciseModel: ExerciseModel)
    {
        binding.etName.setText( exerciseModel.name)
        binding.etDescription.setText(exerciseModel.description)
        binding.npSets.value = exerciseModel.sets
        binding.npReps.value = exerciseModel.reps

    }

    private fun updateExercise()
    {
        val name = binding.etName.text.toString()
        val description = binding.etDescription.text.toString()
        val sets = binding.npSets.value
        val reps = binding.npReps.value

        if (name.isNotBlank() && description.isNotBlank() && clientId.isNotBlank() && exerciseModel.doctorId != null) {

            exerciseModel.name = name
            exerciseModel.description = description
            exerciseModel.sets = sets
            exerciseModel.reps = reps

            ExerciseDataAccess.updateExercise(exerciseModel,
                onSuccess = {
                    // Success callback
                    Log.d("Exercise", "Exercise updated successfully")
                    openPreviousActivity()
                },
                onFailure = { e ->
                    // Failure callback
                    Log.d("Exercise", "Error updating exercise: $e")
                }
            )
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