package com.example.orgcore.internal.attendance.request

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgdatabase.public.model.OfflineRequestType
import java.util.UUID

internal class FakeAttendanceUpdatesServerRequest(
    var responseCode: Int = 200,
    var shouldThrowException: Boolean = false,
    private var requestUuid: String = UUID.randomUUID().toString(),
    private var updatedAttendance: Attendance,
    private val businessType: @AttendanceBusinessType String = AttendanceBusinessType.ATTENDANCE_UPDATE
) : ServerRequest<Attendance> {

    override fun setRequestUuid(uuid: String) {
        requestUuid = uuid
    }

    override suspend fun request(): NetworkResult<Attendance> {
        if (shouldThrowException) {
            throw RuntimeException("Unexpected server error")
        }
        return if (responseCode == 200) {
            NetworkResult.Success(updatedAttendance)
        } else {
            NetworkResult.Error(
                code = responseCode,
                message = "Error"
            )
        }
    }

    override fun payload(): String {
        return "{\"requestUuid\":\"$requestUuid\",\"requestType\":\"${OfflineRequestType.ATTENDANCE}\"," +
                "\"businessType\":\"$businessType\",\"id\":\"${updatedAttendance.id}\"," +
                "\"accountId\":\"${updatedAttendance.serverAccountId}\",\"checkIn\":${updatedAttendance.checkIn}," +
                "\"checkOut\":${updatedAttendance.checkOut},\"date\":${updatedAttendance.date}," +
                "\"attendanceStatus\":\"${updatedAttendance.attendanceStatus}\"," +
                "\"requestReason\":${updatedAttendance.requestReason?.let { "\"$it\"" } ?: "null"}," +
                "\"isRequestInProgress\":${updatedAttendance.isRequestInProgress}}"
    }
}
