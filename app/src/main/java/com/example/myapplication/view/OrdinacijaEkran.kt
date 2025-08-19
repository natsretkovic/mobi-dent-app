package com.example.myapplication.view

import android.net.Uri
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Ordinacija
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.LokacijaViewModel
import com.example.myapplication.viewmodel.OrdinacijaViewModel

@Composable
fun OrdinacijaEkran(
    modifier: Modifier,
    viewModel: OrdinacijaViewModel,
    viewModelLoc: LokacijaViewModel
) {
    var naziv by remember { mutableStateOf("") }
    var doktor by remember { mutableStateOf("") }
    var procedura by remember { mutableStateOf("") }
    var komentar by remember { mutableStateOf("") }
    var ocena by remember { mutableStateOf("") }
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
                label = { Text("Naziv ordinacije") })
            TextField(
                value = doktor,
                onValueChange = { doktor = it },
                label = { Text("Doktor koji je vrsio proceduru") })
            TextField(
                value = procedura,
                onValueChange = { procedura = it },
                label = { Text("Procedura koju ste imali") })
            TextField(
                value = komentar,
                onValueChange = { komentar = it },
                label = { Text("Dodajte komentar") })
            TextField(
                value = ocena,
                onValueChange = { ocena = it },
                label = { Text("Ocena") })
            Button(
                onClick = {
                    viewModel.addOrdinacija(
                        naziv,
                        doktor,
                        procedura,
                        ocena.toDouble(),
                        komentar
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Dodaj")
            }
        }
    }
}