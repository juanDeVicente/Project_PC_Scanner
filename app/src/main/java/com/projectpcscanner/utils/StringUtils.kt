package com.projectpcscanner.utils

import android.content.Context
import android.widget.TextView
import com.projectpcscanner.R
import java.lang.Exception

fun getStringFromResourceByName(context: Context, name: String): String {
    val packageName: String = context.packageName
    return try {
        context.getString(context.resources.getIdentifier(name, "string", packageName))
    }
    catch (e: Exception) {
        name
    }
}