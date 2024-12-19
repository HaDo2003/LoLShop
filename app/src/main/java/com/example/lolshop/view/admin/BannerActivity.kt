package com.example.lolshop.view.admin

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.model.Banner
import com.example.lolshop.repository.BannerRepository
import kotlinx.coroutines.launch

class BannerActivity : AppCompatActivity() {
    private lateinit var bannerRepository: BannerRepository
    private var imageUriState by mutableStateOf<Uri?>(null)
    private var bannersList by mutableStateOf<List<Banner>>(emptyList())
    private var totalBanners by mutableIntStateOf(0)

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUriState = it
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bannerRepository = BannerRepository(this)
        setContent{
            BannerScreen()
        }
    }

    @Composable
    fun BannerScreen(){
        var isLoading by remember { mutableStateOf(false) }

        // Fetch banners and total count from Firebase
        LaunchedEffect(Unit) {
            fetchBanners()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Manage Banners", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            // Card for Add Banner form
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Image selection
                        if (imageUriState != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUriState),
                                contentDescription = "Selected Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        } else {
                            Button(
                                onClick = { imageResultLauncher.launch("image/*") },
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                )
                            ) {
                                Text("Select Banner Image")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Add category button
                        Button(
                            onClick = {
                                if (imageUriState == null) {
                                    Toast.makeText(
                                        this@BannerActivity,
                                        "Please select an image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    isLoading = true
                                    lifecycleScope.launch {
                                        bannerRepository.addBanner(imageUriState)
                                        isLoading = false
                                        imageUriState = null // Reset image
                                        Toast.makeText(
                                            this@BannerActivity,
                                            "Add banner successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Fetch the updated category list
                                        fetchBanners()
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(if (isLoading) "Adding..." else "Add Banner")
                        }
                    }
                }
            }
        }
    }

    private fun fetchBanners() {
        lifecycleScope.launch {
            val category = bannerRepository.fetchCategories()
            bannersList = category
            totalBanners = category.size
        }
    }
}