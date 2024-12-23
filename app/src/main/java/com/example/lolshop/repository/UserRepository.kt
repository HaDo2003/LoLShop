package com.example.lolshop.repository

import com.example.lolshop.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore
) {
    suspend fun signUpUser(name: String, email: String, password: String, phoneNumber: String, address: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(
                id = authResult.user?.uid ?: "",
                full_name = name,
                phone_number = phoneNumber,
                address = address,
                isAdmin = false
            )
            firestore.collection("Users")
                .document(user.id)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserAccessLevel(uid: String): Result<Boolean?> {
        return try {
            val documentSnapshot = firestore.collection("Users").document(uid).get().await()
            val isAdmin = documentSnapshot.getBoolean("isAdmin")
            Result.success(isAdmin)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
