package com.example.myapplication.model

import com.google.firebase.firestore.DocumentId

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
    val longitude:Double=0.0)