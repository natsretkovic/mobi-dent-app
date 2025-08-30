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
import androidx.navigation.NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterOrdinacijeEkran(ordinacijaViewModel: OrdinacijaViewModel, navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var ocena by remember { mutableStateOf(0.0) }
    var showUsersOrdinacije by remember { mutableStateOf(false) }
    var auth = FirebaseAuth.getInstance()
    var izabranDatum by remember { mutableStateOf(false) }
    val userId = auth.currentUser!!.uid

    val filteredList by ordinacijaViewModel.ordinacijaFilter.collectAsState()
    val state = rememberDateRangePickerState()
    val formater = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

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
                    ordinacijaViewModel.filterOrdinacijaPoKomentarima(it)

                },
                label = { Text("Pretrazi po nazivu, doktoru ili proceduri") },
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
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            DateRangePicker(
                state = state,
                modifier = Modifier,
                title = {
                    Text(
                        text = "Izaberi datum",
                        modifier = Modifier.padding(16.dp)
                    )
                },
                headline = {
                    val pocetni = state.selectedStartDateMillis?.let { formater.format(Date(it)) }
                    val krajnji = state.selectedEndDateMillis?.let { formater.format(Date(it)) }
                    ordinacijaViewModel.filterOrdinacijaByDatum(pocetni,krajnji)
                    izabranDatum=true
                    Text(
                        text = "Od: ${pocetni}   Do: ${krajnji}",
                        modifier = Modifier.padding(16.dp)
                    )
                },
                showModeToggle = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (searchText.isNotBlank() || ocena > 0.0 || showUsersOrdinacije || izabranDatum) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList) { ordinacija ->
                        OrdinacijaCard(ordinacija) {
                            println("Kliknuto na ${ordinacija.naziv} njen id ${ordinacija.id}")
                            ordinacijaViewModel.setCurrentOrdinacija(ordinacija)
                            navController.navigate("ordinacijaprofil")
                        }
                    }
                }
            }
        }
    }
}

