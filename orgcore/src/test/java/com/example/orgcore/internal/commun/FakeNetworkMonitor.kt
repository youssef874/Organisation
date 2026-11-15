package com.example.orgcore.internal.commun

import com.example.orgcore.api.network.data.NetworkConnection
import com.example.orgcore.api.network.data.NetworkStatus
import com.example.orgcore.internal.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeNetworkMonitor(var isConnectedVal: Boolean) : NetworkMonitor {
    override fun isConnected(): Boolean = isConnectedVal
    override fun getNetworkStatus(): NetworkConnection = NetworkConnection(
        connectionStatus = if (isConnectedVal) NetworkStatus.GOOD_CONNECTION else NetworkStatus.NOT_CONNECTED
    )
    override fun listenToNetworkChanges(): Flow<NetworkConnection> = flowOf(getNetworkStatus())
}
