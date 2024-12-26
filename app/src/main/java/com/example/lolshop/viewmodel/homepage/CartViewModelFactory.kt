package com.example.lolshop.viewmodel.homepage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lolshop.repository.CartRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class CartViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val db: FirebaseDatabase,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(CartRepository(firestore, db, context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}