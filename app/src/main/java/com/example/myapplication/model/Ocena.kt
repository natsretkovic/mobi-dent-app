package com.example.myapplication.model

import com.google.firebase.firestore.DocumentId

data class Ocena(
    val userId :String = "",
    val ordinacijaId :String ="",
    val vrednost :Double =0.0,
    @DocumentId
    val id :String = ""
)
