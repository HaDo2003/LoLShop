package com.example.lolshop.view.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lolshop.R
import androidx.compose.ui.text.style.TextAlign
import com.example.lolshop.model.Product


@Composable
fun PopularProduct(product: List<Product>, pos: Int){
    val context= LocalContext.current

    Column(modifier = Modifier
        .padding(8.dp)
        .wrapContentHeight()
    ) {

        AsyncImage(
            model = product[pos].imageUrl.firstOrNull(),
            contentDescription = product[pos].name,
            modifier = Modifier
                .width(175.dp)
                .background(colorResource(R.color.purple_200),
                    shape = RoundedCornerShape(10.dp))
                .height(195.dp)
                .clickable{

                }, contentScale = ContentScale.Crop
        )
        Text(
            text = product[pos].name,
            color= Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = Ellipsis,
            modifier= Modifier.padding(top=8.dp)
        )

        Row (
            modifier = Modifier.width(175.dp).padding(top=4.dp)

        ){Text(
            text = "$${product[pos].price}",
            color = colorResource(R.color.black),
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold

        )}
    }
}


@Composable
fun ListProduct(product: SnapshotStateList<Product>){
    LazyRow (modifier = Modifier
        .padding(top=8.dp)
        .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(product.size){
            index: Int->
            PopularProduct(product, index)
        }
    }
}

@Composable
fun ListProductFullSize(product: List<Product>){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical=16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(product.size){row ->
            PopularProduct(product, row)
        }
    }
}

