package com.projectpcscanner.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.projectpcscanner.R
import kotlin.system.exitProcess
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