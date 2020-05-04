package com.projectpcscanner.utils

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import kotlin.system.exitProcess

fun setActivityFullScreen(activity: Activity) {
    activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
    activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun exitApplication(activity: Activity) {
    activity.finishAffinity()
    exitProcess(0)
}