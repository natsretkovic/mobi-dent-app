package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.LokacijaKorisnika
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LokacijaViewModel() : ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private var userId : String? = null

    private val userLoc = MutableStateFlow<LokacijaKorisnika?>(null)
    val userLocation: StateFlow<LokacijaKorisnika?> = userLoc

    private var listenerRegistration: ListenerRegistration? = null

    init {
        auth.currentUser?.let { user ->
            listenerRegistration = db.collection("korisnici").document(user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        userLoc.value = snapshot.toObject(LokacijaKorisnika::class.java)
                    } else {
                        userLoc.value = null // Ako dokument više ne postoji, resetujte vrednost
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
