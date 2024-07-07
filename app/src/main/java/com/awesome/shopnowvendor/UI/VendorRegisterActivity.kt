package com.awesome.shopnowvendor.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.awesome.shopnowvendor.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class VendorRegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            redirectToVendorUpload()
            return
        }

        setContentView(R.layout.activity_main)

        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.email).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid

                        // Generate vendorId
                        val vendorId = UUID.randomUUID().toString()

                        val vendor = hashMapOf(
                            "role" to "vendor",
                            "vendorId" to vendorId
                            // Add more vendor details as needed
                        )

                        if (userId != null) {
                            firestore.collection("users").document(userId).set(vendor)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Vendor registered", Toast.LENGTH_SHORT).show()
                                    // Redirect to VendorUploadActivity
                                    redirectToVendorUpload()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        val tvRedirectLogin: TextView = findViewById(R.id.tvRedirectLogin)
        tvRedirectLogin.setOnClickListener {
            redirectToLogin()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, VendorLoginActivity::class.java)
        startActivity(intent)
    }

    private fun redirectToVendorUpload() {
        val intent = Intent(this, VendorUploadActivity::class.java)
        startActivity(intent)
        finish()
    }
}
