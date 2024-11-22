package com.example.mobileappscw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        val loginButton = findViewById<Button>(R.id.signInButton)
        loginButton.setOnClickListener{_ -> launchHome()}
    }

    private fun launchHome() {
        val homeIntent = Intent(this, HomeScreen::class.java)
        startActivity(homeIntent)
    }
}