package com.example.lolshop.viewmodel.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Please fill in all fields")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val loginResult = userRepository.loginUser(email, password)
            loginResult.fold(
                onSuccess = { uid ->
                    checkUserAccessLevel(uid)
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error.message ?: "Login failed")
                }
            )
        }
    }

    private fun checkUserAccessLevel(uid: String) {
        viewModelScope.launch {
            val accessLevelResult = userRepository.getUserAccessLevel(uid)
            accessLevelResult.fold(
                onSuccess = { isAdmin ->
                    _loginState.value = when (isAdmin) {
                        true -> LoginState.Success(isAdmin = true)
                        false -> LoginState.Success(isAdmin = false)
                        else -> LoginState.Error("Access level not defined")
                    }
                },
                onFailure = { error ->
                    _loginState.value =
                        LoginState.Error(error.message ?: "Failed to fetch access level")
                }
            )
        }
    }
}