package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.StorageService
import com.example.myapplication.model.Ordinacija
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.model.Komentar
import com.example.myapplication.model.Ocena
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date


class OrdinacijaViewModel(private val storageService: StorageService,
                          private val auth: FirebaseAuth,
                          private val firestore: FirebaseFirestore,
    private val lokacijaViewModel: LokacijaViewModel
) : ViewModel() {


    private val _ordinacijaList = MutableStateFlow<List<Ordinacija>>(emptyList())
    val ordinacijaList: StateFlow<List<Ordinacija>> = _ordinacijaList
    private val _ordinacija = MutableStateFlow<Ordinacija?>(null)
    val ordinacija : StateFlow<Ordinacija?> = _ordinacija

    private val fOrdinacija = MutableStateFlow<List<Ordinacija>>(emptyList())
    val ordinacijaFilter: StateFlow<List<Ordinacija>> = fOrdinacija
    private val _oceneList = MutableStateFlow<List<Ocena>>(emptyList())
    val oceneList : StateFlow<List<Ocena>> = _oceneList

    private val _komentariList = MutableStateFlow<List<Komentar>>(emptyList())
    val komentariList: StateFlow<List<Komentar>> = _komentariList



    fun setCurrentOrdinacija(ord: Ordinacija) {
        _ordinacija.value = ord
        println("Selektovana ordinacija: ${_ordinacija.value?.naziv} id njen ${_ordinacija.value?.id}")
        ord.id?.let { id ->
            viewModelScope.launch {
                try {
                    getOcene(ord.id)
                    getKomentari(ord.id)

                } catch (e: Exception) {
                    println("Greska pri ucitavanju podkolekcija: ${e.message}")
                }
            }
        }
    }

    init{
       listOrdinacija()
        getOrdinacijeFromUser()
    }

    fun addOrdinacija(
        naziv: String,
        doktor: String,
        procedura: String,
        ocena: Double,
        text: String
       // latitude : Double,
        //longitude : Double
    ) {
        val trenutnaLokacija = lokacijaViewModel.listenerKorisnik.value
        if(trenutnaLokacija!=null) {
            val novaOrdinacija = Ordinacija(
                naziv = naziv,
                latitude = trenutnaLokacija.latitude,
                longitude = trenutnaLokacija.longitude
            )
            val noviKomentar = Komentar(
                tekst = text,
                doktor = doktor,
                procedura = procedura
            )
            val novaOcena = Ocena(
                vrednost = checkOcena(ocena)
            )
            viewModelScope.launch {
                try {
                    val id = storageService.save(novaOrdinacija)
                    storageService.saveKomentar(id, noviKomentar)
                    storageService.saveOcena(id, novaOcena)
                    println("Ordinacija uspešno dodata.")
                    addPoints(ocena, text)

                } catch (e: Exception) {
                    println("Greska: ${e.message}")
                }
            }
        }
        else{
            println("Lokacija je null")
        }
    }
    fun deleteOrdinacija(id: String) {
        try {
            if (id.isNotEmpty())
                viewModelScope.launch {
                    storageService.delete(id)
                }
        }
        catch(e : Exception){
            println("Greska: ${e.message}")
        }
    }

    fun listOrdinacija() {
        viewModelScope.launch {
            storageService.getAllOrdinacije().collect { ordinacije ->
                _ordinacijaList.value = ordinacije
            }
        }
    }
    fun filterOrdinacija(naziv: String) {
        viewModelScope.launch {
            storageService.filterByNaziv(naziv).collect { ordinacije ->
                fOrdinacija.value = ordinacije
            }
        }
    }
    fun filterOrdinacijaByAverageOcena(ocena : Double){
        viewModelScope.launch {
            storageService.filterByAverageOcena(ocena).collect { ordinacije ->
                fOrdinacija.value = ordinacije
            }
        }
    }
    fun filterOrdinacijaByDatum(pocetniDatum : Date?, krajnjiDatum : Date?){


        viewModelScope.launch {
            storageService.filterByDatum(pocetniDatum, krajnjiDatum).collect { ordinacije ->
                fOrdinacija.value = ordinacije
            }
        }
    }
    fun getOrdinacijeFromUser(){
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid
                    ?: throw IllegalStateException("Korisnik nije prijavljen.")
                storageService.getOrdinacijeByUser(uid).collect{ordinacije ->
                    fOrdinacija.value = ordinacije
                }
            }
            catch(e : Exception){
                println("Greska: ${e.message}")
            }
        }
    }
    fun getOcene(ordinacijaId : String){
        viewModelScope.launch {
                storageService.getAllOcene(ordinacijaId).collect { ocene ->
                    _oceneList.value = ocene
                }
        }
    }
    fun getKomentari(ordinacijaId : String){
        viewModelScope.launch {
                storageService.getAllKomentari(ordinacijaId).collect { komentari ->
                    _komentariList.value = komentari
                }
        }
    }
    fun updateOrdinaciju(ordinacijaId: String, ocena: Double, komentar: String, doktor: String, procedura: String) {
        val noviKomentar = Komentar(tekst = komentar, doktor = doktor, procedura = procedura)
        val novaOcena = Ocena(vrednost = checkOcena(ocena), ordinacijaId = ordinacijaId)

        viewModelScope.launch {
            try {
                storageService.saveKomentar(ordinacijaId, noviKomentar)
                storageService.saveOcena(ordinacijaId, novaOcena)

                addPoints(ocena,komentar)

            } catch (e: Exception) {
                println("Greska: ${e.message}")
            }
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
            println("Korisnik nije ulogovan")
        }
    }
}
class OrdinacijaViewModelFactory(private val storageService: StorageService,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore, private val lokacijaViewModel: LokacijaViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdinacijaViewModel::class.java)) {
            return OrdinacijaViewModel(storageService,
                                        auth,
                                         firestore, lokacijaViewModel) as T
        }
        throw IllegalArgumentException("nepoznata ViewModel klasa")
    }
}

