package com.example.orgcore.api.network.data

data class NetworkConnection(
    val connectionStatus: @NetworkStatus Int = NetworkStatus.NOT_CONNECTED,
    val isVpnActive: Boolean = false
)
