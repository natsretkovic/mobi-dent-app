package com.example.myapplication.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.viewmodel.AuthViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier, viewModel: AuthViewModel) {
    val navController = rememberNavController();

    NavHost(navController, startDestination = "auth") {
        composable("auth"){
            AuthEkran(modifier, navController)
        }
        composable("login"){
            LoginEkran(modifier,viewModel)
        }
        composable("register"){
            RegistracijaEkran(modifier, viewModel)
        }
    }

}