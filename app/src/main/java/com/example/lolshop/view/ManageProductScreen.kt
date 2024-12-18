package com.example.lolshop.view

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.R
import com.example.lolshop.model.Product
import com.example.lolshop.viewmodel.AdminViewModel

@Composable
fun ManageProductScreen(adminViewModel: AdminViewModel, navController: NavController) {
    val products by adminViewModel.products.collectAsState()
    val navController = rememberNavController()

    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(products) { product ->
            ProductItem(
                product = product,
                onDelete = { adminViewModel.deleteProduct(product.id) },
                onEdit = {
                    if (product.id.isNotEmpty()) {
                        navController.navigate("edit_product/${product.id}")
                    }
                }
            )
        }
    }
}


@Composable
fun ProductItem(product: Product, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = product.price, style = MaterialTheme.typography.bodyMedium)

            if (product.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = product.imageUrl,
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.placeholder_image),
                        placeholder = painterResource(id = R.drawable.placeholder_image
                        )
                    ),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            } else {
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
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

