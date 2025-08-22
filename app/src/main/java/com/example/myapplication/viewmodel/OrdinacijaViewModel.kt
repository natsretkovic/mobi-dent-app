package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.StorageService
import com.example.myapplication.model.Ordinacija
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class OrdinacijaViewModel(private val storageService: StorageService,private val lokacijaViewModel: LokacijaViewModel,
                          private val auth: FirebaseAuth,
                          private val firestore: FirebaseFirestore
) : ViewModel() {

    private val ordinacijaList = MutableStateFlow<List<Ordinacija>>(emptyList())
    val listOrdinacija: StateFlow<List<Ordinacija>> = ordinacijaList

    private val fOrdinacija = MutableStateFlow<List<Ordinacija>>(emptyList())
    val ordinacijaFilter: StateFlow<List<Ordinacija>> = fOrdinacija

    var selectedOrdinacija: Ordinacija by mutableStateOf(Ordinacija())
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
        val trenutnaLokacija = lokacijaViewModel.userLocation.value
        if (trenutnaLokacija != null) {

            val novaOrdinacija = Ordinacija(
                naziv = naziv,
                doktor = doktor,
                procedura = procedura,
                ocena = checkOcena(ocena),
                komentar = komentar,
                latitude = trenutnaLokacija.latitude,
                longitude = trenutnaLokacija.longitude
            )
            viewModelScope.launch {
                try {
                    storageService.save(novaOrdinacija)
                    println("Ordinacija uspešno dodata.")
                    addPoints(ocena, komentar)

                } catch (e: Exception) {
                    println("Greška: ${e.message}")
                }
            }
        } else {
            println("Lokacija nije dostupna.")
        }
    }

    fun updateOrdinacija(
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
            ocena = checkOcena(ocena),
            komentar = komentar
        )
        viewModelScope.launch { storageService.update(ord) }
    }

    fun deleteOrdinacija(id: String) {
        if (id.isNotEmpty())
            viewModelScope.launch {
                storageService.delete(id)
            }
    }

    fun listOrdinacija() {
        viewModelScope.launch {
            ordinacijaList.value = storageService.getAllOrdinacije()
        }
    }

    fun filterOrdinacija(atribute: String) {
        val atributelowcase = atribute.lowercase()
        viewModelScope.launch {
            val result = storageService.getAllOrdinacije().filter { ordinacija ->
                ordinacija.naziv.lowercase().contains(atributelowcase) ||
                        ordinacija.doktor.lowercase().contains(atributelowcase) ||
                        ordinacija.procedura.lowercase().contains(atributelowcase)
            }
            fOrdinacija.value = result
        }
    }

    private fun checkOcena(ocena: Double): Double {
        if (ocena > 5.0)
            return 5.0
        else if (ocena < 1.0) {
            return 1.0
        } else {
            return ocena
        }
    }

    private fun calculatePoints(ocena: Double, komentar: String): Double {
        var poeni = 1.0;
        if (ocena > 0.0)
            poeni += 5
        if (!komentar.isEmpty())
            poeni += 5.0
        if (komentar.length > 200)
            poeni += 10.0
        return poeni
    }

    private suspend fun addPoints(ocena: Double, komentar: String) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            val poeni = calculatePoints(ocena, komentar)
            val userDocRef = firestore.collection("korisnici").document(currentUserId)
            userDocRef.update("poeni", FieldValue.increment(poeni)).await()
            println("Korisniku su dodati poeni: $poeni")

        } else {
            println("Greška: Korisnik nije ulogovan. Poeni nisu dodati.")
        }
    }
}
/*class PoiViewModelFactory(private val storageService: StorageService,
    private val lokacijaViewModel: LokacijaViewModel,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdinacijaViewModel::class.java)) {
            return OrdinacijaViewModel(storageService,
                                        lokacijaViewModel,
                                        auth,
                                         firestore) as T
        }
        throw IllegalArgumentException("nepoznata ViewModel klasa")
    }
}*/



    /*val id : String = "",
    val naziv: String = "",
    val doktor : String="",
    val procedura : String = "",
    val ocena : Double =0.0,
    val komentar : String="",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0)    */
