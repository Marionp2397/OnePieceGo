package com.example.onepiecego.screens

import android.util.Log
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.*
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.onepiecego.R
import com.example.onepiecego.models.MenuMusicPlayer
import com.example.onepiecego.models.MenuMusicPlayer.toggleMute
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Login(
    navController: NavHostController,
    auth: FirebaseAuth
) { //Los parametros de navController y el firebase
    var email by remember { mutableStateOf("") }    //Campo de texto para el email
    var password by remember { mutableStateOf("") } //Campo de texto para la contraseña
    val context = LocalContext.current //Para el mediaplayer

    // Estado de mute/unmute
    var isMuted by remember { mutableStateOf(MenuMusicPlayer.isCurrentlyMuted()) }

    //Cambiar el estado de mute a unmute
    val toggleMute = {
        MenuMusicPlayer.toggleMute()
        isMuted = MenuMusicPlayer.isCurrentlyMuted()
    }

    //Reproducir la musica aleatoria al entrar al Login
    LaunchedEffect(true) {
        MenuMusicPlayer.changeMusicRandomForLogin(context)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {

            Row(                        //En esta fila se encuentra el boton unmute y el pasar cancion
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Ajusta el padding
                horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre los botones
                verticalAlignment = Alignment.CenterVertically // Centrado verticalmente
            ) {
                // Botón para cambiar la canción (flecha)
                IconButton(onClick = {
                    val currentMusic = MenuMusicPlayer.getCurrentMusic() // Obtener la música actual
                    var newSongResId: Int

                    // Lista de canciones
                    val availableSongs = listOf(
                        R.raw.musica_logn_1,
                        R.raw.musica_login_2,
                        R.raw.musica_login_3,
                        R.raw.musica_login_4
                    )

                    // Seleccionar una nueva canción diferente a la actual
                    do {
                        newSongResId = availableSongs.random()
                    } while (newSongResId == currentMusic) // Si la canción seleccionada es la misma, elige otra

                    MenuMusicPlayer.changeMusic(context, newSongResId) // Cambiar la música
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.pasar_cancion), // Flecha para pasara cancion
                        contentDescription = "Cambiar Canción"
                    )
                }

                // Botón de mute/unmute (derecha)
                IconButton(onClick = { toggleMute() }) {
                    val iconRes = if (isMuted) R.drawable.mute_icon else R.drawable.unmute_icon
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (isMuted) "Unmute" else "Mute"
                    )
                }
            }

            Spacer(modifier = Modifier.size(200.dp))

            Text("Inicia sesión", color = Color.White, fontSize = 28.sp)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(  //Caja para introducir el email
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        "Email",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(      //Caja para introducir la contraeña
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        "Contraseña",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                if (email.isEmpty() || password.isEmpty()) {    //Comprobacion de que los campos esten llenos
                    Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                    //Si los campos estan llenos usa FIREBASE
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) { //Si funciona entra, te lleva a MENU y para musica e incia la musica del menu
                            // Detenemos la música del login
                            MenuMusicPlayer.stop()

                            // Iniciamos la música del menú
                            MenuMusicPlayer.start(context)  // Aquí se arranca la música del menú

                            navController.navigate("Menu")
                            Log.i("Login", "Inicio de sesión correcto")
                        } else { //Si hay un error o es incorrecto, pues no entras
                            val errorMsg = task.exception?.message ?: "Error desconocido"
                            Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                            Log.e("Login", errorMsg)
                        }
                    }
            }) {
                Text("Login")
            }
        }
    }
}