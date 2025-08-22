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

    private val userId = auth.currentUser!!.uid
    private val user = MutableStateFlow<Korisnik?>(null)
    val korisnik: StateFlow<Korisnik?> = user.asStateFlow()
    init{
           getKorisnik()
        }
    fun getKorisnik(){
        viewModelScope.launch {
            try {
                user.value = service.getKorisnik(userId)
            } catch (e: Exception) {
                println(e.toString())
            }

        }
    }
}