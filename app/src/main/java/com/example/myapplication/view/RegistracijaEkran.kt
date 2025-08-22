package com.example.myapplication.view

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.myapplication.viewmodel.AuthViewModel
import java.io.File
import java.util.UUID
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image


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
    val context = LocalContext.current

    val tempImageUri: Uri by remember {
        val tempDir = File(context.cacheDir, "images").apply { mkdirs() }
        val tempFile = File.createTempFile(
            "temp_image_${UUID.randomUUID()}",
            ".jpg",
            tempDir
        ).apply {
            createNewFile()
        }
        mutableStateOf(
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
        )
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        slikaUri = uri
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            slikaUri = tempImageUri
        }
    }

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
            slikaUri?.let { uri ->
                Image(
                    painter = rememberImagePainter(data = uri),
                    contentDescription = "Profilna slika",
                    modifier = Modifier
                        .size(120.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Galerija")
                }
                Button(
                    onClick = { cameraLauncher.launch(tempImageUri) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Kamera")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.registerUser(
                    email,
                    password,
                    ime,
                    prezime,
                    brTelefona,
                    username,
                    //sslikaUri
                ) { success, errorMessage ->
                    if (success) {
                        message = "Registracija je uspesna!"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        navContoller.navigate("auth"){
                            popUpTo("register") {
                                inclusive = true
                            }
                        }
                    } else {
                        message = "Registracije nije uspela, proverite podatke!"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }, modifier = Modifier.fillMaxWidth())
            {
                Text(text = "Registruj se")
            }

        }
    }
    }

