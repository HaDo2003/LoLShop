package com.example.lolshop.view.homepage

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lolshop.R
import com.example.lolshop.model.Banner
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Banners(banners: SnapshotStateList<Banner>) {
    AutoSlidingCarousel(banners =banners)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier.padding(top=16.dp),
    pagerState: PagerState = remember { PagerState() },
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
    modifier: Modifier = Modifier,
    totalDots:Int,
    selectedIndex:Int,
    selectedColor: Color = colorResource(R.color.purple_200),
    unSelectedColor: Color = colorResource(R.color.grey),
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
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
){
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}