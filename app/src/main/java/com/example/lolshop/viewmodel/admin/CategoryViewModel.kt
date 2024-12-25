package com.example.lolshop.viewmodel.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.model.Category
import com.example.lolshop.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val context: Context) : ViewModel() {
        private val categoryRepository = CategoryRepository(context)
        private val _category = MutableStateFlow<List<Category>>(emptyList())
        val category: StateFlow<List<Category>> = _category

        init {
            fetchCategory()
        }

        fun fetchCategory(){
            viewModelScope.launch {
                _category.value = categoryRepository.fetchCategory()
            }
        }

        fun addCategory(
            name: String,
            imageUri: Uri?,
            onValidationError: () -> Unit,
            onNavigationSuccess: () -> Unit
        ){
            if(name.isEmpty() || imageUri == null){
                onValidationError()
            }

            viewModelScope.launch {
                categoryRepository.addCategory(name, imageUri)
                fetchCategory()
                onNavigationSuccess()
            }
        }
}