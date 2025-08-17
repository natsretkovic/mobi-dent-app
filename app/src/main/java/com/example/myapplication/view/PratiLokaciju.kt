package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.viewmodel.LocationService

@Composable
fun PratiLokaciju() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                val intent = Intent(context, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                }
                context.startService(intent)
            }) {
                Text("Start")
            }

            Button(onClick = {
                val intent = Intent(context, LocationService::class.java).apply {
                    action = LocationService.ACTION_STOP
                }
                context.startService(intent)
            }) {
                Text("Stop")
            }
        }
    }
}
