package com.example.onepiecego.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onepiecego.models.Attack
import com.example.onepiecego.models.PirateCharacter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class PiratasViewModel : ViewModel() {


    // Lista de piratas
    private val _piratass = MutableStateFlow<List<PirateCharacter>>(emptyList())
    val piratass: StateFlow<List<PirateCharacter>> get() = _piratass

    //Controla si batalla esta curso o no
    private val _battleInProgress = MutableStateFlow(true)
    val battleInProgress: StateFlow<Boolean> get() = _battleInProgress

    //Controla quien fue ganador.
    private val _winner = MutableStateFlow<PirateCharacter?>(null)
    val winner: StateFlow<PirateCharacter?> get() = _winner


    // Lista de IDs de piratas seleccionados (debe ser una lista de Strings)
    val piratasSeleccionados = MutableStateFlow<List<String>>(emptyList())


    // Función para seleccionar o deseleccionar un pirata
    fun togglePirataSeleccionado(id: String) {
        piratasSeleccionados.value = piratasSeleccionados.value.toMutableList().let {
            if (it.contains(id)) it - id else if (it.size < 2) it + id else it
        }
    }

    //Limpia seleccion de piratas
    fun resetSeleccionados() {
        piratasSeleccionados.value = emptyList()
    }

    // Obtener piratas desde Firestore
    suspend fun getPiratas() {
        val db = FirebaseFirestore.getInstance()
        try {
            val querySnapshot = db.collection("piratasOnePieceGo").get().await()
            val piratas = querySnapshot.toObjects(PirateCharacter::class.java)
            _piratass.value = piratas
        } catch (e: Exception) {
            _piratass.value = emptyList()  // Si hay error, devolver lista vacía
        }
    }


    //codigo viejo

    //Guarda pirata cargado por ID
    private val _pirataById = MutableLiveData<PirateCharacter?>()
    val pirataById: LiveData<PirateCharacter?> = _pirataById

    //Almacena campo individual Id
    private val _id = MutableLiveData<String>()
    val id: LiveData<String> = _id

    //Almacena campo individual nombre
    private val _nombre = MutableLiveData<String>()
    val nombre: LiveData<String> = _nombre

    //Almacena campo individual fruta
    private val _fruit = MutableLiveData<String>()
    val fruit: LiveData<String> = _fruit

    //Almacena campo individual vida
    private val _health = MutableLiveData<Long>()
    val health: LiveData<Long> = _health

    //Almacena campo individual ataque
    private val _attack = MutableLiveData<List<Attack>>(emptyList())
    val attack: LiveData<List<Attack>> = _attack

    //COntrle datos y la Interfaz usuario los lea
    private val _piratas = MutableStateFlow<List<PirateCharacter>>(emptyList())
    val piratas: StateFlow<List<PirateCharacter>> = _piratas

    //Estado observable dice si un boton debe estar habilitado o no
    private val _ButtonEnable = MutableLiveData<Boolean>()
    val buttonEnable: LiveData<Boolean> = _ButtonEnable

    //Borra la lista de ataques del pirata
    fun clearAttacks() {
        _attack.value = emptyList()
    }

    //Completa los campos formulario y activa booton de guardar datos
    fun OnCompleteFields(
        id: String,
        nombre: String,
        fruit: String,
        health: Long,
        attacks: List<Attack>
    ) {
        _id.value = id
        _nombre.value = nombre
        _fruit.value = fruit
        _health.value = health
        _attack.value = attacks
        _ButtonEnable.value = enableButton(id, nombre, fruit, health, attacks)
    }

    //Retorna True solo si hay ID,nmbre,furta,salud>0 y al mneos 1 ataque
    fun enableButton(
        id: String,
        nombre: String,
        fruit: String,
        health: Long,
        attacks: List<Attack>
    ): Boolean {
        // Asegurarse de que la lista no sea null
        val safeAttacks = attacks ?: emptyList()  // Aseguramos que no sea null
        return id.isNotBlank() && nombre.isNotBlank() && fruit.isNotBlank() && health > 0 && safeAttacks.isNotEmpty()
    }

    //Añade ataque a la lista editable
    fun addAttack(attack: Attack) {
        // Asegurarse de que la lista no sea nula antes de realizar operaciones
        val currentAttacks =
            _attack.value?.toMutableList() ?: mutableListOf()  // Si es null, usamos una lista vacía
        currentAttacks.add(attack)
        _attack.value = currentAttacks
    }

}

