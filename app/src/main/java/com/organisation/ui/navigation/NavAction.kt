package com.organisation.ui.navigation

interface NavAction {
    fun navigate(destination: Destination)

    fun navigateClearPrevious(destination: Destination)
    fun popBackStack()
}