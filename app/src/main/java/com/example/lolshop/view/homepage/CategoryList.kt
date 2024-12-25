package com.example.lolshop.view.homepage

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.lolshop.model.Category

@Composable
fun CategoryList(
    categories: SnapshotStateList<Category>,
    uid: String,
    isAdmin: Boolean
) {
    var selectedIndex by rememberSaveable { mutableStateOf(-1) }
    val context = LocalContext.current
    // Remember activity result to reset selection
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        selectedIndex = -1  // Reset selection when returning
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy((-15).dp),
        contentPadding = PaddingValues(
            start = 5.dp,
            end = 5.dp,
            top = 3.dp
        )
    ) {
        items(categories.size) { index ->
            CategoryItem(
                item = categories[index],
                isSelected = selectedIndex == index,
                onItemClick = {
                    selectedIndex = index
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(context, ListProductActivity::class.java).apply {
                            putExtra("id", categories[index].id)
                            putExtra("title", categories[index].name)
                            putExtra("uid", uid)
                            putExtra("isAdmin", isAdmin)
                        }
                        activityLauncher.launch(intent)
                    }, 10)
                }
            )
        }
    }
}


@Composable
fun CategoryItem(item: Category, isSelected: Boolean, onItemClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.Transparent else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary

    Column(
        modifier = Modifier
            .clickable(onClick = onItemClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 120.dp else 100.dp) // Outer size of the circular frame
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .padding(5.dp) // Adjusted padding for making the picture smaller
        ) {
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .padding(10.dp), // Reduce picture size further inside the circle
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "No Image",
                    style = MaterialTheme.typography.body2,
                    color = textColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        // Display category name below the image
        Text(
            text = item.name,
            style = MaterialTheme.typography.body1,
            color = textColor,
        )
    }
}