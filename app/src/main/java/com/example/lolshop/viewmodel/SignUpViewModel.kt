package com.example.lolshop.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.model.UserRepository
import com.example.lolshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _signUpState = MutableStateFlow<Resource<Unit>>(Resource.Empty())
    val signUpState: StateFlow<Resource<Unit>> get() = _signUpState

    fun signUp(name: String, email: String, password: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _signUpState.value = Resource.Error("Please fill in all fields")
            return
        }

        _signUpState.value = Resource.Loading()

        viewModelScope.launch {
            val result = userRepository.signUpUser(name, email, password)
            if (result.isSuccess) {
                _signUpState.value = Resource.Success(Unit)
            } else {
                _signUpState.value = Resource.Error(result.exceptionOrNull()?.message ?: "Sign up failed")
            }
        }
    }
}
