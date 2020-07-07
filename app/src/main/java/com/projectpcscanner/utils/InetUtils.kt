package com.projectpcscanner.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.io.IOException
import java.net.InetAddress

@Throws(IOException::class)
fun getBroadcastAddress(context: Context): InetAddress? {
    val wifi: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val info = wifi.dhcpInfo
    val broadcast = info.ipAddress and info.netmask or info.netmask.inv()
    val quads = ByteArray(4)
    for (k in 0..3) quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
    return InetAddress.getByAddress(quads)
}