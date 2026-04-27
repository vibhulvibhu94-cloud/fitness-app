package com.elitecoach.app.data

data class DailyLog(
    val date: String, // yyyy-MM-dd
    var steps: Int = 0,
    val stepGoal: Int = 8000,
    var warmupCompleted: Boolean = false,
    var cardioCompleted: Boolean = false,
    var stretchingCompleted: Boolean = false,
    var exercisesCompleted: MutableMap<String, Boolean> = mutableMapOf(),
    var workoutCompleted: Boolean = false,
    var completionScore: Int = 0, // 0-100
    var streakKept: Boolean = false,
    var locked: Boolean = false
)
