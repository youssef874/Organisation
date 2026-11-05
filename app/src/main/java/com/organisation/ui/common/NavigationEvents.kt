package com.organisation.ui.common

import com.organisation.ui.navigation.Destination

sealed class NavigationEvents {
    data class UpdateInitialDestination(val destination: Destination) : NavigationEvents()
}
