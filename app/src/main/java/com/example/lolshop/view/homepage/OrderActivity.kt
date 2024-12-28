package com.example.lolshop.view.homepage

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.R
import com.example.lolshop.model.Order
import com.example.lolshop.model.OrderProduct
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.admin.AdminActivity
import com.example.lolshop.viewmodel.homepage.OrderViewModel
import com.example.lolshop.viewmodel.homepage.OrderViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

class OrderActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = intent.getStringExtra("uid") ?: ""
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        setContent{
            val orderViewModel: OrderViewModel = viewModel(
                factory = OrderViewModelFactory(
                    FirebaseFirestore.getInstance(),
                    applicationContext
                )
            )
            OrdersScreen(
                uid,
                isAdmin = isAdmin,
                onBackClick = { finish() },
                onCartClick = {
                    val intent = Intent(this, CartActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    startActivity(intent)
                },
                onProfileClick = {
                    val intent = Intent(this, UserProfile::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    startActivity(intent)
                },
                onAdminClick = {
                    val intent = Intent(this, AdminActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    startActivity(intent)
                },
                onHomeClick = {
                    val intent = Intent(this, MainScreen::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    startActivity(intent)
                },
                onOrderClick = {
                },
                orderViewModel = orderViewModel
            )
        }
    }
}

@Composable
fun OrdersScreen(
    uid: String,
    isAdmin: Boolean,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick: () -> Unit,
    onHomeClick: () -> Unit,
    onOrderClick: () -> Unit,
    orderViewModel: OrderViewModel
) {
    // Trigger orders fetching when UID changes
    LaunchedEffect(uid) {
        orderViewModel.fetchOrders(uid)
    }

    val userOrders by orderViewModel.userOrders.observeAsState(emptyList())
    val error by orderViewModel.error.observeAsState(null)
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                BottomMenu(
                    isAdmin = isAdmin,
                    modifier = Modifier.fillMaxWidth(),
                    onItemClick = onCartClick,
                    onProfileClick = onProfileClick,
                    onAdminClick = onAdminClick,
                    onHomeClick = onHomeClick,
                    onOrderClick = onOrderClick,
                    currentScreen = "order"
                )
            }
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp, // Override any top padding caused by Scaffold
                    bottom = paddingValue.calculateBottomPadding(),
                )
                .padding(bottom = 0.dp)
                .background(Color.White)
                .verticalScroll(scrollState)
        ) {
            ConstraintLayout(modifier = Modifier.padding(top = 36.dp)) {
                val (backBtn, cartTxt) = createRefs()
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(cartTxt) { centerTo(parent) },
                    text = "Your Order",
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .constrainAs(backBtn) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .size(40.dp)
                        .padding(start = 5.dp)
                )
            }
            if (error != null) {
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            } else if (userOrders.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "No orders found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp)
                        .background(Color.White)
                ) {
                    val sortedOrders = userOrders.sortedByDescending { it.orderDate }

                    items(sortedOrders) { order ->
                        OrderItem(
                            order = order
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun OrderItem(
    order: Order,
) {
    val totalQuantity = order.products?.sumOf { it.quantity } ?: 0
    val productList = order.products ?: emptyList()
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors( // Set the card background color
                containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${order.orderId}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display products
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Always show first product
                if (productList.isNotEmpty()) {
                    ProductItem(product = productList[0])
                }

                // Show "See more" button if there are additional products
                if (productList.size > 1) {
                    TextButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (expanded) "Show less" else "See ${productList.size - 1} more products",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Show remaining products if expanded
                    if (expanded) {
                        productList.drop(1).forEach { product ->
                            ProductItem(product = product)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (order.status == "Completed") Color.Green else Color.Red
                )

                Text(
                    text = "Total(${totalQuantity} products): ${order.totalPriceAtAll}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ProductItem(
    product: OrderProduct,
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .padding(3.dp)
    ) {
        val (pic, name, quantity, price) = createRefs()
        Image(
            painter = rememberAsyncImagePainter(product.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(pic) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            contentScale = ContentScale.Crop
        )
        Text(
            text = product.name,
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(pic.end)
                    top.linkTo(parent.top)
                }
                .padding(start = 8.dp)
        )
        Text(
            text = "x${product.quantity}",
            modifier = Modifier
                .constrainAs(quantity) {
                    start.linkTo(name.end)
                    top.linkTo(parent.top)
                }
                .padding(start = 100.dp)
        )

        Text(
            text = "$${product.price}",
            color = colorResource(R.color.black),
            modifier = Modifier
                .constrainAs(price) {
                    start.linkTo(pic.end)
                    top.linkTo(quantity.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
    }
}

