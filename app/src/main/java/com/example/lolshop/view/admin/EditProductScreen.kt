package com.example.lolshop.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lolshop.model.Category
import com.example.lolshop.viewmodel.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    adminViewModel: AdminViewModel,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val product by rememberSaveable{ mutableStateOf(adminViewModel.getProductById(productId)) }
    var name by rememberSaveable { mutableStateOf(product?.name ?: "") }
    var price by rememberSaveable { mutableStateOf(product?.price ?: "") }
    var description by rememberSaveable { mutableStateOf(product?.description ?: "") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val categoryOptions = listOf(
        Category("-OE4s6JDMNBmybnPHxzj", "LCK"),
        Category("-OE4tac7kwwSFADLtfoG", "LPL"),
        Category("-OE4tefxh5HJh8UgGyU5", "VCS"),
        Category("-OE4tiYv8OMefiXdZ-el", "LEC"),
        Category("-OE4tmqfq9DWVUO-5Kpm", "PCS"),
        Category("-OE4ts0w9YaDnfT2UM_-", "LCS")
    )
    var categoryId by rememberSaveable {
        mutableStateOf(
            categoryOptions.find { it.id == product?.categoryId } ?: categoryOptions.firstOrNull()
        )
    }

    val recommendOption = arrayOf("Unrecommended", "Recommended")
    var showRecommended by rememberSaveable {
        mutableStateOf(product?.showRecommended ?: false)
    }
    var isRecommended by rememberSaveable {
        mutableStateOf(if (showRecommended) recommendOption[1] else recommendOption[0])
    }
    var expanded1 by rememberSaveable { mutableStateOf(false) }
    val imageUrl by rememberSaveable { mutableStateOf<String>(product?.imageUrl ?: "") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit Product", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            categoryId?.let {
                OutlinedTextField(
                    value = it.name,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoryOptions.forEach { categoryItem ->
                    DropdownMenuItem(
                        text = { Text(text = categoryItem.name) },
                        onClick = {
                            categoryId = categoryItem
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded1,
            onExpandedChange = { expanded1 = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = isRecommended,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded1)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded1,
                onDismissRequest = { expanded1 = false }
            ) {
                recommendOption.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            isRecommended = option
                            showRecommended = option == "Recommended"
                            expanded1 = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
                categoryId?.let {
                    adminViewModel.updateProduct(
                        productId = productId,
                        name = name,
                        categoryId = it.id,
                        price = price,
                        description = description,
                        showRecommended = showRecommended,
                        imageUrl = imageUrl
                    )
                }
            navController.popBackStack()
        }) {
            Text("Save Changes")
        }
    }
}
