package com.example.lolshop.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.model.Product
import com.example.lolshop.utils.CloudinaryConfig
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class AdminActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().reference.child("products")
    private var imageUriState by mutableStateOf<Uri?>(null)

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUriState = it
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminScreen()
        }
    }

    @Composable
    fun AdminScreen() {
        var name by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Welcome Admin!", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            // Input fields
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            Spacer(modifier = Modifier.height(16.dp))

            // Image selection
            if (imageUriState != null) {
                Image(painter = rememberAsyncImagePainter(imageUriState), contentDescription = "Selected Image", modifier = Modifier.size(100.dp))
            } else {
                Button(onClick = { imageResultLauncher.launch("image/*") }) {
                    Text("Select Product Image")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Add product button
            Button(
                onClick = {
                    if (name.isEmpty() || price.isEmpty() || description.isEmpty() || category.isEmpty() || imageUriState == null) {
                        Toast.makeText(this@AdminActivity, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        lifecycleScope.launch {
                            addProduct(name, price, description, category, imageUriState)
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Uploading..." else "Add Product")
            }
        }
    }

    private suspend fun addProduct(name: String, price: String, description: String, category: String, imageUri: Uri?) {
        imageUri?.let { uri ->
            val file = uriToFile(uri) ?: run {
                Toast.makeText(this@AdminActivity, "Invalid image file", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                // Upload image to Cloudinary
                val downloadUrl = uploadImageToCloudinary(file)

                // Save product to Firebase
                val productId = database.push().key ?: return
                val product = Product(productId, name, price, description, category, downloadUrl)

                database.child(productId).setValue(product).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@AdminActivity, "Product added successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AdminActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun uploadImageToCloudinary(file: File): String {
        return withContext(Dispatchers.IO) {
            val requestParams = mapOf(
                "public_id" to UUID.randomUUID().toString(),
                "overwrite" to true
            )
            val result = CloudinaryConfig.cloudinary.uploader().upload(file, requestParams)
            result["url"]?.toString() ?: throw Exception("Image upload failed")
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
            inputStream?.copyTo(tempFile.outputStream())
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
