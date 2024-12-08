package com.example.mobileappscw


import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappscw.database.SqliteDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class QuizScreen : AppCompatActivity() {

    private lateinit var difficultyText : TextView
    private lateinit var countText : TextView
    private lateinit var typeText : TextView
    private lateinit var categoryText : TextView
    private lateinit var db : SqliteDatabase

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_screen)

        currentUser = auth.currentUser
        db = SqliteDatabase(this)
        val userPreferences = db.findUserPrefs(currentUser!!.email.toString())
        difficultyText = findViewById(R.id.questionDifficulty)
        val difficultyPref = userPreferences!!.difficulty
        countText = findViewById(R.id.questionCount)
        val countPref = userPreferences.count
        typeText = findViewById(R.id.questionType)
        val typePref = userPreferences.type
        categoryText = findViewById(R.id.questionCategory)
        val categoryPref = userPreferences.category

        difficultyText.text = String.format(getString(R.string.question_difficulty), difficultyPref)
        countText.text = String.format(getString(R.string.count_pref), countPref)
        typeText.text = String.format(getString(R.string.question_type), typePref)
        categoryText.text = String.format(getString(R.string.question_categories), categoryPref)

        lateinit var navBarIntent: Intent
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_bar_quiz)
        bottomNavigationView.setOnItemReselectedListener() {
            when (it.itemId) {
                R.id.homeButton -> navBarIntent = Intent(this, HomeScreen::class.java)
                R.id.alarmButton -> navBarIntent = Intent(this, AlarmScreen::class.java)
                R.id.profileButton -> navBarIntent = Intent(this, AccountScreen::class.java)
                R.id.settingsButton -> navBarIntent = Intent(this, SettingsScreen::class.java)
                R.id.quizButton -> navBarIntent = Intent(this, QuizScreen::class.java)
            }
            startActivity(navBarIntent)
        }

        //
    }
}