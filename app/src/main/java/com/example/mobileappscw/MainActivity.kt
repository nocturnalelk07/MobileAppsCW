package com.example.mobileappscw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

//the main activity will take the user to the login screen if the user hasn't already used the remember me feature
//otherwise it will take them to the home screen, logged in
class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //for now this activity will just start the login activity but this will be fixed when remember me is implemented
        if(isSignedIn()) {
            launchHomeActivity()
        } else {
            launchLoginActivity()
        }
    }

    private fun launchLoginActivity() {
        val loginIntent = Intent(this, LoginScreen::class.java)
        startActivity(loginIntent)
    }

    private fun launchHomeActivity() {
        //sign in the user

        //go to the home screen
        val homeIntent = Intent(this, HomeScreen::class.java)
        startActivity(homeIntent)
    }

    private fun isSignedIn(): Boolean {
        //determine if the user is still signed in before sending to login screen
        val currentUser = auth.currentUser
        var returnBool = false

        if(!(currentUser == null)) {
            returnBool = true
        }
        return returnBool
    }
}
