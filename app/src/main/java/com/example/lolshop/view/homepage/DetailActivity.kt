package com.example.lolshop.view.homepage

import android.R.attr.text
import android.os.Bundle
import android.text.Layout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.lolshop.Helper.ManagmentCart
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.R

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
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

                }
            )
        }
    }


    @Composable
    private fun DetailScreen(
        product: Product,
        onBackClick: () -> Unit,
        onAddToCartClick: () -> Unit,
        onCartClick: () -> Unit,
    ) {
        var selectedImageUrl by remember { mutableStateOf(product.imageUrl.first()) }
        var selectedModelIndex by remember { mutableStateOf(-1) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .height(430.dp)
                    .padding(bottom = 16.dp)
            ) {
                val (back,mainImage,)=createRefs()
                Image(
                    painter = rememberAsyncImagePainter(model = selectedImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .constrainAs(mainImage){
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                            start.linkTo(parent.start)
                        }
                )
                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top=48.dp)
                        .clickable{onBackClick()}
                        .constrainAs(back){
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )


            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top=16.dp)
                    .padding(horizontal = 16.dp)
            ){
                Text(
                    text=product.name,
                    fontSize = 23.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end=16.dp)
                )
                Text(
                    text="$${product.price}",
                    fontSize = 22.sp
                )
                Text(text=product.description,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(18.dp)
                ){
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier.background(
                            colorResource(R.color.purple_700),
                            shape = RoundedCornerShape(10.dp)
                        )
                    ) {
                        Icon(
                            painter = painterResource(id= R.drawable.btn_2),
                            contentDescription =  "Cart",
                            tint= Color.Black
                        )
                    }
                    Button(
                        onClick = onAddToCartClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = colorResource(R.color.purple_700)
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
        }
    }

}