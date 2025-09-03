package com.example.myapplication.viewmodel

import android.location.Location
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.model.Korisnik
import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LokacijaViewModel() : ViewModel() {
    private val _listenerKorisnik = MutableStateFlow<Korisnik?>(null)
    val listenerKorisnik: StateFlow<Korisnik?> = _listenerKorisnik
    private val _listenerOrdinacije = MutableStateFlow<List<Ordinacija>>(emptyList())
    val listenerOrdinacije: StateFlow<List<Ordinacija>> = _listenerOrdinacije
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var listenerRegistration : ListenerRegistration? = null
    private var objectListenerRegistration : ListenerRegistration? = null


   init {
        /*auth.currentUser?.let { user ->
            listenerRegistration = db.collection("korisnici").document(user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        println("Greska kod slusanja lokacije: $error")
                        _listenerKorisnik.value = null
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val korisnik = snapshot.toObject(Korisnik::class.java)
                        _listenerKorisnik.value = korisnik
                    }
                    else{
                        _listenerKorisnik.value=null
                    }
                }*/


    }
   fun initListenerForCurrentUser() {
       val user = auth.currentUser ?: return
       listenerRegistration?.remove()

       listenerRegistration = db.collection("korisnici").document(user.uid)
           .addSnapshotListener { snapshot, error ->
               if (error != null) {
                   _listenerKorisnik.value = null
                   println("Greska kod slušanja korisnika: $error")
                   return@addSnapshotListener
               }
               _listenerKorisnik.value = snapshot?.toObject(Korisnik::class.java)
           }
       objectListenerRegistration = db.collection("kolekcijaordinacija")
           .addSnapshotListener { snapshot, error ->
               if(error!=null){
                   return@addSnapshotListener
               }
               if(snapshot!=null){
                   val objectList = snapshot.toObjects(Ordinacija::class.java)
                   _listenerOrdinacije.value = objectList
               }
           }
   }

    fun getOrdinacijaRadius(radius : Double) : List<Ordinacija> {
        val userLocation = _listenerKorisnik.value
        if(userLocation==null){
            return emptyList()
        }
       val lista = _listenerOrdinacije.value.filter { ordinacija ->
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
/*class LokacijaViewModelFactory(private val ordinacijaViewModel: OrdinacijaViewModel) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LokacijaViewModel::class.java)) {
            return LokacijaViewModel(ordinacijaViewModel) as T
        }
        throw IllegalArgumentException("Nije u redu")
    }

}*/

