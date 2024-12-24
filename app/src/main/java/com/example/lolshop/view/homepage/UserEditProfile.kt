package com.example.lolshop.view.homepage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lolshop.viewmodel.homepage.UserViewModel

@Composable
fun UserEditProfileScreen(
    userViewModel: UserViewModel,
    uid: String,
    navController: NavController
){
    val userState = userViewModel.getUserData(uid).collectAsState(initial = null)
    // Define text fields for user information
    var full_name by rememberSaveable { mutableStateOf(userState.value?.full_name ?: "") }
    var phone_number by rememberSaveable { mutableStateOf(userState.value?.phone_number ?: "") }
    var address by rememberSaveable { mutableStateOf(userState.value?.address ?: "") }

    LaunchedEffect(userState.value) {
        // Trigger UI update or action when data changes
        full_name = userState.value?.full_name ?: ""
        phone_number = userState.value?.phone_number ?: ""
        address = userState.value?.address ?: ""
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Edit Profile", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = full_name,
            onValueChange = { full_name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number TextField
        OutlinedTextField(
            value = phone_number,
            onValueChange = { phone_number = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Address TextField
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                // Trigger the updateProfile function
                userViewModel.updateProfile(full_name, phone_number, address)
                navController.navigate("user_profile")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
        ) {
            Text("Save Profile")
        }
    }
}