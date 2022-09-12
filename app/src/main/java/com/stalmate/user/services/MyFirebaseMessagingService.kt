package com.mechanicforyou.user.FirebaseNotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.stalmate.user.R
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.dashboard.ActivityDashboard


/**
 * Created by Vaibhav
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    var CHANNEL_ID: String = Constants.CHANNEL_ID
    var name: CharSequence = "Product"
    var description = "Notifications"
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("notii", s)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val notificationIntent = Intent(Constants.FILTER_NOTIFICATION_BROADCAST)
        val nData = remoteMessage.data
        if (nData != null && nData.isNotEmpty()) {
            Log.d("Notification", "From: $nData")
            for (key in nData.keys) {
                notificationIntent.putExtra(key, nData[key])
            }
            sendBroadcast(notificationIntent)
        }
        sendNotification(remoteMessage)
    }

    fun sendNotification(remoteMessage: RemoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            notificationManager?.createNotificationChannel(mChannel)
            val intent = Intent(this, ActivityDashboard::class.java)



            if (remoteMessage.data["title"] != null) {
                intent.putExtra("title", remoteMessage.data["title"])
            }
            intent.putExtra("notificationType", remoteMessage.data["notificationType"])

            //   intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP);
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            mBuilder.setSmallIcon(R.drawable.add_friend_icon)
            if (remoteMessage.notification != null) {
                mBuilder.setContentTitle(remoteMessage.notification!!.title)
                mBuilder.setContentText(remoteMessage.notification!!.body)
            } else {
                mBuilder.setContentTitle(remoteMessage.data["title"])
                mBuilder.setContentText(remoteMessage.data["text"])
            }
            mBuilder.color = getColor(R.color.colorLightGray)
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            mBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
            mBuilder.setContentIntent(pendingIntent)
            mBuilder.setCategory(NotificationCompat.CATEGORY_PROMO)
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            mBuilder.setSound(defaultSoundUri)
            mBuilder.setAutoCancel(true)
            val notificationManagerr = NotificationManagerCompat.from(this)
            notificationManagerr.notify(
                SystemClock.currentThreadTimeMillis().toInt(),
                mBuilder.build()
            )
        } else {
            val notificationIntent = Intent(Constants.FILTER_NOTIFICATION_BROADCAST)
            val nData = remoteMessage.data
            if (nData != null && nData.isNotEmpty()) {
                Log.d("Notification", "From: $nData")
                for (key in nData.keys) {
                    notificationIntent.putExtra(key, nData[key])
                }
                Log.d("notiii", "smaller")
                sendBroadcast(notificationIntent)
            }

            // Create an explicit intent for an Activity in your app
            val intent = Intent(this, ActivityDashboard::class.java)
            intent.putExtra("notificationType", remoteMessage.data["notificationType"])
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mBuilder = NotificationCompat.Builder(this)
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
            if (remoteMessage.notification != null) {
                mBuilder.setContentTitle(remoteMessage.notification!!.title)
                mBuilder.setContentText(remoteMessage.notification!!.body)
            } else {
                mBuilder.setContentTitle(remoteMessage.data["title"])
                mBuilder.setContentText(remoteMessage.data["text"])
            }
            mBuilder.color = resources.getColor(R.color.colorLightGray)
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            mBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
            mBuilder.setContentIntent(pendingIntent)
            mBuilder.setCategory(NotificationCompat.CATEGORY_PROMO)
            mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            mBuilder.setSound(defaultSoundUri)
            mBuilder.setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(
                SystemClock.currentThreadTimeMillis().toInt(),
                mBuilder.build()
            )
        }
    }


    fun setupNotificationDataOnNotificationLayout(remoteMessage: RemoteMessage) : Intent{

        var intent=Intent()





        return  intent
    }
}