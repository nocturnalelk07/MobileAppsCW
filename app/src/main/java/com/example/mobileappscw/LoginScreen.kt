package com.example.mobileappscw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        val loginButton = findViewById<Button>(R.id.signInButton)
        loginButton.setOnClickListener{_ -> logIn()}
    }

    private fun logIn() {
        //takes the sign in input from the relevant fields
        val username = findViewById<TextInputEditText>(R.id.loginUsernameField).text.toString()
        val password = findViewById<TextInputEditText>(R.id.loginPasswordField).text.toString()
        val homeIntent = Intent(this, HomeScreen::class.java)
        startActivity(homeIntent)
    }
}