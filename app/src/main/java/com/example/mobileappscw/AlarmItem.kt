package com.example.mobileappscw

import java.time.LocalDateTime

class AlarmItem(var alarmTime : LocalDateTime, var message : String) {

    fun setTime(newTime: LocalDateTime) {
        alarmTime = newTime
    }
}
