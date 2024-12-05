package com.example.mobileappscw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class AccountScreen : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_screen)

        //the button for changing profile picture
        val pfpButton = findViewById<ImageButton>(R.id.pfpButton)
        pfpButton.setOnClickListener{_ -> changePfp()}




        lateinit var navBarIntent: Intent
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_bar_account)
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
    }

    //TODO: implement this later if there is time
    private fun changePfp() {
        //asks user to select picture from gallery
        val uri = null
        //changes the users picture in the firebase database
        val currentUser = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }
        //confirms success to the user?
    }
}