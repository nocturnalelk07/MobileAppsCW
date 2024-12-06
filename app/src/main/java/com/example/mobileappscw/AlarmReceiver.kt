package com.example.mobileappscw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
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

            //dont think this is needed
            /*mySharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
            val editor = mySharedPreferences.edit()
            val alarmTimeHour = alarmItem.alarmTime.hour
            val alarmTimeMin = alarmItem.alarmTime.minute
            editor.putInt("alarm_hour", alarmTimeHour)
            editor.apply()
            editor.putInt("alarm_min", alarmTimeMin)
            editor.apply()*/
            //Log.i("test306", "showing new string")
            //Log.i("test306", alarmTimeHour.toString() + alarmTimeMin.toString())
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