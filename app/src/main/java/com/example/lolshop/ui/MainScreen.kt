package com.example.lolshop.ui

import android.os.Bundle
import android.widget.Space
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.lolshop.R
import com.example.lolshop.model.SlideModle
import com.example.lolshop.ui.ViewModel.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState


@Composable
fun AlignedContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Hello, World!")
    }
}




class MainScreen : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            HomePageScreen()
        }
    }
}

@Composable
fun HomePageScreen() {
    val viewModel= MainViewModel()

    val banners = remember { mutableStateListOf<SlideModle>() }

    var showBannerLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        viewModel.loadBanner().observeForever{
            banners.clear()
            banners.addAll(it)
            showBannerLoading=false
        }
    }
    ConstraintLayout(modifier = Modifier.background(Color.White)) {
        val (scrollList, bottomMenu) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Welcome Back", color = Color.Black)
                        Text(
                            "Huy",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.search_icon),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(
                        painter = painterResource(R.drawable.bell_icon),
                        contentDescription = null
                    )

                }
            }

            //Banners
            item{
                if (showBannerLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp),
                        contentAlignment = Alignment.Center

                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    Banners(banners)
                }
            }
        }
    }
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun Banners(banners: List<SlideModle>) {
    AutoSlidingCarousel(banners=banners)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier= Modifier.padding(top=16.dp),
    pagerState: PagerState= remember { PagerState() },
    banners: List<SlideModle>
) {
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(count = banners.size, state = pagerState ) { page->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].url)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top=16.dp, bottom = 8.dp)
                    .height(150.dp)
            )
                    }
        DotIndicator(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterHorizontally),
            totalDots = banners.size,
            selectedIndex = if (isDragged)pagerState.currentPage else pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}
@Composable
fun DotIndicator(
    modifier: Modifier= Modifier,
    totalDots:Int,
    selectedIndex:Int,
    selectedColor:Color= colorResource(R.color.purple_200),
    unSelectedColor: Color= colorResource(R.color.grey),
    dotSize: Dp
){
    LazyRow(
        modifier = Modifier
            .wrapContentSize()
    ) {
        items(totalDots){index->
            IndicatorDot(
                color = if (index==selectedIndex)selectedColor else unSelectedColor,
                size = dotSize
            )
            if(index!= totalDots-1){
                Spacer(modifier = Modifier.padding(horizontal = 2.dp) )
            }
        }
    }
}


@Composable
fun IndicatorDot(
    modifier: Modifier= Modifier,
    size:Dp,
    color:Color
){
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}



