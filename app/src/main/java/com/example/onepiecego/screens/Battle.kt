package com.example.onepiecego.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.*
import com.example.onepiecego.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.onepiecego.ViewModel.PiratasViewModel
import com.example.onepiecego.models.MenuMusicPlayer
import com.example.onepiecego.models.PirateCharacter
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SelectPiratesScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: PiratasViewModel
) {
    val piratas by viewModel.piratass.collectAsState() //Contiene datos de los piratas disponibles
    val piratasSeleccionados by viewModel.piratasSeleccionados.collectAsState() //Contiene datos de los pirtas seleccionados
    val context = LocalContext.current //MUSICAA

    var showBattle by remember { mutableStateOf(false) }    //Contrla si debe mostar pantalla de batalla o seleccin de piratas
    var winner by remember { mutableStateOf<PirateCharacter?>(null) } //Controla el ganador
    var battleInProgress by remember { mutableStateOf(false) } //Contrla el estado de la batalla
    var battleLogs by remember { mutableStateOf<List<String>>(emptyList()) } //Controla los logs de info de la batalla
    var selectedAttackIndex by remember { mutableStateOf<Int?>(null) } //Seguimiento del indice de ataque que se ha seleccionado
    var isPlayerTurn by remember { mutableStateOf(true) } //Contrla el turno

    // Guardar la música anterior
    var previousMusic by remember { mutableStateOf<Int?>(null) }

    // Establecer salud inicial
    var playerHealth by remember { mutableStateOf(100L) }
    var enemyHealth by remember { mutableStateOf(100L) }

    // Estado de mute/unmute
    var isMuted by remember { mutableStateOf(MenuMusicPlayer.isCurrentlyMuted()) }

    // Controlar mute/unmute
    val toggleMute = {
        isMuted = !isMuted
        MenuMusicPlayer.toggleMute() // Cambia el estado de mute
    }

    //Usa para lanzar la funcion getpiratas del viewModel y carga piratas de bd
    LaunchedEffect(true) {
        viewModel.getPiratas()
    }

    //Deteca cuando 2 piratas han sido seleccioandos y empeiza la batalla
    LaunchedEffect(piratasSeleccionados) {
        if (piratasSeleccionados.size == 2) {
            showBattle = true
            battleInProgress = true
        }
    }

    // Cambiar la música al entrar a la pantalla
    LaunchedEffect(Unit) {
        // Guardar la música anterior
        previousMusic = MenuMusicPlayer.getCurrentMusic()
        MenuMusicPlayer.changeMusic(context, R.raw.batalla_musica)
    }
    // Restaurar la música anterior al salir de la pantalla
    DisposableEffect(context) {
        onDispose {
            if (previousMusic != null) {
                // Restaurar la música anterior al salir de la pantalla
                MenuMusicPlayer.changeMusic(context, previousMusic!!)
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {                //Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Selección de piratas o batalla
            if (!showBattle) {      //Como es falsa entra en la seleccion de personajes
                Text(
                    "Selecciona 2 piratas para iniciar la batalla:",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 50.dp)
                )

                Row(            //En esta fila se encuentra el boton unmute y la flecha para atras
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


                LazyColumn {
                    items(piratas) { pirata ->      //Crea elemento de lista por cada objeto pirata( VIewModel)
                        PirateItem(             //Muestra datos del pirata y un check para seleccionarlo
                            pirate = pirata,
                            isSelected = piratasSeleccionados.contains(pirata.id),
                            onSelect = { viewModel.togglePirataSeleccionado(pirata.id) }
                        )
                    }
                }
            } else {        //Si showbattle es true se lanza la batalla
                val selectedPirates = piratas.filter { piratasSeleccionados.contains(it.id) } //Filtra lista original dejando solo seleccioandos
                val playerPirate = selectedPirates[0]   //Asigna el primer y segundo pirata
                val enemyPirate = selectedPirates[1]

                Text(
                    "¡Batalla entre:", fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 50.dp)


                )
                Row(                  //En esta fila se encuentra el boton unmute y la flecha para atras
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
                selectedPirates.forEach {       //Bucle que recorre lista piratas seleccionados y muestra una tarjeta visual para cada una
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color(0x80EEEEEE)) //Fondo trasparenteee
                            .align(Alignment.CenterHorizontally)
                    ) {

                        Text(
                            "- ${it.name} (Vida: ${it.health})",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,

                                ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                if (battleInProgress) {     //Cuando es TRUE y isplayerTurn tambien muestra lista botones
                    if (isPlayerTurn) {     //De los ataques disponibles jugador, y seleccionar uno
                        Text(
                            "Es tu turno. Elige un ataque:",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        // Mostrar los botones de ataque siempre en el turno del jugador
                        LazyColumn {
                            itemsIndexed(playerPirate.attacks) { index, attack ->
                                val isSelected = selectedAttackIndex == index //Verifica ataque actual esta seleccionado

                                Button(
                                    onClick = { selectedAttackIndex = index },  //Guarda el idnice de ataque seleccionado
                                    colors = ButtonDefaults.buttonColors(       //Cambia a verde si esta seleccionado
                                        containerColor = if (isSelected) Color.Green else MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text("Usar ${attack.name} (Daño: ${attack.damage})")
                                }
                            }
                        }

                        // Botón para confirmar el ataque
                        if (selectedAttackIndex != null) {  //Boton aparece si has selecionad ataque
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                simulateTurn(   //Ejecuta el turno del jugador
                                    playerPirate,   //Quein ataca
                                    enemyPirate,    //A quien ataca
                                    selectedAttackIndex,    //Ataque que se eligue
                                    true,       //Si es el turno del jugadr
                                    playerHealth,       //Tu vida
                                    enemyHealth,        //Vida enemigo
                                    battleLogs,         //Historial batalla
                                    onTurnFinished = { newPHealth, newEHealth, logs, nextTurn, winnerPirate ->  //Termina turno jgador 1
                                        playerHealth = newPHealth   //Actualiza la vida tras el ataque
                                        enemyHealth = newEHealth    //Actualiza la vida tras el ataque
                                        battleLogs = logs       //Guarda mensajes del turno
                                        isPlayerTurn = nextTurn     //cambia el turno
                                        winner = winnerPirate       //Si gana alguno se guarda aqui
                                        battleInProgress = winnerPirate == null     //Se termina la batalla si hay ganadr
                                        selectedAttackIndex = null      //Deselecciona el ataque
                                    }
                                )
                            }) {
                                Text("¡Atacar!")
                            }
                        }
                    } else {
                        Text("Es el turno del enemigo...")

                        LaunchedEffect(isPlayerTurn) {  //Se lanza automatico cuando cambia isPlayerTunr
                            simulateTurn(       //Ejecuta el turno del enemigo
                                playerPirate,      //A quien ataca
                                enemyPirate,        //Quein ataca
                                null,   //Eligue al azar
                                false,      //Al ser false indica no ataco
                                playerHealth, //Actualiza la vida tras el ataque
                                enemyHealth,    //Actualiza la vida tras el ataque
                                battleLogs,      //Historial batalla
                                onTurnFinished = { newPHealth, newEHealth, logs, nextTurn, winnerPirate ->  //Actualiza las variables
                                    playerHealth = newPHealth   //Actualiza la vida tras el ataque
                                    enemyHealth = newEHealth    //Actualiza la vida tras el ataque
                                    battleLogs = logs           //Guarda mensajes del turno
                                    isPlayerTurn = nextTurn     //cambia el turno
                                    winner = winnerPirate       //Si gana alguno se guarda aqui
                                    battleInProgress = winnerPirate == null     //Se termina la batalla si hay ganadr
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        "¡La batalla ha terminado!",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        "Ganador: ${winner?.name ?: "Nadie"}",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Historial de batalla
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Historial de batalla:", modifier = Modifier.align(Alignment.CenterHorizontally)

                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    LazyColumn {
                        items(battleLogs) { log ->
                            Text(log)
                        }
                    }
                }
            }
        }

        // Botón de reinicio siempre visible al fondo
        if (showBattle) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, top = 35.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        viewModel.resetSeleccionados()  //LLama a ViewModel para desmarcar los pirtas seleccionados
                        showBattle = false  //Oculta la vista de batalla
                        winner = null
                        battleInProgress = false
                        battleLogs = emptyList()
                        selectedAttackIndex = null              //Reinicia todos los estados internos
                        isPlayerTurn = true
                        playerHealth = 100L
                        enemyHealth = 100L
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Reiniciar", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun PirateItem(
    pirate: PirateCharacter,        //Objet pirata con sus datos
    isSelected: Boolean,            //SI esta seleccioando o no
    onSelect: () -> Unit            //Accion al cambiar seleccion
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0x80EEEEEE))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),// Padding interno para el contenido

        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // <- Esto lo hace transparente


    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "ID: ${pirate.id}")             //CAmpo basico del objeo PirateCharacter
            Text(text = "Nombre: ${pirate.name}")       //CAmpo basico del objeo PirateCharacter
            Text(text = "Fruta: ${pirate.fruit}")       //CAmpo basico del objeo PirateCharacter
            Text(text = "Vida: ${pirate.health}")       //CAmpo basico del objeo PirateCharacter

            if (pirate.attacks.isNotEmpty()) {      //Muestra los ataque ssi hay al menos uno
                Text(text = "Ataques:")
                pirate.attacks.forEach { attack ->
                    Text(text = "- ${attack.name}: ${attack.damage} de daño")
                }
            } else {
                Text(text = "Sin ataques registrados")  //Si no, muestra esto
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar para Batalla")
                Checkbox(                           //Refleja si esta seleccionado y llama si se cambia
                    checked = isSelected,
                    onCheckedChange = { onSelect() }
                )
            }
        }
    }
}

// Simular un solo turno
fun simulateTurn(
    pirate1: PirateCharacter,   //Pirata del Jugador
    pirate2: PirateCharacter,   //Pirata enemigo
    selectedAttackIndex: Int?,  // Indice ataque elegido. Puede ser null si es turno del enemigo
    isPlayerTurn: Boolean,      //Bolleanao. Turn Jugadr
    playerHealth: Long,         //VIda actual Jugador
    enemyHealth: Long,          //VIda actual enemigoo
    currentLogs: List<String>,  //Lista de logs (Mensaje del combate)
    onTurnFinished: (               //LLama al final del turno
        newPlayerHealth: Long,
        newEnemyHealth: Long,
        logs: List<String>,
        nextTurn: Boolean,
        winner: PirateCharacter?
    ) -> Unit
) {
    val logs = currentLogs.toMutableList()          //Copian los logs actuales
    var newPlayerHealth = playerHealth.toLong()        //Copias vidas para modificar
    var newEnemyHealth = enemyHealth.toLong()           //Cpia vida para modificar
    var winner: PirateCharacter? = null             //Empieza en null

    // Log de separación entre turnos
    logs.add("----- Inicio de turno -----")

    if (isPlayerTurn && selectedAttackIndex != null) {      //Si es turno jugador se usa ataque seleccionado y se baja vida enemigo
        val attack = pirate1.attacks[selectedAttackIndex]
        newEnemyHealth -= attack.damage
        logs.add("${pirate1.name} ataca con ${attack.name}, causando ${attack.damage} de daño.")
    } else {
        val attack = pirate2.attacks.random()       //SI es turno enemigo, ataque aleatorio y se baja vida jugador
        newPlayerHealth -= attack.damage
        logs.add("${pirate2.name} ataca con ${attack.name}, causando ${attack.damage} de daño.")
    }

    // Agregar log de estado de la vida después del ataque
    logs.add("Vida de ${pirate1.name}: ${createLifeBar(newPlayerHealth)} ${newPlayerHealth}")
    logs.add("Vida de ${pirate2.name}: ${createLifeBar(newEnemyHealth)} ${newEnemyHealth}")

    if (newPlayerHealth <= 0) {             //Si llega vida 0, gana pirata 2
        logs.add("${pirate1.name} ha sido derrotado. ¡${pirate2.name} gana!")
        winner = pirate2
    } else if (newEnemyHealth <= 0) {       //Si llega vida a 0, gana pirata1
        logs.add("${pirate2.name} ha sido derrotado. ¡${pirate1.name} gana!")
        winner = pirate1
    }

    // Log de separación al final del turno
    logs.add("----- Fin de turno -----\n")

    //LLama pasandoo nueva vida, nueva vida enemigo, logs actualizados, cambia turno y ganador
    onTurnFinished(newPlayerHealth, newEnemyHealth, logs, !isPlayerTurn, winner)
}


fun createLifeBar(currentHealth: Long): String {    //Recibe la vida actual del persnaje como numero largo, devuelve string
    val totalBlocks = 10  // Total de bloques que vamos a mostrar en la barra

    // Nos aseguramos de que la salud no sea menor que 0
    val health = currentHealth.coerceAtLeast(0L)

    // Cuántos bloques llenos de vida (basado en la salud restante)
    val filledBlocks = (health.toFloat() / totalBlocks.toFloat()).toInt()

    // Cuántos bloques vacíos de vida
    val emptyBlocks = totalBlocks - filledBlocks

    // Retornamos la barra de vida como una cadena
    return "[" + "█".repeat(filledBlocks) + "▒".repeat(emptyBlocks) + "]"
}