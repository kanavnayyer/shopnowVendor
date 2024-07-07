package com.awesome.shopnowvendor.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.awesome.shopnowvendor.Model.Product
import com.awesome.shopnowvendor.R
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ProductAdapter(
    private val products: MutableList<Product>,
    private val onProductDeleted: () -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)

        init {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = products[position]
                    showDeleteConfirmationDialog(product, itemView.context)
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = product.price.toString()
        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    private fun showDeleteConfirmationDialog(product: Product, context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteProduct(product, context)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteProduct(product: Product, context: Context) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("products").document(product.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
                products.remove(product)
                notifyDataSetChanged()
                onProductDeleted()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
