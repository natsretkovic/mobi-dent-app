package com.example.myapplication.model

data class Korisnik(
    val ime: String = "",
    val prezime: String = "",
    val brojTelefona: String = "",
    val username: String="",
    val email : String="",
    val profilnaSlikaUrl : String? = null,
    val poeni: Int = 0
)
// sto se logike oko poena tice: korisnik doda ocenu +5p, korisnik doda komentar +5p,
// korisnik doda komentar > 200 karaktera +10p