package com.example.lolshop.view.homepage

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.lolshop.R
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
import com.example.lolshop.view.BaseActivity
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue

import androidx.core.content.ContextCompat.startActivity
class MainScreen : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            HomePageScreen{
                startActivity(Intent(this, CartActivity::class.java))
            }
        }
    }
}

@Composable
fun HomePageScreen(onCartClick:()-> Unit) {
    val viewModel= MainViewModel()

    val banners = remember { mutableStateListOf<Banner>() }
    val categories = remember { mutableStateListOf<Category>() }
    val Popular = remember { mutableStateListOf<Product>() }


    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember {mutableStateOf(true)}
    var showPopularLoading by remember { mutableStateOf(true) }

    //Banner
    LaunchedEffect(Unit) {
        viewModel.loadBanner().observeForever{
            banners.clear()
            banners.addAll(it)
            showBannerLoading=false
        }
    }

    //category
    LaunchedEffect(Unit) {
        viewModel.loadCategory().observeForever{
            categories.clear()
            categories.addAll(it)
            showCategoryLoading=false
        }
    }

    //Popular
    LaunchedEffect(Unit) {
        viewModel.loadPopular().observeForever{
            Popular.clear()
            Popular.addAll(it)
            showPopularLoading=false
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
                            .height(250.dp),
                        contentAlignment = Alignment.Center

                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    Banners(banners)
                }
            }

            item{
                Text(
                    text="Region",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                        .padding(horizontal = 16.dp)
                )

            }
            item{
                if (showCategoryLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }else{
                    CategoryList(categories)
                }
            }
            item{
                SectionTitLe("Most Popular", "See All")
            }
            item{
                if(showPopularLoading){
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    ListProduct(Popular)
                }
            }
        }

        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomMenu){
                    bottom.linkTo(parent.bottom)
                },
            onItemClick = onCartClick
        )
    }
}

@Composable
fun CategoryList(categories: SnapshotStateList<Category>) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val context = LocalContext.current

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(
                item = categories[index],
                isSelected = selectedIndex == index,
                onItemClick = {
                    selectedIndex = index
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(context, ListProductActivity::class.java).apply {
                            putExtra("id", categories[index].id.toString())
                            putExtra("title", categories[index].name)
                        }
                        startActivity(context,intent,null)
                    }, 500)
                }
            )
        }
    }
}


@Composable
fun CategoryItem(item: Category, isSelected: Boolean, onItemClick: () -> Unit) {
    val backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary

    Column(
        modifier = Modifier
            .clickable(onClick = onItemClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 120.dp else 100.dp) // Outer size of the circular frame
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
                .padding(5.dp) // Adjusted padding for making the picture smaller
        ) {
            if (item.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .padding(10.dp), // Reduce picture size further inside the circle
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "No Image",
                    style = MaterialTheme.typography.body2,
                    color = textColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Display category name below the image
        Text(
            text = item.name,
            style = MaterialTheme.typography.body1,
            color = textColor,
            modifier = Modifier.padding(top = 8.dp) // Add spacing between image and text
        )
    }
}


@Composable
fun SectionTitLe(title: String, actionText: String) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text=title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text= actionText,
            color=colorResource(R.color.purple_200)
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Banners(banners: SnapshotStateList<Banner>) {
    AutoSlidingCarousel(banners =banners)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier= Modifier.padding(top=16.dp),
    pagerState: PagerState= remember { PagerState() },
    banners: SnapshotStateList<Banner>
) {
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(count = banners.size, state = pagerState ) { page->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].imageUrl)
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



@Composable
fun BottomMenu(modifier: Modifier = Modifier, onItemClick: () -> Unit) {
    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .background(
                colorResource(R.color.purple_700),
                shape = RoundedCornerShape(18.dp)
            ),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomMenuItem(icon = painterResource(R.drawable.btn_1), text = "Explorer")
        BottomMenuItem(icon = painterResource(R.drawable.btn_2), text = "Cart", onItemClick = onItemClick)
        BottomMenuItem(icon = painterResource(R.drawable.btn_3), text = "Favorite")
        BottomMenuItem(icon = painterResource(R.drawable.btn_4), text = "Order")
        BottomMenuItem(icon = painterResource(R.drawable.btn_5), text = "Profile")
    }
}

@Composable
fun BottomMenuItem(icon: Painter, text: String, onItemClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .height(80.dp) // Increase the height of the item
            .clickable { onItemClick?.invoke() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(33.dp) // Increase the icon size here
        )
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(text, color = Color.White, fontSize = 10.sp)
    }
}



