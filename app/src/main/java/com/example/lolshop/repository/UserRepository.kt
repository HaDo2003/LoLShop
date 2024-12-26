package com.example.lolshop.repository

import android.content.Context
import android.util.Log
import com.example.lolshop.model.User
import com.example.lolshop.utils.CloudinaryHelper
import com.example.lolshop.utils.Resource
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.auth.FirebaseAuthException

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    val cloudinaryHelper = CloudinaryHelper(context)
    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    //Get UserId
    fun getUserById(uid: String): Flow<User?> = flow {
        val userSnapshot = FirebaseFirestore.getInstance()
            .collection("Users")
            .document(uid)
            .get()
            .await()

        val user = userSnapshot.toObject(User::class.java)
        emit(user)
    }.catch {
        emit(null) // Handle errors
    }

    //Sign Up
    suspend fun signUpUser(name: String, email: String, password: String, phoneNumber: String, address: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = hashMapOf(
                "id" to (authResult.user?.uid ?: ""),
                "full_name" to name,
                "email" to email,
                "phone_number" to phoneNumber,
                "address" to address,
                "isAdmin" to false,
                "pictureProfile" to ""
            )

            authResult.user?.uid?.let { uid ->
                firestore.collection("Users")
                    .document(uid)  // Use user ID as document ID
                    .set(user)
                    .await()
            }

            // Get the user ID (uid) from Firebase Authentication
            val uid = authResult.user?.uid
            uid?.let {
                // Create a new cart document in the Carts collection
                val cartId = firestore.collection("Carts").document(uid) // Unique cartId from Firestore

                // Cart details (e.g., an empty cart or with some initial data)
                val cart = hashMapOf(
                    "cartId" to uid,
                    "products" to listOf<Map<String, Any>>(),
                    "total" to 0.0
                )

                // Store the cart in Firestore
                firestore.collection("Carts")
                    .document(uid)
                    .set(cart)
                    .await()

                // Now update the user's document to include the cartId
                firestore.collection("Users")
                    .document(uid)
                    .update("cartId", cartId)
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

    //Forgot password and reset password
    suspend fun resetPasswordWithCode(oobCode: String, newPassword: String): Result<String> {
        return try {
            auth.confirmPasswordReset(oobCode, newPassword).await()
            Result.success("Password updated successfully.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Update the user profile
    suspend fun updateUserProfile(uid: String, name: String, phoneNumber: String, address: String): Result<Unit> {
        return try {
            // Prepare the updated user data
            val updatedUser = hashMapOf(
                "full_name" to name,
                "phone_number" to phoneNumber,
                "address" to address
            )

            // Update the Firestore document with the new details
            firestore.collection("Users")
                .document(uid)
                .update(updatedUser as Map<String, Any>)
                .await()

            // Return success
            Result.success(Unit)
        } catch (e: Exception) {
            // Return failure in case of an exception
            Result.failure(e)
        }
    }

    //Log out
    suspend fun logout(): Resource<Unit> {
        return try {
            auth.signOut()  // Sign out the current user
            Resource.Success(Unit)  // Return success
        } catch (e: Exception) {
            Resource.Error("$e")  // Return failure if there's an error
        }
    }

    //Change password
    suspend fun changePassword(currentPassword: String, newPassword: String): Resource<String> {
        val user = getCurrentUser()
        return if (user != null && user.email != null) {
            try {
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
                Resource.Success("Password updated successfully.")
            } catch (e: Exception) {
                Resource.Error("$e")
            }
        } else {
            Resource.Error("User is not authenticated or email is null")
        }
    }

    //Change profile picture
    suspend fun changeProfilePicture(userId: String, imageUri: Uri?) {
        imageUri?.let { uri ->
            val file = cloudinaryHelper.uriToFile(uri)  // Convert URI to File
            if (file != null) {
                try {
                    // Upload image to Cloudinary
                    val downloadUrl = cloudinaryHelper.uploadImageToCloudinary(file, "MobileProject/UserImages")

                    // Save the profile picture URL to Firestore
                    val firestore = FirebaseFirestore.getInstance()
                    val userRef = firestore.collection("Users").document(userId)
                    userRef.update("profilePicture", downloadUrl)  // Assuming "profilePicture" field is present in Firestore
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error uploading image or updating profile picture: ${e.message}")
                }
            }
        }
    }
}
