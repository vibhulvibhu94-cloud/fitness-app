package com.elitecoach.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getsharedPreferences("elite_coach_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_PROFILE = "user_profile"
        private const val KEY_WORKOUT_PLAN = "workout_plan"
        private const val KEY_DIET_PLAN = "diet_plan"
        private const val KEY_DAILY_LOGS_PREFIX = "daily_log_"
    }
    
    // Profile
    fun saveProfile(profile: UserProfile) {
        prefs.edit().putString(KEY_PROFILE, gson.toJson(profile)).apply()
    }
    
    fun loadProfile(): UserProfile? {
        val json = prefs.getString(KEY_PROFILE, null) ?: return null
        return try {
            gson.fromJson(json, UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    // Workout Plan
    fun saveWorkoutPlan(plan: WorkoutPlan) {
        prefs.edit().putString(KEY_WORKOUT_PLAN, gson.toJson(plan)).apply()
    }
    
    fun loadWorkoutPlan(): WorkoutPlan? {
        val json = prefs.getString(KEY_WORKOUT_PLAN, null) ?: return null
        return try {
            gson.fromJson(json, WorkoutPlan::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    // Diet Plan
    fun saveDietPlan(plan: DietPlan) {
        prefs.edit().putString(KEY_DIET_PLAN, gson.toJson(plan)).apply()
    }
    
    fun loadDietPlan(): DietPlan? {
        val json = prefs.getString(KEY_DIET_PLAN, null) ?: return null
        return try {
            gson.fromJson(json, DietPlan::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    // Daily Logs
    fun saveDailyLog(date: String, log: DailyLog) {
        prefs.edit().putString("$KEY_DAILY_LOGS_PREFIX$date", gson.toJson(log)).apply()
    }
    
    fun loadDailyLog(date: String): DailyLog? {
        val json = prefs.getString("$KEY_DAILY_LOGS_PREFIX$date", null) ?: return null
        return try {
            gson.fromJson(json, DailyLog::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun getAllDailyLogs(): Map<String, DailyLog> {
        val logs = mutableMapOf<String, DailyLog>()
        prefs.all.forEach { (key, value) ->
            if (key.startsWith(KEY_DAILY_LOGS_PREFIX) && value is String) {
                val date = key.removePrefix(KEY_DAILY_LOGS_PREFIX)
                try {
                    val log = gson.fromJson(value, DailyLog::class.java)
                    logs[date] = log
                } catch (e: Exception) {
                    // Skip invalid logs
                }
            }
        }
        return logs
    }
    
    // Clear all data
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
