package com.example.lolshop.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lolshop.R
import com.example.lolshop.model.Category
import com.example.lolshop.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryActivity : ComponentActivity() {
    private lateinit var categoryRepository: CategoryRepository
    private var imageUriState by mutableStateOf<Uri?>(null)
    private var categoriesList by mutableStateOf<List<Category>>(emptyList())
    private var totalCategories by mutableStateOf(0)

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUriState = it
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryRepository = CategoryRepository(this) // Initialize repository with context

        setContent {
            CategoryScreen()
        }
    }

    @Composable
    fun CategoryScreen() {
        var categoryName by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        // Fetch categories and total count from Firebase
        LaunchedEffect(Unit) {
            fetchCategories()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Manage Categories", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            // Card for Add Category form
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Input field for Category name
                    BasicTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            // Perform action on done, if needed
                        }),
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
                            Text("Select Category Image")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Add category button
                    Button(
                        onClick = {
                            if (categoryName.isEmpty() || imageUriState == null) {
                                Toast.makeText(this@CategoryActivity, "Please enter a category name and select an image", Toast.LENGTH_SHORT).show()
                            } else {
                                isLoading = true
                                lifecycleScope.launch {
                                    categoryRepository.addCategory(categoryName, imageUriState)
                                    isLoading = false
                                    categoryName = ""  // Clear field after successful addition
                                    imageUriState = null // Reset image
                                    // Fetch the updated category list
                                    fetchCategories()
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(if (isLoading) "Adding..." else "Add Category")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Display total categories and category list
            Text(text = "Total Categories: $totalCategories", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Display list of categories
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(categoriesList) { category ->
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

    private fun fetchCategories() {
        lifecycleScope.launch {
            val category = categoryRepository.fetchCategories()
            categoriesList = category
            totalCategories = category.size
        }
    }
}