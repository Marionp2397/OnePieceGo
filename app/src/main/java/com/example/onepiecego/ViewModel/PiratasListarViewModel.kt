package com.example.onepiecego.ViewModel

import androidx.lifecycle.ViewModel
import com.example.onepiecego.models.PirateCharacter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PiratasListarViewModel : ViewModel() {

    suspend fun getPiratasViewModel(): List<PirateCharacter> {
        return try {
            val db = FirebaseFirestore.getInstance()

            var nombre_coleccion = "piratas"

            val query = db.collection(nombre_coleccion).get().await()

            var piratas = mutableListOf<PirateCharacter>()

            for (document in query.documents) {
                val pirata = document.toObject(PirateCharacter::class.java)
                if (pirata != null) {
                    piratas.add(pirata)
                }
            }
            query.toObjects(PirateCharacter::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}