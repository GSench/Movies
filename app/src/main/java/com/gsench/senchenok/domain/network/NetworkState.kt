package com.gsench.senchenok.domain.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

enum class NetworkState {
    UNDEFINED,
    AVAILABLE,
    UNAVAILABLE,
}

private val networkRequest = NetworkRequest
    .Builder()
    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    .build()

fun networkStateFlow(context: Context) : Flow<NetworkState> {
    val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    return callbackFlow {
        trySendBlocking(NetworkState.UNDEFINED)
        connectivityManager.requestNetwork(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySendBlocking(NetworkState.AVAILABLE)
            }
            override fun onUnavailable() {
                super.onUnavailable()
                trySendBlocking(NetworkState.UNAVAILABLE)
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                trySendBlocking(NetworkState.UNAVAILABLE)
            }
        })
        awaitClose()
    }
}