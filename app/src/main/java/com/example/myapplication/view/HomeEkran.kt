package com.example.myapplication.view

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.Navigator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomeEkran(modifier: Modifier, navContoller : NavHostController){
    var searchText by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column() {
            Text("Ovo je home screen")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                Firebase.auth.signOut()
                navContoller.navigate("auth"){
                    popUpTo("home"){
                        inclusive = true
                    }
                }
            }) {

                Text("Odjavi se")
            }
            Spacer(modifier = Modifier.height(16.dp))
            val context = LocalContext.current

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                } else {
                    Toast.makeText(context, "Lokacija nije odobrena", Toast.LENGTH_SHORT).show()
                }
            }
            Button(onClick = {
                navContoller.navigate("map")
            }){
                Text("Klik za mapu")
            }
            Button(onClick = {
                navContoller.navigate("trackLocation")
            }){
                Text("Klik za odobrenje")
            }
            Button(onClick = {
                navContoller.navigate("ordinacija")
            }){
                Text("Upravljajte ordinacijom")
            }
            Button(onClick = {
                navContoller.navigate("rank")
            }){
                Text("Pogledajte ranking")
            }

        }

    }

}
