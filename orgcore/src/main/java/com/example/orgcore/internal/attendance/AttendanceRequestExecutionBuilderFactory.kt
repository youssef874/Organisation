package com.example.orgcore.internal.attendance

import com.example.orgcore.internal.attendance.cache.AttendanceUpdateCache
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.attendance.request.FakeAttendanceUpdatesServerRequest
import com.example.orgcore.internal.commun.RequestExecutionBuilder
import com.example.orgcore.internal.commun.model.RequestPriority
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.OfflineRequestType

internal class AttendanceRequestExecutionBuilderFactory(
    private val accountDataApi: AccountDataApi
) {

    fun create(
        attendance: Attendance
    ): RequestExecutionBuilder<Attendance> {

        return RequestExecutionBuilder(
            request = FakeAttendanceUpdatesServerRequest(
                updatedAttendance = attendance,
                businessType = AttendanceBusinessType.ATTENDANCE_UPDATE
            )
        )
            .allowOfflineQueue(true)
            .requestType(OfflineRequestType.ATTENDANCE)
            .priority(RequestPriority.PRIORITY_HIGH)
            .cache(AttendanceUpdateCache(accountDataApi))
    }
}
