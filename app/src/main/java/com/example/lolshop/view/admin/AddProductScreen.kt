package com.example.lolshop.view.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.model.Category
import com.example.lolshop.model.Product
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.viewmodel.admin.AdminViewModel
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
    var name by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val categoryOptions = listOf(
        Category("-OE4s6JDMNBmybnPHxzj", "LCK"),
        Category("-OE4tac7kwwSFADLtfoG", "LPL"),
        Category("-OE4tefxh5HJh8UgGyU5", "VCS"),
        Category("-OE4tiYv8OMefiXdZ-el", "LEC"),
        Category("-OE4tmqfq9DWVUO-5Kpm", "PCS"),
        Category("-OE4ts0w9YaDnfT2UM_-", "LCS")
    )
    var categoryId by rememberSaveable { mutableStateOf(categoryOptions[0]) }

    val recommendOption = arrayOf("Unrecommended", "Recommended")
    var isRecommended by rememberSaveable { mutableStateOf(recommendOption[0]) }
    var showRecommended by rememberSaveable { mutableStateOf(false) }
    var expanded1 by rememberSaveable { mutableStateOf(false) }
    var showSnackbar by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                    Button(
                        onClick = { imageResultLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Select Product Image")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                    adminViewModel.addProduct(
                        name = name,
                        categoryId = categoryId.id,
                        price = price,
                        description = description,
                        showRecommended = showRecommended,
                        imageUri = imageUriState,
                        onValidationError = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please fill in all fields")
                            }
                        },
                        onNavigationSuccess = {
                            coroutineScope.launch {
                                Toast.makeText(
                                    context,
                                    "Product added successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("admin_main")
                            }
                        }
                    )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator() // Display loading indicator
                    } else {
                        Text("Add Product")
                    }
                }
                SnackbarHost(
                    hostState = snackbarHostState
                )
            }
        }
    }
}
