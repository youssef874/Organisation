package com.example.orgcore.commun

import com.example.orgcore.api.model.SyncResult
import com.example.orgcore.internal.commun.sync.SequentialSynchronizer
import com.example.orgcore.internal.commun.sync.Synchronizer
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SequentialSynchronizerTest {

    @Test
    fun `synchronize executes sequentially in order`() = runTest {
        val executionOrder = mutableListOf<String>()
        val sync1 = object : Synchronizer {
            override suspend fun synchronize(): SyncResult {
                executionOrder.add("sync1")
                return SyncResult.Success
            }
            override suspend fun isSynchronized(): Boolean = true
        }
        val sync2 = object : Synchronizer {
            override suspend fun synchronize(): SyncResult {
                executionOrder.add("sync2")
                return SyncResult.Success
            }
            override suspend fun isSynchronized(): Boolean = true
        }

        val sequential = SequentialSynchronizer(listOf(sync1, sync2))
        sequential.synchronize()

        assertEquals(listOf("sync1", "sync2"), executionOrder)
    }

    @Test
    fun `synchronize halts execution on error (fail-fast)`() = runTest {
        val executionOrder = mutableListOf<String>()
        val sync1 = object : Synchronizer {
            override suspend fun synchronize(): SyncResult {
                executionOrder.add("sync1")
                return SyncResult.Error.NetworkError()
            }
            override suspend fun isSynchronized(): Boolean = false
        }
        val sync2 = object : Synchronizer {
            override suspend fun synchronize(): SyncResult {
                executionOrder.add("sync2")
                return SyncResult.Success
            }
            override suspend fun isSynchronized(): Boolean = true
        }

        val sequential = SequentialSynchronizer(listOf(sync1, sync2))
        val result = sequential.synchronize()

        assertTrue(result is SyncResult.Error.NetworkError)
        assertEquals(listOf("sync1"), executionOrder)
    }

    @Test
    fun `synchronize halts execution on partial success`() = runTest {
        val executionOrder = mutableListOf<String>()
        val sync1 = object : Synchronizer {
            override suspend fun synchronize(): SyncResult {
                executionOrder.add("sync1")
                return SyncResult.PartialSuccess("Partial")
            }
            override suspend fun isSynchronized(): Boolean = false
        }
        val sync2 = object : Synchronizer {
            override suspend fun synchronize(): SyncResult {
                executionOrder.add("sync2")
                return SyncResult.Success
            }
            override suspend fun isSynchronized(): Boolean = true
        }

        val sequential = SequentialSynchronizer(listOf(sync1, sync2))
        val result = sequential.synchronize()

        assertTrue(result is SyncResult.PartialSuccess)
        assertEquals(listOf("sync1"), executionOrder)
    }

    @Test
    fun `returns Success when all succeed`() = runTest {
        val sequential = SequentialSynchronizer(
            listOf(
                FakeSynchronizer(isSynchronized = true, result = SyncResult.Success),
                FakeSynchronizer(isSynchronized = true, result = SyncResult.Success)
            )
        )

        assertEquals(SyncResult.Success, sequential.synchronize())
    }

    @Test
    fun `isSynchronized returns true when all children are synchronized`() = runTest {
        val sequential = SequentialSynchronizer(
            listOf(
                FakeSynchronizer(isSynchronized = true, result = SyncResult.Success),
                FakeSynchronizer(isSynchronized = true, result = SyncResult.Success)
            )
        )

        assertTrue(sequential.isSynchronized())
    }

    @Test
    fun `isSynchronized returns false when any child is not synchronized`() = runTest {
        val sequential = SequentialSynchronizer(
            listOf(
                FakeSynchronizer(isSynchronized = true, result = SyncResult.Success),
                FakeSynchronizer(isSynchronized = false, result = SyncResult.Success)
            )
        )

        assertTrue(!sequential.isSynchronized())
    }

    private class FakeSynchronizer(
        private val isSynchronized: Boolean = false,
        private val result: SyncResult
    ) : Synchronizer {
        override suspend fun synchronize(): SyncResult = result
        override suspend fun isSynchronized(): Boolean = isSynchronized
    }
}
