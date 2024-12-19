package com.example.lolshop.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserRoleViewModel : ViewModel() {
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> get() = _isAdmin

    fun setAdminRole(isAdmin: Boolean) {
        _isAdmin.value = isAdmin
    }
}
