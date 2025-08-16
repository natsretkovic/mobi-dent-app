package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Korisnik
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(email: String, password: String, ime: String, prezime: String, brTelefona: String, onResult : (Boolean,String?)-> Unit) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser!!.uid
                    val korisnik = Korisnik(ime, prezime, brTelefona)
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
    fun loginUser(email: String, password: String){

    }
}