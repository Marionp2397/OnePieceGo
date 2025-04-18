package com.example.onepiecego.models

//Aqui tenems la clase separada de los ataques.

data class Attack(
    val name: String = "", //Nombre del ataque
    val damage: Long = 0L   //Daño del ataque
)