package com.example.lolshop.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.model.Product
import com.example.lolshop.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val context: android.content.Context) : ViewModel() {
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
        price: String,
        description: String,
        categoryId: String,
        isRecommended: Boolean,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            productRepository.addProduct(name, price, description, categoryId, isRecommended, imageUri)
            fetchProducts()
        }
    }

    fun updateProduct(productId: String, name: String, price: String, description: String) {
        viewModelScope.launch {
            productRepository.updateProduct(productId, name, price, description)
            fetchProducts()
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
            fetchProducts()
        }
    }
}



