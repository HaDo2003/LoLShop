package com.example.lolshop.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lolshop.model.Category
import com.example.lolshop.model.CategoryModel
import com.example.lolshop.model.SlideModle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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
}