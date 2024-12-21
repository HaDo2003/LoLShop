package com.example.lolshop.view.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lolshop.R
import androidx.compose.ui.text.style.TextAlign
import com.example.lolshop.model.Product


import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun PopularProduct(product: List<Product>, pos: Int) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image layer
        Box(
            modifier = Modifier
                .size(175.dp) // Standard size for all images
                .clip(RoundedCornerShape(10.dp)) // Ensure the image is clipped
        ) {
            if (product[pos].imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = product[pos].imageUrl,
                    contentDescription = product[pos].name,
                    modifier = Modifier.fillMaxSize(), // Full size within the clipped area
                    contentScale = ContentScale.Crop // Ensures the image is cropped to fit
                )
            } else {
                // Fallback text when no image is available
                Text(
                    text = "No Image",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Display product name below the image
        Text(
            text = product[pos].name,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier.padding(top = 8.dp), // Add spacing between image and text
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // Ensures long names are truncated
        )

        // Display product price below the name
        Text(
            text = "$${product[pos].price}",
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}




@Composable
fun ListProduct(product: SnapshotStateList<Product>){
    LazyRow (modifier = Modifier
        .padding(top=8.dp)
        .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(product.size){
                index: Int->
            PopularProduct(product, index)
        }
    }
}

@Composable
fun ListProductFullSize(product: List<Product>){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical=16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(product.size){row ->
            PopularProduct(product, row)
        }
    }
}


