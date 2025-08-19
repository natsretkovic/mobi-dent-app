package com.example.myapplication.model

data class Ordinacija(
    val naziv: String = "",
    val doktor : String="",
    val procedura : String = "",
    val ocena : Double =0.0,
    val komentar : String="")