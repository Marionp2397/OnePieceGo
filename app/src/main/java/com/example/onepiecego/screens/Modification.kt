package com.example.onepiecego.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.onepiecego.R
import com.example.onepiecego.ViewModel.PiratasModificarViewModel
import com.example.onepiecego.models.Attack
import com.example.onepiecego.models.MenuMusicPlayer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun ModificarPirata(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: PiratasModificarViewModel
) {
    val pirateState by viewModel.pirate.collectAsState()    //Se inicializa campos formulario con los valores de pirateState del PirataModificarVIewModel
    var name by remember { mutableStateOf(pirateState?.name ?: "") }    //Usando remember para los valores se mantengan
    var fruit by remember { mutableStateOf(pirateState?.fruit ?: "") }  //Usando remember para los valores se mantengan
    var health by remember { mutableStateOf(pirateState?.health ?: 100L) }      //Usando remember para los valores se mantengan
    var attacks by remember { mutableStateOf(pirateState?.attacks ?: emptyList<Attack>()) }     //Usando remember para los valores se mantengan
    var mensajeConfirmacion by remember { mutableStateOf("") }  //variable para mostar mensjae de exito o errr

    val db = FirebaseFirestore.getInstance()    //Conexion a al base de datos
    val coleccion = "piratasOnePieceGo" //Nombre coleccion donde se guardan los piratas

    // Estado de mute/unmute
    var isMuted by remember { mutableStateOf(MenuMusicPlayer.isCurrentlyMuted()) }

    // Controlar mute/unmute
    val toggleMute = {
        isMuted = !isMuted
        MenuMusicPlayer.toggleMute() // Cambia el estado de mute
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Formulario de modificación de pirata
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .padding(horizontal = 10.dp)
                .background(Color.White.copy(alpha = 0.2f)) // Fondo semi-transparente para el formulario

        ) {
            Text(
                text = "Modificar Pirata",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
            Row(
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

            Spacer(modifier = Modifier.size(5.dp))

            // ID del pirata
            var id by remember { mutableStateOf("") }
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = {
                    Text(
                        "ID",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80EEEEEE)),
            )
            Spacer(modifier = Modifier.size(5.dp))


            // Nombre del pirata
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(
                        "Nombre",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80EEEEEE)),
            )
            Spacer(modifier = Modifier.size(5.dp))


            // Fruta del pirata
            OutlinedTextField(
                value = fruit,
                onValueChange = { fruit = it },
                label = {
                    Text(
                        "Fruta",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80EEEEEE)),
            )
            Spacer(modifier = Modifier.size(5.dp))


            // Vida del pirata
            OutlinedTextField(
                value = health.toString(),
                onValueChange = { health = it.toLongOrNull() ?: 100L },
                label = { Text("Vida") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80EEEEEE)),
            )
            Spacer(modifier = Modifier.size(5.dp))


            // Mostrar ataques existentes
            Text("Ataques del pirata:")
            LazyColumn {
                items(attacks) { attack ->
                    Text(
                        "${attack.name}: ${attack.damage} de daño",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

            }
            Spacer(modifier = Modifier.size(5.dp))


            // Agregar o modificar ataque
            var attackName by remember { mutableStateOf("") }
            var attackDamage by remember { mutableStateOf("") }
            var attackMessage by remember { mutableStateOf("") }  // Mensaje de error para los ataques

            OutlinedTextField(
                value = attackName,
                onValueChange = { attackName = it },
                label = {
                    Text(
                        "Nombre del ataque",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80EEEEEE)),

                singleLine = true
            )
            Spacer(modifier = Modifier.size(5.dp))


            OutlinedTextField(
                value = attackDamage,
                onValueChange = { attackDamage = it },
                label = {
                    Text(
                        "Daño del ataque",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x80EEEEEE)),
                singleLine = true
            )

            // Mostrar mensaje de error si los campos están vacíos
            if (attackMessage.isNotEmpty()) {
                Text(
                    text = attackMessage,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    if (attackName.isBlank() || attackDamage.isBlank()) {
                        attackMessage = "Ambos campos son obligatorios."
                        return@Button
                    } else {
                        attackMessage = ""  // Limpiar mensaje de error
                        val newAttack = Attack(attackName, attackDamage.toLongOrNull() ?: 0L)
                        attacks = attacks + newAttack
                        attackName = ""
                        attackDamage = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp),
                enabled = attackName.isNotBlank() && attackDamage.isNotBlank()  // Deshabilitar el botón si los campos están vacíos
            ) {
                Text("Agregar ataque")
            }

            // Verificar si todos los campos son válidos antes de habilitar el botón "Modificar"
            val isValid =
                id.isNotBlank() && name.isNotBlank() && fruit.isNotBlank() && health > 0 && attacks.isNotEmpty()

            // Datos que se enviarán a la base de datos
            val dato = hashMapOf(
                "id" to id,
                "name" to name,
                "fruit" to fruit,
                "health" to health,
                "attacks" to attacks.map { attack ->
                    hashMapOf("name" to attack.name, "damage" to attack.damage)
                }
            )

            // Guardar cambios y que los campos estes llenos
            Button(
                onClick = {
                    if (id.isBlank()) {
                        mensajeConfirmacion = "La ID no puede estar vacía"
                        return@Button
                    }
                    if (name.isBlank()) {
                        mensajeConfirmacion = "El nombre no puede estar vacío"
                        return@Button
                    }
                    if (fruit.isBlank()) {
                        mensajeConfirmacion = "La fruta no puede estar vacía"
                        return@Button
                    }
                    if (attacks.isEmpty()) {
                        mensajeConfirmacion = "El pirata debe tener al menos un ataque."
                        return@Button
                    }

                    db.collection(coleccion)        //Actualizar en Firestore
                        .document(id)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Si el documento existe, actualizamos
                                db.collection(coleccion)
                                    .document(id)
                                    .set(dato)      //Sobrescribe los datos
                                    .addOnSuccessListener {
                                        mensajeConfirmacion = "Datos modificados correctamente"
                                        // Limpiar el formulario después de modificar
                                        id = ""
                                        name = ""
                                        fruit = ""
                                        health = 100L
                                        attacks = emptyList()
                                    }
                                    .addOnFailureListener {
                                        mensajeConfirmacion = "No se ha podido guardar"
                                    }
                            } else {
                                mensajeConfirmacion = "No se encontró la ID. No se puede modificar"
                            }
                        }
                        .addOnFailureListener {
                            mensajeConfirmacion = "Error al verificar ID"
                        }
                },
                enabled = isValid,  // Solo habilitar el botón si todos los campos son válidos
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(text = "Modificar")
            }

            Spacer(modifier = Modifier.size(5.dp))

            // Mostrar mensaje de confirmación
            Text(text = mensajeConfirmacion)
        }
    }
}

