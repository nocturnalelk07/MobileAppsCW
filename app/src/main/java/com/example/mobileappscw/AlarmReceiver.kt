package com.example.mobileappscw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.mobileappscw.database.SqliteDatabase
import java.time.LocalDateTime

class AlarmReceiver: BroadcastReceiver() {
    private lateinit var mySharedPreferences : SharedPreferences

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return

        if (context != null) {
            //we want to call the alarm again when it finishes so that it repeats
            val repeatTime = getNewTime(context)
            val alarmItem = AlarmItem(repeatTime, message)
            AndroidAlarmScheduler(context).schedule(alarmItem)

            val db = SqliteDatabase(context)
            db.newCurrentQuestionsTable()
            //TODO api call to put new questions in the database for user ... through db?

            //TODO send a call to the alarm system to go off here with a notification

        }
    }

    private fun getNewTime(context: Context) : LocalDateTime {
        val currentTime = LocalDateTime.now()

        //get the stored time for hour and minute in case it has changed, assume it hasn't by default
        mySharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val setHour = mySharedPreferences.getInt("alarm_Hour", currentTime.hour)
        val setMin = mySharedPreferences.getInt("alarm_min", currentTime.minute)

        //construct a local date time from the user prefs, plus one to go off next day
        val newTime = LocalDateTime.of(currentTime.year, currentTime.month,
            currentTime.dayOfMonth.plus(1), setHour, setMin)

        return newTime
    }
}