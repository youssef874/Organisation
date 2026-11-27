package com.example.orgcore.internal.attendance.request

import com.example.orgcore.SDKComponent
import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.attendance.mapper.toAttendance
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.commun.model.ServerRequestErrorType
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.OfflineRequestType
import java.util.UUID

/**
 * Fake implementation of [ServerRequest] to cancel an attendance request.
 */
internal class FakeCancelAttendanceServerRequest(
    private var accountId: String,
    private val date: Long,
    var responseCode: Int = 200,
    var shouldThrowException: Boolean = false,
    private var requestUuid: String = UUID.randomUUID().toString(),
    private val accountDataApi: AccountDataApi? = null,
    private val businessType: @AttendanceBusinessType String = AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST
) : ServerRequest<Attendance> {

    private val api: AccountDataApi
        get() = accountDataApi ?: AccountDataApi(SDKComponent.SDKContext.getContext())

    override fun setRequestUuid(uuid: String) {
        requestUuid = uuid
    }

    override suspend fun request(): NetworkResult<Attendance> {
        if (shouldThrowException) {
            throw RuntimeException("Unexpected server error")
        }
        return if (responseCode == 200) {
            val attendance = api.getAttendanceByDateAndAccountServerId(
                accountServerId = accountId,
                date = date
            )
                ?: return NetworkResult.Error(
                    ServerRequestErrorType.UNKNOWN_ERROR,
                    "No attendance found"
                )
            NetworkResult.Success(attendance.toAttendance(accountId))
        } else {
            NetworkResult.Error(
                code = responseCode,
                message = "Error"
            )
        }
    }

    override fun payload(): String {
        return "{\"requestUuid\":\"$requestUuid\",\"requestType\":\"${OfflineRequestType.ATTENDANCE}\"," +
                "\"businessType\":\"$businessType\",\"accountId\":\"$accountId\",\"date\":$date}"
    }
}
