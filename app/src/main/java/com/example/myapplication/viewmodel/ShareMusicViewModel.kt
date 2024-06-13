package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ShareMusic
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShareMusicViewModel : ViewModel() {

    private val _sharedMusics: MutableStateFlow<List<ShareMusic>> = MutableStateFlow(emptyList())
    val sharedMusics: StateFlow<List<ShareMusic>> = _sharedMusics

    var showToast by mutableStateOf(false)

    var toastMessage by mutableStateOf("")
        private set

    init {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("sharedMusics")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val shareMusicList = mutableListOf<ShareMusic>()
                for (shareMusicSnapshot in dataSnapshot.children) {
                    val shareMusic = shareMusicSnapshot.getValue(ShareMusic::class.java)
                    if (shareMusic != null) {
                        shareMusicList.add(shareMusic)
                    }
                }
                _sharedMusics.value = shareMusicList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ShareMusicViewModel", "Error fetching shared musics: ${error.message}")
            }
        })
    }

    fun saveShareMusic(userId: String, musicId: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("sharedMusics")

        val shareMusicId = myRef.push().key
        if (shareMusicId != null) {
            val createdDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val shareMusic = ShareMusic(shareMusicId, userId, musicId, createdDate)
            myRef.child(shareMusicId).setValue(shareMusic)
                .addOnSuccessListener {
                    Log.d("ShareMusicViewModel", "Share music saved successfully")
                    val successMessage = "Share music saved successfully"
                    toastMessage = successMessage
                    showToast = true
                }
                .addOnFailureListener { exception ->
                    Log.e("ShareMusicViewModel", "Error saving share music: ${exception.message}")
                }
        } else {
            Log.e("ShareMusicViewModel", "Error generating unique share music ID")
        }
    }

    fun isMusicShared(musicId: String): Boolean {
        val sharedMusicList = sharedMusics.value
        return sharedMusicList.any { it.musicId == musicId }
    }

    fun deleteShareMusic(shareMusicId: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("sharedMusics").child(shareMusicId)
        myRef.removeValue()
            .addOnSuccessListener {
                Log.d("ShareMusicViewModel", "Share music deleted successfully")
                val successMessage = "Share music deleted successfully"
                toastMessage = successMessage
                showToast = true
            }
            .addOnFailureListener { exception ->
                Log.e("ShareMusicViewModel", "Error deleting share music: ${exception.message}")
            }
    }
}
