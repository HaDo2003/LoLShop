package com.example.lolshop.view.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lolshop.R
import com.example.lolshop.model.Banner
import com.example.lolshop.model.Category
import com.example.lolshop.repository.BannerRepository
import com.example.lolshop.viewmodel.admin.AdminViewModel
import com.example.lolshop.viewmodel.admin.BannerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddBannerScreen(
    bannerViewModel: BannerViewModel,
    bannerRepository: BannerRepository,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    var isLoading by remember { mutableStateOf(false) }
    var showSnackbar by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val imageUriState = rememberSaveable { mutableStateOf<Uri?>(null) }
    val bannersList = remember { mutableStateOf<List<Banner>>(emptyList()) }
    val totalBanners = bannersList.value.size
    val imageResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUriState.value = uri
    }
    val fetchBanner: () -> Unit = {
        CoroutineScope(Dispatchers.IO).launch {
            val banner = bannerRepository.fetchBanner()
            bannersList.value = banner
        }
    }
    val context = LocalContext.current

    // Fetch banners and total count from Firebase
    LaunchedEffect(Unit) {
        fetchBanner()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Manage Banners", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        if (showSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showSnackbar = false }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text("Please select an image")
            }
        }
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
                    if (imageUriState.value != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUriState.value),
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
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Select Banner Image")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Add category button
                    Button(
                        onClick = {
                            bannerViewModel.addBanner(
                                imageUri = imageUriState.value,
                                onValidationError = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please select image")
                                    }
                                },
                                onNavigationSuccess = {
                                    coroutineScope.launch {
                                        Toast.makeText(
                                            context,
                                            "Banner added successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("admin_main")
                                    }
                                }
                            )
                        },
                        enabled = !isLoading,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(if (isLoading) "Adding..." else "Add Banner")
                    }
                    if (isLoading) {
                        CircularProgressIndicator()
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
        // Display total categories and category list
        Text(text = "Total Banners: $totalBanners", style = MaterialTheme.typography.displaySmall)
        Spacer(modifier = Modifier.height((-200).dp))
        // Display list of categories
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy((-110).dp)
        ) {
            items(bannersList.value) { banner ->
                BannerItem(banner)
            }
        }
    }
}

@Composable
fun BannerItem(banner: Banner) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display category image from Cloudinary
        if (banner.imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(banner.imageUrl)  // Set the image URL
                        .size(200)  // Set the image size
                        .placeholder(R.drawable.placeholder)  // Set a placeholder image
                        .error(R.drawable.error_image)  // Set an error image
                        .build()
                ),
                contentDescription = "Banner Image",
                modifier = Modifier
                    .size(300.dp)  // Set the size for image display
            )
        } else {
            // Placeholder text if imageUrl is empty
            Text("No image available", style = MaterialTheme.typography.bodySmall)
        }
    }
}