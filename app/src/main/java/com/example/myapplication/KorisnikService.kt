package com.example.myapplication

import com.example.myapplication.model.Korisnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class KorisnikService(private val auth: FirebaseAuth,
                      private val firestore: FirebaseFirestore)
{
    //private val userId = auth.currentUser?.uid
    private val collectionName="korisnici"
    suspend fun getKorisnik(id:String): Korisnik? {
        if(id.isNullOrEmpty()){
            return null
        }
        val snapshot = firestore.collection(collectionName).document(id).get().await()
        return snapshot.toObject(Korisnik::class.java)
    }

    suspend fun updateKorisnik(id :String, ime: String,prezime:String, brojTelefona:String) {
        if(id.isNullOrEmpty()){
            return
        }
        val newkorisnik = Korisnik(ime,prezime,brojTelefona)
        val snapshot = firestore.collection(collectionName).document(id)
            .set(newkorisnik, SetOptions.merge()).await()
    }
     fun getAllUsers() : Flow<List<Korisnik>> {
         return firestore.collection(collectionName)
             .snapshots()
             .map{ snapshot ->
             snapshot.toObjects()
        }
    }
    fun getUser(id : String) : Flow<Korisnik?>{
        if (id.isNullOrEmpty()) {
            return emptyFlow()
        }
        return firestore.collection(collectionName)
            .document(id)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(Korisnik::class.java)
            }
    }
}