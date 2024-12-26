package com.example.lolshop.view.homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.lolshop.utils.Result
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.admin.AdminActivity
import com.example.lolshop.viewmodel.homepage.CartViewModel
import com.example.lolshop.viewmodel.homepage.CartViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

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
    cartViewModel: CartViewModel
) {
    // Trigger cart fetching when UID changes
    LaunchedEffect(uid) {
        cartViewModel.fetchCart(uid)
    }

    // Observe the cart state (LiveData for cart and StateFlow for loading/error state)
    val cartState by cartViewModel.cart.observeAsState(Result.Empty) // Collect the cart state (loading/success/error)
    val error by cartViewModel.error.observeAsState("") // Observe error messages

    val currentScreen = "cart"
    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (cartState is Result.Success && (cartState as Result.Success<Cart>).data.products.isNotEmpty()) {
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    CartSummary()
                }
                Spacer(Modifier.height(10.dp))
                BottomMenu(
                    isAdmin = isAdmin,
                    modifier = Modifier.fillMaxWidth(),
                    onItemClick = onCartClick,
                    onProfileClick = onProfileClick,
                    onAdminClick = onAdminClick,
                    onHomeClick = onHomeClick,
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
                .padding(horizontal = 10.dp)
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
                )
            }

            when (cartState) {
                is Result.Success -> {
                    val cart = (cartState as Result.Success<Cart>).data
                    if (cart.products.isEmpty()) {
                        Text("Cart is empty")
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
                is Result.Empty -> Text("Cart is empty")
            }

            if (error.isNotEmpty()) {
                Text(text = error, color = Color.Red)
            }
        }
    }
}




@Composable
fun CartSummary(
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        // Button for Checkout
        Button(
            onClick = {
                // Add your checkout logic here
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
                text = "Check Out",
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
//                onItemChange = { newQuantity ->
//                    onItemChange(item.productId, newQuantity) // Pass the productId and newQuantity to the onItemChange
//                }
            )
        }
    }
}

@Composable
fun CartProduct(
    product: CartProduct,
    //onItemChange: (productId: String, newQuantity: Int) -> Unit
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
                .padding(start = 8.dp)
        )

        // Quantity controls (decrement and increment buttons)
//        Row(
//            modifier = Modifier
//                .constrainAs(quantity) {
//                    start.linkTo(totalEachItem.end)
//                    top.linkTo(totalEachItem.top)
//                }
//                .padding(start = 8.dp)
//        ) {
//            IconButton(onClick = {
//                if (quantityState > 1) {
//                    quantityState -= 1
//                    onItemChange(product.productId, quantityState) // Update the quantity and notify parent
//                }
//            }) {
//                Icon(imageVector = Icons.Default.Menu, contentDescription = "Decrement")
//            }
//            Text(text = quantityState.toString())
//            IconButton(onClick = {
//                quantityState += 1
//                onItemChange(product.productId, quantityState) // Update the quantity and notify parent
//            }) {
//                Icon(imageVector = Icons.Default.Add, contentDescription = "Increment")
//            }
//        }
    }
}


//        ConstraintLayout(
//            modifier = Modifier
//                .width(100.dp)
//                .constrainAs(quantity) {
//                    end.linkTo(parent.end)
//                    bottom.linkTo(parent.bottom)
//                }
//        ) {
//            QuantitySelector(
//                currentQuantity = item.numberInCart,
//                onIncrease = {
//                    managementCart.plusItem(
//                        managementCart.getListCart(),
//                        managementCart.getListCart().indexOf(item),
//                        object : ChangeNumberItemsListener {
//                            override fun onChanged() {
//                                onItemChange()
//                            }
//                        }
//                    )
//                },
//                onDecrease = {
//                    managementCart.minusItem(
//                        managementCart.getListCart(),
//                        managementCart.getListCart().indexOf(item),
//                        object : ChangeNumberItemsListener {
//                            override fun onChanged() {
//                                onItemChange()
//                            }
//                        }
//                    )
//                }
//            )
//        }
//    }
//}


@Composable
fun QuantitySelector(
    currentQuantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Decrease Button
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color.White, RoundedCornerShape(50))
                .clickable {
                    if (currentQuantity > 0) onDecrease()
                }
        ) {
            Text(
                text = "-",
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Current Quantity
        Text(
            text = currentQuantity.toString(),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        // Increase Button
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color.White, RoundedCornerShape(50))
                .clickable { onIncrease() }
        ) {
            Text(
                text = "+",
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
