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
    private var userId : String? = null

    private val userLoc = MutableStateFlow<LokacijaKorisnika?>(null)
    val userLocation: StateFlow<LokacijaKorisnika?> = userLoc

    init {
        auth.currentUser?.let { user ->
            this.userId = user.uid
            db.collection("korisnici").document(this.userId!!)
                .addSnapshotListener { snapshot, error ->
                    if (snapshot != null && snapshot.exists()) {
                        userLoc.value = snapshot.toObject(LokacijaKorisnika::class.java)
                    }


                }
        }
    }


}