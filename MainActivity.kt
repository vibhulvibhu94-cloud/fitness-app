package com.elitecoach.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elitecoach.app.data.*
import com.elitecoach.app.engine.FitnessEngine
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    
    private lateinit var prefsManager: PreferencesManager
    private lateinit var adView: AdView
    
    private var profile: UserProfile? = null
    private var todayLog: DailyLog? = null
    private var workoutPlan: WorkoutPlan? = null
    
    // UI Elements
    private lateinit var greetingText: TextView
    private lateinit var streakCount: TextView
    private lateinit var completionPercentage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var warmupCheckbox: CheckBox
    private lateinit var cardioCheckbox: CheckBox
    private lateinit var stretchingCheckbox: CheckBox
    private lateinit var exerciseRecyclerView: RecyclerView
    private lateinit var stepsInput: EditText
    private lateinit var saveButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefsManager = PreferencesManager(this)
        
        // Check if profile exists
        profile = prefsManager.loadProfile()
        if (profile == null) {
            // No profile, go to onboarding
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)
        
        // Initialize AdMob
        MobileAds.initialize(this) {}
        
        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        // Find views
        greetingText = findViewById(R.id.greetingText)
        streakCount = findViewById(R.id.streakCount)
        completionPercentage = findViewById(R.id.completionPercentage)
        progressBar = findViewById(R.id.progressBar)
        warmupCheckbox = findViewById(R.id.warmupCheckbox)
        cardioCheckbox = findViewById(R.id.cardioCheckbox)
        stretchingCheckbox = findViewById(R.id.stretchingCheckbox)
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView)
        stepsInput = findViewById(R.id.stepsInput)
        saveButton = findViewById(R.id.saveButton)
        adView = findViewById(R.id.adView)
        
        // Load data
        loadData()
        
        // Setup UI
        setupUI()
        
        // Load AdMob banner
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
    
    private fun loadData() {
        profile = prefsManager.loadProfile()!!
        workoutPlan = prefsManager.loadWorkoutPlan()
        
        // Generate workout plan if not exists
        if (workoutPlan == null) {
            workoutPlan = FitnessEngine.generateWorkoutPlan(
                profile!!.calculatedBodyType,
                profile!!.goalType,
                profile!!.trainingMode
            )
            prefsManager.saveWorkoutPlan(workoutPlan!!)
        }
        
        // Load or create today's log
        val allLogs = prefsManager.getAllDailyLogs()
        val exerciseIds = workoutPlan!!.exercises.map { it.id }
        todayLog = FitnessEngine.getOrCreateTodayLog(allLogs, exerciseIds)
    }
    
    private fun setupUI() {
        // Greeting
        greetingText.text = getString(R.string.greeting_hi) + " ${profile!!.displayName.uppercase()}"
        
        // Streak
        streakCount.text = profile!!.currentStreak.toString()
        
        // Update completion UI
        updateCompletionUI()
        
        // Checkboxes
        warmupCheckbox.isChecked = todayLog!!.warmupCompleted
        cardioCheckbox.isChecked = todayLog!!.cardioCompleted
        stretchingCheckbox.isChecked = todayLog!!.stretchingCompleted
        
        warmupCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (!todayLog!!.locked) {
                todayLog!!.warmupCompleted = isChecked
                updateLog()
            }
        }
        
        cardioCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (!todayLog!!.locked) {
                todayLog!!.cardioCompleted = isChecked
                updateLog()
            }
        }
        
        stretchingCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (!todayLog!!.locked) {
                todayLog!!.stretchingCompleted = isChecked
                updateLog()
            }
        }
        
        // Disable checkboxes if locked
        if (todayLog!!.locked) {
            warmupCheckbox.isEnabled = false
            cardioCheckbox.isEnabled = false
            stretchingCheckbox.isEnabled = false
        }
        
        // Exercise RecyclerView
        exerciseRecyclerView.layoutManager = LinearLayoutManager(this)
        exerciseRecyclerView.adapter = ExerciseAdapter(
            workoutPlan!!.exercises,
            todayLog!!.exercisesCompleted,
            todayLog!!.locked
        ) { exerciseId, checked ->
            todayLog!!.exercisesCompleted[exerciseId] = checked
            updateLog()
        }
        
        // Steps input
        stepsInput.setText(if (todayLog!!.steps > 0) todayLog!!.steps.toString() else "")
        stepsInput.isEnabled = !todayLog!!.locked
        
        // Save button
        updateSaveButton()
        saveButton.setOnClickListener {
            if (canCompletesProtocol()) {
                showLockConfirmation()
            }
        }
    }
    
    private fun updateLog() {
        // Update steps from input
        val stepsText = stepsInput.text.toString()
        todayLog!!.steps = stepsText.toIntOrNull() ?: 0
        
        // Calculate completion score
        todayLog!!.completionScore = FitnessEngine.calculateCompletionScore(
            todayLog!!,
            workoutPlan!!.exercises.size
        )
        
        todayLog!!.streakKept = FitnessEngine.isStreakKept(todayLog!!)
        
        // Check if all exercises done
        val allDone = workoutPlan!!.exercises.all { exercise ->
            todayLog!!.exercisesCompleted[exercise.id] == true
        }
        todayLog!!.workoutCompleted = allDone
        
        // Save to preferences
        prefsManager.saveDailyLog(todayLog!!.date, todayLog!!)
        
        // Update UI
        updateCompletionUI()
        updateSaveButton()
    }
    
    private fun updateCompletionUI() {
        completionPercentage.text = "${todayLog!!.completionScore}%"
        progressBar.progress = todayLog!!.completionScore
    }
    
    private fun canCompletesProtocol(): Boolean {
        val completedExercises = todayLog!!.exercisesCompleted.values.count { it }
        val requiredExercises = (workoutPlan!!.exercises.size * 0.8).toInt()
        return todayLog!!.steps >= todayLog!!.stepGoal && completedExercises >= requiredExercises
    }
    
    private fun updateSaveButton() {
        if (todayLog!!.locked) {
            saveButton.visibility = android.view.View.GONE
        } else {
            saveButton.isEnabled = canCompletesProtocol()
            saveButton.text = if (canCompletesProtocol()) {
                getString(R.string.save_and_lock)
            } else {
                getString(R.string.incomplete_protocol)
            }
        }
    }
    
    private fun showLockConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.last_chance))
            .setMessage(getString(R.string.lock_warning) + "\n\n" + getString(R.string.are_you_certain))
            .setPositiveButton(getString(R.string.save_protocol)) { _, _ ->
                lockDay()
            }
            .setNegativeButton(getString(R.string.go_back), null)
            .show()
    }
    
    private fun lockDay() {
        todayLog!!.locked = true
        
        // Update streak
        val (updatedProfile, updatedLog) = FitnessEngine.checkAndUpdateStreak(profile!!, todayLog!!)
        profile = updatedProfile
        todayLog = updatedLog
        
        // Check badges
        val allLogs = prefsManager.getAllDailyLogs()
        val newBadges = FitnessEngine.checkBadges(profile!!, allLogs)
        if (newBadges.isNotEmpty()) {
            profile!!.badges = profile!!.badges + newBadges
            showBadgeNotification(newBadges)
        }
        
        // Save everything
        prefsManager.saveProfile(profile!!)
        prefsManager.saveDailyLog(todayLog!!.date, todayLog!!)
        
        // Update UI
        streakCount.text = profile!!.currentStreak.toString()
        warmupCheckbox.isEnabled = false
        cardioCheckbox.isEnabled = false
        stretchingCheckbox.isEnabled = false
        stepsInput.isEnabled = false
        saveButton.visibility = android.view.View.GONE
        
        (exerciseRecyclerView.adapter as? ExerciseAdapter)?.setLocked(true)
        
        Toast.makeText(
            this,
            if (todayLog!!.streakKept) getString(R.string.day_complete_saved) 
            else getString(R.string.day_complete_missed),
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun showBadgeNotification(badges: List<String>) {
        val badgeNames = badges.joinToString(", ") { FitnessEngine.getBadgeName(it) }
        Toast.makeText(this, "🏆 New Badge(s): $badgeNames", Toast.LENGTH_LONG).show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_privacy -> {
                startActivity(Intent(this, PrivacyPolicyActivity::class.java))
                true
            }
            R.id.action_reset -> {
                showResetConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showResetConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.reset_data))
            .setMessage(getString(R.string.reset_confirmation))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                prefsManager.clearAll()
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    override fun onPause() {
        adView.pause()
        super.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        adView.resume()
    }
    
    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
}

// Exercise Adapter for RecyclerView
class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val completionMap: MutableMap<String, Boolean>,
    private var isLocked: Boolean,
    private val onCheckChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
    
    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.exerciseCheckbox)
        val nameText: TextView = view.findViewById(R.id.exerciseName)
        val setsRepsText: TextView = view.findViewById(R.id.exerciseSetsReps)
        val notesText: TextView = view.findViewById(R.id.exerciseNotes)
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercises[position]
        val isCompleted = completionMap[exercise.id] == true
        
        holder.nameText.text = exercise.name.uppercase()
        holder.setsRepsText.text = "${exercise.sets}x${exercise.reps}"
        holder.notesText.text = exercise.notes
        holder.checkbox.isChecked = isCompleted
        holder.checkbox.isEnabled = !isLocked
        
        if (isCompleted) {
            holder.nameText.paintFlags = holder.nameText.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            holder.nameText.alpha = 0.5f
        } else {
            holder.nameText.paintFlags = holder.nameText.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.nameText.alpha = 1.0f
        }
        
        holder.checkbox.setOnCheckedChangeListener { _, checked ->
            if (!isLocked) {
                onCheckChanged(exercise.id, checked)
                notifyItemChanged(position)
            }
        }
        
        holder.itemView.setOnClickListener {
            if (!isLocked) {
                holder.checkbox.isChecked = !holder.checkbox.isChecked
            }
        }
    }
    
    override fun getItemCount() = exercises.size
    
    fun setLocked(locked: Boolean) {
        isLocked = locked
        notifyDataSetChanged()
    }
}
