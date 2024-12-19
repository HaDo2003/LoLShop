package com.example.lolshop.Activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.lolshop.R
import com.example.lolshop.ui.BaseActivity
import com.example.lolshop.ui.ViewModel.MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign

class ListProductActivity : BaseActivity() {
    private val viewModel= MainViewModel()
    private var id: String=""
    private var name: String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    id= intent.getStringExtra("id")?:""
         name=intent.getStringExtra("name")?:""


        setContent{
            ListItemScreen(
                name=name,
                onBackClick={finish()},
                viewModel=viewModel,
                id=id
        )
        }
    }

    @Composable
    private fun ListItemScreen(
        name: String,
        onBackClick: () -> Unit,
        viewModel: MainViewModel,
        id: String
    ) {
    val product by viewModel.loadFiltered(id).observeAsState(emptyList())
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(id) {
            viewModel.loadFiltered(id)
        }

        Column (modifier = Modifier.fillMaxSize()){
            ConstraintLayout(
                modifier = Modifier.padding(top=36.dp, start=16.dp,end=16.dp)
            ) {
                val(backBtn, CartTxt)=createRefs()

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(CartTxt){centerTo(parent)},
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    text = name
                )

                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable{
                            onBackClick()
                        }
                        .constrainAs(backBtn){
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                )
            }
            if (isLoading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }else{
                ListProductFullSize(product)
            }
        }
        LaunchedEffect(product) {
            isLoading=product.isEmpty()
        }
    }
}

