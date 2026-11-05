package com.organisation.ui.navigation.internal

import androidx.navigation3.runtime.NavBackStack
import com.organisation.LoggerHelper
import com.organisation.ui.navigation.Destination
import com.organisation.ui.navigation.NavAction

internal class NavActionImpl private constructor(
    private val backStack: NavBackStack<InternalNavKey>
): NavAction {

    companion object{
        private const val CLASS_NAME = "NavActionImpl"
        private const val TAG = "NAVIGATION"

        private var INSTANCE: NavAction? = null

        fun getInstance(backStack: NavBackStack<InternalNavKey>): NavAction =
            INSTANCE?:synchronized(this){
                NavActionImpl(backStack)
        }

    }

    override fun navigate(destination: Destination) {
        LoggerHelper.logger.d(
            TAG,
            "${CLASS_NAME}|navigate|destination: $destination"
        )
        val id = NavigationRegistry.register(destination)
        backStack.add(InternalNavKey(id))
    }

    override fun navigateClearPrevious(destination: Destination) {
        val previous = backStack.last()
        LoggerHelper.logger.d(
            TAG,
            "${CLASS_NAME}|navigateClearPrevious|destination: $destination, previous: $previous"
        )
        val id = NavigationRegistry.register(destination)
        backStack.add(InternalNavKey(id))
        backStack.remove(previous)
    }

    override fun popBackStack() {
        backStack.removeLastOrNull()
    }
}