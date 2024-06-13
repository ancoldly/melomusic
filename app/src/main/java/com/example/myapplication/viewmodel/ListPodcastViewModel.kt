package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ListPodcast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ListPodcastViewModel : ViewModel() {

    private val _listPodcasts: MutableStateFlow<List<ListPodcast>> = MutableStateFlow(emptyList())
    val listPodcasts: StateFlow<List<ListPodcast>> = _listPodcasts

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.getReference("listPodcasts")

    var showToast by mutableStateOf(false)

    var toastMessage by mutableStateOf("")
        private set

    init {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listPodcastList = mutableListOf<ListPodcast>()
                for (listPodcastSnapshot in dataSnapshot.children) {
                    val listPodcast = listPodcastSnapshot.getValue(ListPodcast::class.java)
                    listPodcast?.let { listPodcastList.add(it) }
                }
                _listPodcasts.value = listPodcastList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ListPodcastViewModel", "Failed to read value.", error.toException())
            }
        })
    }

    fun getListPodcastCountForUser(userId: String): Int {
        return listPodcasts.value.count { it.userId == userId }
    }

    fun addListPodcast(userId: String, podcastId: String) {
        myRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isAlreadyAdded = false
                for (listPodcastSnapshot in dataSnapshot.children) {
                    val listPodcast = listPodcastSnapshot.getValue(ListPodcast::class.java)
                    if (listPodcast != null && listPodcast.podcastId == podcastId) {
                        isAlreadyAdded = true
                        break
                    }
                }

                if (!isAlreadyAdded) {
                    val newListPodcastRef = myRef.push()
                    val listPodcastId = newListPodcastRef.key ?: ""
                    val listPodcast = ListPodcast(
                        listPodcastId = listPodcastId,
                        userId = userId,
                        podcastId = podcastId
                    )

                    newListPodcastRef.setValue(listPodcast)
                        .addOnSuccessListener {
                            Log.d("ListPodcastViewModel", "ListPodcast added successfully")
                            val successMessage = "ListPodcast added successfully"
                            toastMessage = successMessage
                            showToast = true
                        }
                        .addOnFailureListener { e ->
                            Log.w("ListPodcastViewModel", "Error adding listPodcast", e)
                        }
                } else {
                    val message = "Podcast already exists in the list"
                    toastMessage = message
                    showToast = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ListPodcastViewModel", "Failed to read value.", error.toException())
            }
        })
    }

    fun removeListPodcast(listPodcastId: String) {
        myRef.child(listPodcastId).removeValue()
            .addOnSuccessListener {
                Log.d("ListPodcastViewModel", "ListPodcast removed successfully")
                val message = "ListPodcast removed successfully"
                toastMessage = message
                showToast = true
            }
            .addOnFailureListener { e ->
                Log.w("ListPodcastViewModel", "Error removing listPodcast", e)
            }
    }

    fun isListPodcast(userId: String, podcastId: String): Boolean {
        return listPodcasts.value.any { it.userId == userId && it.podcastId == podcastId }
    }
}
