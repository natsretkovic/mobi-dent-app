package com.example.myapplication

import com.example.myapplication.model.Komentar
import com.example.myapplication.model.Ocena
import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.map

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
        val komentari = getAllKomentari(ordinacijaId)
        if(!komentari.isEmpty()) {
            for (komentar in komentari) {
                firestore.collection(collectionName).document(ordinacijaId).collection("komentari")
                    .document(komentar.id)
                    .delete()
                    .await()
            }
        }
        val ocene = getAllOcene(ordinacijaId)
        if(!ocene.isEmpty()) {
            for (ocena in ocene) {
                firestore.collection(collectionName).document(ordinacijaId).collection("ocene")
                    .document(ocena.id)
                    .delete()
                    .await()
            }
        }
        ordinacija.delete().await()
    }
    fun getAllOrdinacije(): Flow<List<Ordinacija>>{
        return firestore.collection("kolekcijaordinacija")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects()
            }
    }
    suspend fun saveKomentar(ordinacijaId :String,komentar: Komentar): String{
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Korisnik nije prijavljen.")
        val komentarSaOrdinacijaId = komentar.
            copy(ordinacijaId = ordinacijaId, userId = userId)
        return firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("komentari")
            .add(komentarSaOrdinacijaId)
            .await()
            .id

    }
    suspend fun getKomentar(ordinacijaId :String, korisnikId :String) : Komentar?{
        val snapshot = firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("komentari")
            .whereEqualTo("userId",korisnikId)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(Komentar::class.java)
    }
    suspend fun getAllKomentari(ordinacijaId: String): List<Komentar> {
        val snapshot = firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("komentari")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Komentar::class.java) }
    }
    suspend fun saveOcena(ordinacijaId :String, ocena : Ocena) :String {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Korisnik nije prijavljen.")
        val ocenaSaOrdinacijaId = ocena.copy(ordinacijaId=ordinacijaId, userId = userId)
        return firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("ocene")
            .add(ocenaSaOrdinacijaId)
            .await()
            .id
    }
    suspend fun getOcena(ordinacijaId :String, korisnikId :String) : Ocena?{
        val snapshot = firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("ocene")
            .whereEqualTo("userId",korisnikId)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(Ocena::class.java)
    }
    suspend fun getAllOcene(ordinacijaId :String) : List<Ocena> {
        val snapshot = firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("ocene")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Ocena::class.java) }
    }
    suspend fun numOfOcena(ordinacijaId : String) : Int{
        return getAllOcene(ordinacijaId).size
    }
 //ocekuje listu koja nije prazna zato mapNotNull




}