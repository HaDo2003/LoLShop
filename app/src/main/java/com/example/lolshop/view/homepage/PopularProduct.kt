package com.example.lolshop.view.homepage

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.text.style.TextAlign
import com.example.lolshop.model.Product


import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme

import androidx.compose.ui.text.style.TextOverflow


@Composable
fun PopularProduct(
    product: List<Product>,
    pos: Int,
    uid: String,
    isAdmin: Boolean
) {
    val context = LocalContext.current
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
                .clickable{
                    val intent =Intent(context, DetailActivity::class.java).apply{
                        putExtra("object",product[pos])
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    context.startActivity(intent)
                }
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
fun ListProduct(
    product: SnapshotStateList<Product>,
    uid: String,
    isAdmin: Boolean
){
    LazyRow (modifier = Modifier
        .padding(top=8.dp)
        .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(product.size){
                index: Int->
            PopularProduct(product, index, uid, isAdmin)
        }
    }
}

@Composable
fun ListProductFullSize(
    product: List<Product>,
    uid: String,
    isAdmin: Boolean
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(product.size){row ->
            PopularProduct(product, row, uid, isAdmin)
            Log.d("ListProductFullSize", uid)
        }
    }
}



