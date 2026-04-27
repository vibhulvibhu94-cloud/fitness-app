package com.elitecoach.app.engine

import com.elitecoach.app.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

object FitnessEngine {
    
    // Body type calculation
    fun calculateBodyType(height: Int, weight: Int, activityLevel: String): String {
        val heightM = height / 100.0
        val bmi = weight / (heightM.pow(2))
        
        return when {
            bmi < 18.5 -> "Ectomorph"
            bmi < 25.0 -> if (activityLevel == "high") "Mesomorph" else "Healthy"
            bmi < 30.0 -> if (activityLevel == "high") "Mesomorph" else "Overweight"
            bmi < 35.0 -> "Obese"
            else -> "Extremely Obese"
        }
    }
    
    fun getBodyTypeDescription(bodyType: String): String {
        return when (bodyType) {
            "Ectomorph" -> "Lean build, fast metabolism. Focus: gain strength and muscle."
            "Mesomorph" -> "Athletic build, gains muscle easily. Focus: maintain performance."
            "Healthy" -> "Balanced physique, good baseline. Focus: consistent training."
            "Overweight" -> "Above ideal weight. Focus: fat loss with strength retention."
            "Obese" -> "High body fat. Focus: sustainable fat loss and mobility."
            "Extremely Obese" -> "Very high body fat. Prioritize health and gradual progress."
            else -> "Unknown body type. Continue with custom plan."
        }
    }
    
    // Completion score calculation
    fun calculateCompletionScore(log: DailyLog, exerciseCount: Int): Int {
        var score = 0
        
        // Warmup: 10%
        if (log.warmupCompleted) score += 10
        
        // Cardio: 15%
        if (log.cardioCompleted) score += 15
        
        // Stretching: 10%
        if (log.stretchingCompleted) score += 10
        
        // Exercises: 40% (scaled by completion)
        val completedExercises = log.exercisesCompleted.values.count { it }
        if (exerciseCount > 0) {
            score += (40.0 * completedExercises / exerciseCount).toInt()
        }
        
        // Steps: 25%
        if (log.steps >= log.stepGoal) score += 25
        
        return score.coerceIn(0, 100)
    }
    
    // Streak logic
    fun isStreakKept(log: DailyLog): Boolean {
        return log.completionScore >= 80
    }
    
    fun checkAndUpdateStreak(profile: UserProfile, log: DailyLog): Pair<UserProfile, DailyLog> {
        val updatedLog = log.copy()
        val updatedProfile = profile.copy()
        
        updatedLog.streakKept = isStreakKept(updatedLog)
        
        if (updatedLog.locked) {
            if (updatedLog.streakKept) {
                updatedProfile.currentStreak += 1
                updatedProfile.totalDaysCompleted += 1
                if (updatedProfile.currentStreak > updatedProfile.bestStreak) {
                    updatedProfile.bestStreak = updatedProfile.currentStreak
                }
                updatedProfile.lastCompletedTimestamp = getCurrentTimestamp()
            } else {
                updatedProfile.currentStreak = 0
            }
        }
        
        return Pair(updatedProfile, updatedLog)
    }
    
    // Generate workout plan
    fun generateWorkoutPlan(bodyType: String, goalType: String, trainingMode: String): WorkoutPlan {
        val exercises = when (trainingMode) {
            "gym" -> generateGymExercises(bodyType, goalType)
            else -> generateHomeExercises(bodyType, goalType)
        }
        
        val planName = when (goalType) {
            "lean" -> "Fat Loss Protocol"
            "muscular" -> "Hypertrophy Protocol"
            "strong" -> "Strength Protocol"
            else -> "Athletic Protocol"
        }
        
        return WorkoutPlan(
            name = planName,
            weeklySchedule = "6 days/week",
            warmup = "Dynamic stretching, arm circles, leg swings, light cardio",
            stretching = "Hip flexor stretch, hamstring stretch, shoulder stretch, spinal twist",
            exercises = exercises
        )
    }
    
    private fun generateHomeExercises(bodyType: String, goalType: String): List<Exercise> {
        return listOf(
            Exercise("pushups", "Push-ups", 3, "12-15", "Chest, triceps, shoulders"),
            Exercise("squats", "Bodyweight Squats", 4, "20", "Legs, glutes, core"),
            Exercise("plank", "Plank Hold", 3, "60s", "Core stability"),
            Exercise("lunges", "Lunges", 3, "12/leg", "Legs, balance"),
            Exercise("burpees", "Burpees", 3, "10", "Full body cardio"),
            Exercise("mountain_climbers", "Mountain Climbers", 3, "20", "Core, cardio")
        )
    }
    
    private fun generateGymExercises(bodyType: String, goalType: String): List<Exercise> {
        return listOf(
            Exercise("bench_press", "Bench Press", 4, "8-10", "Chest, triceps"),
            Exercise("squats_barbell", "Barbell Squats", 4, "8-10", "Legs, core"),
            Exercise("deadlift", "Deadlift", 3, "6-8", "Back, legs, grip"),
            Exercise("overhead_press", "Overhead Press", 3, "8-10", "Shoulders, triceps"),
            Exercise("rows", "Barbell Rows", 4, "10-12", "Back, biceps"),
            Exercise("pull_ups", "Pull-ups", 3, "AMRAP", "Back, biceps")
        )
    }
    
    // Generate diet plan
    fun generateDietPlan(bodyType: String, goalType: String, dietType: String): DietPlan {
        val weeklyMeals = List(7) { day -> generateDailyMeal(dietType, day) }
        val focus = when (goalType) {
            "lean" -> "High protein, moderate carbs"
            "muscular" -> "High protein, high calories"
            "strong" -> "Balanced macros, high protein"
            else -> "Balanced nutrition"
        }
        
        val avoid = when (dietType) {
            "vegetarian" -> listOf(
                "Meat, fish, poultry",
                "Processed foods",
                "Excessive sugar",
                "Trans fats",
                "Alcohol"
            )
            else -> listOf(
                "Processed foods",
                "Excessive sugar",
                "Trans fats",
                "Fast food",
                "Alcohol"
            )
        }
        
        return DietPlan(
            type = if (dietType == "vegetarian") "Vegetarian" else "Non-Vegetarian",
            focus = focus,
            weeklyMeals = weeklyMeals,
            avoid = avoid
        )
    }
    
    private fun generateDailyMeal(dietType: String, day: Int): Meal {
        return if (dietType == "vegetarian") {
            Meal(
                breakfast = "Oatmeal with berries, nuts, Greek yogurt",
                lunch = "Quinoa bowl with chickpeas, vegetables, tahini",
                dinner = "Tofu stir-fry with brown rice, mixed vegetables",
                snacks = "Protein shake, almonds, fruit"
            )
        } else {
            Meal(
                breakfast = "Eggs, whole grain toast, avocado, fruit",
                lunch = "Grilled chicken breast, sweet potato, broccoli",
                dinner = "Salmon, quinoa, asparagus, salad",
                snacks = "Protein shake, Greek yogurt, nuts"
            )
        }
    }
    
    // Badge checking
    fun checkBadges(profile: UserProfile, logs: Map<String, DailyLog>): List<String> {
        val newBadges = mutableListOf<String>()
        val existing = profile.badges.toSet()
        
        // First Step - Complete first day
        if ("first_step" !in existing && profile.totalDaysCompleted >= 1) {
            newBadges.add("first_step")
        }
        
        // 3-Day Warrior
        if ("three_day" !in existing && profile.currentStreak >= 3) {
            newBadges.add("three_day")
        }
        
        // Week Strong
        if ("week_strong" !in existing && profile.currentStreak >= 7) {
            newBadges.add("week_strong")
        }
        
        // Unbreakable
        if ("unbreakable" !in existing && profile.currentStreak >= 30) {
            newBadges.add("unbreakable")
        }
        
        // Century Club
        if ("century_club" !in existing && profile.totalDaysCompleted >= 100) {
            newBadges.add("century_club")
        }
        
        return newBadges
    }
    
    fun getBadgeName(badgeId: String): String {
        return when (badgeId) {
            "first_step" -> "First Step"
            "three_day" -> "3-Day Warrior"
            "week_strong" -> "Week Strong"
            "unbreakable" -> "Unbreakable"
            "century_club" -> "Century Club"
            else -> "Unknown"
        }
    }
    
    fun getBadgeIcon(badgeId: String): String {
        return when (badgeId) {
            "first_step" -> "🎯"
            "three_day" -> "🔥"
            "week_strong" -> "💪"
            "unbreakable" -> "🏆"
            "century_club" -> "👑"
            else -> "⭐"
        }
    }
    
    // Utility functions
    fun getRandomTagline(): String {
        val taglines = listOf(
            "Discipline equals freedom",
            "No excuses, only results",
            "Pain is temporary, pride forever",
            "The only bad workout is the one you didn't do",
            "Consistency is the secret",
            "Your body can stand almost anything. It's your mind you have to convince",
            "Success is what comes after you stop making excuses"
        )
        return taglines.random()
    }
    
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    
    fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
    }
    
    fun getDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0
    }
    
    fun getOrCreateTodayLog(existingLogs: Map<String, DailyLog>, exerciseIds: List<String>): DailyLog {
        val today = getTodayDate()
        return existingLogs[today] ?: DailyLog(
            date = today,
            steps = 0,
            stepGoal = 8000,
            exercisesCompleted = exerciseIds.associateWith { false }.toMutableMap()
        )
    }
}
