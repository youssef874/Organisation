package com.example.orgcore.internal.account

import com.example.orgcore.api.model.OrgEvent
import com.example.orgcore.internal.account.service.AccountService
import com.example.orgcore.internal.commun.event.OrgEventListener
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class AccountManagerTest {

    @Mock
    private lateinit var accountService: AccountService

    @Mock
    private lateinit var orgEventListener: OrgEventListener

    private lateinit var accountManager: AccountManager

    private class TestEvent(type: String) : OrgEvent(type)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        accountManager = AccountManager(accountService, orgEventListener)
    }

    @Test
    fun `fetchData calls accountService requestData`() = runTest {
        accountManager.fetchData()

        verify(accountService).requestData()
    }

    @Test
    fun `addAccountEventsListener delegates to orgEventListener`() {
        val event = TestEvent("test_type")
        val listener: (OrgEvent) -> Unit = {}

        accountManager.addAccountEventsListener(event, listener)

        verify(orgEventListener).addListener(event, listener)
    }

    @Test
    fun `removeAccountEventsListener delegates to orgEventListener`() {
        val event = TestEvent("test_type")
        val listener: (OrgEvent) -> Unit = {}

        accountManager.removeAccountEventsListener(event, listener)

        verify(orgEventListener).removeListener(event, listener)
    }

    @Test
    fun `observeAccountEventsListener returns flow from orgEventListener`() {
        val type = "test_type"
        val expectedFlow = flowOf(TestEvent(type))
        `when`(orgEventListener.observe(type)).thenReturn(expectedFlow)

        val resultFlow = accountManager.observeAccountEventsListener(type)

        assertEquals(expectedFlow, resultFlow)
        verify(orgEventListener).observe(type)
    }
}
