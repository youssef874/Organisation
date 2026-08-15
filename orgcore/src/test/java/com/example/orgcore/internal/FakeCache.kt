package com.example.orgcore.internal

import com.example.orgcore.commun.OfflineCache

class FakeCache<T>: OfflineCache<T> {

    private val _cachedItems = mutableListOf<T>()
    val cachedItems: List<T> get() = _cachedItems

    var cache: T? = null
        private set

    var isCached = true

    var failOnCallIndex: Int? = null
    private var callCount = 0

    override suspend fun cache(data: T): Boolean {
        cache = data
        _cachedItems.add(data)
        val shouldFail = failOnCallIndex == callCount
        callCount++
        return if (shouldFail) false else isCached
    }
}