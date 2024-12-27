package com.example.lolshop.viewmodel.homepage

import android.util.Log
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
import com.example.lolshop.viewmodel.authentication.LoginState


class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {
    private val _cartState = MutableStateFlow<Resource<Unit>>(Resource.Empty())
    val cartState: StateFlow<Resource<Unit>> get() = _cartState

    private val _cart = MutableLiveData<Result<Cart>>(Result.Empty)
    val cart: LiveData<Result<Cart>> get() = _cart

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _orderState = MutableStateFlow<Resource<Unit>>(Resource.Empty())
    val orderState: StateFlow<Resource<Unit>> get() = _orderState


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
            Log.d("Cart", _cart.value.toString())

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

    fun placeOrderFromCart(
        cart: Cart,
        uid: String,
        totalPriceOfAllProduct: Double,
        tax: Double,
        deliveryFee: Double,
        totalPriceAtAll: Double
    ){
        viewModelScope.launch {
            try {
                cartRepository.placeOrderFromCart(cart, uid, totalPriceOfAllProduct, tax, deliveryFee, totalPriceAtAll)
                // If successful, update the status
                _orderState .value = Resource.Success(Unit)
            } catch (e: Exception) {
                // If an error occurs, update the status
                _orderState.value = Resource.Error("$e")
            }
        }
    }

    fun clearError() {
        _cartState.value = Resource.Empty() // or your default state
    }
}