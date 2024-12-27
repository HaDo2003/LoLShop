package com.example.lolshop.view.homepage

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.lolshop.R
import com.example.lolshop.model.Product
import com.example.lolshop.utils.Resource
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.SuccessScreen
import com.example.lolshop.viewmodel.homepage.CartViewModel
import com.example.lolshop.viewmodel.homepage.CartViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay


class DetailActivity : BaseActivity() {
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product=intent.getSerializableExtra("object") as Product
        val uid = intent.getStringExtra("uid") ?: ""
        val isAdmin = intent.getBooleanExtra("isAdmin", false)

        setContent{
            val cartViewModel: CartViewModel = viewModel(
                factory = CartViewModelFactory(
                    FirebaseFirestore.getInstance(),
                    FirebaseDatabase.getInstance(),
                    applicationContext
                )
            )

            DetailScreen(
                uid,
                product=product,
                onBackClick={finish()},
                onAddToCartClick={
                    if (uid.isNotEmpty()) {
                        cartViewModel.addProductToCart(uid, product.id)
                    }
                },
                onCartClick={
                    val intent = Intent(this, CartActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    startActivity(intent)
                },
                cartViewModel = cartViewModel
            )
        }
    }
}

@Composable
fun DetailScreen(
    uid: String,
    cartViewModel: CartViewModel,
    product: Product,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onCartClick: () -> Unit,
) {
    val cartState by cartViewModel.cartState.collectAsState()
    val successMessage by remember { mutableStateOf("Added to Cart") }
    var isSuccessScreenVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp, start = 5.dp, end = 5.dp)
            ) {
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(10.dp)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cart_white),
                        contentDescription = "Cart",
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Button(
                    onClick = {
                        if (uid.isNotEmpty()) {
                            cartViewModel.addProductToCart(uid, product.id)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = " Add to cart", fontSize = 18.sp
                    )
                }
            }
        }

    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 0.dp, // Override any top padding caused by Scaffold
                    bottom = paddingValue.calculateBottomPadding(),
                )
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp)
                    .padding(bottom = 16.dp)
            ) {
                val (back, mainImage) = createRefs()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .constrainAs(mainImage) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    if (product.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp)), // Rounded corners
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "No Image Available",
                            style = androidx.compose.material.MaterialTheme.typography.body2,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back Button",
                    modifier = Modifier
                        .padding(top = 48.dp, start = 5.dp)
                        .clickable { onBackClick() }
                        .constrainAs(back) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .size(40.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 23.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                Text(
                    text = "$${product.price}",
                    fontSize = 22.sp
                )
            }

            Text(
                text = product.description,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )

            when (cartState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is Resource.Success -> {
                    LaunchedEffect(cartState) {
                        isSuccessScreenVisible = true
                        cartViewModel.clearError()
                    }
                }
                is Resource.Error -> {
                }
                else -> Unit
            }
        }
    }
    if (isSuccessScreenVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 56.dp)
                .padding(top = 381.dp, bottom = 370.dp)
                .background(Color(0x80000000)),
            contentAlignment = Alignment.Center
        ) {
            SuccessScreen(
                message = successMessage,
            )

            LaunchedEffect(Unit) {
                delay(2000) // Wait for 2 seconds
                isSuccessScreenVisible = false // Hide the SuccessScreen
            }
        }
    }
}