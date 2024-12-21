package com.example.lolshop.utils

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ChangeField {
    fun changeField() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val adminValue = document.getBoolean("admin")
                    if (adminValue != null) {
                        // Update the field from "admin" to "isAdmin"
                        val updates = mapOf(
                            "isAdmin" to adminValue,
                            "admin" to FieldValue.delete() // Remove the old "admin" field
                        )
                        firestore.collection("Users").document(document.id).update(updates)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Updated document: ${document.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error updating document: ${document.id}", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching documents", e)
            }
    }
}