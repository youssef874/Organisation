package com.organisation.ui.navigation.internal

import com.organisation.ui.navigation.Destination
import java.util.concurrent.ConcurrentHashMap

internal object NavigationRegistry {

    private val registry =
        ConcurrentHashMap<String, Destination>()

    fun register(destination: Destination): String {
        val id = destination.key
        registry[id] = destination
        return id
    }

    fun get(id: String): Destination? = registry[id]
}