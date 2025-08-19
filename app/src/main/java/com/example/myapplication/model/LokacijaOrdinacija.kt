package com.example.myapplication.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class LokacijaOrdinacija(
    @DocumentId
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now())

