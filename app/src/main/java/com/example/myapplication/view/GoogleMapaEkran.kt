package com.example.myapplication.view

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.google.maps.android.compose.rememberMarkerState
import androidx.core.graphics.scale
import com.example.myapplication.model.Ordinacija


@Composable
fun GoogleMapaEkran(
    viewModel: LokacijaViewModel,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(43.321445, 21.896104), 12f)
    },
    properties: MapProperties = MapProperties(),
    uiSettings: MapUiSettings = MapUiSettings(),
) {

    val userLocation = viewModel.userLocation.collectAsState(initial = null)
    val ordinacije = viewModel.listOrdinacija.collectAsState(initial = emptyList())
    var showNearbyOnly by remember { mutableStateOf(false) }

    val ordinacijeZaPrikaz : List<Ordinacija>
        if (showNearbyOnly) {
        ordinacijeZaPrikaz = viewModel.getOrdinacijaRadius(1000.0) // 1 km
    } else {
        ordinacijeZaPrikaz=ordinacije.value
    }

    Box(modifier= Modifier.fillMaxSize()) {
        GoogleMap(
           // modifier = modifier.fillMaxSize(),
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
            }
            userLocation.value?.let { loc ->
                val newPosition = LatLng(loc.latitude, loc.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(newPosition, 15f)
            }
            ordinacijeZaPrikaz.forEach { ordinacija ->
                Marker(
                    state = rememberMarkerState(
                        position = LatLng(ordinacija.latitude, ordinacija.longitude)

                    ),
                    title = "Ordinacija ${ordinacija.naziv}",
                    icon = scaledMarkerIcon
                )
            }


        }
        Button(
            onClick = { showNearbyOnly = false },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            if(!showNearbyOnly) {
                Text("Obican prikaz")
            }
            Text("Filtrijaj po radijusu")

        }
    }
}