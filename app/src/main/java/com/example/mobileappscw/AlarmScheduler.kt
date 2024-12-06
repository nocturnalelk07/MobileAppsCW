package com.example.mobileappscw

interface AlarmScheduler {
    fun schedule(item : AlarmItem)
    fun cancel(item: AlarmItem)
}