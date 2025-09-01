package com.example.myapplication.view

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.viewmodel.LokacijaViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.core.graphics.scale
import com.example.myapplication.model.Ordinacija
import com.example.myapplication.viewmodel.OrdinacijaViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.Circle


@Composable
fun GoogleMapaEkran(
    lokacijaViewModel: LokacijaViewModel,
    viewModelOrdinacija: OrdinacijaViewModel,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(43.321445, 21.896104), 12f)
    },
    properties: MapProperties = MapProperties(),
    uiSettings: MapUiSettings = MapUiSettings(),
) {
    val userLocation = lokacijaViewModel.listenerKorisnik.collectAsState(null)
    val ordinacije = lokacijaViewModel.listenerOrdinacija.collectAsState(emptyList())
    val filteredOrdinacije = viewModelOrdinacija.ordinacijaFilter.collectAsState(emptyList())
    var showOrdinacijeRadius by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var inputRadiusText by remember { mutableStateOf("") }
    var inputRadius by remember { mutableStateOf(0.0) }

    var ordinacijeZaPrikaz : List<Ordinacija>
    if (showOrdinacijeRadius) {
        ordinacijeZaPrikaz = lokacijaViewModel.getOrdinacijaRadius(inputRadius)
    } else if (searchText.isNotBlank()) {
        ordinacijeZaPrikaz = filteredOrdinacije.value
    } else {
        ordinacijeZaPrikaz = ordinacije.value
    }
    LaunchedEffect(userLocation.value) {
        userLocation.value?.let { loc ->
            val newPosition = LatLng(loc.latitude, loc.longitude)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(newPosition, 15f)
                ),
                durationMs = 1000
            )
        }
    }

    Column(modifier= Modifier.fillMaxSize().safeContentPadding()) {
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                viewModelOrdinacija.filterOrdinacija(it)
            },
            label = { Text("Pretraži po nazivu, doktoru ili proceduri") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        GoogleMap(
            modifier = modifier.fillMaxHeight(0.8f),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings
        ) {
            val drawable = ContextCompat.getDrawable(
                LocalContext.current,
                R.drawable.toothmarker
            ) as BitmapDrawable
            val bitmap = drawable.bitmap
            val scaledBitmap = bitmap.scale(80, 80, false)
            val scaledMarkerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

            userLocation.value?.let { loc ->
                val userLatLng = LatLng(loc.latitude, loc.longitude)
                Marker(
                    state = MarkerState(position = userLatLng),
                    title = "Vi ste ovde",
                    snippet = "Trenutna lokacija"
                )
                if (showOrdinacijeRadius) {
                    Circle(
                        center = userLatLng,
                        radius = inputRadius,
                        strokeColor = Color.Black,
                        strokeWidth = 3f,
                    )
                }
            }
            userLocation.value?.let { loc ->
                val newPosition = LatLng(loc.latitude, loc.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(newPosition, 15f)
            }
            ordinacijeZaPrikaz.forEach { ordinacija ->
                key(ordinacija.id) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(ordinacija.latitude, ordinacija.longitude)
                        ),
                        title = "Ordinacija ${ordinacija.naziv}",
                        icon = scaledMarkerIcon
                    )
                }
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(3.dp).align(Alignment.End),
        ) {
            Button(
                onClick = {
                    if (!showOrdinacijeRadius) {
                        showOrdinacijeRadius = true
                    } else {
                        showOrdinacijeRadius = false
                    }
                },
            ) {
                if (showOrdinacijeRadius) {
                    Text("Obican prikaz")
                } else {
                    Text("Filtriraj po radijusu")
                }

            }
            TextField(
                value = inputRadiusText,
                onValueChange = { txt ->
                    if (txt.all { it.isDigit() }) {
                        inputRadiusText = txt
                        inputRadius = txt.toDoubleOrNull()?.times(1000) ?: 0.0
                    }
                },
                label = { Text("Unesite radijus") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
