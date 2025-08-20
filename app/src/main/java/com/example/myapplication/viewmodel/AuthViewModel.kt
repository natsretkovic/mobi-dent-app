package com.example.myapplication.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Korisnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun registerUser(
        email: String,
        password: String,
        ime: String,
        prezime: String,
        brTelefona: String,
        username: String,
        onResult: (Boolean, String?) -> Unit,

        ) {
        viewModelScope.launch {
            try {
                if (isUsernameTaken(username)) {
                    onResult(false, "Korisnicko ime je zauzeto")
                    return@launch
                }
                val userCredential =
                    auth.createUserWithEmailAndPassword(email, password).await()
                val userId = userCredential.user?.uid ?: throw IllegalStateException("User ID not found.")
               /* val slikaUrl: String? = if (slikaUri != null) {
                    val storageRef = storage.reference.child("profilna_slika/${userId}.jpg")
                        storageRef.putFile(slikaUri).await()
                        storageRef.downloadUrl.await().toString()
                    } else {
                        null
                    }
                    *\
                */
                    val korisnik = Korisnik(ime, prezime, brTelefona, username, email)
                    db.collection("korisnici").document(userId).set(korisnik).await()
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Došlo je do greške.")
            }
        }
    }
    fun loginUser(
        userName: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                    val email = getEmailByUsername(userName)
                    if (email == null) {
                        onResult(false, "Korisničko ime nije pronađeno.")
                        return@launch
                    }
                    auth.signInWithEmailAndPassword(email, password).await()
                    onResult(true, null)
                } catch (e: Exception) {
                    onResult(false, e.localizedMessage ?: "Došlo je do greške.")
                }
            }
        }
   private suspend fun getEmailByUsername(inputUsername: String): String? {
        val snapshot = db.collection("korisnici")
            .whereEqualTo("username", inputUsername)
            .get()
            .await()
        if (snapshot.isEmpty) {
            return null
        }
       return snapshot.documents[0].getString("email")
    }
    private suspend fun isUsernameTaken(inputUsername: String): Boolean {
        val snapshot = db.collection("korisnici")
            .whereEqualTo("username", inputUsername)
            .get()
            .await()
        if(snapshot.isEmpty){
            return false
        }
        return true
    }

}
