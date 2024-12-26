package com.example.lolshop.viewmodel.homepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.model.Cart
import com.example.lolshop.model.Product
import com.example.lolshop.repository.CartRepository
import com.example.lolshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.lolshop.utils.Result


class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {
    private val _cartState = MutableStateFlow<Resource<Unit>>(Resource.Empty())
    val cartState: StateFlow<Resource<Unit>> get() = _cartState

    private val _cart = MutableLiveData<Result<Cart>>(Result.Empty)
    val cart: LiveData<Result<Cart>> get() = _cart

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    fun addProductToCart(uid: String, productId: String) {
        viewModelScope.launch {
            try {
                cartRepository.addProductToCart(uid, productId)
                // If successful, update the status
                _cartState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                // If an error occurs, update the status
                _cartState.value = Resource.Error("$e")
            }
        }
    }

    fun fetchCart(uid: String) {
        viewModelScope.launch {
            when (val result = cartRepository.getCart(uid)) {
                is Result.Success -> _cart.value = result
                is Result.Error -> _error.value = "Error fetching cart: ${result.exception.message}"
                is Result.Empty -> _cart.value = Result.Empty
            }
        }
    }

    fun updateProductQuantity(uid: String, productId: String, newQuantity: Int) {
        viewModelScope.launch {
            when (val result = cartRepository.updateProductQuantity(uid, productId, newQuantity)) {
                is Result.Success -> fetchCart(uid)
                is Result.Error -> _error.value = "Error updating quantity: ${result.exception.message}"
                is Result.Empty -> _error.value = "No product found to update"
            }
        }
    }
}