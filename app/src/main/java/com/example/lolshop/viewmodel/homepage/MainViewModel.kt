package com.example.lolshop.viewmodel.homepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.lolshop.model.Banner
import com.example.lolshop.model.Category
import com.example.lolshop.model.Product
import com.example.lolshop.repository.MainRepository

class MainViewModel(): ViewModel() {
    private val repository= MainRepository()
    fun loadBanner(): LiveData<MutableList<Banner>>{
        return repository.loadBanner()
    }

    fun loadCategory(): LiveData<MutableList<Category>>{
            return repository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<Product>>{
            return repository.loadPopular()
    }

    fun loadFiltered(id: String): LiveData<MutableList<Product>>{
        return repository.loadFilterd(id)
    }
}