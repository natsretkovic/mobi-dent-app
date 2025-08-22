package com.example.myapplication.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Korisnik(
    val ime: String = "",
    val prezime: String = "",
    val brojTelefona: String = "",
    val username: String="",
    val email : String="",
   // val profilnaSlikaUrl : String? = null,
    val poeni: Int = 0,
    val rank: Int =0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now(),
    @get:Exclude @set:Exclude var id: String? = null)
// sto se logike oko poena tice:korisnik doda objekat +1, korisnik doda ocenu +5p, korisnik doda komentar +5p,
// korisnik doda komentar > 200 karaktera +10p