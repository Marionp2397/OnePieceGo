package com.example.onepiecego.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController

import com.example.onepiecego.R
import com.example.onepiecego.ViewModel.PiratasViewModel
import com.example.onepiecego.models.Attack
import com.example.onepiecego.models.MenuMusicPlayer

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PirataAlta(navController: NavHostController, auth: FirebaseAuth, ViewModel: PiratasViewModel) { //Argumentos para navegar por pantalla, auth de FIrebase y el ViewModel contiene logica
    val db = FirebaseFirestore.getInstance()        //Conexion a base de datos FIrebase
    val nombre_coleccion = "piratasOnePieceGo"      //Nombre coleccion donde se guardan los piratas

    val id: String by ViewModel.id.observeAsState(initial = "")         //valor que se observa del VIewModel PIratasViewModel
    val nombre: String by ViewModel.nombre.observeAsState(initial = "")         //valor que se observa del VIewModel PIratasViewModel
    val fruta: String by ViewModel.fruit.observeAsState(initial = "")       //valor que se observa del VIewModel PIratasViewModel
    val health: Long by ViewModel.health.observeAsState(100L)           //valor que se observa del VIewModel PIratasViewModel
    val attacks: List<Attack> by ViewModel.attack.observeAsState(emptyList())       //valor que se observa del VIewModel PIratasViewModel
    val buttonEnable: Boolean by ViewModel.buttonEnable.observeAsState(initial = false)     //valor que se observa del VIewModel PIratasViewModel

    val attackName = remember { mutableStateOf("") }        //Manejar el nombre del ataque
    val attackDamage = remember { mutableStateOf("") }      //Manejar el daño del ataque

    var mensajeConfirmacion by remember { mutableStateOf("") } //variable para que el menasje si va todo bien o mal
    val visible by remember { mutableStateOf(true) }            //variable animacion

    val context = LocalContext.current      //MediaPlayer de la musica

    // Inicializa la música al entrar en la pantalla
    LaunchedEffect(Unit) {
        MenuMusicPlayer.start(context)
    }
    // Estado de mute/unmute
    var isMuted by remember { mutableStateOf(MenuMusicPlayer.isCurrentlyMuted()) }

    // Limpiar ataques al entrar
    LaunchedEffect(true) {
        ViewModel.clearAttacks()
    }
    //Cambiar el estado de mute a unmute
    val toggleMute = {
        MenuMusicPlayer.toggleMute()
        isMuted = MenuMusicPlayer.isCurrentlyMuted()
    }

    //Imagen de fondo
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(tween(800)) + fadeIn(tween(800))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent) //Color Trasnparente para que no se vea
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(                 //En esta fila se encuentra el boton unmute y la flecha para atras
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre los botones
                        verticalAlignment = Alignment.CenterVertically // Centrado verticalmente
                    ) {
                        // Botón de Atras (izquierda)
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.atras),
                                contentDescription = "Atras"
                            )
                        }

                        // Botón de mute/unmute (derecha)
                        IconButton(onClick = { toggleMute() }) {
                            val iconRes =
                                if (isMuted) R.drawable.mute_icon else R.drawable.unmute_icon
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = if (isMuted) "Unmute" else "Mute"
                            )
                        }
                    }
                    Text(
                        text = "Alta Pirata",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(          //Caja para escribir texto
                        value = id,
                        onValueChange = {
                            ViewModel.OnCompleteFields(id = it, nombre, fruta, health, attacks) //Cada cambio en el campo actualiza el PiratasViewModel
                        },
                        label = {
                            Text(
                                "Introduce el ID",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x80EEEEEE)),
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center)

                    )
                    Spacer(modifier = Modifier.size(8.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            ViewModel.OnCompleteFields(id, it, fruta, health, attacks) //Cada cambio en el campo actualiza el PiratasViewModel
                        },
                        label = {
                            Text(
                                "Nombre del pirata",
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
                    Spacer(modifier = Modifier.size(8.dp))

                    OutlinedTextField(
                        value = fruta,
                        onValueChange = {
                            ViewModel.OnCompleteFields(id, nombre, it, health, attacks) //Cada cambio en el campo actualiza el PiratasViewModel
                        },
                        label = {
                            Text(
                                "Fruta del pirata",
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
                    Spacer(modifier = Modifier.size(8.dp))

                    OutlinedTextField(
                        value = health.toString(),
                        onValueChange = {
                            ViewModel.OnCompleteFields( //Cada cambio en el campo actualiza el PiratasViewModel
                                id,
                                nombre,
                                fruta,
                                it.toLongOrNull() ?: 100L,
                                attacks
                            )
                        },
                        label = {
                            Text(
                                "Vida del pirata",
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
                    Spacer(modifier = Modifier.size(16.dp))

                    Text("Agregar Ataques", fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = attackName.value,
                        onValueChange = { attackName.value = it },
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
                    Spacer(modifier = Modifier.size(16.dp))


                    OutlinedTextField(
                        value = attackDamage.value,
                        onValueChange = { attackDamage.value = it },
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

                    Button(
                        onClick = {
                            val damage = attackDamage.value.toLongOrNull() ?: 0 //Convierte String en Long
                            if (attackName.value.isNotBlank() && attackDamage.value.isNotBlank()) { //Asegura que los campos esten llenos
                                val newAttack = Attack(attackName.value, damage) //Crea el objeto con nombre y daño
                                ViewModel.addAttack(newAttack)  //Lo envia al ViewsModel para acutalizar lista ataques
                                attackName.value = ""   //Limpia el campo
                                attackDamage.value = "" //Limpia el campo
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Agregar Ataque")
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 120.dp)) {
                        items(attacks) { attack ->
                            if (attack.name.isNotBlank() && attack.damage > 0) {
                                Text("${attack.name}: ${attack.damage} de daño")
                            }
                        }
                    }

                    val dato = hashMapOf(       //Contiene toda la info del pirata
                        "id" to id,
                        "name" to nombre,
                        "fruit" to fruta,
                        "health" to health,
                        "attacks" to attacks.map {  //CAmpo especial prque es una lista de ataques
                            hashMapOf(
                                "name" to it.name,
                                "damage" to it.damage
                            )
                        }
                    )

                    val buttonAlpha by animateFloatAsState(             //Pone el boton opaco para saber si esta bien
                        targetValue = if (buttonEnable) 1f else 0.5f,
                        animationSpec = tween(500)
                    )

                    Button(
                        onClick = {
                            db.collection(nombre_coleccion).document(id).get() //Busca en FIRESTORE si existe documento con esa ID
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        mensajeConfirmacion = "La ID ya está registrada." //SI esta regitrdado te lo dice y no sobrescribirlo
                                    } else {
                                        db.collection(nombre_coleccion).document(id).set(dato) //Guarda datos de antes
                                            .addOnSuccessListener {
                                                mensajeConfirmacion =
                                                    "Datos guardados correctamente" //Si se guarda bien manda este mensaje y reincia formulario
                                                ViewModel.OnCompleteFields(
                                                    "",
                                                    "",
                                                    "",
                                                    100L,
                                                    emptyList()
                                                )
                                                ViewModel.clearAttacks()
                                                attackName.value = ""
                                                attackDamage.value = ""
                                            }
                                            .addOnFailureListener {
                                                mensajeConfirmacion =
                                                    "No se ha podido guardar correctamente" //Si hay error
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    mensajeConfirmacion = "Error al verificar ID" //Si falla consulta con FIRESTORE
                                }
                        },
                        enabled = buttonEnable, //Si estan campos llenos, se puede usar
                        modifier = Modifier
                            .alpha(buttonAlpha) //Opcac si no se puede usar
                            .fillMaxWidth(),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text("Guardar")
                    }

                    if (mensajeConfirmacion.isNotBlank()) {             //Imprime el menasje debajo de boton
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = mensajeConfirmacion, color = Color.DarkGray)
                    }
                }
            }
        }

    }
}