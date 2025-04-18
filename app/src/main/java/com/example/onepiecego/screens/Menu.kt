package com.example.onepiecego.screens


import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.tween
import androidx.compose.animation.*
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.onepiecego.R
import com.example.onepiecego.ViewModel.PiratasViewModel
import com.example.onepiecego.models.MenuMusicPlayer
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MainMenuScreen(navController: NavController, auth: FirebaseAuth, ViewModel: PiratasViewModel) { //Argumentos de navController, auth para FIREBASE y ViewModel
    val userEmail = auth.currentUser?.email ?: "Invitado" //Email del usuario actual o Invitado si no hay sesion

    //Cierra la Sesion con Firebase, te pasa a la pantalla login y elimino el menu backstac para que no de atras y vuelva.
    fun logout() {
        auth.signOut()
        navController.navigate("Login") {
            // Aseguramos que al hacer logout volvamos al Login y no podamos regresar al menú sin iniciar sesión
            popUpTo("Menu") { inclusive = true }
        }
    }


    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    //MediaPlayer de la musica
    val context = LocalContext.current

    // Lanza la música al entrar en la pantalla
    LaunchedEffect(Unit) {
        MenuMusicPlayer.start(context)
    }

    // Estado de mute/unmute
    var isMuted by remember { mutableStateOf(MenuMusicPlayer.isCurrentlyMuted()) }

    //Cambiar el estado de mute a unmute
    val toggleMute = {
        MenuMusicPlayer.toggleMute()
        isMuted = MenuMusicPlayer.isCurrentlyMuted()
    }

    //Variable para la animacion del titulo
    val startAnim = remember { mutableStateOf(false) }

    //Animacion para el titulo escalando y desvaneciendo
    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnim.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )

    //Animacion para el titulo escalando y desvaneciendo
    val titleScale by animateFloatAsState(
        targetValue = if (startAnim.value) 1f else 0.5f, // empieza pequeño
        animationSpec = tween(durationMillis = 1500, easing = EaseOutBack)
    )

    //Lanza la animacion
    LaunchedEffect(Unit) {
        startAnim.value = true
    }

    //Imagen de fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título con animación
            // Título con animación (dividido en dos líneas)
            Text(
                text = "One Piece",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = titleScale
                        scaleY = titleScale
                        alpha = titleAlpha
                    }
            )

            Text(
                text = "Pirate Battle",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = titleScale
                        scaleY = titleScale
                        alpha = titleAlpha
                    }
            )


            Spacer(modifier = Modifier.height(20.dp))

            Row(                   //En esta fila se encuentra el boton unmute y el cerrar sesion
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre los botones
                verticalAlignment = Alignment.CenterVertically // Centrado verticalmente
            ) {
                // Icono de cerrar sesión,
                IconButton(
                    onClick = { logout() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.fuera_login),
                        contentDescription = "Salirse del login"
                    )


                }

                // Botón de mute/unmute
                IconButton(onClick = { toggleMute() }) {
                    val iconRes = if (isMuted) R.drawable.mute_icon else R.drawable.unmute_icon
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (isMuted) "Unmute" else "Mute"
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))


            // Título de bienvenida con el email del usuario
            Text(
                text = "Bienvenido, ${userEmail}",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))


            // Botón Crear Personaje con animación
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            ) {
                Button(
                    onClick = { navController.navigate("Create") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Personaje")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Ver Personajes con animación
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            ) {
                Button(
                    onClick = { navController.navigate("View") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver Personajes")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Modificar Personaje con animación
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
            ) {
                Button(
                    onClick = { navController.navigate("Modification") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Modificar Personaje")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Eliminar Personaje con animación
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn()
            ) {
                Button(
                    onClick = { navController.navigate("Delete") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar Personaje")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Batalla con animación
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn() + fadeIn()
            ) {
                Button(
                    onClick = { navController.navigate("Battle") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text("⚔️ Batalla", color = Color.White)
                }
            }
        }

    }

}
