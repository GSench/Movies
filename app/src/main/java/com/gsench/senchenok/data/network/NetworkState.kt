package com.gsench.senchenok.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private val networkRequest = NetworkRequest
    .Builder()
    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    .build()

fun isNetworkAvailable(context: Context) : Flow<Boolean> {
    val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    return callbackFlow {
        connectivityManager.requestNetwork(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySendBlocking(true)
            }
            override fun onUnavailable() {
                super.onUnavailable()
                trySendBlocking(false)
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                trySendBlocking(false)
            }
        })
        awaitClose()
    }
}