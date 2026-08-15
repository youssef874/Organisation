package com.example.orgcore.internal.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import com.example.orgcore.commun.LoggerHelper
import com.example.orgcore.api.network.data.NetworkConnection
import com.example.orgcore.api.network.data.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * Implementation of [NetworkMonitor] using [ConnectivityManager]
 */
internal class ConnectivityManagerNetworkMonitor(private val context: Context) : NetworkMonitor {

    companion object {
        private const val CLASS_NAME = "ConnectivityManagerNetworkMonitor"
        private const val TAG = "NETWORK"
        private const val NETWORK_METRIC = 500
    }

    private fun getConnectivityManager(context: Context) =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isConnected(): Boolean {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|isConnected| checking internet connectivity"
        )
        val connMgr = getConnectivityManager(context)
        val caps = connMgr.getNetworkCapabilities(connMgr.activeNetwork)
        return caps?.let {
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ||
                            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED))
        } ?: false
    }

    override fun getNetworkStatus(): NetworkConnection {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|getNetworkStatus| getting current network status"
        )
        val connMgr = getConnectivityManager(context)
        val caps = connMgr.getNetworkCapabilities(connMgr.activeNetwork)

        return caps?.let { extractNetworkConnection(it) } ?: NetworkConnection()
    }

    override fun listenToNetworkChanges(): Flow<NetworkConnection> {
        val connectivityManager = getConnectivityManager(context)
        return callbackFlow {
            LoggerHelper.logger.i(
                TAG,
                "$CLASS_NAME|listenToNetworkChanges| listening to network changes"
            )
            val callback = object : NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                    LoggerHelper.logger.d(TAG, "$CLASS_NAME|onCapabilitiesChanged")
                    if (network == connectivityManager.activeNetwork) {
                        trySend(extractNetworkConnection(caps))
                    }
                }

                override fun onLost(network: Network) {
                    LoggerHelper.logger.d(TAG, "$CLASS_NAME|onLost")
                    trySend(getNetworkStatus())
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            trySend(getNetworkStatus())

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

    private fun extractNetworkConnection(caps: NetworkCapabilities): NetworkConnection {
        return NetworkConnection(
            connectionStatus = calculateStatus(caps),
            isVpnActive = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        ).also {
            LoggerHelper.logger.d(
                TAG,
                "$CLASS_NAME|extractNetworkConnection|result: $it"
            )
        }
    }

    private fun calculateStatus(
        caps: NetworkCapabilities
    ): @NetworkStatus Int {
        val isConnected = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val trusted = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
        val notMetered = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|calculateStatus:isConnected: $isConnected,  isValidated: $isValidated, " +
                    "trusted: $trusted, notMetered: $notMetered"
        )

        if (!isConnected || (!isValidated && !trusted)) {
            return NetworkStatus.NOT_CONNECTED
        }

        if (notMetered && trusted) {
            return NetworkStatus.GOOD_CONNECTION
        }

        return if (caps.linkDownstreamBandwidthKbps >= NETWORK_METRIC) {
            NetworkStatus.GOOD_CONNECTION
        } else {
            NetworkStatus.BAD_CONNECTION
        }
    }
}
