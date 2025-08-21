package com.example.myapplication.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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


@Composable
fun GoogleMapaEkran( viewModel: LokacijaViewModel,
                     modifier: Modifier = Modifier,
                     cameraPositionState: CameraPositionState = rememberCameraPositionState {
                         position = CameraPosition.fromLatLngZoom(LatLng(43.321445, 21.896104), 12f)
                     },
                     properties: MapProperties = MapProperties(),
                     uiSettings: MapUiSettings = MapUiSettings(),
) {

    val userLocation = viewModel.userLocation.collectAsState(initial = null)
    val ordinacije = viewModel.listOrdinacija.collectAsState(initial = emptyList())

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        val drawable = ContextCompat.getDrawable(LocalContext.current, R.drawable.toothmarker) as BitmapDrawable
        val bitmap = drawable.bitmap

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false)

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
        ordinacije.value.forEach { ordinacija ->
            Marker(
                state = rememberMarkerState(
                    position = LatLng(ordinacija.latitude, ordinacija.longitude)

                ),
                title = "Ordinacija ${ordinacija.naziv}",
                icon=scaledMarkerIcon
        )

        }
    }
}