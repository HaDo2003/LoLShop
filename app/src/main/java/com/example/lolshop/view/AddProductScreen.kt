package com.example.lolshop.view

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.model.Category
import com.example.lolshop.model.Product
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.viewmodel.AdminViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    adminViewModel: AdminViewModel,
    navController: NavController,
    productRepository: ProductRepository,
    imageUriState: Uri?,
    productsList: List<Product>,
    totalProducts: Int,
    imageResultLauncher: ActivityResultLauncher<String>,
    fetchProducts: () -> Unit
) {
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
    var categoryId by remember { mutableStateOf(categoryOptions[0]) }

    val recommendOption = arrayOf("Unrecommended", "Recommended")
    var isRecommended by remember { mutableStateOf(recommendOption[0]) }
    var showRecommended by remember { mutableStateOf(false) }
    var expanded1 by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }

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
        Text(text = "Add Products!", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        if (showSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text("Please fill all fields and select an image")
            }
        }

        // Card for Add Product form
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categoryId.name,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categoryOptions.forEach { categoryItem ->
                            DropdownMenuItem(
                                text = { Text(text = categoryItem.name) },
                                onClick = {
                                    categoryId = categoryItem
                                    expanded = false
                                }
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

                ExposedDropdownMenuBox(
                    expanded = expanded1,
                    onExpandedChange = { expanded1 = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = isRecommended,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded1)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded1,
                        onDismissRequest = { expanded1 = false }
                    ) {
                        recommendOption.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    isRecommended = option
                                    showRecommended = option == "Recommended"
                                    expanded1 = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (imageUriState != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUriState),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    Button(onClick = { imageResultLauncher.launch("image/*") }) {
                        Text("Select Product Image")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || imageUriState == null) {
                            showSnackbar = true
                        } else {
                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                productRepository.addProduct(name, price, description, categoryId.id, showRecommended, imageUriState)
                                isLoading = false
                                navController.navigate("AdminScreen") // Navigate to AdminScreen
                            }
                        }
                    },
                    enabled = !isLoading,
                ) {
                    Text(if (isLoading) "Uploading..." else "Add Product")
                }
            }
        }
    }
}
