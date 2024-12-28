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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    val customerName: StateFlow<String?> get() = _customerName

    private val _filteredOrders = MutableLiveData<List<Order>>()
    val filteredOrders: LiveData<List<Order>> get() = _filteredOrders

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

    fun getCustomerName(uid: String): Flow<String?> = flow {
        try {
            // Fetch customer name from the repository and emit it
            val name = orderRepository.getCustomerName(uid)
            emit(name)
        } catch (e: Exception) {
            // Emit a default value in case of an error
            emit("Unknown Customer")
        }
    }.flowOn(Dispatchers.IO)


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

    fun filterOrdersByStatus(status: String) {
        viewModelScope.launch {
            try {
                val result = orderRepository.filterStatus(status) // Fetch filtered orders from the repository
                when (result) {
                    is Result.Success -> {
                        _filteredOrders.value = result.data
                    }
                    is Result.Empty -> {
                        _filteredOrders.value = emptyList() // Set empty list if no orders match
                    }
                    is Result.Error -> {
                        _error.value = result.exception.message // Handle the error case
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message // Handle unexpected errors
            }
        }
    }
}