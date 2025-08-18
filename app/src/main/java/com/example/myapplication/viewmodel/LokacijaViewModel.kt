package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.LokacijaKorisnika
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LokacijaViewModel() : ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private var userId = auth.currentUser!!.uid

    private val userLoc = MutableStateFlow<LokacijaKorisnika?>(null)
    val userLocation: StateFlow<LokacijaKorisnika?> = userLoc

    init {
        db.collection("korisnici").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null && snapshot.exists()) {
                    userLoc.value = snapshot.toObject(LokacijaKorisnika::class.java)
                }
            }
    }


}