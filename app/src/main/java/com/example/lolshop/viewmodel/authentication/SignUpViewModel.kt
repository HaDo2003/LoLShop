package com.example.lolshop.viewmodel.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.repository.UserRepository
import com.example.lolshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _signUpState = MutableStateFlow<Resource<Unit>>(Resource.Empty())
    val signUpState: StateFlow<Resource<Unit>> get() = _signUpState

    fun signUp(name: String, email: String, password: String, phoneNumber: String, address: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
            _signUpState.value = Resource.Error("Please fill in all fields")
            return
        }

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
}
