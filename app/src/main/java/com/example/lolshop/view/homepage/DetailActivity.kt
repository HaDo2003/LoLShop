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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.lolshop.Helper.ManagmentCart
import com.example.lolshop.R
import com.example.lolshop.model.Product
import com.example.lolshop.view.BaseActivity


class DetailActivity : BaseActivity() {
    private lateinit var product: Product
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product=intent.getSerializableExtra("object") as Product
        managmentCart= ManagmentCart(this)

        setContent{
            DetailScreen(
                product=product,
                onBackClick={finish()},
                onAddToCartClick={
                    product.numberInCart=1
                    managmentCart.insertItem(product)
                },
                onCartClick={
                    startActivity(Intent(this, CartActivity::class.java))
                }
            )
        }
    }
}

@Composable
private fun DetailScreen(
    product: Product,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onCartClick: () -> Unit,
) {
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
                        painter = painterResource(id = R.drawable.btn_2),
                        contentDescription = "Cart",
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Button(
                    onClick = onAddToCartClick,
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
        }
    }
}