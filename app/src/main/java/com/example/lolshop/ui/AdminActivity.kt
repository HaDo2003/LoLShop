package com.example.lolshop.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lolshop.R
import com.example.lolshop.model.Category
import com.example.lolshop.model.Product
import com.example.lolshop.repository.ProductRepository
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

class AdminActivity : ComponentActivity() {

    private lateinit var productRepository: ProductRepository
    private var imageUriState by mutableStateOf<Uri?>(null)
    private var productsList by mutableStateOf<List<Product>>(emptyList())
    private var totalProducts by mutableIntStateOf(0)

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUriState = it
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productRepository = ProductRepository(this) // Initialize repository with context

        setContent {
            AdminScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AdminScreen() {

        var name by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        val categoryOptions = listOf(
            Category("-OE4s6JDMNBmybnPHxzj", "LCK"),
            Category("-OE4tac7kwwSFADLtfoG", "LPL"),
            Category("-OE4tefxh5HJh8UgGyU5", "VCS"),
            Category("-OE4tiYv8OMefiXdZ-el", "LEC"),
            Category("-OE4tmqfq9DWVUO-5Kpm", "PCS"),
            Category("-OE4ts0w9YaDnfT2UM_-", "LCS")
        )
        var categoryId by remember { mutableStateOf(categoryOptions[0])}
        // Fetch products and total count from Firebase
        LaunchedEffect(Unit) {
            fetchProducts()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Welcome Admin!", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            // Card for Add Product form
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Input fields
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
//                    OutlinedTextField(
//                        value = categoryId,
//                        onValueChange = { categoryId = it },
//                        label = { Text("Category") },
//                        modifier = Modifier.fillMaxWidth()
//                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // OutlinedTextField for category selection
                        OutlinedTextField(
                            value = categoryId.name, // Display the category name
                            onValueChange = { },
                            readOnly = true, // Make it read-only since it's used as a dropdown
                            trailingIcon = {
                                TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .clickable { expanded = !expanded } // Toggle dropdown
                        )

                        // Dropdown menu with category options
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categoryOptions.forEach { categoryItem ->
                                DropdownMenuItem(
                                    text = { Text(text = categoryItem.name) },
                                    onClick = {
                                        categoryId = categoryItem
                                        expanded = false // Close dropdown after selection
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Image selection
                    if (imageUriState != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUriState),
                            contentDescription = "Selected Image",
                            modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Button(onClick = { imageResultLauncher.launch("image/*") }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text("Select Product Image")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Add product button
                    Button(
                        onClick = {
                            if (name.isEmpty() || price.isEmpty() || description.isEmpty() || imageUriState == null) {
                                Toast.makeText(this@AdminActivity, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                            } else {
                                isLoading = true
                                lifecycleScope.launch {
                                    productRepository.addProduct(name, price, description, categoryId.id, imageUriState)
                                    isLoading = false
                                    // Clear fields after successful addition
                                    name = ""
                                    price = ""
                                    categoryId = categoryOptions[0]
                                    description = ""
                                    imageUriState = null
                                    // Fetch the updated product list
                                    fetchProducts()
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(if (isLoading) "Uploading..." else "Add Product")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Display total products and product list
            Text(text = "Total Products: $totalProducts", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Display list of products in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().weight(1f), // Ensure it takes up remaining space
                contentPadding = PaddingValues(8.dp)
            ) {
                items(productsList) { product ->
                    ProductItem(product)
                }
            }
        }
    }

    @Composable
    fun ProductItem(product: Product) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display product name
            Text(text = product.name, style = MaterialTheme.typography.bodyLarge)

            // Display product price
            Text(text = product.price, style = MaterialTheme.typography.bodyMedium)

            // Display product image from Cloudinary
            if (product.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)  // Set the image URL
                            .size(200)  // Set the image size
                            .placeholder(R.drawable.placeholder)  // Set a placeholder image
                            .error(R.drawable.error_image)  // Set an error image
                            .build()
                    ),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(100.dp)  // Set the size for image display
                        .padding(top = 8.dp)
                )
            } else {
                // Placeholder text if imageUrl is empty
                Text("No image available", style = MaterialTheme.typography.bodySmall)
            }

            // Display product description with a character limit
            Text(
                text = product.description.take(100) + if (product.description.length > 100) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Fetch products and update UI
    private fun fetchProducts() {
        lifecycleScope.launch {
            val products = productRepository.fetchProducts()
            productsList = products
            totalProducts = products.size
        }
    }
}
