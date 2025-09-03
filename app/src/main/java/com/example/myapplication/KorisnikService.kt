package com.example.myapplication

import com.example.myapplication.model.Korisnik
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class KorisnikService(private val firestore: FirebaseFirestore)
{
    private val collectionName="korisnici"
    suspend fun getKorisnik(id:String): Korisnik? {
        if(id.isNullOrEmpty()){
            return null
        }
        val snapshot = firestore.collection(collectionName).document(id).get().await()
        return snapshot.toObject(Korisnik::class.java)
    }
     fun getAllUsers() : Flow<List<Korisnik>> {
         return firestore.collection(collectionName)
             .snapshots()
             .map{ snapshot ->
             snapshot.toObjects()
        }
    }
}