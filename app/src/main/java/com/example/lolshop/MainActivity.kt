package com.example.lolshop

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Set the login screen layout

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
                // Perform login logic here (e.g., authenticate with backend)
                Toast.makeText(this, "Login successful for $email", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle sign-up text click
        signupTextView.setOnClickListener {
            // Navigate to the sign-up screen or perform another action
            Toast.makeText(this, "Sign-up clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
