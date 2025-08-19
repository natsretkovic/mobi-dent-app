package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.StorageService
import com.example.myapplication.model.Ordinacija
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class OrdinacijaViewModel(private val storageService: StorageService) : ViewModel() {
    var selectedOrdinacija : Ordinacija by mutableStateOf(Ordinacija())
        private set

    fun setCurrentOrdinacija(ordinacija: Ordinacija) {
        selectedOrdinacija = ordinacija
    }

    fun resetCurrentOrdinacija() {
        selectedOrdinacija = Ordinacija()
    }

    fun addOrdinacija(
        naziv: String,
        doktor: String,
        procedura: String,
        ocena: Double,
        komentar: String
    ) {
        val ord = Ordinacija(
            naziv = naziv,
            doktor = doktor,
            procedura = procedura,
            ocena = ocena,
            komentar = komentar
        )
        viewModelScope.launch {
                storageService.save(ord)
            }
        }
    }




    /*val id : String = "",
    val naziv: String = "",
    val doktor : String="",
    val procedura : String = "",
    val ocena : Double =0.0,
    val komentar : String="",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0)    */
