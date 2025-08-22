package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
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

    //private val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
    private val user = MutableStateFlow<Korisnik?>(null)
    val korisnik: StateFlow<Korisnik?> = user.asStateFlow()

    private val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    user.value = service.getKorisnik(currentUser.uid)
                } catch (e: Exception) {
                    println(e.toString())
                }
            }
        } else {
            user.value = null
        }
    }
    init {
        auth.addAuthStateListener(listener)
    }
    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(listener)
    }

}
// bez stateFlow sa userId mozda null pada kad se pokrece, zato mora da se prati dal je ulogovan
// ili kad se odjavi da ne padne