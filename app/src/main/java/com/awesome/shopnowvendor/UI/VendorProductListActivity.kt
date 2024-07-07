package com.awesome.shopnowvendor.UI

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awesome.shopnowvendor.Model.Product
import com.awesome.shopnowvendor.Adapter.ProductAdapter
import com.awesome.shopnowvendor.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class VendorProductListActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: ProductAdapter
    private val products = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_product_list)

        firestore = FirebaseFirestore.getInstance()

        adapter = ProductAdapter(products) {
            loadVendorProducts()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadVendorProducts()
    }

    private fun loadVendorProducts() {
        getVendorIdFromFirestore { vendorId ->
            if (vendorId != null) {
                firestore.collection("products")
                    .whereEqualTo("vendorID", vendorId)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val productsList = mutableListOf<Product>()
                            for (document in task.result!!) {
                                val product = document.toObject(Product::class.java)
                                productsList.add(product)
                            }
                            products.clear()
                            products.addAll(productsList)
                            adapter.notifyDataSetChanged()
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.exception)
                        }
                    }
            } else {
                // Handle case where vendorId is null (e.g., user data not found)
                Log.e("Firestore", "Vendor ID not found for current user")
            }
        }
    }



    private fun getVendorIdFromFirestore(callback: (String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Check if userId is null (user not logged in)
        if (userId == null) {
            callback(null)
            return
        }

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val vendorId = userDoc.getString("vendorId")
                if (vendorId != null) {
                    callback(vendorId)
                } else {
                    Log.e("Firestore", "Vendor ID not found for user: $userId")
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching vendor ID: ${e.message}", e)
                callback(null)
            }
    }


}
