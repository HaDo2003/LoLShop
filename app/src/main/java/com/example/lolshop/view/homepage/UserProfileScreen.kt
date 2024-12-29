package com.example.lolshop.view.homepage

import android.app.Activity
import android.content.Intent
import android.graphics.Color.parseColor
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.R
import com.example.lolshop.utils.Resource
import com.example.lolshop.view.authentication.LoginActivity
import com.example.lolshop.viewmodel.homepage.UserViewModel

@Composable
fun UserProfileScreen(
    userViewModel: UserViewModel,
    uid: String,
    isAdmin: Boolean,
    navController: NavController,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick:() -> Unit,
    onHomeClick:() -> Unit,
    onOrderClick:() -> Unit
) {
    val currentScreen = "profile"
    val userState = userViewModel.getUserData(uid).collectAsState(initial = null)
    val logoutResult by userViewModel.logoutResult.observeAsState(Resource.Empty())
    val changeProfilePictureState = userViewModel.profileImageUpdateState.collectAsState().value
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            userViewModel.changeProfilePicture(uid, uri)
        }
    }
    Log.d("uid", uid)
    Log.d("userState", userState.toString())

    Scaffold(
        bottomBar = {
            BottomMenu(
                isAdmin = isAdmin,
                modifier = Modifier
                    .fillMaxWidth(),
                onItemClick = onCartClick,
                onProfileClick = onProfileClick,
                onAdminClick = onAdminClick,
                onHomeClick = onHomeClick,
                onOrderClick = onOrderClick,
                currentScreen = currentScreen
            )
        }
    ) { paddingValue ->
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp, // Override any top padding caused by Scaffold
                    bottom = paddingValue.calculateBottomPadding(),
                )
                .background(Color.White)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp).size(40.dp))
            } else {
                // Profile image
                ConstraintLayout() {
                    val (topImg, cameraIcon) = createRefs()
                    Image(
                        painter = if (userState.value?.profilePicture == "") {
                            painterResource(id = R.drawable.anonymous_user)
                        } else if(userState.value?.profilePicture != null){
                            rememberAsyncImagePainter(userState.value?.profilePicture)
                        } else {
                            painterResource(id = R.drawable.anonymous_user)
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 50.dp)
                            .size(150.dp)  // Set the size you want
                            .clip(CircleShape)  // This makes the image circular
                            .border(2.dp, Color.Gray, CircleShape)
                            .constrainAs(topImg) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
                    // Camera Icon Overlapping the Profile Image
                    Icon(
                        painter = painterResource(id = R.drawable.camera), // Replace with your camera icon resource
                        contentDescription = "Change Profile Picture",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp) // Adjust the icon size
                            .background(Color.Black, CircleShape) // Optional background for contrast
                            .padding(4.dp)
                            .clickable {
                                // Trigger an action to open the camera or gallery
                                launcher.launch("image/*")
                            }
                            .constrainAs(cameraIcon) {
                                bottom.linkTo(topImg.bottom, margin = 8.dp)
                                end.linkTo(topImg.end, margin = 8.dp) // Position the icon on the bottom-right corner of the image
                            }
                    )
                }

                // User Full Name
                Text(
                    text = userState.value?.full_name ?: "Loading...",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color(parseColor("#646669"))
                )

                // User Email
                Text(
                    text = userState.value?.email ?: "Loading...",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color(parseColor("#646669"))
                )

                // Edit Profile Button
                Button(
                    onClick = {
                        navController.navigate("edit_profile")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFFFFF)
                    ),
                    shape = RoundedCornerShape(15.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.editprofile),
                            contentDescription = "Edit Profile",
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(
                            text = "Edit Profile",
                            color = Color.Black,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                // Change Password Button
                Button(
                    onClick = {
                        navController.navigate("change_password")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFFFFF)
                    ),
                    shape = RoundedCornerShape(15.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.changepassword),
                            contentDescription = "Change Password",
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(
                            text = "Change Password",
                            color = Color.Black,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                // Log Out Button
                Button(
                    onClick = {
                        userViewModel.logout()
                        isLoading = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFFFFF)
                    ),
                    shape = RoundedCornerShape(15.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Log Out",
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(
                            text = "Log Out",
                            color = Color.Black,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
    //Handle change profile picture
    when (changeProfilePictureState) {
        is Resource.Loading -> {
        }
        is Resource.Success -> {
            Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        }
        is Resource.Error -> {
            Toast.makeText(context, "Log out Failed", Toast.LENGTH_SHORT).show()
        }
        else -> {
        }
    }

    // Handle the logout result
    LaunchedEffect(logoutResult) {
        when (logoutResult) {
            is Resource.Loading -> {
                isLoading = true
            }
            is Resource.Success -> {
                Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                context.startActivity(intent)
                (context as? Activity)?.finish()
            }
            is Resource.Error -> {
                Toast.makeText(context, "Log out Failed", Toast.LENGTH_SHORT).show()
            }
            is Resource.Empty -> {
                isLoading = false
            }
            null -> {
            }
        }
    }
}
