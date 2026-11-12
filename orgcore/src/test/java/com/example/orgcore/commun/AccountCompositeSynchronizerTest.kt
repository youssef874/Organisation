package com.example.orgcore.commun

import com.example.orgcore.api.model.SyncResult
import com.example.orgcore.internal.commun.sync.AccountCompositeSynchronizer
import com.example.orgcore.internal.commun.sync.Synchronizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.system.measureTimeMillis

@RunWith(RobolectricTestRunner::class)
class AccountCompositeSynchronizerTest {

    @Test
    fun `synchronize executes in parallel`() = runTest {
        val sync1 = FakeSynchronizer(delayMs = 1000, result = SyncResult.Success)
        val sync2 = FakeSynchronizer(delayMs = 1000, result = SyncResult.Success)
        val composite = AccountCompositeSynchronizer(listOf(sync1, sync2))

        val time = measureTimeMillis {
            composite.synchronize()
        }

        // Should take ~1000ms, not ~2000ms
        assertTrue("Execution should be parallel. Took $time ms", time < 1500)
    }

    @Test
    fun `returns Success when all succeed`() = runTest {
        val composite = AccountCompositeSynchronizer(
            listOf(
                FakeSynchronizer(result = SyncResult.Success),
                FakeSynchronizer(result = SyncResult.Success)
            )
        )

        assertEquals(SyncResult.Success, composite.synchronize())
    }

    @Test
    fun `returns PartialSuccess when some fail`() = runTest {
        val composite = AccountCompositeSynchronizer(
            listOf(
                FakeSynchronizer(result = SyncResult.Success),
                FakeSynchronizer(result = SyncResult.Error.NetworkError())
            )
        )

        val result = composite.synchronize()
        assertTrue(result is SyncResult.PartialSuccess)
    }

    @Test
    fun `returns Error when all fail`() = runTest {
        val error = SyncResult.Error.DatabaseError()
        val composite = AccountCompositeSynchronizer(
            listOf(
                FakeSynchronizer(result = error),
                FakeSynchronizer(result = SyncResult.Error.NetworkError())
            )
        )

        val result = composite.synchronize()
        assertTrue(result is SyncResult.Error)
    }

    @Test
    fun `isSynchronized returns true when all children are synchronized`() = runTest {
        val composite = AccountCompositeSynchronizer(
            listOf(
                FakeSynchronizer(isSynchronized = true, result = SyncResult.Success),
                FakeSynchronizer(isSynchronized = true, result = SyncResult.Success)
            )
        )

        assertTrue(composite.isSynchronized())
    }

    @Test
    fun `isSynchronized returns false when any child is not synchronized`() = runTest {
        val composite = AccountCompositeSynchronizer(
            listOf(
                FakeSynchronizer(isSynchronized = true, result = SyncResult.Success),
                FakeSynchronizer(isSynchronized = false, result = SyncResult.Success)
            )
        )

        assertTrue(!composite.isSynchronized())
    }

    private class FakeSynchronizer(
        private val delayMs: Long = 0,
        private val isSynchronized: Boolean = false,
        private val result: SyncResult
    ) : Synchronizer {
        override suspend fun synchronize(): SyncResult {
            if (delayMs > 0) delay(delayMs)
            return result
        }

        override suspend fun isSynchronized(): Boolean = isSynchronized
    }
}
