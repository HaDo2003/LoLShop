package com.example.lolshop.viewmodel.authentication

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.cloudinary.api.exceptions.ApiException
import com.example.lolshop.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

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
        val userMap = hashMapOf(
            "id" to user.uid,
            "full_name" to user.displayName,
            "email" to user.email,
            "address" to "",
            "phone_number" to "",
            "isAdmin" to true
        )

        // Store user data in Firestore
        firestore.collection("Users")
            .document(user.uid)
            .set(userMap)
            .addOnSuccessListener {
                Log.d("GoogleSignInManager", "User data added to Firestore successfully.")
            }
            .addOnFailureListener { e ->
                Log.w("GoogleSignInManager", "Error adding user data to Firestore", e)
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
