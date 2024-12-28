package com.example.lolshop.view.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lolshop.R
import com.example.lolshop.model.Order
import com.example.lolshop.model.OrderProduct
import com.example.lolshop.viewmodel.homepage.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageOrderScreen(
    orderViewModel: OrderViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        orderViewModel.fetchOrdersAdmin()
    }

    val adminOrders by orderViewModel.adminOrders.observeAsState(emptyList())
    val error by orderViewModel.error.observeAsState(null)
    val statusOptions = arrayOf(
        "All",
        "Waiting for confirmation",
        "Delivering",
        "Delivered",
        "Cancelled"
    )
    var selectedStatus  by rememberSaveable { mutableStateOf(statusOptions[0]) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val filteredOrders by orderViewModel.filteredOrders.observeAsState(adminOrders)

    LaunchedEffect(Unit) {
        selectedStatus = "All" // Reset to "All"
        orderViewModel.filterOrdersByStatus("All") // Apply the "All" filter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp)
            .background(Color.White)
    ) {
        ConstraintLayout(modifier = Modifier.padding(top = 36.dp)) {
            val (cartTxt) = createRefs()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(cartTxt) { centerTo(parent) },
                text = "All Order",
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (error != null) {
            Text(
                text = "Error: $error",
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        } else if (adminOrders.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 125.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "No orders found.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    OutlinedTextField(
                        value = selectedStatus, // Display the selected status
                        onValueChange = { },
                        readOnly = true, // Make the text field read-only
                        trailingIcon = {
                            TrailingIcon(expanded = expanded) // Default dropdown icon
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .width(150.dp)
                            .height(50.dp)
                            .background(Color.White)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        statusOptions.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(text = statusOption) },
                                onClick = {
                                    selectedStatus = statusOption // Update the selected status
                                    expanded = false // Close the dropdown
                                    orderViewModel.filterOrdersByStatus(selectedStatus)
                                }
                            )
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
                    .background(Color.White)
            ) {
                val sortedFilteredOrders = filteredOrders.sortedByDescending { it.orderDate }

                items(sortedFilteredOrders) { order ->
                    val customerName by orderViewModel.getCustomerName(order.customerId)
                        .collectAsState(initial = null)

                    OrderItem(
                        order = order,
                        customerName = customerName,
                        onConfirm = {
                            orderViewModel.updateOrderStatus(order.orderId, "confirm")
                        },
                        onComplete = {
                            orderViewModel.updateOrderStatus(order.orderId, "complete")
                        },
                        onCancel = {
                            orderViewModel.updateOrderStatus(order.orderId, "cancel")
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItem(
    order: Order,
    customerName: String?,
    onConfirm:() -> Unit,
    onComplete:() -> Unit,
    onCancel:() -> Unit,
) {
    val totalQuantity = order.products?.sumOf { it.quantity } ?: 0
    val productList = order.products ?: emptyList()
    var expanded by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors( // Set the card background color
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${order.orderId}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = "Customer: ${customerName ?: "Loading..."}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display products
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Always show first product
                if (productList.isNotEmpty()) {
                    ProductItem(product = productList[0])
                }

                // Show "See more" button if there are additional products
                if (productList.size > 1) {
                    TextButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (expanded) "Show less" else "See ${productList.size - 1} more products",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Show remaining products if expanded
                    if (expanded) {
                        productList.drop(1).forEach { product ->
                            ProductItem(product = product)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (order.status == "Completed") Color.Green else Color.Red
                )

                Text(
                    text = "Total(${totalQuantity} products): ${order.totalPriceAtAll}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB22222),
                        contentColor = Color.White
                    )
                ){
                    Text("Cancel order")
                }

                Button(
                    onClick = {
                        when(order.status) {
                            "Wait for confirmation" -> onConfirm()
                            "Delivering" -> onComplete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF388E3C),
                        contentColor = Color.White
                    )
                ) {
                    Text("Change status")
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: OrderProduct,
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .padding(3.dp)
    ) {
        val (pic, name, quantity, price) = createRefs()
        Image(
            painter = rememberAsyncImagePainter(product.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(pic) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            contentScale = ContentScale.Crop
        )
        Text(
            text = product.name,
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(pic.end)
                    top.linkTo(parent.top)
                }
                .padding(start = 8.dp)
        )
        Text(
            text = "x${product.quantity}",
            modifier = Modifier
                .constrainAs(quantity) {
                    start.linkTo(name.end)
                    top.linkTo(parent.top)
                }
                .padding(start = 100.dp)
        )

        Text(
            text = "$${product.price}",
            color = colorResource(R.color.black),
            modifier = Modifier
                .constrainAs(price) {
                    start.linkTo(pic.end)
                    top.linkTo(quantity.bottom)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
    }
}
