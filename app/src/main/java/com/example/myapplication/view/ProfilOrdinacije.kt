package com.example.myapplication.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Komentar
import com.example.myapplication.model.Ocena
import com.example.myapplication.viewmodel.OrdinacijaViewModel

@Composable
fun ProfilOrdinacije(
    ordinacijaViewModel: OrdinacijaViewModel
) {
    val ordinacija = ordinacijaViewModel.ordinacija.collectAsState().value
    val komentari by ordinacijaViewModel.komentariList.collectAsState()
    val ocene by ordinacijaViewModel.oceneList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().safeContentPadding().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = "Stomatoloska ordinacija",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (ordinacija != null) {
            Text(
                text = ordinacija.naziv,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            Text("ucitava se...")
        }
        if (!komentari.isEmpty()) {
            LazyColumn {
                items(komentari) { komentar ->
                    val oc = ocene.find {
                        it.userId == komentar.userId && it.ordinacijaId == komentar.ordinacijaId
                    }
                    KomentarOcenaCard(komentar,oc!!)
                }
            }
        }
    }
}
@Composable
fun KomentarOcenaCard(komentar : Komentar, ocena: Ocena){
    Card(
        modifier = Modifier
            .fillMaxWidth().padding(4.dp)

    ){
        Column(modifier = Modifier.padding(4.dp)){
            Text("Doktor: ${komentar.doktor}")
            Text("Procedura: ${komentar.procedura}")
            Text("Komentar: ${komentar.tekst}")
            Text("Ocena:  ${ocena.vrednost}")
        }
    }
}
