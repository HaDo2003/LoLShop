package com.example.lolshop.viewmodel.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.repository.UserRepository
import com.example.lolshop.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _signUpState = MutableStateFlow<Resource<Unit>>(Resource.Empty())
    val signUpState: StateFlow<Resource<Unit>> get() = _signUpState

    fun checkEmpty(name: String, email: String, password: String, phoneNumber: String, address: String): Boolean {
        return when {
            name.isBlank() -> {
                _signUpState.value = Resource.Error("Name is required")
                false
            }
            email.isBlank() -> {
                _signUpState.value = Resource.Error("Email is required")
                false
            }
            password.isBlank() -> {
                _signUpState.value = Resource.Error("Password is required")
                false
            }
            phoneNumber.isBlank() -> {
                _signUpState.value = Resource.Error("Phone number is required")
                false
            }
            address.isBlank() -> {
                _signUpState.value = Resource.Error("Address is required")
                false
            }
            else -> true
        }
    }

    fun signUp(name: String, email: String, password: String, phoneNumber: String, address: String) {
        _signUpState.value = Resource.Loading()

        viewModelScope.launch {
            val result = userRepository.signUpUser(name, email, password, phoneNumber, address)
            if (result.isSuccess) {
                _signUpState.value = Resource.Success(Unit)
            } else {
                _signUpState.value = Resource.Error(result.exceptionOrNull()?.message ?: "Sign up failed")
            }
        }
    }
    fun clearError() {
        _signUpState.value = Resource.Empty() // or your default state
    }
}
