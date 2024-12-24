package com.example.lolshop.viewmodel.authentication

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val uid: String, val isAdmin: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
}