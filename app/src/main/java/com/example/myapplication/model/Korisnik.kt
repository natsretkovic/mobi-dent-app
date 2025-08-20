package com.example.myapplication.model

import com.google.firebase.firestore.DocumentId

data class Korisnik(
    @DocumentId
    val id: String = "",
    val ime: String = "",
    val prezime: String = "",
    val brojTelefona: String = "",
    val username: String="",
    val email : String="",
    //val profilnaSlikaUrl : String? = null,
    val poeni: Int = 0,
    val rank: Int =0
)
// sto se logike oko poena tice:korisnik doda objekat +1, korisnik doda ocenu +5p, korisnik doda komentar +5p,
// korisnik doda komentar > 200 karaktera +10p