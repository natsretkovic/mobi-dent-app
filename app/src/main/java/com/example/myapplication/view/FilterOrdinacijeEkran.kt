package com.example.myapplication.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.example.myapplication.viewmodel.OrdinacijaViewModel
import androidx.compose.foundation.lazy.items
import com.example.myapplication.model.Ordinacija

@Composable
fun FilterOrdinacijeEkran(ordinacijaViewModel: OrdinacijaViewModel) {
    var searchText by remember { mutableStateOf("") }
    var ocena by remember { mutableStateOf(0.0) }

    val filteredList by ordinacijaViewModel.ordinacijaFilter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Filtriranje Ordinacija",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                ordinacijaViewModel.filterOrdinacija(it)
            },
            label = { Text("Pretraži po nazivu, doktoru ili proceduri") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList) { ordinacija ->
                OrdinacijaCard(ordinacija)
            }
        }
    }
}
    @Composable
    fun OrdinacijaCard(ordinacija: Ordinacija) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Naziv: ${ordinacija.naziv}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Doktor: ${ordinacija.doktor}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Procedura: ${ordinacija.procedura}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Ocena: ${String.format("%.1f", ordinacija.ocena)}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
