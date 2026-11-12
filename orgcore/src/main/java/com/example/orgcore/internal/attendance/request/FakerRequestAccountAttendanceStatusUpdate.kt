package com.example.orgcore.internal.attendance.request

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.commun.ServerRequest

internal class FakerRequestAccountAttendanceStatusUpdate(
    var responseCode: Int = 200,
    var shouldThrowException: Boolean = false
): ServerRequest<Unit> {
    override suspend fun request(): NetworkResult<Unit> {
        if (shouldThrowException) {
            throw RuntimeException("Unexpected server error")
        }
        return if (responseCode == 200){
            NetworkResult.Success(Unit)
        }else{
            NetworkResult.Error(
                code = responseCode,
                message = "Error"
            )
        }
    }
}