package com.example.lolshop.viewmodel.homepage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.model.Order
import com.example.lolshop.repository.OrderRepository
import com.example.lolshop.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _userOrders = MutableLiveData<List<Order>>()
    val userOrders: LiveData<List<Order>> get() = _userOrders

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _adminOrders = MutableLiveData<List<Order>>()
    val adminOrders: LiveData<List<Order>> get() = _adminOrders

    private val _orderStatus = MutableLiveData<String?>(null)
    val orderStatus: LiveData<String?> get() = _orderStatus

    private val _customerName = MutableStateFlow<String?>(null)
    val customerName: StateFlow<String?> = _customerName.asStateFlow()

    fun fetchOrders(uid: String) {
        viewModelScope.launch {
            when (val result = orderRepository.fetchUserOrders(uid)) {
                is Result.Success -> {
                    _userOrders.value = result.data // Update LiveData with fetched orders
                }
                is Result.Error -> {
                    _error.value = "Error fetching orders: ${result.exception.message}" // Update error message
                }
                is Result.Empty -> {
                    _userOrders.value = emptyList() // Handle empty orders case
                }
            }

            Log.d("Orders", _userOrders.value.toString())
        }
    }

    fun fetchOrdersAdmin() {
        viewModelScope.launch {
            when (val result = orderRepository.fetchAllOrders()) {
                is Result.Success -> {
                    _adminOrders.value = result.data // Update LiveData with fetched orders
                }
                is Result.Error -> {
                    _error.value = "Error fetching orders: ${result.exception.message}" // Update error message
                }
                is Result.Empty -> {
                    _adminOrders.value = emptyList() // Handle empty orders case
                }
            }

            Log.d("Orders", _adminOrders.value.toString())
        }
    }

    fun getCustomerName(uid: String) {
        viewModelScope.launch {
            try {
                _customerName.value = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(uid)
                    .get()
                    .await()
                    .getString("full_name") ?: "Unknown Customer"
            } catch (e: Exception) {
                _customerName.value = "Unknown Customer"
            }
        }
    }


    fun updateOrderStatus(orderId: String, action: String) {
        viewModelScope.launch {
            try {
                when (action) {
                    "confirm" -> {
                        orderRepository.confirm(orderId)
                        fetchOrdersAdmin()
                    }
                    "complete" -> {
                        orderRepository.complete(orderId)
                        fetchOrdersAdmin()
                    }
                    "cancel" -> {
                        orderRepository.cancel(orderId)
                        fetchOrdersAdmin()
                    }
                }
                // Update local state after successful update
                _orderStatus.value = "${action.capitalize()}ed"
            } catch (e: Exception) {
                // Handle error
                _error.value = "Error updating order: ${e.message}"
                _orderStatus.value = "Error: ${e.message}"
            }
        }
    }
}