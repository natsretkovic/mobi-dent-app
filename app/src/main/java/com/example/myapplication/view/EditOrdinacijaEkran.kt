package com.example.myapplication.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.OrdinacijaViewModel
import com.google.firebase.components.Lazy

@Composable
fun EditOrdinacijaEkran(ordinacijaViewModel: OrdinacijaViewModel){

    val usersListOrdiancija by ordinacijaViewModel.ordinacijaFilter.collectAsState()
    var updateClicked by remember { mutableStateOf(false) }
    var deleteClicked by remember { mutableStateOf(false) }

    Row(

    ){
        Button(onClick = {
            updateClicked=true
        }){
            Text("Uredi")

        }
        Button(onClick ={
            deleteClicked=true
        } ) {
            Text("Obrisi")
        }

    }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(usersListOrdiancija, key = { usersListOrdiancija -> usersListOrdiancija.id}) { ordinacija ->
                OrdinacijaCard(ordinacija, modifier = Modifier.clickable {

                    // logika za edit
                })
            }
        }
    }


