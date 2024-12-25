package com.example.lolshop.Helper

import android.content.Context
import android.widget.Toast
import com.example.lolshop.model.Product

class ManagementCart(private val context: Context) {

    private val tinyDB = TinyDB(context)

    // Insert an item into the cart
    fun insertItem(item: Product) {
        val cartList = getListCart()
        val existingItemIndex = cartList.indexOfFirst { it.id == item.id }

        if (existingItemIndex != -1) {
            // If the item already exists in the cart, update the quantity
            cartList[existingItemIndex].numberInCart += item.numberInCart
        } else {
            // Add a new item to the cart
            cartList.add(item)
        }

        tinyDB.putListObject("CartList", cartList)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    // Retrieve the list of items in the cart
    fun getListCart(): ArrayList<Product> {
        return tinyDB.getListObject("CartList", Product::class.java) ?: arrayListOf()
    }

    // Decrease the quantity of an item in the cart or remove it if the quantity is 1
    fun minusItem(cartList: ArrayList<Product>, position: Int, listener: ChangeNumberItemsListener) {
        if (cartList[position].numberInCart > 1) {
            cartList[position].numberInCart--
        } else {
            cartList.removeAt(position)
        }

        tinyDB.putListObject("CartList", cartList)
        listener.onChanged()
    }

    // Increase the quantity of an item in the cart
    fun plusItem(cartList: ArrayList<Product>, position: Int, listener: ChangeNumberItemsListener) {
        cartList[position].numberInCart++
        tinyDB.putListObject("CartList", cartList)
        listener.onChanged()
    }

    // Calculate the total fee of items in the cart
    fun getTotalFee(): Double {
        return getListCart().sumOf { it.price.toDouble() * it.numberInCart }
    }
}
