package com.example.myapplication.view

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import com.example.myapplication.model.Ordinacija
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color

@Composable
fun FilterOrdinacijeEkran(ordinacijaViewModel: OrdinacijaViewModel) {
    var searchText by remember { mutableStateOf("") }
    var ocena by remember { mutableStateOf(0.0) }
    var pocetniDatum by remember { mutableStateOf<Date?>(null) }
    var krajnjiDatum by remember { mutableStateOf<Date?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showUsersOrdinacije by remember { mutableStateOf(false) }
    var auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser!!.uid

    val filteredList by ordinacijaViewModel.ordinacijaFilter.collectAsState()
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    Box( modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center) {

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
        Slider(
            value = ocena.toFloat(),
            onValueChange = {
                ocena = it.toDouble()
                ordinacijaViewModel.filterOrdinacijaByOcena(ocena)
            },
            valueRange = 0f..5f,
            steps =9,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.padding(1.dp),
            onClick = { ordinacijaViewModel.getOrdinacijeFromUser(userId)
                        showUsersOrdinacije=true
            }
        ){
            Text("Dodate ordinacije")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(searchText.isNotBlank() || ocena>0.0 || showUsersOrdinacije) {
            LazyColumn(
                    modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList) { ordinacija ->
                    OrdinacijaCard(ordinacija)
                }
            }
        }
    }
}
}
    /*@Composable
    fun OrdinacijaCard(ordinacija: Ordinacija,showUsersOrdinacija: Boolean) {
        val color = if(showUsersOrdinacija) Color(0xFFA627F5) else Color.Gray

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = color)
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
    }*/
