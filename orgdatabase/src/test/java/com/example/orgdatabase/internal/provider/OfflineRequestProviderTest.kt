package com.example.orgdatabase.internal.provider

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.public.model.DBOfflineRequest
import com.example.orgdatabase.public.model.OfflineRequestType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
internal class OfflineRequestProviderTest {

    private lateinit var database: OrganisationDatabase
    private lateinit var provider: OfflineRequestProvider

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            OrganisationDatabase::class.java,
        ).allowMainThreadQueries().build()

        provider = OfflineRequestProviderImpl(
            offlineRequestDao = database.offlineRequestDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addAndGetRequest() = runTest {
        val request = DBOfflineRequest(
            requestUuid = UUID.randomUUID().toString(),
            requestType = OfflineRequestType.ATTENDANCE,
            payloadJson = "{\"status\":\"present\"}",
            priority = 10,
            createdAt = System.currentTimeMillis()
        )

        val added = provider.addRequest(request)
        assertTrue(added)

        val fetched = provider.getByRequestUuid(request.requestUuid)
        assertNotNull(fetched)
        assertEquals(request.requestUuid, fetched?.requestUuid)
        assertEquals(request.requestType, fetched?.requestType)
        assertEquals(10, fetched?.priority)
    }

    @Test
    fun addRequestsBatchAndCount() = runTest {
        val requests = listOf(
            DBOfflineRequest(
                requestUuid = UUID.randomUUID().toString(),
                requestType = OfflineRequestType.ATTENDANCE,
                payloadJson = "{}",
                priority = 0,
                createdAt = 100L
            ),
            DBOfflineRequest(
                requestUuid = UUID.randomUUID().toString(),
                requestType = OfflineRequestType.ATTENDANCE,
                payloadJson = "{}",
                priority = 5,
                createdAt = 200L
            )
        )

        val added = provider.addRequests(requests)
        assertTrue(added)
        assertEquals(2, provider.getCount())
    }

    @Test
    fun getNextBatchToSyncSorting() = runTest {
        val req1 = DBOfflineRequest(
            requestUuid = "uuid-1",
            requestType = OfflineRequestType.ATTENDANCE,
            payloadJson = "{}",
            priority = 0,
            createdAt = 100L
        )
        val req2 = DBOfflineRequest(
            requestUuid = "uuid-2",
            requestType = OfflineRequestType.ATTENDANCE,
            payloadJson = "{}",
            priority = 10,
            createdAt = 200L
        )
        val req3 = DBOfflineRequest(
            requestUuid = "uuid-3",
            requestType = "other_type",
            payloadJson = "{}",
            priority = 10,
            createdAt = 150L
        )

        provider.addRequests(listOf(req1, req2, req3))

        val batch = provider.getNextBatchToSync(2)
        assertEquals(2, batch.size)
        assertEquals("uuid-3", batch[0].requestUuid)
        assertEquals("uuid-2", batch[1].requestUuid)
    }

    @Test
    fun getNextBatchToSyncByTypeFiltering() = runTest {
        val req1 = DBOfflineRequest(
            requestUuid = "uuid-1",
            requestType = OfflineRequestType.ATTENDANCE,
            payloadJson = "{}",
            priority = 1,
            createdAt = 200L
        )
        val req2 = DBOfflineRequest(
            requestUuid = "uuid-2",
            requestType = OfflineRequestType.ATTENDANCE,
            payloadJson = "{}",
            priority = 1,
            createdAt = 100L
        )
        val req3 = DBOfflineRequest(
            requestUuid = "uuid-3",
            requestType = "other_type",
            payloadJson = "{}",
            priority = 5,
            createdAt = 50L
        )

        provider.addRequests(listOf(req1, req2, req3))

        val batch = provider.getNextBatchToSyncByType(OfflineRequestType.ATTENDANCE, 10)
        assertEquals(2, batch.size)
        assertEquals("uuid-2", batch[0].requestUuid)
        assertEquals("uuid-1", batch[1].requestUuid)
    }

    @Test
    fun deleteAndClear() = runTest {
        val req = DBOfflineRequest(
            requestUuid = "uuid-del",
            requestType = OfflineRequestType.ATTENDANCE,
            payloadJson = "{}",
            createdAt = System.currentTimeMillis()
        )
        provider.addRequest(req)
        assertEquals(1, provider.getCount())

        val deleted = provider.deleteByRequestUuid("uuid-del")
        assertTrue(deleted)
        assertEquals(0, provider.getCount())
    }
}
