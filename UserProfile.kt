package com.elitecoach.app.data

data class UserProfile(
    val id: String,
    val displayName: String,
    val height: Int, // cm
    val weight: Int, // kg
    val age: Int,
    val activityLevel: String, // low, medium, high
    val calculatedBodyType: String,
    val goalType: String, // athletic, lean, muscular, strong
    val trainingMode: String, // home, gym
    val dietType: String, // vegetarian, non-vegetarian
    var currentStreak: Int = 0,
    var bestStreak: Int = 0,
    var totalDaysCompleted: Int = 0,
    var badges: List<String> = emptyList(),
    val createdAt: String,
    var lastCompletedTimestamp: String? = null
)
