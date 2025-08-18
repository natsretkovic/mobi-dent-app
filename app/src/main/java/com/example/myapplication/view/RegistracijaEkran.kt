package com.example.myapplication.view

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController
import com.example.myapplication.viewmodel.AuthViewModel


@Composable
fun RegistracijaEkran(modifier: Modifier = Modifier, viewModel: AuthViewModel, navContoller: NavHostController){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var ime by remember { mutableStateOf("") }
    var prezime by remember { mutableStateOf("") }
    var brTelefona by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var slikaUri by remember {mutableStateOf<Uri?>(null)}
    var username by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") })
            TextField(
                value = ime,
                onValueChange = { ime = it },
                label = { Text("Ime") })
            TextField(
                value = prezime,
                onValueChange = { prezime = it },
                label = { Text("Prezime") })
            TextField(
                value = brTelefona,
                onValueChange = { brTelefona = it },
                label = { Text("Broj Telefona") })
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") })
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()

            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.registerUser(
                    email,
                    password,
                    ime,
                    prezime,
                    brTelefona,
                    username
                ) { success, errorMessage ->
                    if (success) {
                        message = "Registracija je uspesna!"
                        navContoller.navigate("auth"){
                            popUpTo("register") {
                                inclusive = true
                            }
                        }
                    } else {
                        message = errorMessage ?: "Doslo je do greske"
                    }
                }
            }, modifier = Modifier.fillMaxWidth())
            {
                Text(text = "Registruj se")
            }

        }
    }
    }
