import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("VendorApp", "Message received from: ${remoteMessage.from}")

        // Check if message contains a data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d("VendorApp", "Message data payload: " + remoteMessage.data)

            // Handle your custom data here, e.g., extract vendorId
            val vendorId = remoteMessage.data["vendorId"]
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]

            // Handle notification display or data processing
            // Example: Show notification in the vendor app
            // NotificationUtils.showNotification(this, title, body)
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d("VendorApp", "Message Notification Body: ${it.body}")
            // Handle notification display if needed
            // Example: Show notification in the vendor app
            // NotificationUtils.showNotification(this, it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("VendorApp", "Refreshed token: $token")
        // If you want to send notifications to this device, save the token and send it to your server
        // Implement your logic here
    }
}
