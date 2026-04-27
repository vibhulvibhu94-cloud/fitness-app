package com.elitecoach.app.data

data class Exercise(
    val id: String,
    val name: String,
    val sets: Int,
    val reps: String,
    val notes: String
)

data class WorkoutPlan(
    val name: String,
    val weeklySchedule: String,
    val warmup: String,
    val stretching: String,
    val exercises: List<Exercise>
)
