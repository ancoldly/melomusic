package com.example.myapplication.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Category
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoryViewModel() : ViewModel() {

    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    init {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("categories")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categoryList = mutableListOf<Category>()
                for (categorySnapshot in dataSnapshot.children) {
                    val categoryId = categorySnapshot.key
                    val categoryName = categorySnapshot.child("categoryName").getValue(String::class.java)
                    val categoryImageUrl = categorySnapshot.child("categoryImageUrl").getValue(String::class.java)
                    if (categoryId != null && categoryName != null && categoryImageUrl != null) {
                        categoryList.add(Category(categoryId, categoryName, categoryImageUrl))
                    }
                }
                _categories.value = categoryList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun deleteCategory(categoryId: String, imageUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("categories").child(categoryId)
        myRef.removeValue()

        deleteImageFromStorage(imageUrl)
    }

    fun deleteImageFromStorage(imageUrl: String) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete().addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    fun editCategory(categoryId: String, newName: String, newImageUrl: String) {
        val categoryRef = FirebaseDatabase.getInstance().getReference("categories").child(categoryId)

        val updatedData = hashMapOf<String, Any>(
            "categoryName" to newName,
            "categoryImageUrl" to newImageUrl
        )

        categoryRef.updateChildren(updatedData)
            .addOnSuccessListener {
                Log.d(TAG, "Category updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating category: ${exception.message}")
            }
    }
}


