package com.example.mobileappscw

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class HomeScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        //setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.homeToolbar)
        setSupportActionBar(toolbar)

        //setup recycler view for friends list
        val listOfFriends = populateList()

        val recyclerView = findViewById<RecyclerView>(R.id.friendListRecyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = MyAdapter(listOfFriends)
        recyclerView.adapter = adapter

    }

    private fun populateList(): ArrayList<friend> {
        val list = ArrayList<friend>()

        //list of images for friends pfps
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
        list.sortBy {list -> list.username}
        return list
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myView = findViewById<View>(R.id.homeToolbar)
        when (item.itemId) {
            //this is just an example snack bar
            R.id.profilePicture -> {
                val snackbar =
                    Snackbar.make(myView, "profile was pressed",0)
                snackbar.show()
                return true
            }
            R.id.settingsAction -> {
                val settingsIntent = Intent(this, SettingsScreen::class.java)
                startActivity(settingsIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}