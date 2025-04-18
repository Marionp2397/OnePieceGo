package com.example.onepiecego.navigation

sealed class AppScreens(val ruta: String) {
    object Login : AppScreens("Login")  //Ruta para Login
    object Menu : AppScreens("Menu")     //Ruta para Menu
    object Create : AppScreens("Create")     //Ruta para crear personaje
    object View : AppScreens("View")         //Ruta para ver personajes
    object Modification : AppScreens("Modification") //Ruta para modificar esos personajes
    object Delete : AppScreens("Delete")     //Ruta para borrar personajes
    object Battle : AppScreens("Battle")     //Ruta para la batalla


}
