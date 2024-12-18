package com.example.lolshop.ui.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.lolshop.model.SlideModle
import com.example.lolshop.repository.MainRepository

class MainViewModel(): ViewModel() {
    private val repository= MainRepository()
    fun loadBanner(): LiveData<MutableList<SlideModle>>{
        return repository.loadBanner()
    }
}