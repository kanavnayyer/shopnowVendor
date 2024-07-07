package com.awesome.shopnowvendor.UI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.awesome.shopnowvendor.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class VendorUploadActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var categorySpinner: Spinner
    private var selectedImageUri: Uri? = null
    private lateinit var productImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor_upload)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        categorySpinner = findViewById(R.id.categorySpinner)
        val categories = arrayOf("Electronics", "Clothing", "Books", "Home & Kitchen")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        productImageView = findViewById(R.id.productImage)

        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        val viewProductsButton: Button = findViewById(R.id.btnViewProducts)
        viewProductsButton.setOnClickListener {
            val intent = Intent(this, VendorProductListActivity::class.java)
            startActivity(intent)
        }



        val uploadButton: Button = findViewById(R.id.uploadButton)
        uploadButton.setOnClickListener {
            val productName = findViewById<EditText>(R.id.productName).text.toString()
            val productDescription = findViewById<EditText>(R.id.productDescription).text.toString()
            val productPrice = findViewById<EditText>(R.id.productPrice).text.toString().toDoubleOrNull()
            val category = categorySpinner.selectedItem.toString()

            if (productName.isNotEmpty() && productDescription.isNotEmpty() && productPrice != null) {
                checkIfProductExists(productName, productDescription, productPrice, category)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            productImageView.setImageURI(selectedImageUri)
        }
    }

    private fun getSelectedImageUri(): Uri {
        return selectedImageUri ?: throw IllegalStateException("Image not selected")
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    private fun checkIfProductExists(productName: String, productDescription: String, productPrice: Double, category: String) {
        getVendorIdFromFirestore { vendorId ->
            if (vendorId != null) {
                firestore.collection("products")
                    .whereEqualTo("vendorId", vendorId)
                    .whereEqualTo("name", productName)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // Product with the same name doesn't exist, proceed to upload
                            val productImageUri: Uri = getSelectedImageUri()
                            uploadProductToStorage(productName, productDescription, productPrice, productImageUri,category, vendorId)


                        } else {
                            // Product with the same name already exists
                            Toast.makeText(this, "Product with this name already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Handle case where vendorId is null (e.g., user data not found)
                Toast.makeText(this, "Vendor ID not found for current user", Toast.LENGTH_SHORT).show()
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
                callback(vendorId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }



    private fun uploadProductToStorage(
        productName: String,
        productDescription: String,
        productPrice: Double,
        productImageUri: Uri,
        category: String,
        vendorId: String // Add vendorId parameter
    ) {
        val storageRef = FirebaseStorage.getInstance().reference.child("product_images/${productImageUri.lastPathSegment}")

        storageRef.putFile(productImageUri)
            .addOnSuccessListener { uploadTask ->
                // Get the download URL for the uploaded image
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Generate a new document reference with a unique ID
                    val productDocRef = firestore.collection("products").document()
                    val productId = productDocRef.id


                    val product = hashMapOf(
                        "id" to productId,
                        "name" to productName,
                        "description" to productDescription,
                        "price" to productPrice,
                        "imageUrl" to uri.toString(),

                        "category" to category,
                        "vendorID" to vendorId,
                    )

                    // Write the product data to Firestore
                    productDocRef.set(product)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Product uploaded successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
