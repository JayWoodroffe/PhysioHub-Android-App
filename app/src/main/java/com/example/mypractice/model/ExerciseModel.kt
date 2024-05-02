package com.example.mypractice.model

data class ExerciseModel (
    val id: Int,
    val name: String,
    val description: String,
    val sets: Int,
    val reps: Int,
    val clientId: String,
    val doctorId: String,
    val retired: Boolean
)