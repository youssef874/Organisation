package com.example.orgcore.internal.commun.task

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.commun.anyObject
import com.example.orgcore.commun.capture
import com.example.orgcore.internal.FakeCache
import com.example.orgcore.internal.commun.FakeNetworkMonitor
import com.example.orgcore.internal.commun.model.ServerErrorCode
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgdatabase.public.api.OfflineRequestDataApi
import com.example.orgdatabase.public.model.DBOfflineRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OfflineQueueSyncTaskTest {

    @Mock
    private lateinit var offlineRequestDataApi: OfflineRequestDataApi

    private lateinit var networkMonitor: FakeNetworkMonitor
    private lateinit var task: OfflineQueueSyncTask

    @Mock
    private lateinit var payloadProcessor: PayloadProcessor<Any>

    @Mock
    private lateinit var serverRequest: ServerRequest<Any>

    private lateinit var fakeCache: FakeCache<Any>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        networkMonitor = FakeNetworkMonitor(true)
        fakeCache = FakeCache()
        task = OfflineQueueSyncTask(
            offlineRequestDataApi = offlineRequestDataApi,
            networkMonitor = networkMonitor,
            processorFactory = { payloadProcessor }
        )
    }

    @Test
    fun `defaultConstraints have requireNetwork true and OneTime trigger`() {
        val constraints = task.defaultConstraints
        assertTrue(constraints.requireNetwork)
        assertEquals(TaskTrigger.OneTime, constraints.trigger)
    }

    @Test
    fun `execute returns Success when queue is empty`() = runTest {
        `when`(offlineRequestDataApi.getNextBatchToSync(anyInt())).thenReturn(emptyList())

        val result = task.execute()

        assertEquals(TaskResult.Success, result)
    }

    @Test
    fun `execute processes batch and deletes request on success`() = runTest {
        val dbRequest = DBOfflineRequest(
            requestUuid = "uuid-1",
            priority = 1,
            requestType = "attendance",
            payloadJson = "{\"requestType\":\"attendance\"}",
            createdAt = 1000L,
            retryCount = 0
        )

        `when`(offlineRequestDataApi.getNextBatchToSync(anyInt()))
            .thenReturn(listOf(dbRequest))
            .thenReturn(emptyList())

        `when`(payloadProcessor.getServerRequest()).thenReturn(serverRequest)
        `when`(payloadProcessor.getCache()).thenReturn(fakeCache)
        `when`(serverRequest.request()).thenReturn(NetworkResult.Success("data"))

        val result = task.execute()

        assertEquals(TaskResult.Success, result)
        verify(offlineRequestDataApi).deleteByRequestUuid("uuid-1")
    }

    @Test
    fun `execute drops request immediately on permanent client error`() = runTest {
        val dbRequest = DBOfflineRequest(
            requestUuid = "uuid-2",
            priority = 1,
            requestType = "attendance",
            payloadJson = "{\"requestType\":\"attendance\"}",
            createdAt = 1000L,
            retryCount = 0
        )

        `when`(offlineRequestDataApi.getNextBatchToSync(anyInt()))
            .thenReturn(listOf(dbRequest))
            .thenReturn(emptyList())

        `when`(payloadProcessor.getServerRequest()).thenReturn(serverRequest)
        `when`(payloadProcessor.getCache()).thenReturn(fakeCache)
        `when`(serverRequest.request()).thenReturn(NetworkResult.Error(ServerErrorCode.BAD_REQUEST, "Bad Request"))

        val result = task.execute()

        assertEquals(TaskResult.Success, result)
        verify(offlineRequestDataApi).deleteByRequestUuid("uuid-2")
    }

    @Test
    fun `execute increments retry count on transient server error`() = runTest {
        val dbRequest = DBOfflineRequest(
            requestUuid = "uuid-3",
            priority = 1,
            requestType = "attendance",
            payloadJson = "{\"requestType\":\"attendance\"}",
            createdAt = 1000L,
            retryCount = 0
        )

        `when`(offlineRequestDataApi.getNextBatchToSync(anyInt()))
            .thenReturn(listOf(dbRequest))
            .thenReturn(emptyList())

        `when`(offlineRequestDataApi.addRequest(anyObject())).thenReturn(true)
        `when`(payloadProcessor.getServerRequest()).thenReturn(serverRequest)
        `when`(payloadProcessor.getCache()).thenReturn(fakeCache)
        `when`(serverRequest.request()).thenReturn(NetworkResult.Error(ServerErrorCode.INTERNAL_SERVER_ERROR, "Server Error"))

        val result = task.execute()

        assertEquals(TaskResult.Retry(), result)

        val captor = ArgumentCaptor.forClass(DBOfflineRequest::class.java) as ArgumentCaptor<DBOfflineRequest>
        verify(offlineRequestDataApi).addRequest(capture(captor))
        assertEquals("uuid-3", captor.value.requestUuid)
        assertEquals(1, captor.value.retryCount)
    }

    @Test
    fun `execute drops request when max transient retries reached`() = runTest {
        val dbRequest = DBOfflineRequest(
            requestUuid = "uuid-max",
            priority = 1,
            requestType = "attendance",
            payloadJson = "{\"requestType\":\"attendance\"}",
            createdAt = 1000L,
            retryCount = 9 // 9 + 1 = 10 >= MAX_TRANSIENT_RETRIES
        )

        `when`(offlineRequestDataApi.getNextBatchToSync(anyInt()))
            .thenReturn(listOf(dbRequest))
            .thenReturn(emptyList())

        `when`(payloadProcessor.getServerRequest()).thenReturn(serverRequest)
        `when`(payloadProcessor.getCache()).thenReturn(fakeCache)
        `when`(serverRequest.request()).thenReturn(NetworkResult.Error(ServerErrorCode.INTERNAL_SERVER_ERROR, "Server Error"))

        val result = task.execute()

        assertEquals(TaskResult.Success, result)
        verify(offlineRequestDataApi).deleteByRequestUuid("uuid-max")
    }
}
