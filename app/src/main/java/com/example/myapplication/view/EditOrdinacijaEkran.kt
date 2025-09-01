package com.example.myapplication.view


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Ordinacija
import com.example.myapplication.viewmodel.OrdinacijaViewModel


@Composable
fun EditOrdinacijaEkran(ordinacijaViewModel: OrdinacijaViewModel) {

    val usersListOrdiancija by ordinacijaViewModel.ordinacijaFilter.collectAsState()
    var selectedOrdinacija by remember { mutableStateOf<Ordinacija?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize().safeContentPadding()
            .padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            Button(
                onClick = {
                    selectedOrdinacija?.let {
                        ordinacijaViewModel.deleteOrdinacija(it.id)
                        selectedOrdinacija = null
                    }
                },
                enabled = selectedOrdinacija != null
            ) {
                Text("Obrisi")
            }

        }
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()

        ) {
            items(
                items = usersListOrdiancija,
                key = { it.id }
            ) { ordinacija ->
                OrdinacijaCard(
                    ordinacija = ordinacija,
                    onClick = {
                        selectedOrdinacija = ordinacija
                    }
                )
            }
        }
    }
}
