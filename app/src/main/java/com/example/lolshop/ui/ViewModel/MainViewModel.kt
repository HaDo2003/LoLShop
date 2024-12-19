package com.example.lolshop.ui.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.lolshop.model.Category
import com.example.lolshop.model.CategoryModel
import com.example.lolshop.model.ProductModel
import com.example.lolshop.model.SlideModle
import com.example.lolshop.repository.MainRepository

class MainViewModel(): ViewModel() {
    private val repository= MainRepository()
    fun loadBanner(): LiveData<MutableList<SlideModle>>{
        return repository.loadBanner()
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
            return repository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<ProductModel>>{
            return repository.loadPopular()
    }

    fun loadFiltered(id: String): LiveData<MutableList<ProductModel>>{
        return repository.loadFilterd(id)
    }
}