package com.example.orgcore.internal.attendance.request

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgdatabase.public.model.OfflineRequestType
import java.util.UUID

internal class FakerRequestAccountAttendanceStatusUpdate(
    var responseCode: Int = 200,
    var shouldThrowException: Boolean = false,
    private val newStatus: @AttendanceStatus String,
    private val accountId: Long = 0L,
    private val date: Long = 0L,
    private var requestUuid: String = UUID.randomUUID().toString(),
    private val businessType: @AttendanceBusinessType String = if (newStatus == AttendanceStatus.NONE) AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST else AttendanceBusinessType.ATTENDANCE_STATUS_UPDATE
): ServerRequest<@AttendanceStatus String> {
    override fun setRequestUuid(uuid: String) {
        requestUuid = uuid
    }

    override fun payload(): String =
        "{\"requestUuid\":\"$requestUuid\",\"accountId\":$accountId,\"date\":$date,\"requestType\":\"${OfflineRequestType.ATTENDANCE}\",\"businessType\":\"$businessType\",\"status\":\"$newStatus\"}"

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
