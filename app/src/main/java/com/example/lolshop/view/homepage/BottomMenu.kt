package com.example.lolshop.view.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lolshop.R

@Composable
fun BottomMenu(
    modifier: Modifier = Modifier,
    isAdmin: Boolean,
    onItemClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick: () -> Unit,
    onHomeClick: () -> Unit,
    onOrderClick: () -> Unit,
    currentScreen: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Gray)
    ) {
        // Divider on top to separate the taskbar from the content above
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        // Taskbar Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // "Explorer" button
            BottomMenuItem(
                icon = if(currentScreen == "homepage") painterResource(R.drawable.home_black)
                       else painterResource(R.drawable.home_white),
                text = "Explorer",
                onItemClick = onHomeClick,
                textColor = Color.Black
            )

            // "Cart" button
            BottomMenuItem(
                icon = if(currentScreen == "cart") painterResource(R.drawable.cart_black)
                       else painterResource(R.drawable.cart_white),
                text = "Cart",
                onItemClick = onItemClick,
                textColor = Color.Black
            )

            // "Order" button
            BottomMenuItem(
                icon = if(currentScreen == "order") painterResource(R.drawable.order_black)
                       else painterResource(R.drawable.order_white),
                text = "Order",
                onItemClick = onOrderClick,
                textColor = Color.Black
            )

            // "Profile" button
            BottomMenuItem(
                icon = if(currentScreen == "profile") painterResource(R.drawable.profile_black)
                       else painterResource(R.drawable.profile_white),
                text = "Profile",
                onItemClick = onProfileClick,
                textColor = Color.Black
            )

            // "Admin" button (only visible if isAdmin is true)
            if (isAdmin) {
                BottomMenuItem(
                    icon = if(currentScreen == "admin") painterResource(R.drawable.adm_black)
                           else painterResource(R.drawable.adm_white),
                    text = "Admin",
                    onItemClick = onAdminClick,
                    textColor = Color.Black
                )
            }
        }
    }
}



@Composable
fun BottomMenuItem(
    icon: Painter,
    text: String,
    onItemClick: (() -> Unit)? = null,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .height(80.dp) // Increase the height of the item
            .clickable {
                onItemClick?.invoke() // Call onItemClick for other item
            }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = text,
            modifier = Modifier.size(33.dp),
        )
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(text, color = textColor, fontSize = 10.sp)
    }
}