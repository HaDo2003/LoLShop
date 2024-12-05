package com.example.lolshop.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lolshop.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Set the login screen layout

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find views by ID
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signupTextView = findViewById<TextView>(R.id.signupTextView)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Perform login with Firebase
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login successful, navigate to Admin screen
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                            finish() // Finish the login activity so the user can't go back
                        } else {
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Handle sign-up text click
        signupTextView.setOnClickListener {
            // Navigate to the sign-up screen or perform another action
            Toast.makeText(this, "Sign-up clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
