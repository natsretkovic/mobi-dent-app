package com.example.myapplication

import com.example.myapplication.model.Komentar
import com.example.myapplication.model.Ocena
import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.map
import java.util.Date

class StorageService(private val firestore: FirebaseFirestore) {
    private val auth = FirebaseAuth.getInstance()
    private val collectionName = "kolekcijaordinacija"
    suspend fun save(ordinacija :Ordinacija): String {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Korisnik nije prijavljen.")
        val ordinacijaSaUserId = ordinacija.copy(userId = userId)
        return firestore.collection(collectionName).add(ordinacijaSaUserId).await().id
    }
    suspend fun delete(ordinacijaId:String) {
        val ordinacija = firestore.collection(collectionName).document(ordinacijaId)
        val komentari = ordinacija.collection("komentari").get().await()
        val ocene = ordinacija.collection("ocene").get().await()

        firestore.runBatch { batch ->
            for (komentar in komentari.documents) {
                batch.delete(komentar.reference)
            }
            for (ocena in ocene.documents) {
                batch.delete(ocena.reference)
            }
            batch.delete(ordinacija)
        }.await()

    }
    fun getAllOrdinacije(): Flow<List<Ordinacija>>{
        return firestore.collection(collectionName)
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
    fun getAllKomentari(ordinacijaId: String): Flow<List<Komentar>> {
        return firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("komentari")
            .snapshots()
            .map{
                snapshots ->
                snapshots.toObjects(Komentar::class.java)
            }
    }
    suspend fun saveOcena(ordinacijaId :String, ocena : Ocena) :String {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Korisnik nije prijavljen.")
        val ocenaSaOrdinacijaId = ocena.copy(ordinacijaId=ordinacijaId, userId = userId)

        val ocenaId = firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("ocene")
            .add(ocenaSaOrdinacijaId)
            .await().id

        averageOcena(ordinacijaId)

        return ocenaId
    }
    suspend fun averageOcena(ordinacijaId: String) {
        val snapshot = firestore.collection(collectionName)
            .document(ordinacijaId)
            .collection("ocene")
            .get()
            .await()

        val sveOcene = snapshot.toObjects(Ocena::class.java)

        val totalOcena = sveOcene.sumOf { ocene -> ocene.vrednost }
        val novaProsecnaOcena = if (sveOcene.isNotEmpty()) totalOcena / sveOcene.size else 0.0

        firestore.collection(collectionName)
            .document(ordinacijaId)
            .update("prosecnaOcena", novaProsecnaOcena, "brojOcena", sveOcene.size)
            .await()

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
     fun getAllOcene(ordinacijaId :String) : Flow<List<Ocena>> {
        return  firestore.collection(collectionName)
             .document(ordinacijaId)
             .collection("ocene")
             .snapshots()
             .map { snapshot ->
                 snapshot.toObjects(Ocena::class.java)
             }
     }
    fun filterByNaziv(naziv: String): Flow<List<Ordinacija>> {
        return firestore.collection(collectionName)
            .whereEqualTo("naziv", naziv)
            .snapshots()
            .map { it.toObjects(Ordinacija::class.java) }
    }
    fun filterByAverageOcena(ocena: Double): Flow<List<Ordinacija>> {
        return firestore.collection(collectionName)
            .whereEqualTo("prosecnaOcena", ocena)
            .snapshots()
            .map { it.toObjects(Ordinacija::class.java) }
    }
    fun filterByDatum(pocetniDatum: Date?, krajnjiDatum: Date?): Flow<List<Ordinacija>> {
        var query: Query = firestore.collection(collectionName)
        if (pocetniDatum != null) {
            query = query.whereGreaterThanOrEqualTo("timestamp", pocetniDatum)
        }

        if (krajnjiDatum != null) {
            query = query.whereLessThanOrEqualTo("timestamp", krajnjiDatum)
        }

        return query.snapshots()
            .map { it.toObjects(Ordinacija::class.java) }
    }
    fun getOrdinacijeByUser(userId: String): Flow<List<Ordinacija>> {
        return firestore.collection(collectionName)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Ordinacija::class.java)
            }
    }
}
 //ocekuje listu koja nije prazna zato mapNotNull



