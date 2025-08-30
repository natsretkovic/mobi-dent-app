package com.example.myapplication.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.OrdinacijaViewModel

@Composable
fun ProfilOrdinacije(
    ordinacijaViewModel: OrdinacijaViewModel
) {
    val ordinacija = ordinacijaViewModel.selectedOrdinacija.collectAsState().value
    val komentari by ordinacijaViewModel.komentariList.collectAsState(emptyList())
    val ocene by ordinacijaViewModel.oceneList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        if (ordinacija != null) {
            Text("Naziv ${ordinacija.naziv}")
        }
            else{
                Text("niSTA")
            }
           /* if(!komentari.isEmpty()) {
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
                }*/
        }
    }
