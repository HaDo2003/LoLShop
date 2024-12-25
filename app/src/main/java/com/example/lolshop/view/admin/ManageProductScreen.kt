package com.example.lolshop.view.admin

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.R
import com.example.lolshop.model.Category
import com.example.lolshop.model.Product
import com.example.lolshop.viewmodel.admin.AdminViewModel

@Composable
fun ManageProductScreen(adminViewModel: AdminViewModel, navController: NavController) {
    val products by adminViewModel.products.collectAsState()


    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
    ) {
        items(products) { product ->
            ProductItem(
                product = product,
                onDelete = { adminViewModel.deleteProduct(product.id) },
                onEdit = {
                    if (product.id.isNotEmpty()) {
                        navController.navigate("edit_product/${product.id}")
                    }else {
                        Log.e("ManageProductScreen", "Product ID is empty; cannot navigate.")
                    }
                }
            )
        }
    }
}

@Composable
fun ProductItem(product: Product, onDelete: () -> Unit, onEdit: () -> Unit) {
    val categoryOptions = listOf(
        Category("-OE4s6JDMNBmybnPHxzj", "LCK"),
        Category("-OE4tac7kwwSFADLtfoG", "LPL"),
        Category("-OE4tefxh5HJh8UgGyU5", "VCS"),
        Category("-OE4tiYv8OMefiXdZ-el", "LEC"),
        Category("-OE4tmqfq9DWVUO-5Kpm", "PCS"),
        Category("-OE4ts0w9YaDnfT2UM_-", "LCS")
    )
    val selectedCategory = categoryOptions.find { it.id == product.categoryId }

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Row {
                    Text("Region: ", style = MaterialTheme.typography.bodyMedium)
                    selectedCategory?.let { Text(text = it.name, style = MaterialTheme.typography.bodyMedium) }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Row {
                    Text("Price: ", style = MaterialTheme.typography.bodyMedium)
                    Text(text = product.price, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (product.imageUrl.isNotEmpty()) {
                Log.d("ImageDebug", "Loading image from URL: ${product.imageUrl}")
                Image(
                    painter = rememberAsyncImagePainter(
                        model = product.imageUrl,
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.placeholder_image),
                        placeholder = painterResource(id = R.drawable.placeholder_image),
                        onSuccess = { _ ->
                            Log.d("ImageDebug", "Image loaded successfully")
                        },
                        onError = { error ->
                            Log.e("ImageDebug", "Failed to load imageError. Error: ${error.result.throwable?.message}")
                        }
                    ),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            } else {
                Log.d("ImageDebug", "No image URL available")
                Text("No image available", style = MaterialTheme.typography.bodySmall)
            }

            Text(
                text = product.description.take(100) + if (product.description.length > 100) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Text("Edit")
                }
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
