package com.example.myapplication.view

import android.content.Context
import androidx.compose.runtime.Composable
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
import com.google.firebase.Firebase
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

    val korisnikService = KorisnikService(auth, firestore)
    val storageService = StorageService(firestore)

    NavHost(navController, startDestination = prvaStrana) {
        composable("auth"){
            AuthEkran(modifier, navController)
        }
        composable("login"){
            val authViewModel: AuthViewModel = viewModel()
            LoginEkran(modifier,authViewModel,navController)
        }
        composable("register"){
            val authViewModel: AuthViewModel = viewModel()
            RegistracijaEkran(modifier, authViewModel, navController)
        }
        composable("home"){
            val korisnikViewModel: KorisnikViewModel = viewModel(
                factory = KorisnikViewModelFactory(korisnikService, auth, firestore)
            )
            HomeEkran(modifier,navController,korisnikViewModel)
        }
        composable("map"){
            val viewModelLocation: LokacijaViewModel = viewModel()
            val ordinacijaViewModel: OrdinacijaViewModel = viewModel(
                factory = OrdinacijaViewModelFactory(
                    storageService,
                    viewModelLocation,
                    auth,
                    firestore
                ))
            GoogleMapaEkran(viewModelLocation,ordinacijaViewModel)
        }
        composable("trackLocation"){
            PratiLokaciju(context)
        }
        composable("ordinacija"){
            val lokacijaViewModel: LokacijaViewModel = viewModel()
            val ordinacijaViewModel: OrdinacijaViewModel = viewModel(
                factory = OrdinacijaViewModelFactory(
                    storageService,
                    lokacijaViewModel,
                    auth,
                    firestore
                )
            )
            OrdinacijaEkran(modifier,ordinacijaViewModel,
                lokacijaViewModel)
        }
        composable("rank"){
            val korisnikViewModel: KorisnikViewModel = viewModel(
                factory = KorisnikViewModelFactory(korisnikService, auth, firestore)
            )
            RankingEkran(modifier,navController,korisnikViewModel)
        }
        composable("filterOrd"){
            val lokacijaViewModel: LokacijaViewModel = viewModel()
            val ordinacijaViewModel: OrdinacijaViewModel = viewModel(
                factory = OrdinacijaViewModelFactory(
                    storageService,
                    lokacijaViewModel,
                    auth,
                    firestore,
                )
            )
            FilterOrdinacijeEkran(ordinacijaViewModel)
        }
        composable("allOrd"){
            val lokacijaViewModel: LokacijaViewModel = viewModel()
            val ordinacijaViewModel: OrdinacijaViewModel = viewModel(
                factory = OrdinacijaViewModelFactory(storageService,lokacijaViewModel, auth, firestore)
            )
            SveOrdinacijeEkran(modifier,ordinacijaViewModel)
        }
        composable("editordinacije"){
            val lokacijaViewModel: LokacijaViewModel = viewModel()
            val ordinacijaViewModel: OrdinacijaViewModel = viewModel(
                factory = OrdinacijaViewModelFactory(storageService,lokacijaViewModel, auth, firestore)
            )
            EditOrdinacijaEkran(ordinacijaViewModel)
        }
    }

}