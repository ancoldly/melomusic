package com.example.myapplication.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {

    private val _users: MutableStateFlow<List<User>> = MutableStateFlow(emptyList())
    val users: StateFlow<List<User>> = _users

    init {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (userSnapshot in dataSnapshot.children) {
                    val userId = userSnapshot.key
                    val email = userSnapshot.child("email").getValue(String::class.java)
                    if (userId != null && email != null) {
                        userList.add(User(userId, email))
                    }
                }
                _users.value = userList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun deleteUser(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.child(userId).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "User with ID $userId deleted successfully.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting user with ID $userId", e)
            }
    }
}