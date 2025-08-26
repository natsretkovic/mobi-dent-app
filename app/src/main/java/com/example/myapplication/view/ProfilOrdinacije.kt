package com.example.myapplication.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.OrdinacijaViewModel

@Composable
fun ProfilOrdinacije(
    ordinacijaViewModel: OrdinacijaViewModel,
    onBack: () -> Unit
) {
    val ordinacija by ordinacijaViewModel.selectedOrdinacija.collectAsState()
    val komentari = ordinacijaViewModel.komentariList.collectAsState(emptyList())
    val ocene by ordinacijaViewModel.oceneList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { onBack() }) { Text("Nazad") }

        ordinacija.let { ord ->
            Text("Naziv: ${ord.naziv}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Prosečna ocena: ${if (ocene.isNotEmpty()) ocene.map { it.vrednost }.average() else 0.0}")
            Text("Broj ocena: ${ocene.size}")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Komentari:", fontWeight = FontWeight.Bold)
            LazyColumn {
                items(komentari) { komentar ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Doktor: ${komentar.doktor}")
                            Text("Procedura: ${komentar.procedura}")
                            Text("Komentar: ${komentar.tekst}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* otvori dijalog za dodavanje ocene i komentara */ }) {
                Text("Dodaj ocenu i komentar")
            }
        }
    }
}}