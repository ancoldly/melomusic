package com.example.myapplication.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Music
import com.example.myapplication.data.Playlist
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaylistViewModel : ViewModel() {

    private val _playlists: MutableStateFlow<List<Playlist>> = MutableStateFlow(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    private val _currentPlaylistSongs: MutableStateFlow<List<Music>> = MutableStateFlow(emptyList())
    val currentPlaylistSongs: StateFlow<List<Music>> = _currentPlaylistSongs

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("playlists")

    var showToast by mutableStateOf(false)

    var toastMessage by mutableStateOf("")
        private set

    init {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val playlistList = mutableListOf<Playlist>()
                for (playlistSnapshot in dataSnapshot.children) {
                    val playlistId = playlistSnapshot.key
                    val playlistName = playlistSnapshot.child("playlistName").getValue(String::class.java)
                    val userId = playlistSnapshot.child("userId").getValue(String::class.java)
                    val createdDate = playlistSnapshot.child("createdDate").getValue(String::class.java)

                    val songs = mutableListOf<String>()
                    val songsSnapshot = playlistSnapshot.child("songs")
                    for (songSnapshot in songsSnapshot.children) {
                        val songId = songSnapshot.getValue(String::class.java)
                        songId?.let { songs.add(it) }
                    }

                    if (playlistId != null && playlistName != null && userId != null && createdDate != null) {
                        playlistList.add(
                            Playlist(
                                playlistId,
                                playlistName,
                                userId,
                                createdDate,
                                songs
                            )
                        )
                    }
                }
                _playlists.value = playlistList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("PlaylistViewModel", "Failed to read value.", error.toException())
            }
        })
    }

    fun addPlaylist(playlist: Playlist) {
        database.reference.child("playlists").child(playlist.playlistId).setValue(playlist)
            .addOnSuccessListener {
                Log.d("PlaylistViewModel", "Playlist created successfully")
            }
            .addOnFailureListener { e ->
                Log.w("PlaylistViewModel", "Error creating playlist", e)
            }
    }

    fun deletePlaylist(playlistId: String) {
        database.reference.child("playlists").child(playlistId).removeValue()
            .addOnSuccessListener {
                Log.d("PlaylistViewModel", "Playlist deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w("PlaylistViewModel", "Error deleting playlist", e)
            }
    }

    fun deleteSongFromPlaylist(songId: String, playlistId: String) {
        val playlistRef = database.getReference("playlists").child(playlistId).child("songs")

        playlistRef.orderByValue().equalTo(songId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue().addOnSuccessListener {
                        Log.d("PlaylistViewModel", "Song deleted from playlist successfully")
                    }.addOnFailureListener { e ->
                        Log.w("PlaylistViewModel", "Error deleting song from playlist", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("PlaylistViewModel", "Failed to read value.", error.toException())
            }
        })
    }


    fun addSongToPlaylist(songId: String, playlistId: String) {
        val playlistRef = database.getReference("playlists").child(playlistId).child("songs")

        playlistRef.orderByValue().equalTo(songId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val message = "Song already exists in the playlist"
                    toastMessage = message
                    showToast = true
                } else {
                    playlistRef.push().setValue(songId)
                        .addOnSuccessListener {
                            val successMessage = "Song add to playlist successfully"
                            toastMessage = successMessage
                            showToast = true
                        }
                        .addOnFailureListener { e ->
                            val errorMessage = "Error adding song to playlist: ${e.message}"
                            toastMessage = errorMessage
                            showToast = true
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                val errorMessage = "Failed to read value: ${error.toException()}"
                toastMessage = errorMessage
                showToast = true
            }
        })
    }

    fun getNumberOfSongsInPlaylist(playlistId: String): Int {
        return playlists.value.firstOrNull { it.playlistId == playlistId }?.songs?.size ?: 0
    }
}
