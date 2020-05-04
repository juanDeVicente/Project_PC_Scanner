package com.projectpcscanner.utils

import android.app.Activity
import android.view.Window
import android.view.WindowManager

fun setActivityFullScreen(activity: Activity) {
    activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
    activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}