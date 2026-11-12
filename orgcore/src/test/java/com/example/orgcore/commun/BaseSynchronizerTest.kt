package com.example.orgcore.commun

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.api.model.SyncResult
import com.example.orgcore.internal.commun.ServerRequest
import com.example.orgcore.internal.commun.sync.BaseSynchronizer
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BaseSynchronizerTest {

    @Mock
    private lateinit var serverRequest: ServerRequest<String>

    private lateinit var synchronizer: TestSynchronizer

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        synchronizer = TestSynchronizer(serverRequest)
    }

    @Test
    fun `synchronize returns Success when network and cache succeed`() = runTest {
        `when`(serverRequest.request()).thenReturn(NetworkResult.Success("data"))
        synchronizer.cacheResult = true

        val result = synchronizer.synchronize()

        assertEquals(SyncResult.Success, result)
        assertEquals("data", synchronizer.receivedData)
    }

    @Test
    fun `synchronize returns DatabaseError when cache fails`() = runTest {
        `when`(serverRequest.request()).thenReturn(NetworkResult.Success("data"))
        synchronizer.cacheResult = false

        val result = synchronizer.synchronize()

        assertEquals(SyncResult.Error.DatabaseError(), result)
    }

    @Test
    fun `synchronize returns NetworkError when server request fails`() = runTest {
        `when`(serverRequest.request()).thenReturn(NetworkResult.Error(500, "Server Error"))

        val result = synchronizer.synchronize()

        assertEquals(SyncResult.Error.NetworkError(), result)
    }

    @Test
    fun `synchronize returns Unauthorized when server returns 401`() = runTest {
        `when`(serverRequest.request()).thenReturn(NetworkResult.Error(401, "Unauthorized"))

        val result = synchronizer.synchronize()

        assertTrue(result is SyncResult.Error.Unauthorized)
        assertEquals("Unauthorized", (result as SyncResult.Error.Unauthorized).message)
    }

    @Test
    fun `synchronize returns Unknown when exception occurs`() = runTest {
        val exception = RuntimeException("Boom")
        `when`(serverRequest.request()).thenThrow(exception)

        val result = synchronizer.synchronize()

        assertTrue(result is SyncResult.Error.Unknown)
        assertEquals(exception, (result as SyncResult.Error.Unknown).exception)
    }

    @Test
    fun `synchronize updates isSynchronized to true on success`() = runTest {
        `when`(serverRequest.request()).thenReturn(NetworkResult.Success("data"))
        synchronizer.cacheResult = true

        synchronizer.synchronize()

        assertTrue(synchronizer.isSynchronized())
    }

    @Test
    fun `synchronize updates isSynchronized to false on cache failure`() = runTest {
        `when`(serverRequest.request()).thenReturn(NetworkResult.Success("data"))
        synchronizer.cacheResult = false

        synchronizer.synchronize()

        assertTrue(!synchronizer.isSynchronized())
    }

    @Test
    fun `synchronize updates isSynchronized to false on network failure`() = runTest {
        `when`(serverRequest.request()).thenReturn(NetworkResult.Error(500, "Error"))

        synchronizer.synchronize()

        assertTrue(!synchronizer.isSynchronized())
    }

    private class TestSynchronizer(
        serverRequest: ServerRequest<String>
    ) : BaseSynchronizer<String>(serverRequest) {
        override val className: String = "TestSynchronizer"
        override val tag: String = "TEST"

        private var isSynched = true
        override suspend fun updateIsSynchronized(isSynchronized: Boolean) {
            isSynched = isSynchronized
        }

        var cacheResult: Boolean = true
        var receivedData: String? = null

        override suspend fun performCaching(data: String): Boolean {
            receivedData = data
            return cacheResult
        }

        override suspend fun isSynchronized(): Boolean {
            return isSynched
        }
    }
}
