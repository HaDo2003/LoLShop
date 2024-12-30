package com.example.lolshop.view.homepage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.lolshop.model.Cart
import com.example.lolshop.model.CartProduct
import com.example.lolshop.utils.Resource
import com.example.lolshop.utils.Result
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.SuccessScreen
import com.example.lolshop.view.admin.AdminActivity
import com.example.lolshop.viewmodel.homepage.CartViewModel
import com.example.lolshop.viewmodel.homepage.CartViewModelFactory
import com.example.lolshop.viewmodel.homepage.OrderViewModel
import com.example.lolshop.viewmodel.homepage.OrderViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class CartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = intent.getStringExtra("uid") ?: ""
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        Log.d("uid", uid)
        Log.d("isAdmin", isAdmin.toString())
        setContent {
            val cartViewModel: CartViewModel = viewModel(
                factory = CartViewModelFactory(
                    FirebaseFirestore.getInstance(),
                    FirebaseDatabase.getInstance(),
                    applicationContext
                )
            )
            CartScreen(
                uid,
                isAdmin = isAdmin,
                onBackClick = { finish() },
                onCartClick = {
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
                    val intent = Intent(this, OrderActivity::class.java).apply {
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
private fun CartScreen(
    uid: String,
    isAdmin: Boolean,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick: () -> Unit,
    onHomeClick: () -> Unit,
    onOrderClick:() -> Unit,
    cartViewModel: CartViewModel
) {
    // Trigger cart fetching when UID changes
    LaunchedEffect(uid) {
        cartViewModel.fetchCart(uid)
    }

    // Observe the cart state (LiveData for cart and StateFlow for loading/error state)
    val cartState by cartViewModel.cart.observeAsState(Result.Empty) // Collect the cart state (loading/success/error)
    val error by cartViewModel.error.observeAsState("") // Observe error messages
    val orderState by cartViewModel.orderState.collectAsState() // Collect the cart state (loading/success/error)
    var isSuccessScreenVisible by remember { mutableStateOf(false) }
    val successMessage by remember { mutableStateOf("Place Order Successful") }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val currentScreen = "cart"
    Scaffold(
        bottomBar = {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
            ) {
                if (cartState is Result.Success && (cartState as Result.Success<Cart>).data.products.isNotEmpty()) {
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    CartSummary(
                        cart = (cartState as Result.Success<Cart>).data,
                        uid,
                        (cartState as Result.Success<Cart>).data.total,
                        cartViewModel = cartViewModel,
                        navigateToOrder = onOrderClick
                    )
                }
                Spacer(Modifier.height(10.dp))
                BottomMenu(
                    isAdmin = isAdmin,
                    modifier = Modifier.fillMaxWidth(),
                    onItemClick = onCartClick,
                    onProfileClick = onProfileClick,
                    onAdminClick = onAdminClick,
                    onHomeClick = onHomeClick,
                    onOrderClick = onOrderClick,
                    currentScreen = currentScreen
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
        ) {
            ConstraintLayout(modifier = Modifier.padding(top = 36.dp)) {
                val (backBtn, cartTxt) = createRefs()
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(cartTxt) { centerTo(parent) },
                    text = "Your Cart",
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

            when (cartState) {
                is Result.Success -> {
                    val cart = (cartState as Result.Success<Cart>).data
                    Log.d("Cart", cart.toString())
                    if (cart.products.isEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cart is empty")
                        }

                    } else {
                        CartList(
                            cart = cart,
                            onItemChange = { productId, newQuantity ->
                                cartViewModel.updateProductQuantity(uid, productId, newQuantity)
                            }
                        )
                    }
                }
                is Result.Error -> {
                    Text(
                        text = "Error: ${(cartState as Result.Error).exception.message}",
                        color = Color.Red
                    )
                }
                is Result.Empty ->{
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cart is empty")
                    }
                }
            }

            if (error.isNotEmpty()) {
                Text(text = error, color = Color.Red)
            }

            when (orderState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is Resource.Success -> {
                    LaunchedEffect(orderState) {
                        isSuccessScreenVisible = true
                        cartViewModel.clearError()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(
                        context,
                        "Place Order Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Empty -> {
                }
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

@SuppressLint("DefaultLocale")
@Composable
fun CartSummary(
    cart: Cart,
    uid: String,
    Total: Double,
    cartViewModel: CartViewModel,
    navigateToOrder:() -> Unit
) {
    val tax: Double = Total * 0.1
    val taxRounded:Double = String.format("%.2f", tax).toDouble()
    val delivery = 10.0
    val total = Total + taxRounded + delivery
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
            .background(Color.White)
    ) {
        Text(text = "Item Total: $${Total}", fontSize = 18.sp)
        Text(text = "Tax: $${taxRounded}", fontSize = 18.sp)
        Text(text = "Delivery: $${delivery}", fontSize = 18.sp)
        Text(
            text = "Total: $${total}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        // Button for Place Order
        Button(
            onClick = {
                cartViewModel.placeOrderFromCart(cart, uid, Total, taxRounded, delivery, total)
                Handler(Looper.getMainLooper()).postDelayed({
                    navigateToOrder()
                }, 2000)
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Place Order",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}


@Composable
fun CartList(
    cart: Cart,
    onItemChange: (String, Int) -> Unit // Change the signature to accept productId and newQuantity
) {
    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
        items(cart.products.size) { index ->
            val item = cart.products[index]
            CartProduct(
                product = item,
                onItemChange = onItemChange
            )
        }
    }
}

@Composable
fun CartProduct(
    product: CartProduct,
    onItemChange: (productId: String, newQuantity: Int) -> Unit
) {
    var quantityState by remember { mutableStateOf(product.quantity) } // Track the current quantity

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val (pic, titleTxt, feeEachTime, totalEachItem, quantity) = createRefs()

        Image(
            painter = rememberAsyncImagePainter(product.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
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
                .constrainAs(titleTxt) {
                    start.linkTo(pic.end)
                    top.linkTo(pic.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = "$${product.price}",
            color = colorResource(R.color.black),
            modifier = Modifier
                .constrainAs(feeEachTime) {
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = "$${product.price * product.quantity}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.black),
            modifier = Modifier
                .constrainAs(totalEachItem) {
                    start.linkTo(titleTxt.start)
                    bottom.linkTo(pic.bottom)
                }
                .padding(start = 8.dp, end = 124.dp)
        )

        // Quantity controls (decrement and increment buttons)
        Row(
            modifier = Modifier
                .constrainAs(quantity) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .padding(top = 63.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Decrement Button
            Box(
                modifier = Modifier
                    .border(1.dp,
                        colorResource(id = R.color.black),
                        shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp))
                    .clickable {
                        quantityState -= 1
                        onItemChange(product.productId, quantityState) // Update the quantity and notify parent
                    }
                    .padding(7.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.minus),
                    contentDescription = "Decrease quantity",
                    modifier = Modifier.size(18.dp)
                )
            }

            // Quantity Display with Border
            Box(
                modifier = Modifier
                    .border(1.dp, colorResource(id = R.color.black)) // Border around the quantity display
                    .padding(8.dp)
                    .width(20.dp)
                    .height((15.5).dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quantityState.toString(),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp)
                )
            }

            // Increment Button
            Box(
                modifier = Modifier
                    .border(1.dp,
                        colorResource(id = R.color.black),
                        shape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)) // Border around the button
                    .clickable {
                        quantityState += 1
                        onItemChange(product.productId, quantityState) // Update the quantity and notify parent
                    }
                    .padding(7.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Increase quantity",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

    }
}
