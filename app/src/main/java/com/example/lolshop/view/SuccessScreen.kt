package com.example.lolshop.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lolshop.R

@Composable
fun SuccessScreen(
    message: String
){
    // Content
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.success),
            contentDescription = "Success",
            modifier = Modifier
                .size(60.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Error Message
        Text(
            text = message,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }

}

@Preview()
@Composable
fun PreviewSuccess(){
    SuccessScreen(
        message = "Added to Cart"
    )
}