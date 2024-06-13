package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Favorite
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoriteViewModel : ViewModel() {

    private val _favorites: MutableStateFlow<List<Favorite>> = MutableStateFlow(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.getReference("favorites")

    var showToast by mutableStateOf(false)

    var toastMessage by mutableStateOf("")
        private set


    init {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val favoriteList = mutableListOf<Favorite>()
                for (favoriteSnapshot in dataSnapshot.children) {
                    val favorite = favoriteSnapshot.getValue(Favorite::class.java)
                    favorite?.let { favoriteList.add(it) }
                }
                _favorites.value = favoriteList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FavoriteViewModel", "Failed to read value.", error.toException())
            }
        })
    }

    fun getFavoriteCountForUser(userId: String): Int {
        return favorites.value.count { it.userId == userId }
    }

    fun addFavorite(userId: String, musicId: String) {
        myRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isAlreadyAdded = false
                for (favoriteSnapshot in dataSnapshot.children) {
                    val favorite = favoriteSnapshot.getValue(Favorite::class.java)
                    if (favorite != null && favorite.musicId == musicId) {
                        isAlreadyAdded = true
                        break
                    }
                }

                if (!isAlreadyAdded) {
                    val newFavoriteRef = myRef.push()
                    val favoriteId = newFavoriteRef.key ?: ""
                    val favorite = Favorite(
                        favoriteId = favoriteId,
                        userId = userId,
                        musicId = musicId
                    )

                    newFavoriteRef.setValue(favorite)
                        .addOnSuccessListener {
                            Log.d("FavoriteViewModel", "Favorite added successfully")
                            val successMessage = "Favorite added successfully"
                            toastMessage = successMessage
                            showToast = true
                        }
                        .addOnFailureListener { e ->
                            Log.w("FavoriteViewModel", "Error adding favorite", e)
                        }
                } else {
                    val message = "Song already exists in the favorite"
                    toastMessage = message
                    showToast = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FavoriteViewModel", "Failed to read value.", error.toException())
            }
        })
    }

    fun removeFavorite(favoriteId: String) {
        myRef.child(favoriteId).removeValue()
            .addOnSuccessListener {
                Log.d("FavoriteViewModel", "Favorite removed successfully")
                val message = "Favorite removed successfully"
                toastMessage = message
                showToast = true
            }
            .addOnFailureListener { e ->
                Log.w("FavoriteViewModel", "Error removing favorite", e)
            }
    }

    fun isFavorite(userId: String, musicId: String): Boolean {
        return favorites.value.any { it.userId == userId && it.musicId == musicId }
    }
}
