package com.example.myapplication.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Podcast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PodcastViewModel : ViewModel() {

    private val _podcasts: MutableStateFlow<List<Podcast>> = MutableStateFlow(emptyList())
    val podcasts: StateFlow<List<Podcast>> = _podcasts

    init {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("podcasts")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val podcastList = mutableListOf<Podcast>()
                for (podcastSnapshot in dataSnapshot.children) {
                    val podcastId = podcastSnapshot.key
                    val artist = podcastSnapshot.child("artist").getValue(String::class.java)
                    val audioUrl = podcastSnapshot.child("audioUrl").getValue(String::class.java)
                    val imageUrl = podcastSnapshot.child("imageUrl").getValue(String::class.java)
                    val podcastName = podcastSnapshot.child("podcastName").getValue(String::class.java)
                    if (podcastId != null && artist != null && audioUrl != null && imageUrl != null && podcastName != null) {
                        podcastList.add(
                            Podcast(
                                podcastId,
                                artist,
                                audioUrl,
                                imageUrl,
                                podcastName
                            )
                        )
                    }
                }
                _podcasts.value = podcastList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun deletePodcast(podcastId: String, imageUrl: String, audioUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("podcasts").child(podcastId)
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

    fun editPodcast(
        podcastId: String,
        newArtist: String,
        newAudioUrl: String,
        newImageUrl: String,
        newPodcastName: String
    ) {
        val musicRef = FirebaseDatabase.getInstance().getReference("podcasts").child(podcastId)

        val updatedData = hashMapOf<String, Any>(
            "artist" to newArtist,
            "audioUrl" to newAudioUrl,
            "imageUrl" to newImageUrl,
            "songName" to newPodcastName
        )

        musicRef.updateChildren(updatedData)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Podcast updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error updating podcast: ${exception.message}")
            }
    }
}
