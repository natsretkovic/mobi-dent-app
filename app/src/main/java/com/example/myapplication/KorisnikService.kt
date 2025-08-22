package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.model.Korisnik
import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class KorisnikService(private val auth: FirebaseAuth,
                      private val firestore: FirebaseFirestore)
{
    //private val userId = auth.currentUser?.uid
    private val collectionName="korisnici"

    suspend fun getKorisnik(id:String): Korisnik? {
        if(id==null){
            return null
        }
        val snapshot = firestore.collection(collectionName).document(id).get().await()
        return snapshot.toObject(Korisnik::class.java)
    }

    suspend fun updateKorisnik(id :String, ime: String,prezime:String, brojTelefona:String) {
        if(id==null){
            return
        }
        val newkorisnik = Korisnik(ime,prezime,brojTelefona)
        val snapshot = firestore.collection(collectionName).document(id)
            .set(newkorisnik, SetOptions.merge()).await()
    }
}