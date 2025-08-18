package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Korisnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(email: String, password: String, ime: String, prezime: String, brTelefona: String,username :String, onResult : (Boolean,String?)-> Unit) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid
                    val korisnik = Korisnik(ime, prezime, brTelefona,username,email)
                    db.collection("korisnici").document(userId).set(korisnik).addOnCompleteListener {
                        dbTask -> if(dbTask.isSuccessful){
                            onResult(true,null)
                    }
                        else{
                            onResult(false, "Nesto je lose")
                        }
                    }
                }
                else{ onResult(false,
                    task.exception?.localizedMessage) }
            }
    }
    fun loginUser(
        userName: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()

        isUsernameTaken(userName) { exists ->
            if (!exists) {
                onResult(false, "Username not found")
            } else {
                getEmailByUsername(userName) { email ->
                    if (email != null) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, task.exception?.localizedMessage)
                                }
                            }
                    } else {
                        onResult(false, "Email not found for this username")
                    }
                }
            }
        }
    }
    fun queryUserByUsername(
        inputUsername: String,
        onResult: (QuerySnapshot?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("korisnici")
            .whereEqualTo("username", inputUsername)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onResult(null)
            }
    }
    fun getEmailByUsername(inputUsername: String, onResult: (String?) -> Unit) {
        queryUserByUsername(inputUsername) { snapshot ->
            if (snapshot != null && !snapshot.isEmpty) {
                val document = snapshot.documents[0]
                val email = document.getString("email")
                Log.d("LoginDebug", "Pronađen email: $email")
                onResult(email)
            } else {
                Log.d("LoginDebug", "Nije pronadjen email:")
                onResult(null)
            }
        }
    }
    fun isUsernameTaken(inputUsername: String, onResult: (Boolean) -> Unit) {
        queryUserByUsername(inputUsername) { snapshot ->
            if (snapshot != null && !snapshot.isEmpty) {
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

}