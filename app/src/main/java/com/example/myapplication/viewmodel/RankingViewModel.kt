package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.Korisnik
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RankingViewModel(private val firestore: FirebaseFirestore) : ViewModel() {
    private val rankList = MutableStateFlow<List<Korisnik>>(emptyList())
    val list: StateFlow<List<Korisnik>> = rankList
    val ranklist: StateFlow<List<Korisnik>> = rankList
    init{
        getAllUsers()
    }
    private fun getAllUsers(){
        viewModelScope.launch {
            val usersCollection = firestore.collection("korisnici")
            val querySnapshot = usersCollection
                .orderBy("poeni", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val users = querySnapshot.toObjects(Korisnik::class.java)
            rankList.value = users
        }
    }
}