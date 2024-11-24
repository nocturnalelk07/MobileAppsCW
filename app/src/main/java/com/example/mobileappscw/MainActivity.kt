package com.example.mobileappscw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//the main activity will take the user to the login screen if the user hasn't already used the remember me feature
//otherwise it will take them to the home screen, logged in
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //for now this activity will just start the login activity but this will be fixed when remember me is implemented
        if(true) {
            launchLoginActivity()
        } else {
            launchHomeActivity()
        }
    }

    private fun launchLoginActivity() {
        val loginIntent = Intent(this, LoginScreen::class.java)
        startActivity(loginIntent)
    }

    private fun launchHomeActivity() {
        val homeIntent = Intent(this, HomeScreen::class.java)
        startActivity(homeIntent)
    }
}
