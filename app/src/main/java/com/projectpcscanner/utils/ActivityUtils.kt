package com.projectpcscanner.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlin.system.exitProcess

fun setActivityFullScreen(activity: Activity) {
    if(activity.requestWindowFeature(Window.FEATURE_NO_TITLE))
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
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