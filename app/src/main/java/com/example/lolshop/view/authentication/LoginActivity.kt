package com.example.lolshop.view.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lolshop.R
import com.example.lolshop.repository.UserRepository
import com.example.lolshop.view.admin.AdminActivity
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.homepage.MainScreen
import com.example.lolshop.view.homepage.UserProfile
import com.example.lolshop.viewmodel.authentication.LoginState
import com.example.lolshop.viewmodel.authentication.LoginViewModel
import com.example.lolshop.viewmodel.authentication.LoginViewModelFactory
import com.example.lolshop.viewmodel.authentication.GoogleSignInManager
import com.example.lolshop.viewmodel.homepage.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : BaseActivity() {
    private lateinit var googleSignInManager: GoogleSignInManager
    private var isLoading by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(
                    FirebaseAuth.getInstance(),
                    FirebaseFirestore.getInstance(),
                    applicationContext
                )
            )
            LoginScreen(
                onSignUp = {
                    Log.d("LoginScreen", "Navigating to SignUpActivity.")
                    val intent = Intent(this, SignUpActivity::class.java)
                    startActivity(intent)
                },
                onLoginWithGG = {
                    Log.d("LoginScreen", "Navigating to Google Sign-In.")
                    isLoading = true

                    googleSignInManager = GoogleSignInManager(this)
                    googleSignInManager.signIn { task ->
                        isLoading = false
                        if (task != null && task.isSuccessful) {

                        } else {
                            // Handle failed sign-in
                            Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onForgetPassword = {
                    val intent = Intent(this, ForgetPassword::class.java)
                    startActivity(intent)
                },
                isLoading = isLoading,
                viewModel = viewModel
            )
        }
    }

    // Handle the result from Google Sign-In
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleSignInManager.handleSignInResult(requestCode, data) { success ->
            isLoading = false
            if (success) {
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    // Navigate to AdminActivity after successful sign-in
                    val intent = Intent(this, MainScreen::class.java).apply {
                        putExtra("id", it.uid)
                    }
                    startActivity(intent)
                    finish() // Optional: finish LoginActivity to prevent going back to it
                }
            } else {
                // Handle failed sign-in
                Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    isLoading: Boolean,
    onSignUp: () -> Unit,
    onLoginWithGG: () -> Unit,
    onForgetPassword: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Image(
            painter = painterResource(id = R.drawable.mobilelogo),
            contentDescription = stringResource(id = R.string.logo),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
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

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
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

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.loginUser(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Login", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onForgetPassword) {
                Text(
                    "Forget Password",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start
                )
            }
            TextButton(onClick = onSignUp) {
                Text(
                    "Don't have an account? Sign up",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color.Gray
            )
            Text(
                text = "or Sign In With",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            OutlinedButton(
                onClick = onLoginWithGG,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp)) // Adds a 10.dp space
                    Text("Google")
                }
            }
        }

        when (loginState) {
            is LoginState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is LoginState.Success -> {
                val isAdmin = (loginState as LoginState.Success).isAdmin
                val uid = (loginState as LoginState.Success).uid
                val intent = Intent(
                    context,
                    MainScreen::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("id", uid)
                    putExtra("IS_ADMIN", isAdmin)
                }
                context.startActivity(intent)
            }
            is LoginState.Error -> {
                val errorMessage = (loginState as LoginState.Error).message
                Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }
}
