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
        val arrayList = populateList()

        val recyclerView = findViewById<RecyclerView>(R.id.friendListRecyclerView)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val adapter = MyAdapter(arrayList)
        recyclerView.adapter = adapter

    }

    fun populateList(): ArrayList<MyModel> {
        val list = ArrayList<MyModel>()

        val myImageList = arrayOf(R.drawable.photo, R.drawable.photo, R.drawable.photo,
            R.drawable.photo, R.drawable.photo, R.drawable.photo,
            R.drawable.photo, R.drawable.photo, R.drawable.photo)

        val myNameList = arrayOf("finn", "holly", "molly",
            "trolley", "ceri", "harvey",
            "ford", "stan", "daniel")

        for (i in 0 .. 8) {
            val imageModel = MyModel()
            imageModel.setNames(myNameList[i])
            imageModel.setImages(myImageList[i])
            list.add(imageModel)

        }
        list.sortBy {list -> list.modelName}
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