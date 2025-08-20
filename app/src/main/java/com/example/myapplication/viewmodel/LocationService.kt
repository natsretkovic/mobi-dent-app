package com.example.myapplication.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.myapplication.viewmodel.utils.LocationClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import com.example.myapplication.R
import com.example.myapplication.model.Korisnik
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions


class LocationService() : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var db: FirebaseFirestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var allUsersListenerRegistration: ListenerRegistration? = null


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        db = FirebaseFirestore.getInstance()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification =
            NotificationCompat.Builder(this, "location").setContentTitle("Pracenje lokacije...")
                .setContentText("Lokacija: ")
                .setSmallIcon(R.drawable.ic_launcher_background).setOngoing(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(30000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText("Lokacija: ($lat) ($long)")
                notificationManager.notify(1, updatedNotification.build())
                val locParametres = mapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                    "timestamp" to Timestamp.now()
                )
                db.collection("korisnici").document(userId)
                    .set(locParametres, SetOptions.merge()).addOnSuccessListener { println("Location updated in Firestore") }
                    .addOnFailureListener { e -> println("Error updating location: $e") }

            }.launchIn(serviceScope)
        allUsersListenerRegistration = db.collection("korisnici")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Listener error: $error")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val allUsers = mutableListOf<Korisnik>()
                    for (doc in snapshot.documents) {
                        val korisnik = doc.toObject(Korisnik::class.java)
                        if (korisnik != null) {
                            korisnik.id = doc.id
                            allUsers.add(korisnik)
                        }
                    }
                    nearUsers(allUsers,userId)
                }
            }
        startForeground(1, notification.build())


    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    private fun nearUsers(allUsers: List<Korisnik>, userId:String) {
        val myUser = allUsers.find { it.id == userId } ?: return

        val nearbyUsers = allUsers.filter { otherUser ->
            otherUser.id != userId &&
                    calculateDistance(
                        myUser.latitude, myUser.longitude,
                        otherUser.latitude, otherUser.longitude
                    ) < 30
        }

        if (nearbyUsers.isNotEmpty()) {
            val firstNearbyUser = nearbyUsers.first()
                 showNotification(firstNearbyUser.ime.toString())
        }
    }
    private fun showNotification(userName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Obavestenje o blizini",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Obavesti kada su drugi korisnici u blizini"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Zamijeni s ikonom
            .setContentTitle("Korisnik u blizini!")
            .setContentText("$userName je u blizini.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationId = 12345
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}