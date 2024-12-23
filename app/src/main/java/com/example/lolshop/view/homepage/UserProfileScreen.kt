package com.example.lolshop.view.homepage

import android.app.Activity
import android.content.Intent
import android.graphics.Color.parseColor
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.lolshop.R
import com.example.lolshop.utils.Resource
import com.example.lolshop.view.authentication.LoginActivity
import com.example.lolshop.viewmodel.homepage.UserViewModel

@Composable
fun UserProfileScreen(
    userViewModel: UserViewModel,
    uid: String,
    navController: NavController
){
    val userState = userViewModel.getUserData(uid).collectAsState(initial = null)
    val logoutResult by userViewModel.logoutResult.observeAsState(Resource.Empty())
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConstraintLayout() {
            val(topImg,profile) = createRefs()
            Image(
                painterResource(
                    id = R.drawable.anonymous_user
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 50.dp)
                    .size(150.dp)  // Set the size you want
                    .clip(CircleShape)  // This makes the image circular
                    .border(2.dp, Color.Gray, CircleShape)
                    .constrainAs(topImg){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }
        Text(
            text = userState.value?.full_name ?: "Loading...",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp),
            color = Color(parseColor("#646669"))
        )
        Text(
            text = userState.value?.email ?: "Loading...",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp),
            color = Color(parseColor("#646669"))
        )

        //Edit Profile
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
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp
            )
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

        //Change Password
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFFFFF)
            ),
            shape = RoundedCornerShape(15.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp
            )
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

        //Log Out
        Button(
            onClick = {
                userViewModel.logout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFFFFF)
            ),
            shape = RoundedCornerShape(15.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp
            )
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
        LaunchedEffect(logoutResult) {
            when (logoutResult) {
                is Resource.Loading -> {
                    // CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is Resource.Success -> {
                    // Show success message
                    Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()

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
                    // Show error message
                    Toast.makeText(context, "Log out Failed", Toast.LENGTH_SHORT).show()
                }
                is Resource.Empty -> {}
                null -> TODO()
            }
        }
    }
}