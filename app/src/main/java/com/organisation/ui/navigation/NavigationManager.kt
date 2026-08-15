package com.organisation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.organisation.LoggerHelper
import com.organisation.ui.navigation.internal.InternalNavKey
import com.organisation.ui.navigation.internal.NavigationRegistry


private const val CLASS_NAME = "NavigationManager"
private const val TAG = "NAVIGATION"

@Composable
fun NavigationManager(initialDestination: Destination) {
    val initialId = NavigationRegistry.register(initialDestination)
    val initialKey = InternalNavKey(initialId)

    val backStack = remember {
        mutableStateListOf(initialKey)
    }
    LoggerHelper.logger.d(
        TAG,
        "$CLASS_NAME|NavigationManager|backStack: $backStack"
    )
    val navAction = remember(Unit) {
        object : NavAction{
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
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    ) { key ->
        val destination = NavigationRegistry.get(key.id)
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|NavDisplay|destination: $destination, key: $key, backStack: $backStack"
        )
        if (destination?.key == key.id){
            NavEntry(key){
                destination.content.invoke(destination.metadata, navAction)
            }
        }else{
            NavEntry(key){

            }
        }
    }
}