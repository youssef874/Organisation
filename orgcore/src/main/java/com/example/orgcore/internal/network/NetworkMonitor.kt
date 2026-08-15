package com.example.orgcore.internal.network

import com.example.orgcore.api.network.data.NetworkConnection
import kotlinx.coroutines.flow.Flow


/**
 * Ths abstraction for network business logic
 */
internal interface NetworkMonitor {

    /**
     * Call this to check if the device can make network operation successfully
     *
     * @return true if the device connected to a stable network connection
     */
    fun isConnected(): Boolean

    /**
     * Call this to get the network status
     *
     * @return see [NetworkConnection]
     */
    fun getNetworkStatus(): NetworkConnection

    /**
     * Call this to listen to the network status changes
     *
     * @return see [NetworkConnection]
     */
    fun listenToNetworkChanges(): Flow<NetworkConnection>
}