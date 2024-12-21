package com.example.lolshop.Helper

import android.content.Context
import android.widget.Toast
import com.example.lolshop.model.Product
import com.example.project1762.Helper.ChangeNumberItemsListener

class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItem(item: Product) {
        var listItem = getListCart()
        val existAlready = listItem.any { it.name == item.name }
        val index = listItem.indexOfFirst { it.name == item.name }

        if (existAlready) {
            listItem[index].numberInCart = item.numberInCart
        } else {
            listItem.add(item)
        }
        tinyDB.putListObject("CartList", listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<Product> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun minusItem(listItem: ArrayList<Product>, position: Int, listener: ChangeNumberItemsListener) {
        if (listItem[position].numberInCart == 1) {
            listItem.removeAt(position)
        } else {
            listItem[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listItem)
        listener.onChanged()
    }

    fun plusItem(listItem: ArrayList<Product>, position: Int, listener: ChangeNumberItemsListener) {
        listItem[position].numberInCart++
        tinyDB.putListObject("CartList", listItem)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        for (item in listItem) {
            fee += item.price * item.numberInCart
        }
        return fee
    }
}