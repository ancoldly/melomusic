package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.CommentPodcast
import com.example.myapplication.data.ShareMusic
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommentPodcastViewModel : ViewModel() {

    private val _commentPodcast: MutableStateFlow<List<CommentPodcast>> = MutableStateFlow(emptyList())
    val commentPodcast: StateFlow<List<CommentPodcast>> = _commentPodcast

    init {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("commentPodcast")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val commentsList = mutableListOf<CommentPodcast>()
                for (commentSnapshot in dataSnapshot.children) {
                    val comment = commentSnapshot.getValue(CommentPodcast::class.java)
                    comment?.let {
                        commentsList.add(it)
                    }
                }
                _commentPodcast.value = commentsList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CommentPodcastViewModel", "Error fetching comments: ${error.message}")
            }
        })
    }

    fun addComment(podcastId: String, userId: String, content: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("commentPodcast")

        val commentId = myRef.push().key
        if (commentId != null) {
            val comment = CommentPodcast(
                commentId = commentId,
                podcastId = podcastId,
                userId = userId,
                content = content
            )
            myRef.child(commentId).setValue(comment)
                .addOnSuccessListener {
                    Log.d("CommentPodcastViewModel", "Comment added successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("CommentPodcastViewModel", "Error adding comment: ${exception.message}")
                }
        } else {
            Log.e("CommentPodcastViewModel", "Error generating unique comment ID")
        }
    }

    fun deleteComment(commentId: String, podcastId: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("commentPodcast").child(commentId)

        myRef.removeValue()
            .addOnSuccessListener {
                Log.d("CommentPodcastViewModel", "Comment deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("CommentPodcastViewModel", "Error deleting comment: ${exception.message}")
            }
    }
}
