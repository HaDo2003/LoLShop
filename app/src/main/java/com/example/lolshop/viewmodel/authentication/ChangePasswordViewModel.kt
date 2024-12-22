package com.example.lolshop.viewmodel.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _resetPasswordStatus = MutableStateFlow<String?>(null)
    val resetPasswordStatus: StateFlow<String?> = _resetPasswordStatus

    fun resetPassword(oobCode: String, newPassword: String) {
        viewModelScope.launch {
            val result = userRepository.resetPasswordWithCode(oobCode, newPassword)
            _resetPasswordStatus.value = result.getOrElse { it.message }
        }
    }

    fun setErrorMessage(message: String) {
        _resetPasswordStatus.value = message
    }

    fun clearMessage() {
        _resetPasswordStatus.value = null
    }
}