package com.example.myapplication

import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class StorageService(private val firestore: FirebaseFirestore) {
    private val auth = FirebaseAuth.getInstance()
    private val collectionName = "kolekcijaordinacija"
    suspend fun save(ordinacija :Ordinacija): String {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Korisnik nije prijavljen.")
        val ordinacijaSaUserId = ordinacija.copy(userId = userId)
        return firestore.collection(collectionName).add(ordinacijaSaUserId).await().id
    }

    suspend fun update(ordinacija: Ordinacija)  {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Korisnik nije prijavljen.")
        val ordinacijaZaUpdate = ordinacija.copy(userId = userId)
        firestore.collection(collectionName)
            .document(ordinacija.id)
            .set(ordinacijaZaUpdate)
            .await()
    }
    suspend fun delete(ordinacijaId:String) {
        val ordinacija = firestore.collection(collectionName).document(ordinacijaId)
        ordinacija.delete().await()
    }
    suspend fun getAllOrdinacije() : List<Ordinacija>{
        val snapshot = firestore.collection(collectionName).get().await()
        return snapshot.documents.mapNotNull { it.toObject(Ordinacija::class.java) }
    }
 //ocekuje listu koja nije prazna zato mapNotNull




}