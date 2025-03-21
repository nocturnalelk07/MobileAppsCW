package com.example.mobileappscw

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime


class AlarmScreen : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var alarmSetTime: LocalDateTime
    private lateinit var scheduler: AndroidAlarmScheduler
    private lateinit var alarmItem: AlarmItem
    private lateinit var mySharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    private val auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_screen)

        scheduler = AndroidAlarmScheduler(this)
        mySharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        editor = mySharedPreferences.edit()
        //temporary time to pass into the alarmItem
        alarmSetTime = LocalDateTime.now()
        currentUser = auth.currentUser
        alarmItem = AlarmItem(alarmSetTime, currentUser!!.email.toString())
        timePicker = findViewById(R.id.timePicker)
        updateShownAlarm(mySharedPreferences.getInt("alarm_hour", 0).toString(),
            mySharedPreferences.getInt("alarm_min", 0).toString())
        val alarmButton = findViewById<Button>(R.id.createAlarmButton)
        alarmButton.setOnClickListener{_ -> createAlarm()}

        val cancelAlarm = findViewById<Button>(R.id.cancelAlarmButton)
        cancelAlarm.setOnClickListener{_ -> cancelAlarm()}

        lateinit var navBarIntent: Intent
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_bar_alarm)
        bottomNavigationView.setOnItemReselectedListener {
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

    private fun createAlarm() {
        //there should only be one alarm at a time so this cancels the possibly existing alarm before making a new one
        //cancelling the alarm also calls to update the time
        cancelAlarm()
        //here we schedule a new alarm
        alarmItem.let(scheduler::schedule)

        //here we make the time set into a string to store for our own use
        val alarmTimeHour = alarmItem.alarmTime.hour
        val alarmTimeMin = alarmItem.alarmTime.minute
        editor.putInt("alarm_hour", alarmTimeHour)
        editor.apply()
        editor.putInt("alarm_min", alarmTimeMin)
        editor.apply()
        updateShownAlarm(alarmTimeHour.toString(), alarmTimeMin.toString())
        //Log.i("test306", mySharedPreferences.getString("alarm_time", "default").toString())
    }
    private fun cancelAlarm() {
        updateTime()
        alarmItem.let(scheduler::cancel)
    }

    private fun updateTime() {
        //updating the alarm items time based on user selection with the time picker widget
        alarmSetTime = LocalDateTime.now()
        val newTime = LocalDateTime.of(alarmSetTime.year, alarmSetTime.month, alarmSetTime.dayOfMonth,
            timePicker.hour, timePicker.minute)

        alarmItem.setTime(newTime)
    }

    private fun updateShownAlarm(hour : String, min : String) {
        val text = findViewById<TextView>(R.id.currentAlarm)
        text.text = String.format(getString(R.string.current_alarm,), hour, min)
    }
}