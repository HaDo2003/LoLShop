package com.example.lolshop.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lolshop.model.Category
import com.example.lolshop.model.CategoryModel
import com.example.lolshop.model.ProductModel
import com.example.lolshop.model.SlideModle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class MainRepository {
    private val firebaseDatabase= FirebaseDatabase.getInstance()

    fun loadBanner(): LiveData<MutableList<SlideModle>>{
        val listData= MutableLiveData<MutableList<SlideModle>>()
        val ref= firebaseDatabase.getReference("Banner")

        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists=mutableListOf<SlideModle>()
                for(childSnapshot in snapshot.children){
                    val item=childSnapshot.getValue(SlideModle::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        val listData= MutableLiveData<MutableList<CategoryModel>>()
        val ref= firebaseDatabase.getReference("Category")

        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists=mutableListOf<CategoryModel>()
                for(childSnapshot in snapshot.children){
                    val item=childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { lists.add(it) }
                }
                listData.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadPopular(): LiveData<MutableList<ProductModel>>{
        val listData= MutableLiveData<MutableList<ProductModel>>()
        val ref= firebaseDatabase.getReference("Product")
        val query: Query=ref.orderByChild("showRecommended").equalTo(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists= mutableListOf<ProductModel>()
                for (childSnapshot in snapshot.children){
                    val list=childSnapshot.getValue(ProductModel::class.java)
                    if(list!=null){
                        lists.add(list)
                    }
                }
                listData.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadFilterd(id: String): LiveData<MutableList<ProductModel>>{
        val listData= MutableLiveData<MutableList<ProductModel>>()
        val ref= firebaseDatabase.getReference("categoryId")
        val query: Query=ref.orderByChild("showRecommended").equalTo(id)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists= mutableListOf<ProductModel>()
                for (childSnapshot in snapshot.children){
                    val list=childSnapshot.getValue(ProductModel::class.java)
                    if(list!=null){
                        lists.add(list)
                    }
                }
                listData.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }


}