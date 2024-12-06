package com.example.mobileappscw

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDateTime


class AlarmScreen : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var alarmSetTime: LocalDateTime
    private lateinit var scheduler: AndroidAlarmScheduler
    private lateinit var alarmItem: AlarmItem

    override fun onCreate(savedInstanceState: Bundle?) {
        //Log.i("test306", "in on create")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_screen)

        scheduler = AndroidAlarmScheduler(this)

        alarmSetTime = LocalDateTime.now()
        alarmItem = AlarmItem(alarmSetTime, "test306")
        timePicker = findViewById(R.id.timePicker)
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
        updateTime()
        alarmItem.let(scheduler::schedule)
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
}