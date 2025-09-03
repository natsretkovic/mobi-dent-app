package com.example.myapplication.view

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.Navigator
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.myapplication.viewmodel.KorisnikViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import coil.compose.AsyncImage
import com.example.myapplication.viewmodel.LokacijaViewModel
import com.example.myapplication.viewmodel.OrdinacijaViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEkran(modifier: Modifier, navContoller : NavHostController,korisnikViewModel : KorisnikViewModel
, ordinacijaViewModel: OrdinacijaViewModel, lokacijaViewModel: LokacijaViewModel){

    val korisnik by korisnikViewModel.korisnik.collectAsState()
    val imageUrl = korisnikViewModel.korisnik.collectAsState().value?.profilnaSlikaUrl

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${korisnik?.ime} ${korisnik?.prezime}", fontSize = 24.sp)
                            Text(text = "${korisnik?.username}", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = Color.Gray
                        ) {
                            imageUrl?.let { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Profilna slika",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.LightGray
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        lokacijaViewModel.initListenerForCurrentUser()
                        navContoller.navigate("map") }) {
                        Text(text = "Mapa")
                    }
                    Button(onClick = {
                        navContoller.navigate("ordinacija")
                    }) {
                        Text(text = "+")
                    }
                    Button(onClick = {
                        korisnikViewModel.getAllUsersList()
                        navContoller.navigate("rank") }) {
                        Text(text = "≡")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Button(
                onClick = { navContoller.navigate("trackLocation") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Upravljaj pracenjem lokacije")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    ordinacijaViewModel.getOrdinacijeFromUser()
                    navContoller.navigate("editordinacije") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Upravljaj dodatim ordinacijama")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    Firebase.auth.signOut()
                    navContoller.navigate("auth") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Odjavi se")
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { navContoller.navigate("filterOrd") }) {
                    Text(text = "Filtriranje ordinacija")
                }
                Button(onClick = { navContoller.navigate("allOrd") }) {
                    Text(text = "Sve ordinacije")
                }
            }
        }
    }
}

