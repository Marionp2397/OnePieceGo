package com.example.onepiecego.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.onepiecego.R
import com.example.onepiecego.ViewModel.PiratasViewModel
import com.example.onepiecego.models.MenuMusicPlayer
import com.example.onepiecego.models.PirateCharacter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await


@Composable
fun ViewCharacter(                      //Pasamos los argumentos de siempre navcontroller, Firebase y viewModel
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: PiratasViewModel
) {

    var listarPiratas by remember { mutableStateOf(emptyList<PirateCharacter>()) }  //Guarda la lista de piratass de FIrestore
    var isMuted by remember { mutableStateOf(MenuMusicPlayer.isCurrentlyMuted()) }  //Para la musica muteada

    // Controlar mute/unmute
    val toggleMute = {
        isMuted = !isMuted
        MenuMusicPlayer.toggleMute() // Cambia el estado de mute
    }

    Box(            //La imagen de fondo
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Lista de Piratas", fontWeight = FontWeight.Bold

            )

            Row(                            //En esta fila se encuentra el boton unmute y el cerrar sesion
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
            DisposableEffect(true) {                //Lanza corrutina cuando Composable entra y cancela si sale.
                val job = CoroutineScope(Dispatchers.IO).launch {
                    val piratas = getPiratas()      //LLama a getpiratas para obtener los dats de Firesbase

                    listarPiratas = piratas
                }
                onDispose {
                    job.cancel()
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(listarPiratas) { pirate ->        //Lista los piratas
                    PirateItem(pirate)

                }
            }

        }
    }
}

suspend fun getPiratas(): List<PirateCharacter> {       //FUncion suspendida acceder a firestore, obtiene documentos,
    return try {                                        //COnviere documento en objeto PirateCharacter
        val db = FirebaseFirestore.getInstance()        //Devuelve Lista

        var nombre_coleccion = "piratasOnePieceGo"

        val querySnapshot = db.collection(nombre_coleccion).get().await()

        val piratas = mutableListOf<PirateCharacter>()

        for (document in querySnapshot.documents) {
            val PirateCharacter = document.toObject(PirateCharacter::class.java)
            if (PirateCharacter != null) {
                piratas.add(PirateCharacter)
            }
        }
        querySnapshot.toObjects(PirateCharacter::class.java)
    } catch (e: Exception) {
        emptyList()
    }
}


@Composable
fun PirateItem(pirata: PirateCharacter) {   //Represneta Visualmente en una card a un pirata.

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0x80EEEEEE))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),

        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)  //Trasparente para que se vea el fondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Text(text = "ID: ${pirata.id}")
            Text(text = "Nombre: ${pirata.name}")
            Text(text = "Fruta: ${pirata.fruit}")
            Text(text = "Vida: ${pirata.health}")

            if (pirata.attacks.isNotEmpty()) {  //Te imprime los ataqques con su nombre y dañoo
                Text(text = "Ataques:")
                pirata.attacks.forEach { attack ->
                    Text(text = "- ${attack.name}: ${attack.damage} de daño")
                }
            } else {
                Text(text = "Sin ataques registrados")  //Si no tiene ataques sale esto
            }
        }
    }
}