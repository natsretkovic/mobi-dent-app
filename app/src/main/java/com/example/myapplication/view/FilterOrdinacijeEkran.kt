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
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterOrdinacijeEkran(ordinacijaViewModel: OrdinacijaViewModel, navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var ocena by remember { mutableStateOf(0.0) }
    var showUsersOrdinacije by remember { mutableStateOf(false) }
    var izabranDatum by remember { mutableStateOf(false) }
    val filteredList by ordinacijaViewModel.ordinacijaFilter.collectAsState()
    var pocetniDatum by remember { mutableStateOf<String?>("") }
    var krajnjiDatum by remember  { mutableStateOf<String?>("") }
    var buttonClicked by remember {mutableStateOf(false)}

    /*LaunchedEffect(Unit) {
        ordinacijaViewModel.resetFilter()
    }*/

    Box(
        modifier = Modifier.fillMaxWidth().safeContentPadding(),
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

                },
                singleLine = true,
                label = { Text("Pretrazi ordinaciju po nazivu") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Slider(
                value = ocena.toFloat(),
                onValueChange = {
                    ocena = it.toDouble()
                    ordinacijaViewModel.filterOrdinacijaByAverageOcena(ocena)
                },
                valueRange = 0f..5f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = pocetniDatum ?: "",
                        onValueChange = { pocetniDatum = it },
                        label = { Text("Od yyyy-mm-dd") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = krajnjiDatum ?: "",
                        onValueChange = { krajnjiDatum = it },
                        label = { Text("Do yyyy-mm-dd") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val start: Date? = try {
                            if (pocetniDatum?.isNotBlank()==true) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .parse(pocetniDatum) else null
                        } catch (e: Exception) { null }

                        val end: Date? = try {
                            if (krajnjiDatum?.isNotBlank()==true) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(krajnjiDatum) else null
                        } catch (e: Exception) { null }
                        ordinacijaViewModel.filterOrdinacijaByDatum(start, end)
                        buttonClicked=true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Primeni filter po datumu")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (searchText.isNotBlank() || ocena > 0.0 || showUsersOrdinacije || izabranDatum ||
                buttonClicked) {
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

