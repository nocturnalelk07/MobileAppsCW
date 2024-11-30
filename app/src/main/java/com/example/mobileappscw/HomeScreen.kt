package com.example.mobileappscw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class HomeScreen : AppCompatActivity() {

    private val sharedPreferenceName = "remember me"
    private val logCatTag = "cwTag"
    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        //setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.homeToolbar)
        setSupportActionBar(toolbar)

        //setup recycler view for friends list
        val listOfFriends = populateList()

        val recyclerView = findViewById<RecyclerView>(R.id.friendListRecyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
            false)
        recyclerView.layoutManager = layoutManager

        val adapter = MyAdapter(listOfFriends)
        recyclerView.adapter = adapter

    }

    override fun onStop() {
        super.onStop()
        Log.i(logCatTag, "in home screen on stop")
        val sharedPreferences = getSharedPreferences("preference", Context.MODE_PRIVATE)
        if (!(sharedPreferences.getBoolean(sharedPreferenceName, false))) {
            currentUser = auth.currentUser
            auth.signOut()
            Log.i(logCatTag, "signed out")
        }
    }

    override fun onStart() {
        val logOutIntent = Intent(this, LoginScreen::class.java)
        super.onStart()
        Log.i(logCatTag, "in home on start")
        currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(logOutIntent)
        }
    }

    private fun populateList(): ArrayList<friend> {
        val list = ArrayList<friend>()

        //list of images for friends profile pictures
        val myImageList = arrayOf(R.drawable.photo, R.drawable.photo, R.drawable.photo,
            R.drawable.photo, R.drawable.photo, R.drawable.photo,
            R.drawable.photo, R.drawable.photo, R.drawable.photo)

        //list of friends on friends list
        val myNameList = arrayOf("finn", "holly", "molly",
            "trolley", "ceri", "harvey",
            "ford", "stan", "daniel")

        //looping through each list making a friend object
        for (i in 0 .. 8) {
            val imageModel = friend()
            imageModel.setNames(myNameList[i])
            imageModel.setImages(myImageList[i])
            list.add(imageModel)

        }
        list.sortBy {list: friend -> list.username}
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val toolbar = findViewById<View>(R.id.homeToolbar)
        currentUser = auth.currentUser
        when (item.itemId) {
            //this is just an example snack bar
            R.id.profilePicture -> {
                val snackbar =
                    Snackbar.make(toolbar, currentUser!!.displayName.toString(),0)
                snackbar.show()
                return true
            }
            R.id.settingsAction -> {
                val settingsIntent = Intent(this, SettingsScreen::class.java)
                startActivity(settingsIntent)
            }
            R.id.logOffAction -> {
                val logOutIntent = Intent(this, LoginScreen::class.java)
                currentUser = auth.currentUser
                auth.signOut()
                Log.i(logCatTag, "signed out")
                startActivity(logOutIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}