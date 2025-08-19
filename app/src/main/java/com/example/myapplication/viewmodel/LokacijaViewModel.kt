package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.LokacijaKorisnika
import com.example.myapplication.model.LokacijaOrdinacija
import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LokacijaViewModel() : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid

    private val userLoc = MutableStateFlow<LokacijaKorisnika?>(null)
    val userLocation: StateFlow<LokacijaKorisnika?> = userLoc
    private val mapOrd = MutableStateFlow<List<Ordinacija>>(emptyList())
    val mapOrdinacija: StateFlow<List<Ordinacija>> = mapOrd
    private var listenerRegistration: ListenerRegistration? = null
    private var objectListenerRegistration: ListenerRegistration? = null

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
                        userLoc.value = null
                    }
                }
                    objectListenerRegistration = db.collection("kolekcijaordinacija")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }
                            if (snapshot != null) {
                                val objectsList = snapshot.toObjects(Ordinacija::class.java)
                                mapOrd.value = objectsList
                            }
                        }
                }
        }
}
