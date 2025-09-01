package com.example.myapplication.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Komentar(
    val userId:String="",
    val ordinacijaId:String="",
    val tekst  :String = "",
    val doktor:String="",
    val procedura :String ="",
    @DocumentId
    var id: String = ""
)
