package com.example.myapplication.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.KorisnikService
import com.example.myapplication.StorageService
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.KorisnikViewModel
import com.example.myapplication.viewmodel.KorisnikViewModelFactory
import com.example.myapplication.viewmodel.LokacijaViewModel
import com.example.myapplication.viewmodel.OrdinacijaViewModel
import com.example.myapplication.viewmodel.OrdinacijaViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val ulogovan = auth.currentUser != null
    val prvaStrana = if (ulogovan) "home" else "auth"
    val context = LocalContext.current

    val korisnikService = remember { KorisnikService(firestore) }
    val storageService = remember { StorageService(firestore) }

    val korisnikViewModel: KorisnikViewModel = viewModel(
        factory = KorisnikViewModelFactory(korisnikService)
    )
    val lokacijaViewModel: LokacijaViewModel = viewModel()
    val ordinacijaViewModel: OrdinacijaViewModel = viewModel(
        factory = OrdinacijaViewModelFactory(
            storageService,
            auth,
            firestore, lokacijaViewModel
        )
    )



    NavHost(navController, startDestination = prvaStrana) {
        composable("auth"){
            AuthEkran(modifier, navController)
        }
        composable("login"){
            val authViewModel: AuthViewModel = viewModel()
            LoginEkran(modifier,authViewModel,navController,lokacijaViewModel)
        }
        composable("register"){
            val authViewModel: AuthViewModel = viewModel()
            RegistracijaEkran(modifier, authViewModel, navController)
        }
        composable("home"){
            HomeEkran(modifier,navController,korisnikViewModel,ordinacijaViewModel,lokacijaViewModel)
        }
        composable("map"){
            GoogleMapaEkran(lokacijaViewModel,ordinacijaViewModel)
        }
        composable("trackLocation"){
            PratiLokaciju(context)
        }
        composable("ordinacija"){
            OrdinacijaEkran(modifier,ordinacijaViewModel)
        }
        composable("rank"){
            RankingEkran(modifier,korisnikViewModel)
        }
        composable("filterOrd"){
            FilterOrdinacijeEkran(ordinacijaViewModel,navController)
        }
        composable("allOrd"){
            SveOrdinacijeEkran(modifier,ordinacijaViewModel,navController)
        }
        composable("editordinacije"){
            EditOrdinacijaEkran(ordinacijaViewModel)
        }
        composable("ordinacijaprofil") {
            ProfilOrdinacije(ordinacijaViewModel)
        }
    }

}