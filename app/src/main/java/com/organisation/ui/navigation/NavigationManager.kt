package com.organisation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.organisation.ui.navigation.internal.InternalNavKey
import com.organisation.ui.navigation.internal.NavActionImpl
import com.organisation.ui.navigation.internal.NavigationRegistry


@Composable
fun NavigationManager(modifier: Modifier = Modifier, initialDestination: Destination) {
    val initialId = NavigationRegistry.register(initialDestination)
    val backStack = rememberNavBackStack(InternalNavKey(initialId ))


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
        if (key is InternalNavKey){
            val destination =  NavigationRegistry.get(key.id)
            if ( destination?.key == key.id) {
                NavEntry(key) {
                    destination.content.invoke(
                         modifier,
                        destination.metadata,
                        NavActionImpl.getInstance(backStack as NavBackStack<InternalNavKey>)
                    )
                }
            } else {
                NavEntry(key) {

                }
            }
        }else {
            NavEntry(key) {

            }
        }
    }
}