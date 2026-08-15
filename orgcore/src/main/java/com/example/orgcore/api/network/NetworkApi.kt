package com.example.orgcore.api.network

import com.example.orgcore.api.network.data.NetworkConnection
import com.example.orgcore.internal.ServicesProvider
import kotlinx.coroutines.flow.Flow

object NetworkApi {


    /**
     * Call this to check if the device can make network operation successfully
     *
     * @return true if the device connected to a stable network connection
     */
    fun isConnected(): Boolean{
        return ServicesProvider.getNetworkMonitor().isConnected()
    }

    /**
     * Call this to get the network status
     *
     * @return instance of  [NetworkConnection]
     *
     * @sample
     */
    suspend fun getNetworkStatus(): NetworkConnection{
        return ServicesProvider.getNetworkMonitor().getNetworkStatus()
    }

    /**
     * Call this to listen to the network status changes
     *
     * @return instance of [NetworkConnection]
     */
    fun listenToNetworkChanges(): Flow<NetworkConnection>{
        return ServicesProvider.getNetworkMonitor().listenToNetworkChanges()
    }
}