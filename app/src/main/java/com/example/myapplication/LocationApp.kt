package com.example.myapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class LocationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var locationChannel =
                NotificationChannel("location", "location", NotificationManager.IMPORTANCE_LOW)

            val nearbyUsersChannel =
                NotificationChannel("channel", "Obavestenje o blizini", NotificationManager.IMPORTANCE_HIGH)

            var notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(locationChannel)
            notificationManager.createNotificationChannel(nearbyUsersChannel)
        }
    }
}