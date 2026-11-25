package com.example.orgcore.internal.commun.task

import com.example.orgcore.internal.commun.executor.RequestExecutor
import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import com.example.orgdatabase.public.api.OfflineRequestDataApi
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
class OfflineQueueSyncTaskTest {

    @Mock
    private lateinit var offlineRequestDataApi: OfflineRequestDataApi

    private lateinit var task: OfflineQueueSyncTask

    @Mock
    private lateinit var payloadProcessor: PayloadProcessor<RequestConstraint>

    @Mock
    private lateinit var requestExecutor: RequestExecutor<Any, RequestConstraint>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        task = OfflineQueueSyncTask(
            offlineRequestDataApi = offlineRequestDataApi,
            processorFactory = { payloadProcessor },
            requestExecutor = { requestExecutor }
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
        `when`(offlineRequestDataApi.getNextBatchToSync(org.mockito.ArgumentMatchers.anyInt())).thenReturn(emptyList())

        val result = task.execute()

        assertEquals(TaskResult.Success, result)
    }
}
