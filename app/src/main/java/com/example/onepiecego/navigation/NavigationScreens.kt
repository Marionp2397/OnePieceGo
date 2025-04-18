package com.example.onepiecego.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


import com.example.onepiecego.screens.BorrarPirata
import com.example.onepiecego.screens.ModificarPirata

import com.example.onepiecego.screens.Login
import com.example.onepiecego.screens.MainMenuScreen
import com.example.onepiecego.screens.PirataAlta
import com.example.onepiecego.screens.SelectPiratesScreen
import com.example.onepiecego.screens.ViewCharacter
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(auth: FirebaseAuth) { //Recibe parametro FIrebase
    val navigationController =
        rememberNavController() // Crear NavContrller para gestion entre pantallas y recuerde el estado de navegacion
    NavHost(
        navController = navigationController,
        startDestination = AppScreens.Login.ruta //Indica la pantalla inical
    )
    {
        composable(AppScreens.Login.ruta) {
            Login(
                navigationController,
                auth
            )
        } //Si la ruta activa es "Login" pues se muestra Login
        composable(AppScreens.Menu.ruta) {
            MainMenuScreen(
                navigationController,
                auth,
                viewModel()
            )
        } //Si la ruta activa es MainMenuScreen, pues se muestra menu, con auth y el viewmodel
        composable(AppScreens.View.ruta) {
            ViewCharacter(
                navigationController,
                auth,
                viewModel()
            )
        }  //Si la ruta activa es ViewCharacter, pues se muestra vista, con auth y el viewmodel
        composable(AppScreens.Modification.ruta) {
            ModificarPirata(
                navigationController,
                auth,
                viewModel()
            )
        }  //Si la ruta activa es ModificarPirata, pues se muestra modificar, con auth y el viewmodel
        composable(AppScreens.Create.ruta) {
            PirataAlta(
                navigationController,
                auth,
                viewModel()
            )
        }  //Si la ruta activa es PirataAlta, pues se muestra crear, con auth y el viewmodel
        composable(AppScreens.Delete.ruta) {
            BorrarPirata(
                navigationController,
                auth,
                viewModel()
            )
        }  //Si la ruta activa es BorrarPirata, pues se muestra eliminat, con auth y el viewmodel
        composable(AppScreens.Battle.ruta) {
            SelectPiratesScreen(
                navigationController,
                auth,
                viewModel()
            )
        }  //Si la ruta activa es SelectPiratesScreen, pues se muestra batalla, con auth y el viewmodel

    }
}


