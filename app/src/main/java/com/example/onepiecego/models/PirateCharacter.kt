package com.example.onepiecego.models

//Aqui tenemos la clase PirateCharacter, la que nos da tdos los datos de nuestro pirata

data class PirateCharacter(
    val id: String = "",    //La ID
    val name: String = "",  //El nombre del pirata
    val fruit: String = "", //La fruta del pirata
    val health: Long = 100L,    //La vida del pirata
    val attacks: List<Attack> = emptyList() //Una lista vacia de ataques que lo pilla de la clase Attack

)
