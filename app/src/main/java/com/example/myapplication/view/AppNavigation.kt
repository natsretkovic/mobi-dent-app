package com.example.myapplication.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.PratiLokaciju
import com.example.myapplication.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
@Composable
fun AppNavigation(modifier: Modifier = Modifier, viewModel: AuthViewModel) {
    val navController = rememberNavController()
    val ulogovan = Firebase.auth.currentUser!=null
    val prvaStrana = if(ulogovan) "home" else "auth"

    NavHost(navController, startDestination = prvaStrana) {
        composable("auth"){
            AuthEkran(modifier, navController)
        }
        composable("login"){
            LoginEkran(modifier,viewModel,navController)
        }
        composable("register"){
            RegistracijaEkran(modifier, viewModel, navController)
        }
        composable("home"){
            HomeEkran(modifier,navController)
        }
        composable("map"){
            GoogleMapaEkran()
        }
        composable("trackLocation"){
            PratiLokaciju()
        }
    }

}