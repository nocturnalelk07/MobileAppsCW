package com.example.mobileappscw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappscw.database.SqliteDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class SettingsScreen : AppCompatActivity() {

    private lateinit var difficultyText : TextView
    private lateinit var countText : TextView
    private lateinit var typeText : TextView
    private lateinit var categoryText : TextView
    private lateinit var db : SqliteDatabase

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_screen)

        //gets the question preferences for the user
        currentUser = auth.currentUser
        db = SqliteDatabase(this)
        val userPreferences = db.findUserPrefs(currentUser!!.email.toString())
        difficultyText = findViewById(R.id.currentDifficultySettingsText)
        val difficultyPref = userPreferences!!.difficulty
        countText = findViewById(R.id.currentCountSettingsText)
        val countPref = userPreferences.count
        typeText = findViewById(R.id.currentTypeSettingsText)
        val typePref = userPreferences.type
        categoryText = findViewById(R.id.currentCategorySettingsText)
        val categoryPref = userPreferences.category

        //displays the user prefs
        difficultyText.text = String.format(getString(R.string.question_difficulty), difficultyPref)
        countText.text = String.format(getString(R.string.count_pref), countPref)
        typeText.text = String.format(getString(R.string.question_type), typePref)
        categoryText.text = String.format(getString(R.string.question_categories), categoryPref)

        //setup navigation bar
        lateinit var navBarIntent: Intent
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_bar_settings)
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
        //set spinners here
        //couldn't find any way to use this for creating an array adapter in kotlin so i had to use the awful hardcoded string
        //val countArray: IntArray = (1..maximumApiQuestions).toList().toIntArray()
        val countSpinner = findViewById<Spinner>(R.id.currentCountSettingsSpinner)
        val difficultySpinner = findViewById<Spinner>(R.id.currentDifficultySettingsSpinner)
        val typeSpinner = findViewById<Spinner>(R.id.currentTypeSettingsSpinner)
        val categorySpinner = findViewById<Spinner>(R.id.currentCategorySettingsSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.count,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            countSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.difficulty,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            difficultySpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        countSpinner.setSelection(countOffByOne(countPref))
        val changePrefsButton = findViewById<Button>(R.id.changePrefsButton)
        changePrefsButton.setOnClickListener() {_ -> updatePrefs(difficultySpinner.selectedItem.toString(),
            countSpinner.selectedItem.toString(), typeSpinner.selectedItem.toString(), categorySpinner.selectedItem.toString(), db)}

        Log.i("test306", "get question answer")
        db.getQuestionAnswer("1")
    }

    private fun updatePrefs(difficulty : String, count : String, type : String, category : String,
    db : SqliteDatabase) {
        Log.i("test306", difficulty)
        Log.i("test306", count)
        Log.i("test306", type)
        Log.i("test306", category)
        db.updatePreferences(currentUser!!.email.toString(), difficulty, count, type, category)
    }

    //fixes off by one error when converting count pref to array index also returns int
    private fun countOffByOne(count : String) : Int{
        return count.toInt()-1
    }
}