package com.example.orgcore.internal.commun

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.FakeCache
import com.example.orgcore.internal.commun.model.RequestPriority
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RequestExecutionBuilderTest {

    private lateinit var fakeNetworkMonitor: FakeNetworkMonitor
    private lateinit var fakeOfflineQueueCache: FakeOfflineQueueCache

    @Before
    fun setUp() {
        fakeNetworkMonitor = FakeNetworkMonitor(true)
        fakeOfflineQueueCache = FakeOfflineQueueCache()
    }

    @Test
    fun `executes network request successfully and returns Success when connected`() = runTest {
        val serverRequest = FakeServerRequest(NetworkResult.Success("test_data"), "payload_123")
        val fakeCache = FakeCache<String>()

        val result = RequestExecutionBuilder(serverRequest)
            .cache(fakeCache)
            .execute(fakeNetworkMonitor, fakeOfflineQueueCache)

        assertTrue(serverRequest.requested)
        assertTrue(result is RequestResult.Success)
        assertEquals("test_data", (result as RequestResult.Success).data)
        assertEquals("test_data", fakeCache.cache)
        assertEquals(null, fakeOfflineQueueCache.cachedRequest)
    }

    @Test
    fun `queues offline request and returns OfflineQueued when disconnected and offline queue is allowed`() = runTest {
        fakeNetworkMonitor.isConnectedVal = false
        val serverRequest = FakeServerRequest(NetworkResult.Success("test_data"), "payload_123")

        val result = RequestExecutionBuilder(serverRequest)
            .requestType("test_type")
            .priority(RequestPriority.PRIORITY_HIGH)
            .allowOfflineQueue(true)
            .execute(fakeNetworkMonitor, fakeOfflineQueueCache)

        assertTrue(result is RequestResult.OfflineQueued)
        assertNotNull(fakeOfflineQueueCache.cachedRequest)
        assertEquals("payload_123", fakeOfflineQueueCache.cachedRequest?.payloadJson)
        assertEquals("test_type", fakeOfflineQueueCache.cachedRequest?.requestType)
    }

    @Test
    fun `returns ServerFailure when server request returns error`() = runTest {
        val serverRequest = FakeServerRequest(NetworkResult.Error(500, "Server Error"))

        val result = RequestExecutionBuilder(serverRequest)
            .execute(fakeNetworkMonitor, fakeOfflineQueueCache)

        assertTrue(result is RequestResult.ServerFailure)
        assertEquals(500, (result as RequestResult.ServerFailure).errorCode)
    }

    @Test
    fun `returns CacheFailure when connected and local cache returns false`() = runTest {
        val serverRequest = FakeServerRequest(NetworkResult.Success("test_data"), "payload_123")
        val fakeCache = FakeCache<String>().apply { isCached = false }

        val result = RequestExecutionBuilder(serverRequest)
            .cache(fakeCache)
            .execute(fakeNetworkMonitor, fakeOfflineQueueCache)

        assertTrue(result is RequestResult.CacheFailure)
    }

    @Test
    fun `returns CacheFailure when disconnected and offline queue cache returns false`() = runTest {
        fakeNetworkMonitor.isConnectedVal = false
        val serverRequest = FakeServerRequest(NetworkResult.Success("test_data"), "payload_123")
        fakeOfflineQueueCache.success = false

        val result = RequestExecutionBuilder(serverRequest)
            .requestType("test_type")
            .execute(fakeNetworkMonitor, fakeOfflineQueueCache)

        assertTrue(result is RequestResult.CacheFailure)
    }

    @Test
    fun `returns ServerFailure when server request throws exception`() = runTest {
        val throwingRequest = object : ServerRequest<String> {
            override suspend fun request(): NetworkResult<String> {
                throw RuntimeException("Unexpected exception")
            }
            override fun payload(): String = "payload"
        }

        val result = RequestExecutionBuilder(throwingRequest)
            .execute(fakeNetworkMonitor, fakeOfflineQueueCache)

        assertTrue(result is RequestResult.ServerFailure)
        assertEquals(-1, (result as RequestResult.ServerFailure).errorCode)
    }
}
