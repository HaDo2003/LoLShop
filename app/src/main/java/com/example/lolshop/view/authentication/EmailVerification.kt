package com.example.lolshop.view.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lolshop.R
import com.example.lolshop.utils.ChangeField
import com.example.lolshop.utils.OTPHelper
import com.example.lolshop.utils.Resource
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.theme.LoLShopTheme
import com.example.lolshop.viewmodel.authentication.SignUpViewModel
import com.example.lolshop.viewmodel.authentication.SignUpViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmailVerification : BaseActivity() {
    private lateinit var random : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name").toString()
        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()
        val phoneNumber = intent.getStringExtra("phoneNumber").toString()
        val address = intent.getStringExtra("address").toString()
        val otpHelper = OTPHelper()
        random = otpHelper.generateOtp(email, "verify your email")
        setContent {
            val viewModel: SignUpViewModel = viewModel(
            factory = SignUpViewModelFactory(
                FirebaseAuth.getInstance(),
                FirebaseFirestore.getInstance()
                )
            )
            LoLShopTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(20.dp),
                    color = Color.White
                ) {
                    OTPVerificationScreen(
                        viewModel, otpHelper, random, name, email, password, phoneNumber, address,
                        navigateToLogin = {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        updateOtp = { newOtp -> random = newOtp }
                    )
                }
            }
        }
    }


}

@Composable
fun OTPVerificationScreen(
    viewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
    ),
    otpHelper: OTPHelper,
    random: String,
    name: String,
    email: String,
    password: String,
    phoneNumber: String,
    address: String,
    navigateToLogin: () -> Unit,
    updateOtp: (String) -> Unit
) {
    val signUpState by viewModel.signUpState.collectAsState()
    var isError by rememberSaveable { mutableStateOf(false) }
    var otpValue by rememberSaveable { mutableStateOf("") }
    var success by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val changeField = ChangeField()

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
            Image(
                painter = painterResource(id = R.drawable.otp),
                contentDescription = "Logo",
            )
            Text(
                text = "OTP Verification",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Enter the OTP sent to your email.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OtpTextField(
                otpText = otpValue,
                onOtpTextChange = { value, _ ->
                    otpValue = value
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (otpValue.length == 6) {
                        success = otpHelper.verifyOtp(otpValue, random)
                        if (success){
                            viewModel.signUp(name, email, password, phoneNumber, address)
                        }else {
                            // Show error toast for wrong OTP
                            Toast.makeText(
                                context,
                                "Wrong OTP",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = otpValue.length == 6
            ) {
                Text("Verify OTP")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    val newOtp = otpHelper.resendOTP(email)
                    updateOtp(newOtp)
                }
            ) {
                Text("Didn't receive OTP? Resend OTP")
            }

            when (signUpState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is Resource.Success -> {
                    Toast.makeText(
                        LocalContext.current,
                        "Sign-up successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToLogin()
                    changeField.changeField()
                }
                is Resource.Error -> {
                    val message = (signUpState as Resource.Error).message
                    Toast.makeText(
                        LocalContext.current,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Empty -> {}
            }
        }
    }
}