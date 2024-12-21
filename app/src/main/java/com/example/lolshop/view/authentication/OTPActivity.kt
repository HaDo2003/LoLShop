package com.example.lolshop.view.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lolshop.R
import com.example.lolshop.utils.ChangeField
import com.example.lolshop.utils.Resource
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.theme.GreyDark
import com.example.lolshop.view.theme.GreyLight
import com.example.lolshop.view.theme.LoLShopTheme
import com.example.lolshop.viewmodel.authentication.SignUpViewModel
import com.example.lolshop.viewmodel.authentication.SignUpViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random
import papaya.`in`.sendmail.SendMail

class OTPActivity : BaseActivity() {
    lateinit var random : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("name").toString()
        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()
        val phoneNumber = intent.getStringExtra("phoneNumber").toString()
        val address = intent.getStringExtra("address").toString()
        random = generateOtp(email)
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
                        viewModel, random, name, email, password, phoneNumber, address,
                        navigateToLogin = {
                            Log.d("LoginScreen", "Navigating to LoginActivity.")
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }


}

fun VerifyOtp(otp: String, random: String): Boolean{
    Log.d("OTP random", otp)
    if(otp != random){
        return false
    }else{
        return true
    }
}

fun generateOtp(email: String): String {
    val randomOtp = (100000..999999).random() // Generates a random OTP with 6 digits
    val subject = "Signup app's OTP"
    val body = "Your OTP is -> $randomOtp"

    val mail = SendMail(
        "hadotaydo20@gmail.com",
        "zjkilymrscoyepyk",
        email,
        subject,
        body
    )

    mail.execute()
    Log.d("OTP random", "$randomOtp")
    return randomOtp.toString()
}

fun resendOTP(email: String) {
    val newOtp = generateOtp(email)
}

@Composable
fun OTPVerificationScreen(
    viewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
    ),
    random: String,
    name: String,
    email: String,
    password: String,
    phoneNumber: String,
    address: String,
    navigateToLogin: () -> Unit
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
                onOtpTextChange = { value, otpInputFilled ->
                    otpValue = value
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (otpValue.length == 6) {
                        success = VerifyOtp(otpValue, random)
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

            TextButton(onClick = { resendOTP(email) }) {
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

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        text = otpText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index == text.length -> "0"
        index > text.length -> ""
        else -> text[index].toString()
    }
    Text(
        modifier = Modifier
            .width(50.dp)
            .height(64.dp)
            .border(
                2.dp, when {
                    isFocused -> GreyDark
                    else -> GreyLight
                }, RoundedCornerShape(8.dp)
            )
            .padding(2.dp),
        text = char,
        style = MaterialTheme.typography.displayLarge,
        color = if (isFocused) {
            GreyLight
        } else {
            GreyDark
        },
        textAlign = TextAlign.Center
    )
}

