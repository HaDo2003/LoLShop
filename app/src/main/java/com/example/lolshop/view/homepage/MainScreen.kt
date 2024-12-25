package com.example.lolshop.view.homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.lolshop.R
import com.example.lolshop.viewmodel.homepage.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.material.CircularProgressIndicator
import com.example.lolshop.model.Banner
import com.example.lolshop.model.Category
import com.example.lolshop.model.Product
import com.example.lolshop.view.BaseActivity

import androidx.compose.runtime.getValue

import com.example.lolshop.view.admin.AdminActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainScreen : BaseActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val uid = intent.getStringExtra("uid").toString()
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        Log.d("uid", uid)
        Log.d("isAdmin", isAdmin.toString())
        setContent {
            HomePageScreen(
                isAdmin = isAdmin,
                uid,
                onCartClick = {
                    val intent = Intent(this, CartActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    startActivity(intent)
                },
                onProfileClick = {
                    val intent = Intent(this, UserProfile::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    startActivity(intent)
                },
                onAdminClick = {
                    val intent = Intent(this, AdminActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                        startActivity(intent)
                },
                onHomeClick = {

                }
            )
        }
    }
}

@Composable
fun HomePageScreen(
    isAdmin: Boolean,
    uid: String,
    onCartClick:()-> Unit,
    onProfileClick:() -> Unit,
    onAdminClick: () -> Unit,
    onHomeClick:() -> Unit
) {
    val viewModel= MainViewModel()
    val currentScreen = "homepage"
    val banners = remember { mutableStateListOf<Banner>() }
    val categories = remember { mutableStateListOf<Category>() }
    val Popular = remember { mutableStateListOf<Product>() }
    var userName = remember { mutableStateOf("Loading...") }

    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember {mutableStateOf(true)}
    var showPopularLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("Users").document(uid)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userName.value = document.getString("full_name") ?: "Unknown User"
                } else {
                    userName.value = "User not found"
                }
            }
            .addOnFailureListener {
                userName.value = "Error fetching user"
            }
    }

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
                            userName.value,
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

            //Region
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
                    CategoryList(categories, uid, isAdmin)
                }
            }

            //Popular Items
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
                    ListProduct(Popular, uid, isAdmin)
                }
            }
        }

        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomMenu){
                    bottom.linkTo(parent.bottom)
                },
            isAdmin = isAdmin,
            onItemClick = onCartClick,
            onProfileClick = onProfileClick,
            onAdminClick = onAdminClick,
            onHomeClick = onHomeClick,
            currentScreen = currentScreen
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
            color= Color.Black
        )
    }
}