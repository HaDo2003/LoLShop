package com.example.lolshop.repository

import com.example.lolshop.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    fun getCurrentUser() = auth.currentUser

    //Sign Up
    suspend fun signUpUser(name: String, email: String, password: String, phoneNumber: String, address: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = hashMapOf(
                "id" to (authResult.user?.uid ?: ""),
                "full_name" to name,
                "phone_number" to phoneNumber,
                "address" to address,
                "isAdmin" to false
            )
            authResult.user?.uid?.let { uid ->
                firestore.collection("Users")
                    .document(uid)  // Use user ID as document ID
                    .set(user)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Login
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

    //Change password
    suspend fun updatePassword(newPassword: String): Result<String> {
        val user = getCurrentUser()
        return if (user != null) {
            try {
                user.updatePassword(newPassword).await()
                Result.success("Password updated successfully.")
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("User not signed in."))
        }
    }

    //Forgot password and reset password
    suspend fun resetPasswordWithCode(oobCode: String, newPassword: String): Result<String> {
        return try {
            auth.confirmPasswordReset(oobCode, newPassword).await()
            Result.success("Password updated successfully.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Sign In with google

}
