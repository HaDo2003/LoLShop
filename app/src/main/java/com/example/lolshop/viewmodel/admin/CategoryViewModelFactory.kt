package com.example.lolshop.viewmodel.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CategoryViewModelFactory (
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        if(modelClass.isAssignableFrom(CategoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}