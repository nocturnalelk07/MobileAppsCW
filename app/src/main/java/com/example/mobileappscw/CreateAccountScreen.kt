package com.example.mobileappscw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class CreateAccountScreen : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    private lateinit var homeIntent: Intent
    private var logCatTag = "cwTag"
    private var currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(logCatTag, "in on create account screen")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account_screen)
        homeIntent = Intent(this, HomeScreen::class.java)

        val createAccountButton = findViewById<Button>(R.id.registerButton)
        createAccountButton.setOnClickListener{_ -> createAccount()}


    }

    private fun createAccount() {
        //takes the create account data in from the user fields
        val username = findViewById<TextInputEditText>(R.id.createUsernameField).text.toString()
        val password = findViewById<TextInputEditText>(R.id.createPasswordField).text.toString()
        val confirmPassword = findViewById<TextInputEditText>(R.id.confirmPasswordField).text.toString()
        val email = findViewById<TextInputEditText>(R.id.createEmailField).text.toString()

        //here we will create an account for the user in the database and then we will log them in
        if(currentUser != null) {
            makeSnackbar(findViewById(R.id.createUsernameField), getString(R.string.register_failed))
        } else {
            if(validationPassed(username, password, confirmPassword)) {
                auth.createUserWithEmailAndPassword(email, password
                ).addOnCompleteListener(this, {task ->
                    if (task.isSuccessful) {
                        closeKeyBoard()
                        startActivity(homeIntent)
                    }
                })
            }
        }



    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if(view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun validationPassed(username: String, password: String, cPassword: String): Boolean {
        //check password and confirm password are actually the same
        var passed = true
        if(!(password == cPassword)) {
            passed = false
        }
        //check username isn't already in database

        return passed
    }

    private fun makeSnackbar(view: View, text: String) {
        val sb = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        sb.show()
    }
}