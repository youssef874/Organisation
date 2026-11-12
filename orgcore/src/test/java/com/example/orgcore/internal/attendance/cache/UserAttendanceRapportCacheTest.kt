package com.example.orgcore.internal.attendance.cache

import com.example.orgcore.commun.anyObject
import com.example.orgcore.commun.capture
import com.example.orgcore.internal.account.Authentication.mapper.toDBAccount
import com.example.orgcore.internal.commun.FakeData
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.DBAttendance
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserAttendanceRapportCacheTest {

    @Mock
    private lateinit var accountDataApi: AccountDataApi

    private lateinit var userAttendanceRapportCache: UserAttendanceRapportCache

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userAttendanceRapportCache = UserAttendanceRapportCache(accountDataApi)
    }

    @Test
    fun `cache maps attendance records correctly and calls database addAttendances`() = runTest {
        val attendances = FakeData.Employee.attendance
        val mockAccount = FakeData.Employee.account.toDBAccount(isUser = true).copy(id = 1L)
        `when`(accountDataApi.getAccountByServerId(anyString())).thenReturn(mockAccount)
        `when`(accountDataApi.addAttendances(anyObject())).thenReturn(true)

        val result = userAttendanceRapportCache.cache(attendances)

        assertTrue(result)
        @Suppress("UNCHECKED_CAST")
        val captor = ArgumentCaptor.forClass(List::class.java) as ArgumentCaptor<List<DBAttendance>>
        verify(accountDataApi).addAttendances(capture(captor))

        val dbAttendances = captor.value
        assertEquals(attendances.size, dbAttendances.size)

        dbAttendances.forEachIndexed { index, dbAttendance ->
            assertEquals(attendances[index].checkIn, dbAttendance.checkIn)
            assertEquals(attendances[index].checkOut, dbAttendance.checkOut)
            assertEquals(attendances[index].date, dbAttendance.date)
            assertEquals(attendances[index].attendanceStatus, dbAttendance.attendanceStatus)
            assertEquals(attendances[index].id, dbAttendance.serverId)
        }
    }

    @Test
    fun `cache returns false when database call fails`() = runTest {
        val attendances = FakeData.Employee.attendance
        val mockAccount = FakeData.Employee.account.toDBAccount(isUser = true).copy(id = 1L)
        `when`(accountDataApi.getAccountByServerId(anyString())).thenReturn(mockAccount)
        `when`(accountDataApi.addAttendances(anyObject())).thenReturn(false)

        val failResult = userAttendanceRapportCache.cache(attendances)
        assertTrue(!failResult)
    }
}
