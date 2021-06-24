package com.example.oxybite.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.oxybite.MainActivity
import com.example.oxybite.R

const val CHANNEL_ID = "notification_reminder_interval"
const val NOTIFICATION_ID = 1001

class NotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        notification(appContext)
        return Result.success()
    }

    private fun createNotificationChannel(appContext: Context) {
        val name = appContext.getString(R.string.channel_reminder)
        val descriptionText = appContext.getString(R.string.channel_desc_reminder)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = descriptionText

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun notification(appContext: Context) {
        createNotificationChannel(appContext)

        val intent = Intent(appContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.oxybite_icon)
            .setContentTitle(appContext.getString(R.string.notification_title))
            .setContentText(appContext.getString(R.string.notification_text))
            .setDefaults(Notification.DEFAULT_SOUND)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

}