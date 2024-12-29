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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.lolshop.R
import com.example.lolshop.utils.OTPHelper
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.theme.LoLShopTheme
import com.google.firebase.auth.FirebaseAuth

class OTPVerification : BaseActivity() {
    private lateinit var random : String
    private var oobCode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        random = intent.getStringExtra("random").toString()
        val otpHelper = OTPHelper()

        val intentData = intent.data

        if (intentData != null) {
            if (FirebaseAuth.getInstance().isSignInWithEmailLink(intentData.toString())) {
                oobCode = intentData.getQueryParameter("oobCode")
            }
        }
        setContent {
            LoLShopTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(20.dp),
                    color = Color.White
                ) {
                    OTPVerificationScreen (
                        otpHelper,
                        random,
                        navigateToChangePass = {
                            val intent = Intent(this, ChangePassword::class.java)
                            intent.putExtra("oobCode", oobCode)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun OTPVerificationScreen(
    otpHelper: OTPHelper,
    random: String,
    navigateToChangePass: () -> Unit
) {
    var otpValue by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(16.dp)
                .verticalScroll(scrollState)
                .background(Color.White),
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
                    if(otpHelper.verifyOtp(otpValue, random)){
                        navigateToChangePass()
                    }else{
                        Toast.makeText(
                            context,
                            "Wrong OTP, please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = otpValue.length == 6,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Verify OTP")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

