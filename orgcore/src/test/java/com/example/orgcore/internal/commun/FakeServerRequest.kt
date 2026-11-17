package com.example.orgcore.internal.commun

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.commun.request.ServerRequest

internal class FakeServerRequest<T>(
    private val result: NetworkResult<T>,
    private val payloadString: String = "test_payload"
) : ServerRequest<T> {
    var requested = false
    override suspend fun request(): NetworkResult<T> {
        requested = true
        return result
    }
    override fun payload(): String = payloadString
}
