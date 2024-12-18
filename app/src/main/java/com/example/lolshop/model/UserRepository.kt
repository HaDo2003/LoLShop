package com.example.lolshop.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    suspend fun signUpUser(name: String, email: String, password: String, phoneNumber: String, address: String, isAdmin: Boolean): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(
                id = authResult.user?.uid ?: "",
                full_name = name,
                phone_number = phoneNumber,
                address = address,
                isAdmin = isAdmin
            )
            firestore.collection("Users").document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
