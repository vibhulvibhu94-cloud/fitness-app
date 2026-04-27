package com.elitecoach.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.elitecoach.app.data.*
import com.elitecoach.app.engine.FitnessEngine
import java.util.UUID

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var prefsManager: PreferencesManager
    
    // Form fields
    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var activityGroup: RadioGroup
    private lateinit var trainingGroup: RadioGroup
    private lateinit var goalGroup: RadioGroup
    private lateinit var dietGroup: RadioGroup
    private lateinit var submitButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        prefsManager = PreferencesManager(this)
        
        // Find views
        nameInput = findViewById(R.id.nameInput)
        ageInput = findViewById(R.id.ageInput)
        heightInput = findViewById(R.id.heightInput)
        weightInput = findViewById(R.id.weightInput)
        activityGroup = findViewById(R.id.activityGroup)
        trainingGroup = findViewById(R.id.trainingGroup)
        goalGroup = findViewById(R.id.goalGroup)
        dietGroup = findViewById(R.id.dietGroup)
        submitButton = findViewById(R.id.submitButton)
        
        submitButton.setOnClickListener {
            if (validateInputs()) {
                createProfileAndGoToDashboard()
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        val name = nameInput.text.toString().trim()
        val ageStr = ageInput.text.toString()
        val heightStr = heightInput.text.toString()
        val weightStr = weightInput.text.toString()
        
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val age = ageStr.toIntOrNull()
        if (age == null || age < 10 || age > 120) {
            Toast.makeText(this, "Age must be between 10-120", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val height = heightStr.toIntOrNull()
        if (height == null || height < 100 || height > 272) {
            Toast.makeText(this, "Height must be between 100-272 cm", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val weight = weightStr.toIntOrNull()
        if (weight == null || weight < 25 || weight > 635) {
            Toast.makeText(this, "Weight must be between 25-635 kg", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun createProfileAndGoToDashboard() {
        val name = nameInput.text.toString().trim()
        val age = ageInput.text.toString().toInt()
        val height = heightInput.text.toString().toInt()
        val weight = weightInput.text.toString().toInt()
        
        // Get selections
        val activityLevel = when (activityGroup.checkedRadioButtonId) {
            R.id.radioActivityLow -> "low"
            R.id.radioActivityHigh -> "high"
            else -> "medium"
        }
        
        val trainingMode = when (trainingGroup.checkedRadioButtonId) {
            R.id.radioGym -> "gym"
            else -> "home"
        }
        
        val goalType = when (goalGroup.checkedRadioButtonId) {
            R.id.radioLean -> "lean"
            R.id.radioMuscular -> "muscular"
            R.id.radioStrong -> "strong"
            else -> "athletic"
        }
        
        val dietType = when (dietGroup.checkedRadioButtonId) {
            R.id.radioNonVeg -> "non-vegetarian"
            else -> "vegetarian"
        }
        
        // Calculate body type
        val bodyType = FitnessEngine.calculateBodyType(height, weight, activityLevel)
        
        // Create profile
        val profile = UserProfile(
            id = UUID.randomUUID().toString(),
            displayName = name,
            height = height,
            weight = weight,
            age = age,
            activityLevel = activityLevel,
            calculatedBodyType = bodyType,
            goalType = goalType,
            trainingMode = trainingMode,
            dietType = dietType,
            currentStreak = 0,
            bestStreak = 0,
            totalDaysCompleted = 0,
            badges = emptyList(),
            createdAt = FitnessEngine.getCurrentTimestamp(),
            lastCompletedTimestamp = null
        )
        
        // Generate plans
        val workoutPlan = FitnessEngine.generateWorkoutPlan(bodyType, goalType, trainingMode)
        val dietPlan = FitnessEngine.generateDietPlan(bodyType, goalType, dietType)
        
        // Save everything
        prefsManager.saveProfile(profile)
        prefsManager.saveWorkoutPlan(workoutPlan)
        prefsManager.saveDietPlan(dietPlan)
        
        // Show body type result
        Toast.makeText(
            this,
            "Body Type: $bodyType\n${FitnessEngine.getBodyTypeDescription(bodyType)}",
            Toast.LENGTH_LONG
        ).show()
        
        // Navigate to main activity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
