package com.example.myapplication.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.viewmodel.LokacijaViewModel
import com.example.myapplication.viewmodel.OrdinacijaViewModel

@Composable
fun OrdinacijaEkran(
    modifier: Modifier = Modifier,
    viewModel: OrdinacijaViewModel,
    viewModelLoc: LokacijaViewModel
) {
    var naziv by remember { mutableStateOf("") }
    var doktor by remember { mutableStateOf("") }
    var procedura by remember { mutableStateOf("") }
    var komentar by remember { mutableStateOf("") }
    var ocenaText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            TextField(
                value = naziv,
                onValueChange = { naziv = it },
                label = { Text("Naziv ordinacije") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = doktor,
                onValueChange = { doktor = it },
                label = { Text("Doktor koji je vrsio proceduru") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = procedura,
                onValueChange = { procedura = it },
                label = { Text("Procedura koju ste imali") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = komentar,
                onValueChange = { komentar = it },
                label = { Text("Dodajte komentar") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = ocenaText,
                onValueChange = { ocenaText = it },
                label = { Text("Ocena 1.0 - 5.0") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (error.isNotEmpty()) {
                Text(text = error, color = androidx.compose.ui.graphics.Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    val ocenaDouble = ocenaText.toDoubleOrNull()
                    if (naziv.isBlank() || doktor.isBlank() || procedura.isBlank() || komentar.isBlank() || ocenaDouble == null) {
                        error = "Molimo popunite sva polja ispravno"
                        return@Button
                    }
                    error = ""
                    viewModel.addOrdinacija(naziv,doktor,procedura,ocenaDouble,komentar)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Dodaj")
            }
        }
    }
}
