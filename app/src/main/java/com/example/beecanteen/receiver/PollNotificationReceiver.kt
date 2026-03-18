package com.example.beecanteen.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class PollNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("TITLE") ?: "Time to Vote!"
        val message = intent.getStringExtra("MESSAGE") ?: "Your custom reminder to check the canteen polls."
        val notificationId = intent.getIntExtra("NOTIF_ID", 100)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0+ requires a Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "poll_channel",
                "Voting Poll Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when a new poll is available"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, "poll_channel")
            // Make sure you have an icon here!
            // If you don't have one, use android.R.drawable.ic_dialog_info for now
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}