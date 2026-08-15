package com.example.orgcore.internal.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.orgcore.api.network.data.NetworkStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ConnectivityManagerNetworkMonitorTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    @Mock
    private lateinit var networkCapabilities: NetworkCapabilities

    @Mock
    private lateinit var network: Network

    private lateinit var networkMonitor: ConnectivityManagerNetworkMonitor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(context.applicationContext).thenReturn(context)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        networkMonitor = ConnectivityManagerNetworkMonitor(context.applicationContext)
    }

    @Test
    fun `isConnected returns true when network is validated and has internet`() {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)

        assertTrue(networkMonitor.isConnected())
    }

    @Test
    fun `isConnected returns false when network has no internet capability`() {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)

        assertFalse(networkMonitor.isConnected())
    }

    @Test
    fun `getNetworkStatus correctly identifies GOOD_CONNECTION when unmetered`() {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)).thenReturn(true)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)).thenReturn(true)

        val connection = networkMonitor.getNetworkStatus()

        assertEquals(NetworkStatus.GOOD_CONNECTION, connection.connectionStatus)
    }

    @Test
    fun `getNetworkStatus correctly identifies BAD_CONNECTION when bandwidth is low`() {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
        `when`(networkCapabilities.linkDownstreamBandwidthKbps).thenReturn(100)

        val connection = networkMonitor.getNetworkStatus()

        assertEquals(NetworkStatus.BAD_CONNECTION, connection.connectionStatus)
    }

    @Test
    fun `getNetworkStatus correctly identifies VPN active`() {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)).thenReturn(true)

        val connection = networkMonitor.getNetworkStatus()

        assertTrue(connection.isVpnActive)
    }

    @Test
    fun `listenToNetworkChanges emits initial status`() = runTest {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)).thenReturn(true)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)).thenReturn(true)

        val firstValue = networkMonitor.listenToNetworkChanges().first()

        assertEquals(NetworkStatus.GOOD_CONNECTION, firstValue.connectionStatus)
    }

    @Test
    fun `listenToNetworkChanges updates when capabilities change`() = runTest {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        val callbackCaptor = ArgumentCaptor.forClass(ConnectivityManager.NetworkCallback::class.java)

        val results = mutableListOf<com.example.orgcore.api.network.data.NetworkConnection>()
        val job = this.launch {
            networkMonitor.listenToNetworkChanges().collect {
                results.add(it)
            }
        }

        this.advanceUntilIdle()

        verify(connectivityManager).registerDefaultNetworkCallback(callbackCaptor.capture())
        val callback = callbackCaptor.value

        // Simulate a capability change for a DIFFERENT network (should NOT emit)
        val otherNetwork = mock(Network::class.java)
        callback.onCapabilitiesChanged(otherNetwork, mock(NetworkCapabilities::class.java))
        this.advanceUntilIdle()
        
        assertEquals(1, results.size)

        // Simulate a capability change for the ACTIVE network (Wi-Fi Connected)
        val wifiCaps = mock(NetworkCapabilities::class.java)
        `when`(wifiCaps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        `when`(wifiCaps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
        `when`(wifiCaps.hasCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)).thenReturn(true)
        `when`(wifiCaps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)).thenReturn(true)

        callback.onCapabilitiesChanged(network, wifiCaps)
        this.advanceUntilIdle()

        assertEquals(2, results.size)
        assertEquals(NetworkStatus.GOOD_CONNECTION, results[1].connectionStatus)

        job.cancel()
    }

}
