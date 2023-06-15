package app.dev.pre_trialgalaxy.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.dev.pre_trialgalaxy.R

/*
This class handles the notifications and the channel through which they are sent.
 */
class NotificationManager(private val context: Context) {

    private val channelId = "primary_notification_channel"
    private val channelName = "Primary Notification Channel"
    private val channelDescription = "This channel shall handle all the prompt-notifications for the user"

    /*
    This function sends out the notification to ask the user about his current affect.
     */
    fun promptNotification(notificationTimeId: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        }

        createNotificationChannel()

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        notificationIntent.putExtra("NotificationTimeId", notificationTimeId)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context,
            notificationTimeId.toInt(), notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Zeit zu interagieren!")
            .setContentText("Wie fühlst du dich gerade?")
            .setSmallIcon(R.drawable.happy)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationTimeId.toInt(), notification.build())
        }
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = channelDescription
        }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}