package com.example.lolshop.view.homepage

import android.R.attr.contentDescription
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lolshop.Helper.ManagmentCart
import com.example.lolshop.R
import com.example.lolshop.view.BaseActivity
import android.content.Intent

import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale

import com.example.lolshop.viewmodel.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lolshop.model.Banner
import com.example.lolshop.model.Category
import com.example.lolshop.model.Product
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.*
import coil.compose.rememberAsyncImagePainter
import com.example.project1762.Helper.ChangeNumberItemsListener

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            )
        }
        if (cartProducts.value.isEmpty()) {
            Text(text = "Cart Is Empty", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            CartList(cartProducts = cartProducts.value, managmentCart) {
                cartProducts.value = managmentCart.getListCart()
                calculatorCart(managmentCart, tax)
            }
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
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(text = "Item Total: $${itemTotal}", fontSize = 18.sp)
        Text(text = "Tax: $${tax}", fontSize = 18.sp)
        Text(text = "Delivery: $${delivery}", fontSize = 18.sp)
        Text(
            text = "Total: $${itemTotal + tax + delivery}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
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
        val (pic, titleTxt, feeEachTime, quantity) = createRefs()

        Image(
            painter = rememberAsyncImagePainter(item.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .constrainAs(pic) { start.linkTo(parent.start) },
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
            color = colorResource(R.color.purple_700),
            modifier = Modifier
                .constrainAs(feeEachTime) {
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        
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
                color = colorResource(R.color.purple_700),
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
                color = colorResource(R.color.purple_700),
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
                color = colorResource(R.color.purple_700),
                modifier = Modifier.align(Alignment.Center),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
