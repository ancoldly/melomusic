package com.example.myapplication.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Music
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicViewModel : ViewModel() {

    private val _musics: MutableStateFlow<List<Music>> = MutableStateFlow(emptyList())
    val musics: StateFlow<List<Music>> = _musics

    fun getLatestMusics(count: Int = 10): List<Music> {
        return _musics.value.takeLast(count)
    }

    private var randomMusics: List<Music>? = null

    fun getRandomMusicsOnce(count: Int = 10): List<Music> {
        if (randomMusics == null) {
            val allMusics = _musics.value
            randomMusics = if (allMusics.size <= count) {
                allMusics.shuffled()
            } else {
                allMusics.shuffled().take(count)
            }
        }
        return randomMusics ?: emptyList()
    }

    init {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("songs")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val musicList = mutableListOf<Music>()
                for (musicSnapshot in dataSnapshot.children) {
                    val musicId = musicSnapshot.key
                    val artist = musicSnapshot.child("artist").getValue(String::class.java)
                    val audioUrl = musicSnapshot.child("audioUrl").getValue(String::class.java)
                    val genre = musicSnapshot.child("genre").getValue(String::class.java)
                    val imageUrl = musicSnapshot.child("imageUrl").getValue(String::class.java)
                    val songName = musicSnapshot.child("songName").getValue(String::class.java)
                    val lyric = musicSnapshot.child("lyric").getValue(String::class.java)
                    val playCount = musicSnapshot.child("playCount").getValue(Int::class.java)
                    if (musicId != null && artist != null && audioUrl != null && genre != null && imageUrl != null && songName != null && lyric != null && playCount != null) {
                        musicList.add(
                            Music(
                                musicId,
                                artist,
                                audioUrl,
                                genre,
                                imageUrl,
                                songName,
                                lyric,
                                playCount
                            )
                        )
                    }
                }
                _musics.value = musicList
                randomMusics = null
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun incrementPlayCount(musicId: String) {
        val musicRef = FirebaseDatabase.getInstance().getReference("songs").child(musicId)

        musicRef.child("playCount").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentCount = currentData.getValue(Int::class.java) ?: 0
                currentData.value = currentCount + 1
                return Transaction.success(currentData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                if (databaseError != null) {
                    Log.e(TAG, "Error updating play count: ${databaseError.message}")
                }
            }
        })
    }

    fun deleteMusic(musicId: String, imageUrl: String, audioUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("songs").child(musicId)
        myRef.removeValue()

        deleteImageFromStorage(imageUrl)
        deleteAudioFromStorage(audioUrl)
    }

    fun deleteImageFromStorage(imageUrl: String) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete().addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun deleteAudioFromStorage(audioUrl: String) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(audioUrl)
        storageReference.delete().addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun editMusic(
        musicId: String,
        newArtist: String,
        newAudioUrl: String,
        newGenre: String,
        newImageUrl: String,
        newSongName: String
    ) {
        val musicRef = FirebaseDatabase.getInstance().getReference("songs").child(musicId)

        val updatedData = hashMapOf<String, Any>(
            "artist" to newArtist,
            "audioUrl" to newAudioUrl,
            "genre" to newGenre,
            "imageUrl" to newImageUrl,
            "songName" to newSongName
        )

        musicRef.updateChildren(updatedData)
            .addOnSuccessListener {
                Log.d(TAG, "Music updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating music: ${exception.message}")
            }
    }
}
