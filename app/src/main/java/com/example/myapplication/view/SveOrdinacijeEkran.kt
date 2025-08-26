package com.example.myapplication.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.Ordinacija
import com.example.myapplication.viewmodel.OrdinacijaViewModel

@Composable
fun SveOrdinacijeEkran(modifier: Modifier, ordinacijeViewModel : OrdinacijaViewModel) {

    val sveOrdinacije = ordinacijeViewModel.listOrdinacija.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    var selektovanaOrdinacija by remember { mutableStateOf<Ordinacija?>(null) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sve ordinacije"
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(sveOrdinacije.value) { ordinacija ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            selektovanaOrdinacija = ordinacija
                            showDialog = true
                        },
                    elevation = CardDefaults.cardElevation()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = ordinacija.naziv,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
        if (showDialog && selektovanaOrdinacija != null) {
            OcenaDialog(
                ordinacija = selektovanaOrdinacija!!,
                onDismiss = { showDialog = false },
                onSave = { ocena, komentar, doktor, procedura ->
                    ordinacijeViewModel.updateOrdinaciju(
                        ordinacijaId = selektovanaOrdinacija!!.id,
                        ocena = ocena,
                        komentar = komentar,
                        doktor = doktor,
                        procedura = procedura
                    )
                    showDialog = false
                }
            )
        }
    }
}
// fun updateOrdinaciju(ordinacijaId:
// String, ocena: Double, komentar: String, doktor: String, procedura: String)
@Composable
fun OcenaDialog(
    ordinacija: Ordinacija,
    onDismiss: () -> Unit,
    onSave: (Double, String, String,String) -> Unit
) {
    var ocenaText by remember { mutableStateOf("") }
    var komentar by remember { mutableStateOf("") }
    var doktor by remember { mutableStateOf("") }
    var procedura by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Dodaj ocenu i komentar") },
        text = {
            Column {
                OutlinedTextField(
                    value = ocenaText,
                    onValueChange = { ocenaText = it },
                    label = { Text("Ocena (1-5)") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = komentar,
                    onValueChange = { komentar = it },
                    label = { Text("Komentar") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = doktor,
                    onValueChange = { doktor = it },
                    label = { Text("Doktor") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = procedura,
                    onValueChange = { procedura = it },
                    label = { Text("Procedura") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val ocena = ocenaText.toDoubleOrNull() ?: 0.0
                if (ocena in 1.0..5.0) {
                    onSave(ocena, komentar,doktor,procedura)
                }
            }) {
                Text("Sačuvaj")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("Otkaži")
            }
        }
    )
}