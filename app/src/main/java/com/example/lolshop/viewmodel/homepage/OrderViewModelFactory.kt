package com.example.lolshop.viewmodel.homepage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lolshop.repository.OrderRepository
import com.google.firebase.firestore.FirebaseFirestore

class OrderViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(OrderViewModel::class.java)){
            return OrderViewModel(OrderRepository(firestore, context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}