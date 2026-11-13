package com.example.orgcore.internal.attendance.request

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.commun.ServerRequest

internal class FakerRequestAccountAttendanceStatusUpdate(
    var responseCode: Int = 200,
    var shouldThrowException: Boolean = false,
    private val newStatus: @AttendanceStatus String
): ServerRequest<@AttendanceStatus String> {
    override suspend fun request(): NetworkResult<@AttendanceStatus String> {
        if (shouldThrowException) {
            throw RuntimeException("Unexpected server error")
        }
        return if (responseCode == 200){
            NetworkResult.Success(newStatus)
        }else{
            NetworkResult.Error(
                code = responseCode,
                message = "Error"
            )
        }
    }
}