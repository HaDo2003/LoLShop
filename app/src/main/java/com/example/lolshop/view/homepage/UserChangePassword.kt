package com.example.lolshop.view.homepage

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lolshop.R
import com.example.lolshop.utils.Resource
import com.example.lolshop.view.ErrorNotificationScreen
import com.example.lolshop.view.authentication.LoginActivity
import com.example.lolshop.viewmodel.homepage.UserViewModel

@Composable
fun ChangePasswordScreen(
    userViewModel: UserViewModel,
    uid: String,
    navController: NavController
){
    val passwordChangeState by userViewModel.passwordChangeState.collectAsState(Resource.Empty())
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var passwordVisible1 by rememberSaveable { mutableStateOf(false) }
    var passwordVisible2 by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var errorMessage by remember { mutableStateOf("") }
    var isErrorScreenVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Change Password", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Curent Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible) R.drawable.hide else R.drawable.show
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    Image(
                        painter = painterResource(id = image),
                        contentDescription = description,
                        modifier = Modifier
                            .clickable { passwordVisible = !passwordVisible }
                            .size(30.dp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password ") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible1) R.drawable.hide else R.drawable.show
                    val description = if (passwordVisible1) "Hide password" else "Show password"

                    Image(
                        painter = painterResource(id = image),
                        contentDescription = description,
                        modifier = Modifier
                            .clickable { passwordVisible1 = !passwordVisible1 }
                            .size(30.dp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible2) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible2) R.drawable.hide else R.drawable.show
                    val description = if (passwordVisible2) "Hide password" else "Show password"

                    Image(
                        painter = painterResource(id = image),
                        contentDescription = description,
                        modifier = Modifier
                            .clickable { passwordVisible2 = !passwordVisible2 }
                            .size(30.dp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                    cursorColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Save Button
            Button(
                onClick = {
                    if (userViewModel.checkPassword(currentPassword, newPassword, confirmPassword)) {
                        userViewModel.changePassword(currentPassword, newPassword)
                        isLoading = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
            ) {
                Text("Change Password")
            }
        }
        LaunchedEffect(passwordChangeState) {
            when (passwordChangeState) {
                is Resource.Loading -> {
                    isLoading = true
                }
                is Resource.Success -> {
                    Toast.makeText(context, "Change Password successfully!", Toast.LENGTH_SHORT).show()

                    // Navigate to LoginActivity after successful logout
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                }
                is Resource.Error -> {
                    keyboardController?.hide()
                    errorMessage = (passwordChangeState as Resource.Error).message
                    isErrorScreenVisible = true
                    userViewModel.clearError()
                }
                is Resource.Empty -> {
                    isLoading = false
                }
                null -> {}
            }
        }
    }
    if (isErrorScreenVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 56.dp)
                .padding(top = 381.dp, bottom = 370.dp),
            contentAlignment = Alignment.Center
        ) {
            ErrorNotificationScreen(
                message = errorMessage,
                onConfirm = {
                    isErrorScreenVisible = false
                    errorMessage = ""
                    userViewModel.clearError()
                }
            )
        }
    }
}