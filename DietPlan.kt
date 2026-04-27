package com.elitecoach.app.data

data class Meal(
    val breakfast: String,
    val lunch: String,
    val dinner: String,
    val snacks: String
)

data class DietPlan(
    val type: String,
    val focus: String,
    val weeklyMeals: List<Meal>,
    val avoid: List<String>
)
