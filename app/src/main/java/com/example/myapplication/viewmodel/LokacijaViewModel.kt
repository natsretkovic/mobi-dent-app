package com.example.myapplication.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Korisnik
import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LokacijaViewModel() : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid

    private val userLoc = MutableStateFlow<Korisnik?>(null)
    val userLocation: StateFlow<Korisnik?> = userLoc
    private val listOrd = MutableStateFlow<List<Ordinacija>>(emptyList())
    val listOrdinacija: StateFlow<List<Ordinacija>> = listOrd
    private val nearbyList = MutableStateFlow<List<Korisnik>>(emptyList())
    val nearbyUserList: StateFlow<List<Korisnik>> = nearbyList
    private var listenerRegistration: ListenerRegistration? = null
    private var objectListenerRegistration: ListenerRegistration? = null
    private var nearListenerRegistration: ListenerRegistration? = null


    init {
        auth.currentUser?.let { user ->
            listenerRegistration = db.collection("korisnici").document(user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        userLoc.value = snapshot.toObject(Korisnik::class.java)
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
                        listOrd.value = objectsList
                    }
                }
            nearListenerRegistration =
                db.collection("korisnici").addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val nbusersList = snapshot.toObjects(Korisnik::class.java)
                        nearbyList.value = nbusersList
                    }
                }
        }
    }


}
