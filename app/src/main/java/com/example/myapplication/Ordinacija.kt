package com.example.myapplication

data class Ordinacija(val naziv: String = "", val grad: String = "", val adresa: String = "", val latitude: Double = 0.0,
                      val longitude: Double = 0.0,  val stomatolozi: List<String> = emptyList() )