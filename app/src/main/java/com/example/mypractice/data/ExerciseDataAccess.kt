package com.example.mypractice.data

import android.util.Log
import com.example.mypractice.model.ExerciseModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

object ExerciseDataAccess {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("exercises")
    fun getActiveExercisesForClient(clientId: String, callback: (List<ExerciseModel>)-> Unit) {
        val exercises = mutableListOf<ExerciseModel>()

        try {
            collection.whereEqualTo("clientId", clientId)
                .whereEqualTo("retired", false)
                .get().addOnSuccessListener { results ->
                for (document in results.documents) {
                    val id = document.getLong("id")?.toInt()?:0
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val sets = document.getLong("sets")?.toInt() ?: 0
                    val reps = document.getLong("reps")?.toInt() ?: 0
                    val clientId = document.getString("clientId") ?: ""
                    val doctorId = document.getString("clientId") ?: ""
                    val retired = document.getBoolean("retired")?: false
                    val exercise = ExerciseModel(id, name, description, sets, reps, clientId, doctorId, retired)
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

    fun getRetiredExercisesForClient(clientId: String, callback: (List<ExerciseModel>)-> Unit) {
        val exercises = mutableListOf<ExerciseModel>()

        try {
            collection.whereEqualTo("clientId", clientId)
                .whereEqualTo("retired", true)
                .get().addOnSuccessListener { results ->
                    for (document in results.documents) {
                        val id = document.getLong("id")?.toInt()?:0
                        val name = document.getString("name") ?: ""
                        val description = document.getString("description") ?: ""
                        val sets = document.getLong("sets")?.toInt() ?: 0
                        val reps = document.getLong("reps")?.toInt() ?: 0
                        val clientId = document.getString("clientId") ?: ""
                        val doctorId = document.getString("clientId") ?: ""
                        val retired = document.getBoolean("retired") ?: true
                        val exercise = ExerciseModel(id, name, description, sets, reps, clientId, doctorId, retired)
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

    fun createNewExercise(exercise: ExerciseModel, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        collection.add(exercise)
            .addOnSuccessListener { documentReference ->
                // Exercise added successfully
                Log.d("Exercise", "added successfully")
                onSuccess()
            }
            .addOnFailureListener { e ->
                // Error adding exercise
                Log.d("Exercise", "Error adding exercise: $e")
                onFailure(e)
            }
    }

    fun getMaxExerciseId(callback: (Int) -> Unit, onFailure: () -> Unit) {
        collection.orderBy("id", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Get the largest ID from the first document
                    val largestId = documents.documents[0].get("id") as Long
                    callback(largestId.toInt())
                } else {
                    // Collection is empty, return -1
                    callback(-1)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Exercise", "Error getting max id", exception)
                onFailure()
            }
    }

    fun updateRetiredField(selectedIds: List<Int>, retired: Boolean, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        for (exerciseId in selectedIds) {
            val query = collection.whereEqualTo("id", exerciseId)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val exerciseRef = documentSnapshot.reference
                        // Update the field in the document
                        exerciseRef.update("retired", retired)
                            .addOnSuccessListener {
                                // Successfully updated the field
                                Log.d("Firestore", "Successfully updated retired field for exercise with ID: $exerciseId")
                            }
                            .addOnFailureListener { e ->
                                // Failed to update the field
                                Log.e("Firestore", "Failed to update retired field for exercise with ID: $exerciseId", e)
                                onFailure(e)
                            }
                    } else {
                        // No document with the specified id found
                        Log.d("Firestore", "No document found with id: $exerciseId")
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }
        onSuccess()
    }
    fun deleteExercises(ids: List<Int>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val batch = db.batch()

        for (id in ids) {
            val query = collection.whereEqualTo("id", id)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val exerciseRef = documentSnapshot.reference
                        batch.delete(exerciseRef)
                        //commit the batch immediately after each delete operation
                        batch.commit()
                            .addOnFailureListener { e ->
                                onFailure(e)
                                return@addOnFailureListener
                            }

                    } else {
                        // No document with the specified id found
                        Log.d("Firestore", "No document found with id: $id")
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                    return@addOnFailureListener
                }
        }


    }

}