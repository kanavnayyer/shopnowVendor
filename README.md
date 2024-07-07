ShopNow Vendor App

Welcome to ShopNow Vendor App! This app enables vendors to manage their products and interact with customers seamlessly.

Dependencies

To ensure the app functions correctly, make sure you have the following dependencies and plugins added to your build.gradle.kts file:

kotlin
Copy code
plugins {
    id("kotlin-android")
    id("kotlin-kapt") // Kotlin Annotation Processing Plugin
}

dependencies {
    implementation("com.google.firebase:firebase-messaging:22.0.0") // Replace with the latest version
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.firebase:firebase-storage:20.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0") // Lifecycle ViewModel
    implementation("com.google.android.material:material:1.5.0") // Material Components
}

Usage

Firebase Messaging: This dependency is crucial for sending real-time notifications to users about orders and updates.

Glide: Glide is used for efficient image loading and caching within the app.

Glide Compiler: The Glide annotation processor is used to generate Glide's API.

Firebase Storage: Firebase Storage is utilized for storing and retrieving product data securely.

Lifecycle ViewModel: Provides a lifecycle-aware ViewModel for your app's UI controllers.

Material Components: Offers a variety of UI components and styles for a modern Android app.


Kotlin Kapt Plugin
Make sure you have the Kotlin Annotation Processing Plugin (kotlin-kapt) applied in your build.gradle.kts file to enable annotation processing for libraries like Glide.

User App
If you're interested in exploring the user side of ShopNow, you can find the user app code here.              https://github.com/kanavnayyer/shopnowuser/tree/master

Feel free to explore and contribute to the user side of our platform!

