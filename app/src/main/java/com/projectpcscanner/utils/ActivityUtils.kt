package com.projectpcscanner.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.projectpcscanner.R
import kotlin.system.exitProcess


const val channelID = "pc_scanner_server_channel"
var notificationManager: NotificationManager? = null

//FIXME En algunos dispositivos, el fullscreen hace que las animaciones se vean raras
fun setActivityFullScreen(activity: Activity) {
    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    activity.window. statusBarColor = ContextCompat.getColor(activity, R.color.dark)
}

fun exitApplication(activity: Activity) {
    activity.finishAffinity()
    exitProcess(0)
}

fun openWebNavigator(activity: Activity, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setPackage("com.android.chrome")
    try {
        activity.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        intent.setPackage(null)
        activity.startActivity(intent)
    }

}

private fun createNotificationChannel(activity: Activity) {
    synchronized(activity::class.java){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = activity.getString(R.string.app_name)// The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance)
            notificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(channel)
        }
    }
}

fun createNotification(activity: Activity, contentTitle: String, contentText: String) {
    if (notificationManager == null)
        createNotificationChannel(activity)

    val notification = NotificationCompat.Builder(activity, channelID)
        .setSmallIcon(R.drawable.logo_foreground)
        .setLargeIcon(BitmapFactory.decodeResource(activity.resources, R.drawable.logo_foreground))
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setChannelId(channelID).build()

    notificationManager!!.notify(1, notification)
}

fun lockRotation(activity: Activity, value: Boolean) {
    if (value)
    {
        val currentLocation = activity.resources.configuration.orientation
        if (currentLocation == Configuration.ORIENTATION_LANDSCAPE)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        else
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }
    else
    {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
    }
}