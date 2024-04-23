package com.example.mypractice.data

import android.util.Log
import com.example.mypractice.model.ExerciseModel
import com.google.firebase.firestore.FirebaseFirestore

object ExerciseDataAccess {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("exercises")
    fun getExercisesForClient(clientId: String, callback: (List<ExerciseModel>)-> Unit) {
        val exercises = mutableListOf<ExerciseModel>()

        try {
            collection.whereEqualTo("clientId", clientId).get().addOnSuccessListener { results ->
                for (document in results.documents) {
                    val id = document.getLong("id")?.toInt()?:0
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val sets = document.getLong("sets")?.toInt() ?: 0
                    val reps = document.getLong("reps")?.toInt() ?: 0
                    val clientId = document.getString("clientId") ?: ""
                    val exercise = ExerciseModel(id, name, description, sets, reps, clientId)
                    exercises.add(exercise)
                }
                callback(exercises)
            }
                .addOnFailureListener { exception ->
                    exception.message?.let { Log.d("Exercises", it) }
                    callback(emptyList())
                }
        } catch (e: java.lang.Exception) {
            e.message?.let {
                Log.d("Exercises", it)
                callback(emptyList())
            }
        }
    }
}