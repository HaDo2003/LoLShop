package com.example.lolshop.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lolshop.R

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
        if(isFirstLaunch){
            setContent {
                IntroScreen(
                    onClick = {
                        // Mark intro as completed
                        sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()

                        // Navigate to MainActivity
                        startActivity(Intent(this, MainActivity:: class.java))
                        finish()
                    }
                )
            }
        } else {
            // Skip directly to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}

@Composable
@Preview
fun IntroScreen(onClick:() -> Unit={}){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.intro),
            color = Color.White,
            fontSize = 45.sp,

            modifier = Modifier
                .padding(
                    top = 300.dp,
                    start = 10.dp,
                    end = 10.dp
                )
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(50.dp))
        Button(
            onClick = onClick ,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .padding(10.dp)

        ) {
            Text(
                text = stringResource(id = R.string.get_started),
                fontSize = 20.sp
            )
        }
    }
}