package com.organisation.ui.common

import android.app.Application
import com.organisation.LoggerHelper
import com.organisation.ui.common.viewmodel.BaseViewModel
import com.organisation.ui.navigation.Destination
import com.organisation.ui.splash.model.SplashDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    application: Application
) : BaseViewModel<NavigationEvents>(application) {

    companion object{
        private const val CLASS_NAME = "NavigationViewModel"
        private const val TAG = "APPLICATION"
    }

    private val _initialDestination = MutableStateFlow<Destination>(SplashDestination)
    val initialDestination: StateFlow<Destination> = _initialDestination.asStateFlow()

    override fun handleEvent(event: NavigationEvents) {
        when (event) {
            is NavigationEvents.UpdateInitialDestination -> {
                LoggerHelper.logger.d(
                    TAG,
                    "$CLASS_NAME|UpdateInitialDestination| destination: ${event.destination}, $this"
                )
                _initialDestination.update { event.destination }
                LoggerHelper.logger.d(
                    TAG,
                    "$CLASS_NAME|UpdateInitialDestination| after destination: ${initialDestination.value}"
                )
            }
        }
    }
}
