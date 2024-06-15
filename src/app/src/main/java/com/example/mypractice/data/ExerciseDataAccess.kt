package com.example.mypractice.data

import android.util.Log
import com.example.mypractice.model.ExerciseModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
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
        var completedOperations = 0 // Counter for tracking completed operations
        val totalOperations = selectedIds.size // Total number of operations
        var hasFailed = false // Flag to track if any operation has failed

        for (exerciseId in selectedIds) {
            val query = collection.whereEqualTo("id", exerciseId) // Query to find the exercise by id
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0] // Get the first document
                        val exerciseRef = documentSnapshot.reference // Get the reference to the document
                        exerciseRef.update("retired", retired) // Update the "retired" field
                            .addOnSuccessListener {
                                completedOperations++ // Increment the completed operations counter
                                if (completedOperations == totalOperations && !hasFailed) {
                                    onSuccess() // Call onSuccess() only if all operations are completed and none failed
                                }
                            }
                            .addOnFailureListener { e ->
                                if (!hasFailed) {
                                    hasFailed = true // Set the failure flag to true
                                    onFailure(e) // Call onFailure() if an update operation fails
                                }
                            }
                    } else {
                        Log.d("Firestore", "No document found with id: $exerciseId")
                        completedOperations++ // Increment the completed operations counter even if no document is found
                        if (completedOperations == totalOperations && !hasFailed) {
                            onSuccess() // Call onSuccess() if all operations are completed and none failed
                        }
                    }
                }
                .addOnFailureListener { e ->
                    if (!hasFailed) {
                        hasFailed = true // Set the failure flag to true
                        onFailure(e) // Call onFailure() if the query fails
                    }
                }
        }
    }


    //deletes exercises from a list of IDs sent from exercise fragment
    fun deleteExercises(ids: List<Int>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val batch = db.batch()
        val documentReferences = mutableListOf<DocumentReference>()


        for (id in ids) {
            val query = collection.whereEqualTo("id", id)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        val exerciseRef = documentSnapshot.reference
                        documentReferences.add(documentSnapshot.reference)
                    } else {
                        Log.d("Firestore", "No document found with id: $id")
                    }
                    // Once all references are collected, commit the batch
                    if (documentReferences.size == ids.size) {
                        documentReferences.forEach { ref ->
                            batch.delete(ref)
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure(e)
                            }
                    }

                }
                .addOnFailureListener { e ->
                    onFailure(e)
                    return@addOnFailureListener
                }
        }
    }


    fun updateExercise(exercise: ExerciseModel, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val query = collection.whereEqualTo("id", exercise.id)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val exerciseRef = documentSnapshot.reference
                    // Now you have the reference to the document with the specified ID field
                    // Update the document using this reference
                    exerciseRef.set(exercise, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("Exercise", "Exercise updated successfully")
                            onSuccess()
                        }
                        .addOnFailureListener { e->
                            Log.e("Exercise", "Error updating exercise: $e")
                            onFailure(e)
                        }
                } else {
                    // No document found with the specified id
                    Log.d("Firestore", "No document found with id: ${exercise.id}")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                onFailure(e)
            }
    }

}