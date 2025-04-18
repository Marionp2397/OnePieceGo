package com.example.onepiecego.ViewModel

import androidx.lifecycle.ViewModel
import com.example.onepiecego.models.PirateCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PiratasModificarViewModel : ViewModel() {

    private val _pirate = MutableStateFlow<PirateCharacter?>(null)
    val pirate: StateFlow<PirateCharacter?> = _pirate


}
