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

    private val fOrdinacija = MutableStateFlow<List<Ordinacija>>(emptyList())
    val ordinacijaFilter: StateFlow<List<Ordinacija>> = fOrdinacija


   /* fun setCurrentOrdinacija(ordinacija: Ordinacija) {
        selectedOrdinacija = ordinacija
    }

    fun resetCurrentOrdinacija() {
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
        text: String
       // onResult: (String, Boolean) -> Unit()
    ) {
        val trenutnaLokacija = lokacijaViewModel.userLocation.value
        if (trenutnaLokacija != null) {
              val novaOrdinacija =  Ordinacija(
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
                    storageService.saveKomentar(id,noviKomentar)
                    storageService.saveOcena(id,novaOcena)
                    println("Ordinacija uspešno dodata.")

                    addPoints(ocena, text)

                } catch (e: Exception) {
                    println("Greška: ${e.message}")
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
          // doktor = null.add(doktor),
           // procedura = null.add(procedura),
            //ocena = null.add(ocena),
            //komentar = null.add(komentar)
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
                val result = storageService.getAllOrdinacije().filter { ordinacija ->
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
                val sveOrdinacije = storageService.getAllOrdinacije()
                val result = sveOrdinacije.filter { ordinacija ->
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
                val sveOrdinacije = storageService.getAllOrdinacije()
                val result = sveOrdinacije.filter { ordinacija ->
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
                var allOrdinacije = storageService.getAllOrdinacije()
                var result = allOrdinacije.filter { ordinacije ->
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
                var allOrdinacije = storageService.getAllOrdinacije()
                var result = allOrdinacije.filter { ordinacije ->
                    (ordinacije.userId == userId)
                }
                fOrdinacija.value = result
            }
            catch(e : Exception){
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

