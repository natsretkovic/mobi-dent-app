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

    private val user = MutableStateFlow<Korisnik?>(null)
    val korisnik: StateFlow<Korisnik?> = user.asStateFlow()

    private val userList = MutableStateFlow<List<Korisnik>>(emptyList())
    val allUsersList : StateFlow<List<Korisnik>> = userList

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
        getAllUsersList()
    }
    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(listener)
    }
    fun getAllUsersList() {
        viewModelScope.launch {
            userList.value = service.getAllUsers()
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
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}