package com.example.lolshop.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.lolshop.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AdminActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private val database = FirebaseDatabase.getInstance().reference.child("products")
    private val storage = FirebaseStorage.getInstance().reference
    private var imageUri: Uri? = null

    // Step 1: Declare ActivityResultLauncher
    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the result from the image picker
            uri?.let {
                imageUri = it
                findViewById<ImageView>(R.id.productImageView).setImageURI(imageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        val addProductButton = findViewById<Button>(R.id.addProductButton)

        addProductButton.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun showAddProductDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView).setTitle("Add Product")

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val productImageView = dialogView.findViewById<ImageView>(R.id.productImageView)
        val selectImageButton = dialogView.findViewById<Button>(R.id.selectImageButton)
        val nameEditText = dialogView.findViewById<EditText>(R.id.productNameEditText)
        val priceEditText = dialogView.findViewById<EditText>(R.id.productPriceEditText)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.productDescriptionEditText)
        val addButton = dialogView.findViewById<Button>(R.id.addProductButton)

        selectImageButton.setOnClickListener {
            // Step 2: Use ActivityResultLauncher to select image
            imageResultLauncher.launch("image/*")
        }

        addButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val price = priceEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (name.isEmpty() || price.isEmpty() || description.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            } else {
                // Upload image to Firebase Storage
                val imageRef = storage.child("products/${UUID.randomUUID()}.jpg")
                imageRef.putFile(imageUri!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
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
                                        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                                        alertDialog.dismiss()
                                    } else {
                                        Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
