package com.example.myapplication.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.model.Korisnik
import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LokacijaViewModel(korisnikViewModel: KorisnikViewModel,
                        ordinacijaViewModel: OrdinacijaViewModel) : ViewModel() {
    val listenerKorisnik : StateFlow<Korisnik?> = korisnikViewModel.korisnik
    val listenerKorisnici : StateFlow<List<Korisnik?>> = korisnikViewModel.listaKorisnika
    val listenerOrdinacija : StateFlow<List<Ordinacija>> = ordinacijaViewModel.ordinacijaList

    fun getOrdinacijaRadius(radius : Double) : List<Ordinacija> {
        val userLocation = listenerKorisnik.value
        if(userLocation==null){
            return emptyList()
        }
       val lista =  listenerOrdinacija.value.filter { ordinacija ->
            calculateDistance(userLocation.latitude, userLocation.longitude,
                                ordinacija.latitude, ordinacija.longitude) <= radius
        }
        return lista
    }
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

}
class LokacijaViewModelFactory(private val korisnikViewModel: KorisnikViewModel,
    private val ordinacijaViewModel: OrdinacijaViewModel) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LokacijaViewModel::class.java)) {
            return LokacijaViewModel(korisnikViewModel, ordinacijaViewModel) as T
        }
        throw IllegalArgumentException("Nije u redu")
    }

}

