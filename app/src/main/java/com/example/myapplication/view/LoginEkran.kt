package com.example.myapplication.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.viewmodel.AuthViewModel

@Composable
fun LoginEkran(modifier: Modifier, viewModel: AuthViewModel, navController: NavHostController) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember {mutableStateOf("")}
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
            Text("Ulogujte se na vas nalog koristeci korisnicko ime i lozinku koju ste naveli pri registraciji")
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Korisnicko ime") })
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Lozinka") },
                visualTransformation = PasswordVisualTransformation(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.loginUser(username, password,) { success, errorMessage ->
                    if (success) {
                        navController.navigate("home") {
                            popUpTo("auth") {
                                inclusive = true
                            }
                        }
                    }
                    else{
                        message = errorMessage ?: "Doslo je do greske, proverite podatke"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                    }
                }
            }) {
                Text("Ulogujte se")
            }
        }
    }
}
