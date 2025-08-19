package com.example.myapplication

import com.example.myapplication.model.Ordinacija
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class StorageService(private val firestore: FirebaseFirestore) {

    suspend fun save(ordinacija :Ordinacija): String {
        return firestore.collection("kolekcijaordinacija").add(ordinacija).await().id
    }

    suspend fun update(ordinacija: Ordinacija) : String {
        val updatedOrdinacija = ordinacija.copy()
        return firestore.collection("kolekcijaordinacija")
            .add(updatedOrdinacija).await().id
    }


}