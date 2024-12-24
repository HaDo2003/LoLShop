package com.example.lolshop.viewmodel.homepage

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.model.User
import com.example.lolshop.repository.UserRepository
import com.example.lolshop.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userProfile = MutableLiveData<Result<User?>>()
    val userProfile: LiveData<Result<User?>> = _userProfile

    private val _logoutResult = MutableLiveData<Resource<Unit>>()
    val logoutResult: LiveData<Resource<Unit>> get() = _logoutResult

    private val _passwordChangeState = MutableStateFlow<Resource<String>?>(null)
    val passwordChangeState: StateFlow<Resource<String>?> = _passwordChangeState

    fun getUserData(uid: String): Flow<User?> {
        return userRepository.getUserById(uid)
    }

    // Update user profile and fetch updated data
    fun updateProfile(name: String, phoneNumber: String, address: String) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                    ?: throw Exception("User not signed in")

                // Update user profile in Firestore
                val updateResult = userRepository.updateUserProfile(currentUser.uid, name, phoneNumber, address)

                if (updateResult.isSuccess) {
                    // Fetch and emit the updated user profile after successful update
                    val updatedUser = userRepository.getUserById(currentUser.uid).first()
                    _userProfile.value = Result.success(updatedUser)
                } else {
                    // Handle the failure case
                    _userProfile.value = Result.failure(Exception("Profile update failed"))
                }
            } catch (e: Exception) {
                _userProfile.value = Result.failure(e)
            }
        }
    }

    //Log out
    fun logout() {
        viewModelScope.launch {
            _logoutResult.value = userRepository.logout()  // Call the repository's logout function
        }
    }

    //Change Password
    fun changePassword(currentPassword: String, newPassword: String){
        viewModelScope.launch {
            _passwordChangeState.value = Resource.Loading()
            val result = userRepository.changePassword(currentPassword, newPassword)
            _passwordChangeState.value = when (result) {
                is Resource.Success -> Resource.Success(result.data ?: "Password updated successfully.")
                is Resource.Error -> Resource.Error(result.message ?: "Unknown error occurred.")
                else -> Resource.Error("Unhandled state.")
            }
        }
    }

}