package com.example.lolshop.view.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lolshop.R
import com.example.lolshop.model.Category
import com.example.lolshop.repository.CategoryRepository
import com.example.lolshop.viewmodel.admin.AdminViewModel
import com.example.lolshop.viewmodel.admin.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddCategoryScreen(
    categoryViewModel: CategoryViewModel,
    categoryRepository: CategoryRepository,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    var categoryName by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSnackbar by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val imageUriState = rememberSaveable { mutableStateOf<Uri?>(null) }
    val categoriesList = remember { mutableStateOf<List<Category>>(emptyList()) }
    val totalCategories = categoriesList.value.size
    val imageResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUriState.value = uri
    }
    val fetchCategory: () -> Unit = {
        CoroutineScope(Dispatchers.IO).launch {
            val category = categoryRepository.fetchCategory()
            categoriesList.value = category
        }
    }
    val context = LocalContext.current

    // Fetch categories and total count from Firebase
    LaunchedEffect(Unit) {
        fetchCategory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Manage Categories", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        if (showSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text("Please select an image")
            }
        }
        // Card for Add Category form
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Input field for Category name
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text(text = "Region") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        // Perform action on done, if needed
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Image selection
                if (imageUriState.value != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUriState.value),
                        contentDescription = "Selected Image",
                        modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
                    )
                } else {
                    Button(
                        onClick = { imageResultLauncher.launch("image/*") },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Select Category Image")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Add category button
                Button(
                    onClick = {
                        categoryViewModel.addCategory(
                            name = categoryName,
                            imageUri = imageUriState.value,
                            onValidationError = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please select image")
                                }
                            },
                            onNavigationSuccess = {
                                coroutineScope.launch {
                                    Toast.makeText(
                                        context,
                                        "Region added successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("admin_main")
                                }
                            }
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isLoading) "Adding..." else "Add Category")
                }
            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Display total categories and category list
        Text(text = "Total Regions: $totalCategories", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Display list of categories
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(categoriesList.value) { category ->
                CategoryItem(category)
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display category name
        Text(text = category.name, style = MaterialTheme.typography.bodyLarge)

        // Display category image from Cloudinary
        if (category.imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(category.imageUrl)  // Set the image URL
                        .size(200)  // Set the image size
                        .placeholder(R.drawable.placeholder)  // Set a placeholder image
                        .error(R.drawable.error_image)  // Set an error image
                        .build()
                ),
                contentDescription = "Category Image",
                modifier = Modifier
                    .size(100.dp)  // Set the size for image display
                    .padding(top = 8.dp)
            )
        } else {
            // Placeholder text if imageUrl is empty
            Text("No image available", style = MaterialTheme.typography.bodySmall)
        }
    }
}