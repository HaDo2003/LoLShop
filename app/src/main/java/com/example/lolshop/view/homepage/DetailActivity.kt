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
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.lolshop.model.Product





class DetailActivity : AppCompatActivity() {
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
                val (back,fav,mainImage, thumbnail)=createRefs()
                Image(
                    painter = rememberAsyncImagePainter(model = selectedImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            colorResource(R.color.purple_200),
                            shape = RoundedCornerShape(8.dp)
                        )
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

                LazyRow(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .background(
                            color = colorResource(R.color.white),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
//                    items(product.imageUrl){imageUrl->
//                        ImageThumbNail(
//                            imageUrl = imageUrl,
//                            isSelected = selectedModelIndex == imageUrl,
//                            onClick = {selectedImageUrl=imageUrl}
//                        )
//                    }
                }



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
                ModelSelector(
                    models=product.model,
                    selectedModelIndex=selectedModelIndex,
                    onModelSelected= {selectedModelIndex=it}
                )
            }
        }
    }

    @Composable
    fun ImageThumbNail(imageUrl: Int, isSelected: Boolean, onClick: () -> Unit) {
        val backColor = if (isSelected) {
            colorResource(R.color.teal_700)
        } else {
            colorResource(R.color.teal_200)
        }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(55.dp)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            1.dp,
                            colorResource(R.color.teal_700),
                            RoundedCornerShape(10.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .background(backColor, shape = RoundedCornerShape(10.dp))
                .clickable(onClick = onClick)
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    @Composable
    fun ModelSelector(
        models: ArrayList<String>,
        selectedModelIndex: Int,
        onModelSelected: (Int) -> Unit
    ) {
        LazyRow(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            itemsIndexed(models){
                    index,model->
                Box(modifier = Modifier
                    .padding(end=16.dp)
                    .height(40.dp)
                    .then(
                        if(index==selectedModelIndex){
                            Modifier.border(1.dp, colorResource(R.color.teal_700)
                                , RoundedCornerShape(10.dp)
                            )
                        }else{
                            Modifier.border(1.dp, colorResource(R.color.teal_700)
                                , RoundedCornerShape(10.dp)
                            )
                        }
                    )
                    .background(
                        if (index==selectedModelIndex) colorResource(R.color.teal_700)else
                        colorResource(R.color.white)
                    )
                    .clickable{onModelSelected(index)}
                    .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text=model,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if(index==selectedModelIndex) colorResource(R.color.white)
                        else colorResource(R.color.black),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }



}






