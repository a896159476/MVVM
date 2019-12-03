package com.epuxun.mvvm.utli.http

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.epuxun.mvvm.utli.setNetworkConnected

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        //网络已连接
        setNetworkConnected(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        //网络已断开
        setNetworkConnected(false)
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {//网络类型为wifi

            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {//蜂窝网络

            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {//蓝牙

            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {//以太网

            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {//vpn

            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {//Wi-Fi Aware

            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)) {//LoWPAN

            } else { //其他网络

            }
        }
    }

}