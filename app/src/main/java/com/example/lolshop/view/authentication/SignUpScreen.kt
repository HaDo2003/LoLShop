package com.example.lolshop.view.authentication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lolshop.R
import com.example.lolshop.viewmodel.authentication.SignUpViewModel
import com.example.lolshop.viewmodel.authentication.SignUpViewModelFactory
import com.example.lolshop.utils.ChangeField
import com.example.lolshop.viewmodel.SignUpViewModel
import com.example.lolshop.viewmodel.SignUpViewModelFactory
import com.example.lolshop.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
    ),
    navigateToLogin: () -> Unit,
    navigateToOtp: (String, String, String, String, String) -> Unit
) {
    val signUpState by viewModel.signUpState.collectAsState()
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.mobilelogo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                //viewModel.signUp(name, email, password, phoneNumber, address, isAdmin = false)
                navigateToOtp(name, email, password, phoneNumber, address)
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = navigateToLogin) {
            Text("Already have an account? Sign In")
        }

        when (signUpState) {
            is Resource.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is Resource.Success -> {
                Toast.makeText(
                    androidx.compose.ui.platform.LocalContext.current,
                    "Sign-up successful!",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToLogin()
            }
            is Resource.Error -> {
                val message = (signUpState as Resource.Error).message
                Toast.makeText(
                    androidx.compose.ui.platform.LocalContext.current,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            is Resource.Empty -> {}
        }
    }
}
