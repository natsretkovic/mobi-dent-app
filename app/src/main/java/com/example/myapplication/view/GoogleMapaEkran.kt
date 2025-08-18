package com.example.myapplication.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun GoogleMapaEkran( modifier: Modifier = Modifier,
                     cameraPositionState: CameraPositionState = rememberCameraPositionState {
                         position = CameraPosition.fromLatLngZoom(LatLng(43.321445, 21.896104), 12f)
                     },
                     properties: MapProperties = MapProperties(),
                     uiSettings: MapUiSettings = MapUiSettings()
) {

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        val nis = LatLng(43.321445, 21.896104)
        Marker(
            state = MarkerState(position = nis),
            title = "Niš",
            snippet = "Ovo je marker u Nisu"
        )
    }
}