package com.example.lolshop.viewmodel.authentication

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cloudinary.api.exceptions.ApiException
import com.example.lolshop.R
import com.example.lolshop.utils.CloudinaryHelper
import com.example.lolshop.view.homepage.MainScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.Normalizer
import java.util.regex.Pattern

class GoogleSignInManager(private val activity: Activity) {

    private val googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.web_client_id)) // Add your web client ID here
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

    }

    fun signIn(onComplete: (Task<com.google.firebase.auth.AuthResult>?) -> Unit) {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Handle the sign-in result in onActivityResult
    fun handleSignInResult(requestCode: Int, data: Intent?, onSignInComplete: (Boolean) -> Unit) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken, onSignInComplete)
            } catch (e: ApiException) {
                onSignInComplete(false)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?, onSignInComplete: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Save user data to Firestore
                        saveUserToFirestore(it)
                    }
                    onSignInComplete(true)
                } else {
                    onSignInComplete(false)
                }
            }
    }

    private fun saveUserToFirestore(user: FirebaseUser) {
        val name = replaceText(user.displayName)
        val userMap = hashMapOf(
            "id" to user.uid,
            "full_name" to name,
            "email" to user.email,
            "address" to "",
            "phone_number" to "",
            "isAdmin" to false,
            "pictureProfile" to ""
        )

        // Store user data in Firestore
        firestore.collection("Users")
            .document(user.uid)
            .set(userMap)
            .addOnSuccessListener {
                Log.d("GoogleSignInManager", "User data added to Firestore successfully.")
                createCartForUser(user.uid)
            }
            .addOnFailureListener { e ->
                Log.w("GoogleSignInManager", "Error adding user data to Firestore", e)
            }
    }

    private fun createCartForUser(userId: String) {
        val cart = hashMapOf(
            "cartId" to userId,
            "products" to listOf<Map<String, Any>>(), // Empty cart
            "total" to 0.0
        )

        // Add the cart to Firestore
        firestore.collection("Carts")
            .document(userId)
            .set(cart)
            .addOnSuccessListener {
                Log.d("GoogleSignInManager", "Cart created successfully.")

                // Update the user's document with the cartId
                firestore.collection("Users")
                    .document(userId)
                    .update("cartId", userId)
                    .addOnSuccessListener {
                        Log.d("GoogleSignInManager", "User document updated with cartId successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.w("GoogleSignInManager", "Error updating user document with cartId", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("GoogleSignInManager", "Error creating cart", e)
            }
    }

    private fun replaceText(input: String?): String {
        // Normalize the text to decompose diacritics
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)

        // Remove diacritics using a regex pattern
        val withoutDiacritics = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("")

        return withoutDiacritics.replace("Đ", "D").replace("đ", "d")
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
