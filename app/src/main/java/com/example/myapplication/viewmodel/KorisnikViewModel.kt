package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.KorisnikService
import com.example.myapplication.model.Korisnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KorisnikViewModel(private val service : KorisnikService,
                        private val auth: FirebaseAuth,
                        private val firestore: FirebaseFirestore) : ViewModel() {

    private val _korisnik = MutableStateFlow<Korisnik?>(null)
    val korisnik: StateFlow<Korisnik?> = _korisnik.asStateFlow()

    private val _listaKorisnika = MutableStateFlow<List<Korisnik>>(emptyList())
    val listaKorisnika : StateFlow<List<Korisnik>> = _listaKorisnika

    private val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    _korisnik.value = service.getKorisnik(currentUser.uid)
                } catch (e: Exception) {
                    println(e.toString())
                }
            }
        } else {
            _korisnik.value = null
        }
    }
    init {
        auth.addAuthStateListener(listener)
        getAllUsersList()
    }
    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(listener)
    }
    fun getAllUsersList() {
        viewModelScope.launch {
            service.getAllUsers().collect { korisnici ->
                _listaKorisnika.value = korisnici
            }
        }
    }


}
// bez stateFlow sa userId mozda null pada kad se pokrece, zato mora da se prati dal je ulogovan
// ili kad se odjavi da ne padne

class KorisnikViewModelFactory(
    private val service: KorisnikService,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KorisnikViewModel::class.java)) {
            return KorisnikViewModel(service, auth, firestore) as T
        }
        throw IllegalArgumentException("Nije u redu")
    }
}