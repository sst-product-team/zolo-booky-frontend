package com.example.test.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.globalContexts.Constants.isAccepted
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.test.tabs.TabBorrowed

const val channelId="notification_channel"
const val channelName="com.example.test"
class NotificationService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("notty","hello world")
        if(remoteMessage.notification != null) {
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("New Token generated: ", token)
    }

    private fun getRemoteView(title: String, message: String): RemoteViews{
        val remoteView = RemoteViews("com.example.test", R.layout.notification)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.logo)

        return remoteView
    }

    fun generateNotification(title: String, message: String) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        intent.putExtra("notification_type", title)
        Log.d("ryva1",title)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // channel id, channel name

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .setBigContentTitle(title)
            .bigText(message)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setStyle(bigTextStyle)

       // builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val x = getFirstThreeWords(title)
        Log.d("ryva2",x)

        when (getFirstThreeWords(title)) {
            "Request accepted" -> {
                Log.d("ryva3","detected")
                isAccepted = true

                val intent = Intent("com.example.test.RELOAD_ACTION")
                applicationContext.sendBroadcast(intent)

                val intent2 = Intent("com.example.test.RELOAD_SEARCH")
                applicationContext.sendBroadcast(intent2)
            }

            "Book request" -> {
                Log.d("ryva4","detected")
                isAccepted = true

                val intent = Intent("com.example.test.RELOAD_YOURBOOKS")
                applicationContext.sendBroadcast(intent)

                val intent2 = Intent("com.example.test.RELOAD_OWNERINFO")
                applicationContext.sendBroadcast(intent2)
            }
            "New BookAlert!!" -> {
                Log.d("ryva3","detected")


                val intent = Intent("com.example.test.RELOAD_SEARCH")
                applicationContext.sendBroadcast(intent)
            }


        }

        when(getLastTwoWords(title)){
            "return completed." -> {
                Log.d("ryva5","detected")
                isAccepted = true

                val intent = Intent("com.example.test.RELOAD_YOURBOOKS")
                applicationContext.sendBroadcast(intent)

                val intent2 = Intent("com.example.test.RELOAD_SEARCH")
                applicationContext.sendBroadcast(intent2)
            }

            "book recieved." -> {
                Log.d("ryva6","detected")
                isAccepted = true

                val intent2 = Intent("com.example.test.RELOAD_SEARCH")
                applicationContext.sendBroadcast(intent2)
            }
        }

        notificationManager.notify(0, builder.build())

    }

    private fun getFirstThreeWords(title: String): String {
        val words = title.split(" ")
        return if (words.size >= 2) {
            "${words[0]} ${words[1]}"
        } else {
            title
        }
    }
    private fun getLastTwoWords(title: String): String {
        val words = title.split(" ")
        return if (words.size >= 2) {
            "${words[words.size - 2]} ${words[words.size - 1]}"
        } else {
            title
        }
    }
}