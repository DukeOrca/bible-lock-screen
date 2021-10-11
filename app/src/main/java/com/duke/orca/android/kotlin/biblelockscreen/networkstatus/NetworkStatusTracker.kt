package com.duke.orca.android.kotlin.biblelockscreen.networkstatus

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class NetworkStatusTracker(context: Context) {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    @ExperimentalCoroutinesApi
    val networkStatus = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Available)
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Unavailable)
            }

            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}

inline fun <T> Flow<NetworkStatus>.map(
    crossinline onAvailable: suspend () -> T,
    crossinline onUnavailable: suspend () -> T
): Flow<T> = map { status ->
    when(status) {
        NetworkStatus.Available -> onAvailable()
        NetworkStatus.Unavailable -> onUnavailable()
    }
}