package com.example.lolshop.view.homepage

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.lolshop.Helper.ManagmentCart
import com.example.lolshop.R
import com.example.lolshop.model.Product
import com.example.lolshop.view.BaseActivity

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CartScreen(
                onBackClick = { finish() }
            )
        }
    }
}
fun calculatorCart(managmentCart: ManagmentCart, tax: MutableState<Double>){
    val percentTax = 0.02
    tax.value= Math.round((managmentCart.getTotalFee()*percentTax)*100)/100.0
}

@Composable
private fun CartScreen(
    managmentCart: ManagmentCart = ManagmentCart(LocalContext.current),
    onBackClick: () -> Unit
) {
    val cartProducts = remember { mutableStateOf(managmentCart.getListCart()) }
    val tax = remember { mutableStateOf(0.0) }
    calculatorCart(managmentCart, tax)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            Text(text = "Cart Is Empty", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            CartList(
                cartProducts = cartProducts.value,
                managmentCart = managmentCart,
                onItemChange = {
                    cartProducts.value = managmentCart.getListCart()
                    calculatorCart(managmentCart, tax)
                }
            )
            CartSummary(
                itemTotal = managmentCart.getTotalFee(),
                tax = tax.value,
                delivery = 10
            )
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
fun CartList(cartProducts: List<Product>, managmentCart: ManagmentCart, onItemChange: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
        items(cartProducts.size) { index ->
            val item = cartProducts[index]
            CartProduct(item, managmentCart, onItemChange)
        }
    }
}

@Composable
fun CartProduct(
    item: Product,
    managmentCart: ManagmentCart,
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
                .constrainAs(pic) { start.linkTo(parent.start)
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
                .constrainAs(quantity){
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            QuantitySelector(
                currentQuantity = item.numberInCart,
                onIncrease = {
                    managmentCart.plusItem(
                        managmentCart.getListCart(),
                        managmentCart.getListCart().indexOf(item),
                        object : ChangeNumberItemsListener {
                            override fun onChanged() {
                                onItemChange()
                            }
                        }
                    )
                },
                onDecrease = {
                    managmentCart.minusItem(
                        managmentCart.getListCart(),
                        managmentCart.getListCart().indexOf(item),
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
