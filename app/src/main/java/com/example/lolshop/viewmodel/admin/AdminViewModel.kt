package com.example.lolshop.viewmodel.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.model.Product
import com.example.lolshop.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val context: Context) : ViewModel() {
    private val productRepository = ProductRepository(context)

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _products.value = productRepository.fetchProducts()
        }
    }

    fun getProductById(productId: String): Product? {
        return products.value.find { it.id == productId }
    }

    fun addProduct(
        name: String,
        categoryId: String,
        price: String,
        description: String,
        showRecommended: Boolean,
        imageUri: Uri?,
        onValidationError: () -> Unit,
        onNavigationSuccess: () -> Unit
    ) {
        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || imageUri == null) {
            onValidationError()
        }
        viewModelScope.launch {
            productRepository.addProduct(name, categoryId, price, description, showRecommended, imageUri)
            fetchProducts()
            onNavigationSuccess()
        }
    }

    fun updateProduct(
        productId: String,
        name: String,
        categoryId: String,
        price: String,
        description: String,
        showRecommended: Boolean,
        imageUrl: String
    ) {
        viewModelScope.launch {
            productRepository.updateProduct(
                productId,
                name,
                categoryId,
                price,
                description,
                showRecommended,
                imageUrl
            )
            fetchProducts() // refresh the list after updating
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
            fetchProducts()
        }
    }
}
