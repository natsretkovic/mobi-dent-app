package com.example.myapplication.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Ordinacija(
    @DocumentId
    val id: String = "",
    val userId :String ="",
    val naziv: String = "",
    val doktor : String="",
    val procedura : String = "",
    val ocena : Double =0.0,
    val komentar : String="",
    val latitude:Double =0.0,
    val longitude:Double=0.0,
    val timestamp: Date = Date())