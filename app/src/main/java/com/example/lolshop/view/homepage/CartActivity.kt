package com.example.lolshop.view.homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Space
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.Helper.ChangeNumberItemsListener
import com.example.lolshop.Helper.ManagementCart
import com.example.lolshop.R
import com.example.lolshop.model.Product
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.admin.AdminActivity
import kotlin.properties.Delegates

class CartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = intent.getStringExtra("uid") ?: ""
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        Log.d("uid", uid)
        Log.d("isAdmin", isAdmin.toString())
        setContent {
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
                }
            )
        }
    }
}
fun calculatorCart(managementCart: ManagementCart, tax: MutableState<Double>){
    val percentTax = 0.02
    tax.value= Math.round((managementCart.getTotalFee()*percentTax)*100)/100.0
}

@Composable
private fun CartScreen(
    uid: String,
    isAdmin: Boolean,
    managementCart: ManagementCart = ManagementCart(LocalContext.current),
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick:() -> Unit,
    onHomeClick:() -> Unit
) {
    val currentScreen = "cart"
    val cartProducts = remember { mutableStateOf(managementCart.getListCart()) }
    val tax = remember { mutableStateOf(0.0) }
    calculatorCart(managementCart, tax)
    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!cartProducts.value.isEmpty()) {
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    CartSummary(
                        itemTotal = managementCart.getTotalFee(),
                        tax = tax.value,
                        delivery = 10
                    )
                }
                Spacer(Modifier.height(10.dp))
                BottomMenu(
                    isAdmin = isAdmin,
                    modifier = Modifier
                        .fillMaxWidth(),
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
            if (cartProducts.value.isEmpty()) {
                Text(
                    text = "Cart Is Empty",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                CartList(
                    cartProducts = cartProducts.value,
                    managementCart = managementCart,
                    onItemChange = {
                        cartProducts.value = managementCart.getListCart()
                        calculatorCart(managementCart, tax)
                    }
                )
            }
        }
    }
}


@Composable
fun CartSummary(itemTotal: Double, tax: Double, delivery: Int) {
    val total = itemTotal + tax + delivery
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(text = "Item Total: $${itemTotal}", fontSize = 18.sp)
        Text(text = "Tax: $${tax}", fontSize = 18.sp)
        Text(text = "Delivery: $${delivery}", fontSize = 18.sp)
        Text(
            text = "Total: $${total}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

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
fun CartList(cartProducts: List<Product>, managementCart: ManagementCart, onItemChange: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
        items(cartProducts.size) { index ->
            val item = cartProducts[index]
            CartProduct(item, managementCart, onItemChange)
        }
    }
}

@Composable
fun CartProduct(
    item: Product,
    managementCart: ManagementCart,
    onItemChange: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val (pic, titleTxt, feeEachTime, totalEachItem, quantity) = createRefs()

        Image(
            painter = rememberAsyncImagePainter(item.imageUrl),
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
            text = item.name,
            modifier = Modifier
                .constrainAs(titleTxt) {
                    start.linkTo(pic.end)
                    top.linkTo(pic.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = "$${item.price}",
            color = colorResource(R.color.black),
            modifier = Modifier
                .constrainAs(feeEachTime) {
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = "$${item.numberInCart * item.price.toDouble()}",
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
        ConstraintLayout(
            modifier = Modifier
                .width(100.dp)
                .constrainAs(quantity) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            QuantitySelector(
                currentQuantity = item.numberInCart,
                onIncrease = {
                    managementCart.plusItem(
                        managementCart.getListCart(),
                        managementCart.getListCart().indexOf(item),
                        object : ChangeNumberItemsListener {
                            override fun onChanged() {
                                onItemChange()
                            }
                        }
                    )
                },
                onDecrease = {
                    managementCart.minusItem(
                        managementCart.getListCart(),
                        managementCart.getListCart().indexOf(item),
                        object : ChangeNumberItemsListener {
                            override fun onChanged() {
                                onItemChange()
                            }
                        }
                    )
                }
            )
        }
    }
}


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
