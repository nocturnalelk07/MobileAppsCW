package com.example.mobileappscw

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.sql.Time
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.Duration
import java.time.LocalDateTime

class HomeScreen : AppCompatActivity() {

    private lateinit var mySharedPreferences: SharedPreferences

    private val logCatTag = "cwTag"

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(logCatTag, "in on create home screen")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        mySharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        editor = mySharedPreferences.edit()
        //starts the countdown clock for the next question
        setTimeRemaining()

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

        val previousQuestionButton = findViewById<Button>(R.id.previousQuestionButton)
        previousQuestionButton.setOnClickListener{
            val quizIntent = Intent(this, QuizScreen::class.java).apply {
                putExtra("PREVIOUS_QUESTION", true)
            }
            startActivity(quizIntent)
        }

        lateinit var navBarIntent : Intent
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_bar_home)
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

    override fun onStop() {
        super.onStop()
        Log.i(logCatTag, "in home screen on stop")
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
        currentUser = auth.currentUser
        when (item.itemId) {
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

    //sets the time remaining on the clock
    private fun setTimeRemaining() {
        val clock = findViewById<TextView>(R.id.timeRemainingView)
        //this is going to calculate the time in milliseconds until the user gets a new question
        var timeToNextQ = calcTimeRemaining()
        if (timeToNextQ <= 0) {
            timeToNextQ = 100000.toLong()
        }
        object : CountDownTimer(timeToNextQ.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val hour = (millisUntilFinished / 3600000) % 24
                val min = (millisUntilFinished / 60000) % 60
                val sec = (millisUntilFinished / 1000) % 60

                clock.text = String.format(getString(R.string.time), f.format(hour), f.format(min),
                    f.format(sec))
            }

            // When the task is over it will print 00:00:00 there
            override fun onFinish() {
                //calls the function again to start the timer with the new question, will always start at 24 hours
                setTimeRemaining()
            }
        }.start()
    }

    private fun calcTimeRemaining(): Long {
        //works out how long until the next question should be released
        val currentTime = LocalDateTime.now()
        val alarmHour = mySharedPreferences.getInt("alarm_hour", currentTime.hour)
        Log.i("test306", alarmHour.toString())
        val alarmMin = mySharedPreferences.getInt("alarm_min", currentTime.minute)
        var alarmTime = LocalDateTime.of(currentTime.year, currentTime.month, currentTime.dayOfMonth.plus(1),
            alarmHour, alarmMin)
        var duration = Duration.between(currentTime, alarmTime)
        var returnValue = duration.toMillis()
        //the alarm may have already happened for today so calculate for tomorrow instead
        if (returnValue <= 0) {
            alarmTime.plusHours(24)
            duration = Duration.between(currentTime, alarmTime)
            returnValue = duration.toMillis()
        }
        return returnValue
    }
}