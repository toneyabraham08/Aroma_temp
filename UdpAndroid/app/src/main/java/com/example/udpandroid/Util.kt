package com.example.udpandroid

import android.content.Context
import android.net.wifi.WifiManager
import java.io.IOException
import java.net.InetAddress

class Util {
    @Throws(IOException::class)
    fun getBroadcastAddress(context:Context?): InetAddress {
        val wifi = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcp = wifi.dhcpInfo
        // handle null somehow
        val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
        val quads = ByteArray(4)
        for (k in 0..3) quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
        return InetAddress.getByAddress(quads)
    }
}