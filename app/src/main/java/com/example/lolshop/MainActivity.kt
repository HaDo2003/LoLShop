package com.example.lolshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lolshop.ui.theme.LoLShopTheme
import com.example.lolshop.model.Product

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoLShopTheme {
                // Hiển thị danh sách sản phẩm trong Compose
                ProductList(products = getProductList())
            }
        }
    }

    // Danh sách sản phẩm mẫu
    private fun getProductList(): List<Product> {
        return listOf(
            Product("Banner", "", R.drawable.banner),
            Product("Running Shoes", "$99", R.drawable.shoe1),
            Product("Sports Jacket", "$79", R.drawable.jacket1),
            Product("Backpack", "$59", R.drawable.bag1)
        )
    }
}

@Composable
fun ProductList(products: List<Product>) {
    // Sử dụng LazyColumn để hiển thị danh sách sản phẩm
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Lặp qua danh sách sản phẩm và hiển thị từng item
        items(products) { product ->
            ProductItem(product = product)
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Hiển thị hình ảnh sản phẩm
        Image(
            painter = painterResource(id = product.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 8.dp)
        )

        // Hiển thị tên sản phẩm
        Text(text = product.name, style = MaterialTheme.typography.bodyLarge)

        // Hiển thị giá sản phẩm
        Text(text = product.price, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoLShopTheme {
        ProductList(products = listOf(
            Product("Banner", "", R.drawable.banner),
            Product("Running Shoes", "$99", R.drawable.shoe1),
            Product("Sports Jacket", "$79", R.drawable.jacket1),
            Product("Backpack", "$59", R.drawable.bag1)
        ))
    }
}
