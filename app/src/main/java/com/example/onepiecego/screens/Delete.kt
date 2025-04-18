package com.example.onepiecego.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.onepiecego.R
import com.example.onepiecego.ViewModel.PiratasViewModel
import com.example.onepiecego.models.MenuMusicPlayer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BorrarPirata(       //Argumentos de siempre navController, auth y viewModel
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: PiratasViewModel
) {
    val db = FirebaseFirestore.getInstance()     //Conexion a base de datos FIrebase
    val nombre_coleccion = "piratasOnePieceGo"       //Nombre coleccion donde se guardan los piratas
    var id by remember { mutableStateOf("") }    //Guarda la id del pirata a buscar
    var pirateName by remember { mutableStateOf("") }   //Almacena info pirata
    var pirateFruit by remember { mutableStateOf("") }      //Almacena info pirata
    var pirateHealth by remember { mutableStateOf(0L) }     //Almacena info pirata
    var pirateAttacks by remember { mutableStateOf<List<String>>(emptyList()) }  // Lista para ataques
    var mensaje_borrado by remember { mutableStateOf("") }  //Muestra el mensaje
    var isLoading by remember { mutableStateOf(false) }     //Indica si la aplicacion esperand respuesta bd

    // Estado de mute/unmute
    var isMuted by remember { mutableStateOf(MenuMusicPlayer.isCurrentlyMuted()) }

    // Controlar mute/unmute
    val toggleMute = {
        isMuted = !isMuted
        MenuMusicPlayer.toggleMute() // Cambia el estado de mute
    }



    Box(              //Fondo
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .padding(start = 10.dp)
                .padding(end = 10.dp)
        ) {
            Text(
                text = "Eliminar Pirata",
                fontWeight = FontWeight.ExtraBold
            )
            Row(                    //En esta fila se encuentra el boton unmute y la flecha para atras
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre los botones
                verticalAlignment = Alignment.CenterVertically // Centrado verticalmente
            ) {
                // Botón de Atras
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.atras),
                        contentDescription = "Atras"
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

            Spacer(modifier = Modifier.size(10.dp))

            // Campo para introducir el ID del pirata
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = {
                    Text(
                        "Introduce el ID del pirata a borrar",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80EEEEEE)),
                singleLine = true,
            )

            Spacer(modifier = Modifier.size(5.dp))

            // Botón para buscar el pirata y mostrar su información
            Button(
                onClick = {
                    if (id.isBlank()) {
                        mensaje_borrado = "La ID no puede estar vacía"
                        return@Button
                    }
                    isLoading = true
                    db.collection(nombre_coleccion)
                        .document(id)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Si existe el documento, mostramos los detalles
                                pirateName = document.getString("name") ?: ""
                                pirateFruit = document.getString("fruit") ?: ""
                                pirateHealth = document.getLong("health") ?: 0L

                                // Obtener los ataques del pirata
                                val attacks = document.get("attacks") as? List<Map<String, Any>>
                                    ?: emptyList()
                                pirateAttacks = attacks.mapNotNull { attack ->
                                    val attackName =
                                        attack["name"] as? String ?: return@mapNotNull null
                                    val attackDamage =
                                        attack["damage"] as? Long ?: return@mapNotNull null
                                    "$attackName (Daño: $attackDamage)"
                                }
                                mensaje_borrado = "" //Limpia mensaje si fue exitoso
                            } else {
                                pirateName = ""
                                pirateFruit = ""
                                pirateHealth = 0L
                                pirateAttacks = emptyList()
                                mensaje_borrado = "No se encontró la ID. No se puede eliminar."
                            }
                        }
                        .addOnFailureListener {
                            mensaje_borrado = "Error al verificar la ID."
                        }
                        .addOnCompleteListener {
                            isLoading = false
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(text = "Buscar Pirata")
            }

            Spacer(modifier = Modifier.size(10.dp))

            // Si encontramos un pirata, mostramos sus datos
            if (pirateName.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp),// Padding interno para el contenido
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Nombre: $pirateName", fontWeight = FontWeight.Bold)
                    Text("Fruta: $pirateFruit", fontWeight = FontWeight.Bold)
                    Text("Vida: $pirateHealth", fontWeight = FontWeight.Bold)

                    // Mostrar los ataques si hay alguno
                    if (pirateAttacks.isNotEmpty()) {
                        Text("Ataques:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        pirateAttacks.forEach { attack ->
                            Text(attack, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("Este pirata no tiene ataques registrados.")
                    }
                }
            }

            Spacer(modifier = Modifier.size(10.dp))


            // Botón para borrar el pirata
            Button(
                onClick = {
                    if (id.isBlank()) {
                        mensaje_borrado = "La ID no puede estar vacía"
                        return@Button
                    }

                    if (pirateName.isEmpty()) {
                        mensaje_borrado = "No se encontró el pirata con esta ID"
                        return@Button
                    }

                    // Iniciar proceso de borrado
                    db.collection(nombre_coleccion)
                        .document(id)
                        .delete()
                        .addOnSuccessListener {
                            mensaje_borrado = "Pirata eliminado correctamente"
                            // Limpiar los campos después de eliminar
                            id = ""
                            pirateName = ""
                            pirateFruit = ""
                            pirateHealth = 0L
                            pirateAttacks = emptyList()
                        }
                        .addOnFailureListener {
                            mensaje_borrado = "No se ha podido eliminar el pirata."
                        }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63),
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(text = "Eliminar Pirata")
            }

            Spacer(modifier = Modifier.size(5.dp))

            // Mostrar mensaje de confirmación o error
            Text(
                text = mensaje_borrado,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}