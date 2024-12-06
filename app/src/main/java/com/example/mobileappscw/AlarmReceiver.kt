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

        //we want to call the alarm again when it finishes so that it repeats
        //val repeatTime = LocalDateTime.now().plusHours(24)
        val repeatTime = LocalDateTime.now().plusSeconds(24)
        val alarmItem = AlarmItem(repeatTime, message)
        context?.let { AndroidAlarmScheduler(it) }?.schedule(alarmItem)

        //now update shared preferences
        if (context != null) {
            mySharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
            val editor = mySharedPreferences.edit()
            val alarmTimeString = alarmItem.alarmTime.toString()
            editor.putString("alarm_time", alarmTimeString)
            editor.apply()
            Log.i("test306", "showing new string")
            Log.i("test306", alarmTimeString)
        }

        println(message)
    }
}