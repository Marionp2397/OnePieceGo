package com.example.onepiecego.models


import android.content.Context
import android.media.MediaPlayer
import com.example.onepiecego.R

//Definicion del objeto que usamos Singleton

object MenuMusicPlayer {
    private var mediaPlayer: MediaPlayer? = null //Clase Android para reprducit musica
    private var isMuted = false //Booleano para indicar si esta mute o no
    private var currentMusicResId: Int? = null // Variable para almacenar la música actual
    private val loginMusicList = listOf( //Lista musica para el login
        R.raw.musica_logn_1,
        R.raw.musica_login_2,
        R.raw.musica_login_3,
        R.raw.musica_login_4

    )

    // Iniciar la música en el menu
    fun start(context: Context) { //Inicia musica
        if (mediaPlayer == null) { // Si es null inicia
            mediaPlayer = MediaPlayer.create(context, R.raw.menu_musica)
                .apply { //Crea instancia de mediaPlayer y carga la cancion
                    isLooping = true //En bucle la cancion
                    setVolume(
                        if (isMuted) 0f else 1f,  //Ajusta volumen para mutear y desmutear
                        if (isMuted) 0f else 1f
                    )
                    start() //Reproduce musica
                    currentMusicResId = R.raw.menu_musica // Guardamos la música actual
                }
        }
    }

    // Cambiar la música
    fun changeMusic( //Detiene musica actual y reproduce nueva, usando e ID
        context: Context,
        resId: Int
    ) {
        stop() // Para la música actual
        mediaPlayer = MediaPlayer.create(context, resId).apply { //Nueva instancia del Mediaplayer
            isLooping = true //Bucle cancion
            setVolume(
                if (isMuted) 0f else 1f, //Ajusta volumen para mutear y desmutear
                if (isMuted) 0f else 1f
            )
            start()  //Reproduce musica
            currentMusicResId = resId // Actualiza el id que se reproduce
        }
    }

    // Detener la música
    fun stop() {
        mediaPlayer?.stop() //Detiene musica si esta sonando
        mediaPlayer?.release()  //Liberaaa memoria
        mediaPlayer = null      //Liberaaa memoria
        currentMusicResId = null // Restablecer música cuando se detiene
    }

    // Mutear o desmutear
    fun toggleMute() {
        isMuted = !isMuted  //Cambia el esado de isMuted a true o false
        mediaPlayer?.setVolume(
            if (isMuted) 0f else 1f, //Ajusta volumen segun este muted o no
            if (isMuted) 0f else 1f
        )
    }

    // Verificar si está muteado
    fun isCurrentlyMuted(): Boolean = isMuted

    // Obtener la música actual
    fun getCurrentMusic(): Int? = currentMusicResId

    //CAmbiarl la musica aleatoriamente para el login
    fun changeMusicRandomForLogin(context: Context) {
        val randomResId = loginMusicList.random()
        changeMusic(context, randomResId)
    }
}
