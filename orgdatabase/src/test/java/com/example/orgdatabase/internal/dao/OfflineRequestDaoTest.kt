package com.example.orgdatabase.internal.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.internal.entity.OfflineRequestEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class OfflineRequestDaoTest {

    private lateinit var database: OrganisationDatabase
    private lateinit var offlineRequestDao: OfflineRequestDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            OrganisationDatabase::class.java,
        ).allowMainThreadQueries().build()

        offlineRequestDao = database.offlineRequestDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetByUuid() = runTest {
        val request = OfflineRequestEntity(
            requestType = OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE,
            payloadJson = "{\"test\":true}",
            priority = OfflineRequestEntity.PRIORITY_NORMAL
        )
        val  id = offlineRequestDao.insert(request)

        val fetched = offlineRequestDao.getByRequestUuid(request.requestUuid)
        assertNotNull(fetched)
        assertEquals(request.requestUuid, fetched?.requestUuid)
        assertEquals(OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE, fetched?.requestType)
        assertEquals(id, fetched?.id)
    }

    @Test
    fun getNextBatchToSyncSortingAndLimit() = runTest {
        val req1 = OfflineRequestEntity(
            requestType = OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE,
            payloadJson = "{}",
            priority = OfflineRequestEntity.PRIORITY_LOW,
            createdAt = 100L
        )
        val req2 = OfflineRequestEntity(
            requestType = OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE,
            payloadJson = "{}",
            priority = OfflineRequestEntity.PRIORITY_HIGH,
            createdAt = 200L
        )
        val req3 = OfflineRequestEntity(
            requestType = "other_type",
            payloadJson = "{}",
            priority = OfflineRequestEntity.PRIORITY_HIGH,
            createdAt = 150L
        )
        val req4 = OfflineRequestEntity(
            requestType = OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE,
            payloadJson = "{}",
            priority = OfflineRequestEntity.PRIORITY_NORMAL,
            createdAt = 50L
        )

        offlineRequestDao.insert(
            listOf(
                req1,
                req2,
                req3,
                req4
            )
        )

        val batch = offlineRequestDao.getNextBatchToSync(2)
        assertEquals(2, batch.size)
        assertEquals(req3.requestUuid, batch[0].requestUuid)
        assertEquals(req2.requestUuid, batch[1].requestUuid)
    }

    @Test
    fun getNextBatchToSyncByTypeFilteringAndSorting() = runTest {
        val req1 = OfflineRequestEntity(
            requestType = OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE,
            payloadJson = "{}",
            priority = OfflineRequestEntity.PRIORITY_NORMAL,
            createdAt = 100L
        )
        val req2 = OfflineRequestEntity(
            requestType = OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE,
            payloadJson = "{}",
            priority = OfflineRequestEntity.PRIORITY_NORMAL,
            createdAt = 50L
        )
        val req3 = OfflineRequestEntity(
            requestType = "other_type",
            payloadJson = "{}",
            priority = OfflineRequestEntity.PRIORITY_HIGH,
            createdAt = 10L
        )

        offlineRequestDao.insert(req1)
        offlineRequestDao.insert(req2)
        offlineRequestDao.insert(req3)

        val batch = offlineRequestDao.getNextBatchToSyncByType(OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE, 10)
        assertEquals(2, batch.size)
        assertEquals(req2.requestUuid, batch[0].requestUuid)
        assertEquals(req1.requestUuid, batch[1].requestUuid)
    }

    @Test
    fun deleteAndCount() = runTest {
        val req = OfflineRequestEntity(
            requestType = OfflineRequestEntity.REQUEST_TYPE_ATTENDANCE,
            payloadJson = "{}"
        )
        offlineRequestDao.insert(req)
        assertEquals(1, offlineRequestDao.getCount())

        offlineRequestDao.deleteByRequestUuid(req.requestUuid)
        assertEquals(0, offlineRequestDao.getCount())
    }
}
