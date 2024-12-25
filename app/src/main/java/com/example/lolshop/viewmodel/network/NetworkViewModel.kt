package com.example.lolshop.viewmodel.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NetworkViewModel(context: Context) : ViewModel() {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(checkInitialConnection())
    val isConnected: StateFlow<Boolean> = _isConnected

    init {
        monitorNetwork()
    }

    private fun checkInitialConnection(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun monitorNetwork() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateConnectionState(true)
            }

            override fun onLost(network: Network) {
                updateConnectionState(false)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun updateConnectionState(isConnected: Boolean) {
        viewModelScope.launch {
            _isConnected.emit(isConnected)
        }
    }
}
