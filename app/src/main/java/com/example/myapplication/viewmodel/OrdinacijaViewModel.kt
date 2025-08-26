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
import com.example.myapplication.model.Komentar
import com.example.myapplication.model.Ocena
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class OrdinacijaViewModel(private val storageService: StorageService,private val lokacijaViewModel: LokacijaViewModel,
                          private val auth: FirebaseAuth,
                          private val firestore: FirebaseFirestore
) : ViewModel() {


    private val ordinacijaList = MutableStateFlow<List<Ordinacija>>(emptyList())
    val listOrdinacija: StateFlow<List<Ordinacija>> = ordinacijaList
    private val ordinacija = MutableStateFlow<Ordinacija?>(null)
    val selectedOrdinacija : StateFlow<Ordinacija?> = ordinacija


    private val fOrdinacija = MutableStateFlow<List<Ordinacija>>(emptyList())
    val ordinacijaFilter: StateFlow<List<Ordinacija>> = fOrdinacija
    private val ocene = MutableStateFlow<List<Ocena>>(emptyList())
    val oceneList : StateFlow<List<Ocena>> = ocene

    private val komentari = MutableStateFlow<List<Komentar>>(emptyList())
    val komentariList: StateFlow<List<Komentar>> = komentari


    fun setCurrentOrdinacija(ord: Ordinacija) {
        ordinacija.value = ord
    }

    /*fun resetCurrentOrdinacija() {
        selectedOrdinacija = Ordinacija()
    }*/

    init{
        listOrdinacija()
        getOrdinacijeFromUser(auth.currentUser?.uid)
    }

    fun addOrdinacija(
        naziv: String,
        doktor: String,
        procedura: String,
        ocena: Double,
        text: String,
    ) {
            val trenutnaLokacija = lokacijaViewModel.userLocation.value
            if (trenutnaLokacija != null) {
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
                        ("Greška: ${e.message}")
                    }
                }
            } else {
                println("Lokacija nije dostupna.")
            }
    }

    fun updateOrdinacija(
        id: String,
        naziv: String,
        doktor: String,
        procedura: String,
        ocena: Double,
        komentar: String
    ) {
        val ord = Ordinacija(
            naziv = naziv,
        )
        viewModelScope.launch {
            try {
                storageService.update(ord)
            }
            catch(e : Exception){
                println("Greska: ${e.message}")
            }
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
            try {
                ordinacijaList.value = storageService.getAllOrdinacije()
            }
            catch(e : Exception){
                println("Greska: ${e.message}")
            }
        }
    }
    fun filterOrdinacija(atribut: String) {
        val atributLower = atribut.lowercase()
        viewModelScope.launch {
            try {
                val result = ordinacijaList.value.filter { ordinacija ->
                    ordinacija.naziv.lowercase().contains(atributLower)
                }
                fOrdinacija.value = result
            } catch (e: Exception) {
                println("Greška: ${e.message}")
            }
        }
    }
    fun filterOrdinacijaPoKomentarima(atribut: String) {
        val atributLower = atribut.lowercase()
        viewModelScope.launch {
            try {
                val result = ordinacijaList.value.filter { ordinacija ->
                    val komentari = storageService.getAllKomentari(ordinacija.id)
                    komentari.any { komentar ->
                            komentar.doktor.lowercase().contains(atributLower) ||
                                    komentar.procedura.lowercase().contains(atributLower)
                    } || ordinacija.naziv.lowercase().contains(atributLower)
                }
                fOrdinacija.value = result
            } catch (e: Exception) {
                println("Greška: ${e.message}")
            }
        }
    }

    fun filterOrdinacijaByOcena(ocena : Double){
        val ocenaDouble = "%.1f".format(ocena)
        viewModelScope.launch {
            try {
                val result = ordinacijaList.value.filter { ordinacija ->
                    val ordinacijaOcena = storageService.getAllOcene(ordinacija.id)
                    ordinacijaOcena.any(){"%.1f".format(it.vrednost) == ocenaDouble}
                }
                fOrdinacija.value = result
            }catch(e : Exception){
                println("Greska: ${e.message}")
            }

        }
    }
    fun filterOrdinacijaByDatum(pocetniDatum : Date, krajnjiDatum : Date){
        viewModelScope.launch {
            try {
                var result = ordinacijaList.value.filter { ordinacije ->
                    (ordinacije.timestamp >= pocetniDatum) &&
                            (ordinacije.timestamp <= krajnjiDatum)
                }
                fOrdinacija.value = result
            }
            catch(e : Exception){
                println("Greska: ${e.message}")
            }
        }
    }
    fun getOrdinacijeFromUser(userId : String?){
        viewModelScope.launch {
            try {
                var result = ordinacijaList.value.filter { ordinacije ->
                    (ordinacije.userId == userId)
                }
                fOrdinacija.value = result
            }
            catch(e : Exception){
                println("Greska: ${e.message}")
            }
        }
    }
    fun getOcene(ordinacijaId : String){
        viewModelScope.launch {
            try{
                ocene.value = storageService.getAllOcene(ordinacijaId)
            }
            catch(e: Exception){
                println(e.message)
            }
        }
    }
    fun getKomentari(ordinacijaId : String){
        viewModelScope.launch {
            try{
                komentari.value = storageService.getAllKomentari(ordinacijaId)
            }
            catch(e: Exception){
                println(e.message)
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

                val trenutneOcene = ocene.value.toMutableList()
                trenutneOcene.add(novaOcena)
                ocene.value = trenutneOcene

            } catch (e: Exception) {
                println(e.message)
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
            println("Greška: Korisnik nije ulogovan. Poeni nisu dodati.")
        }
    }

}
class OrdinacijaViewModelFactory(private val storageService: StorageService,
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
}

