package com.organisation.ui.common

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.organisation.ui.navigation.Destination
import com.organisation.ui.navigation.NavAction
import com.organisation.ui.splash.model.SplashDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {

    @Mock
    private lateinit var application: Application

    private lateinit var viewModel: NavigationViewModel
    private val testDispatcher = StandardTestDispatcher()

    private data class FakeDestination(override val key: String) : Destination {
        override val metadata: Map<String, Any> = emptyMap()
        override val content: @Composable ((modifier: Modifier, metadata: Map<String, Any>, navAction: NavAction) -> Unit) = { _,_, _ -> }
    }

    private val nextDestination = FakeDestination("next")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = NavigationViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is SplashDestination`() = runTest {
        assertTrue(viewModel.initialDestination.value is SplashDestination)
    }

    @Test
    fun `UpdateInitialDestination event updates initialDestination`() = runTest {
        viewModel.onEvent(NavigationEvents.UpdateInitialDestination(nextDestination))
        
        // Advance until idle to allow handleEvent to be called in viewModelScope
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(nextDestination, viewModel.initialDestination.value)
    }
}
