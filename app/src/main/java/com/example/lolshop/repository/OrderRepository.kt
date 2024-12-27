package com.example.lolshop.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class OrderRepository(
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    suspend fun addOrder(uid: String, productId: String){
        try{

        }catch (e: Exception){
            Log.e("OrderRepository", "Error add order", e)
            throw e
        }
    }
}