package com.example.myapplication.model
import com.google.firebase.Timestamp

data class LokacijaKorisnika(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var userId: String = "",
    val timestamp: Timestamp = Timestamp.now()){
}