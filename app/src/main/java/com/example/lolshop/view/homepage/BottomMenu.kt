package com.example.lolshop.view.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lolshop.R

@Composable
fun BottomMenu(
    modifier: Modifier = Modifier,
    isAdmin: Boolean,
    onItemClick: () -> Unit,
    onProfileClick:() -> Unit,
    onAdminClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(bottom = 16.dp)
            .background(
                Color.Black,
            ),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomMenuItem(icon = painterResource(R.drawable.home), text = "Explorer")
        BottomMenuItem(icon = painterResource(R.drawable.cart), text = "Cart", onItemClick = onItemClick)
        BottomMenuItem(icon = painterResource(R.drawable.order), text = "Order")
        BottomMenuItem(icon = painterResource(R.drawable.profile), text = "Profile", onItemClick = onProfileClick)
        if (isAdmin) {
            BottomMenuItem(icon = painterResource(R.drawable.admin), text = "Admin", onItemClick = onAdminClick)
        }
    }
}

@Composable
fun BottomMenuItem(
    icon: Painter,
    text: String,
    onItemClick: (() -> Unit)? = null
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
            tint = Color.White,
            modifier = Modifier.size(33.dp),
        )
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(text, color = Color.White, fontSize = 10.sp)
    }
}