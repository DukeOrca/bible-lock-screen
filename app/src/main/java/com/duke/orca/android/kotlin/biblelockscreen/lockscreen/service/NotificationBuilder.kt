package com.duke.orca.android.kotlin.biblelockscreen.lockscreen.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.main.view.MainActivity
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationBuilder {
    const val ID = 21522224

    private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.lockscreen.service"
    private const val OBJECT_NAME = "NotificationBuilder"
    private const val CHANNEL_ID = "$PACKAGE_NAME.$OBJECT_NAME.CHANNEL_ID"

    fun single(context: Context, notificationManager: NotificationManager): Single<NotificationCompat.Builder> {
        return Single.create {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.app_name)
                val importance = NotificationManager.IMPORTANCE_MIN
                val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)

                notificationChannel.setShowBadge(false)
                notificationManager.createNotificationChannel(notificationChannel)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            CoroutineScope(Dispatchers.IO).launch {
                val smallIcon = R.drawable.ic_holy_bible_overlay_96px
                val color = ContextCompat.getColor(context, R.color.notification)
                val contentTitle = context.getString(R.string.app_name)
                val contentText = context.getString(R.string.bible_lock_screen_is_running)

                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(smallIcon)
                    .setColor(color)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setShowWhen(false)
                it.onSuccess(builder)
            }
        }
    }

    object ManageOverlayPermission {
        const val ID = 21536631

        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.lockscreen.service"
        private const val OBJECT_NAME = "ManageOverlayPermission"
        private const val CHANNEL_ID = "$PACKAGE_NAME.$OBJECT_NAME.CHANNEL_ID"

        fun create(context: Context): NotificationCompat.Builder {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val name = context.getString(R.string.app_name)
                val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)

                notificationChannel.setShowBadge(true)
                notificationManager.createNotificationChannel(notificationChannel)
            }

            val uri = Uri.fromParts("package", context.packageName, null)

            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val contentTitle = context.getString(R.string.request_app_permissions)
            val contentText = context.getString(R.string.allow_appear_on_top_permission)
            val color = ContextCompat.getColor(context, R.color.deep_orange_400)

            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mobile_48px)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setColor(color)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setShowWhen(true)
        }
    }
}