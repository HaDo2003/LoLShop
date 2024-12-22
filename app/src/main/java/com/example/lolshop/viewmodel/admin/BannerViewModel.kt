package com.example.lolshop.viewmodel.admin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lolshop.model.Banner
import com.example.lolshop.repository.BannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BannerViewModel(private val context: Context) : ViewModel() {
    private val bannerRepository = BannerRepository(context)
    private val _banner = MutableStateFlow<List<Banner>>(emptyList())
    val banner: StateFlow<List<Banner>> = _banner

    init {
        fetchBanner()
    }

    fun fetchBanner(){
        viewModelScope.launch {
            _banner.value = bannerRepository.fetchBanner()
        }
    }

    fun addBanner(
        imageUri: Uri?,
        onValidationError: () -> Unit,
        onNavigationSuccess: () -> Unit
    ){
        if(imageUri == null){
            onValidationError()
        }

        viewModelScope.launch {
            bannerRepository.addBanner(imageUri)
            fetchBanner()
            onNavigationSuccess()
        }
    }
}