package com.example.lolshop.view.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.viewmodel.authentication.ChangePasswordViewModel
import com.example.lolshop.viewmodel.authentication.ChangePasswordViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangePassword : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val oobCode = intent.getStringExtra("oobCode") ?: ""
        setContent{
            val viewModel: ChangePasswordViewModel = viewModel(
                factory = ChangePasswordViewModelFactory(
                    FirebaseAuth.getInstance(),
                    FirebaseFirestore.getInstance(),
                    applicationContext
                )
            )

            ChangePasswordScreen(
                viewModel = viewModel,
                oobCode = oobCode,
                navigateToLogin = {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun ChangePasswordScreen(
    viewModel: ChangePasswordViewModel,
    oobCode: String,
    navigateToLogin: () -> Unit
) {
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmationPassword by rememberSaveable { mutableStateOf("") }
    val resetPasswordStatus by viewModel.resetPasswordStatus.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter new password",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 92.dp,
                        vertical = 16.dp
                    )
                    .align(Alignment.CenterHorizontally)
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmationPassword,
                onValueChange = { confirmationPassword = it },
                label = { Text("Confirm New Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (newPassword == confirmationPassword) {
                        viewModel.resetPassword(oobCode, newPassword)
                        navigateToLogin()
                    } else {
                        viewModel.setErrorMessage("Passwords do not match")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Change Password")
            }
            resetPasswordStatus?.let {
                Text(
                    text = it,
                    color = if (it.contains(
                            "success",
                            true
                        )
                    ) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                if (it.contains("success", true)) {
                    viewModel.clearMessage()
                }
            }
        }
    }
}