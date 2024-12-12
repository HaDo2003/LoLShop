package com.example.lolshop.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lolshop.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var FStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        FStore = FirebaseFirestore.getInstance()

        setContent {
            LoginScreen(
                onLogin = { email, password ->
                    performLogin(email, password)
                },
                onSignUp = {
                    Log.d("LoginScreen", "Navigating to SignUpActivity.")
                    val intent = Intent(this, SignUpActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }

    private fun performLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //Log.d("LoginActivity", "Login successful. Navigating to AdminActivity.")
                        //val intent = Intent(this, AdminActivity::class.java)
                        //startActivity(intent)
                        val authResult = FirebaseAuth.getInstance().currentUser
                        authResult?.let { user ->
                            checkUserAccessLevel(user.uid)
                        } ?: run {
                            // Handle the case where the user is not authenticated
                            println("No user is logged in.")
                        }
                    } else {
                        val errorMessage = task.exception?.message ?: "Unknown error occurred"
                        Log.e("LoginActivity", "Authentication failed: $errorMessage")
                        Toast.makeText(this, "Authentication failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkUserAccessLevel(uid: String) {
        val df = FStore.collection("Users").document(uid)
        df.get().addOnSuccessListener { documentSnapshot ->
            val isAdmin = documentSnapshot.getBoolean("isAdmin")

            when (isAdmin) {
                true -> {
                    // Login as Admin
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                false -> {
                    // Login as Customer
                    val intent = Intent(this, MainScreen::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Log.e("TAG", "User is neither admin nor customer")
                    // Handle the case where neither condition is met, maybe show an error or redirect to login
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("TAG", "Error getting document", exception)
        }
    }
}

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.mobilelogo),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier
                .size(100.dp)
                .background(Color.Black)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Login Button
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text(text = "Login", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sign-up text
        TextButton(onClick = onSignUp) {
            Text(
                text = "Don't have an account? Sign up",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}
