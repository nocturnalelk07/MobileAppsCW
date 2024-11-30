package com.example.mobileappscw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginScreen : AppCompatActivity() {

    private val sharedPreferenceName = "remember me"
    private val auth = FirebaseAuth.getInstance()

    private lateinit var homeIntent: Intent
    private var logCatTag = "cwTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        homeIntent = Intent(this, HomeScreen::class.java)

        val loginButton = findViewById<Button>(R.id.signInButton)
        loginButton.setOnClickListener{_ -> logIn()}

        val createAccountRedirectButton = findViewById<Button>(R.id.goToCreateAccountButton)
        createAccountRedirectButton.setOnClickListener{_ -> goToCreateAccount()}

        Log.i(logCatTag, "in on create login screen")
    }

    private fun logIn() {
        //takes the sign in input from the relevant fields
        val emailButton = findViewById<TextInputEditText>(R.id.loginEmailField)
        val password = findViewById<TextInputEditText>(R.id.loginPasswordField).text.toString()
        val rememberMe = findViewById<CheckBox>(R.id.rememberMe).isChecked
        val sharedPreferences = getSharedPreferences( "preference",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        //findViewById<TextInputEditText>(R.id.loginUsernameField).setHint(rememberMe.toString())
        //now we will check this data with the database and log them in if it is correct
            auth.signInWithEmailAndPassword(
                emailButton.text.toString(), password
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    closeKeyBoard()
                    //save remember me value
                    editor.putBoolean(sharedPreferenceName, rememberMe)
                    editor.apply()
                    startActivity(homeIntent)
                } else {
                    closeKeyBoard()
                    makeSnackBar(emailButton, getString(R.string.login_failed))
                }
            }
    }

    private fun goToCreateAccount() {
        val createAccountIntent = Intent(this, CreateAccountScreen::class.java)
        startActivity(createAccountIntent)
    }

    override fun onStop() {
        super.onStop()
        Log.i(logCatTag, "in login screen on stop")
    }

    private fun makeSnackBar(view: View, text: String) {
        val sb = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        sb.show()
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if(view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}