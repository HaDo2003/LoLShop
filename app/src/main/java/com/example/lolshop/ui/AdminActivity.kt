package com.example.lolshop.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.PreviewParameter
import coil.compose.rememberImagePainter
import com.example.lolshop.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class AdminActivity : ComponentActivity() {

    private val database = FirebaseDatabase.getInstance().reference.child("products")
    private val storage = FirebaseStorage.getInstance().reference
    private var imageUri: Uri? = null

    // Step 1: Declare ActivityResultLauncher for selecting image
    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri = it }
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
        var isLoading by remember { mutableStateOf(false) }
        var imageUriState by remember { mutableStateOf<Uri?>(null) }

        // Handle the dialog to add a product
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome Admin!", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Products: 22", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(32.dp))


            // Product Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Product Price Input
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Product Price") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* Handle Done */ }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Product Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Product Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Image Preview or Image Picker Button
            if (imageUriState != null) {
                Image(painter = rememberImagePainter(imageUriState), contentDescription = null, modifier = Modifier.size(100.dp))
            } else {
                Button(onClick = { imageResultLauncher.launch("image/*") }) {
                    Text("Select Product Image")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Add Product Button
            Button(
                onClick = {
                    if (name.isEmpty() || price.isEmpty() || description.isEmpty() || imageUriState == null) {
                        Toast.makeText(this@AdminActivity, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        addProduct(name, price, description, imageUriState)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Uploading..." else "Add Product")
            }
        }
    }

    private fun addProduct(name: String, price: String, description: String, imageUri: Uri?) {
        imageUri?.let { uri ->
            val imageRef = storage.child("products/${UUID.randomUUID()}.jpg")
            imageRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    val productId = database.push().key ?: return@addOnSuccessListener
                    val product = mapOf(
                        "id" to productId,
                        "name" to name,
                        "price" to price,
                        "description" to description,
                        "imageUrl" to uri.toString()
                    )
                    database.child(productId).setValue(product).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this@AdminActivity, "Product added successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@AdminActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this@AdminActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
