package com.example.orgcore.internal.attendance.executor

import com.example.orgcore.internal.attendance.model.AttendanceRequestConstraints
import com.example.orgcore.internal.attendance.model.CancelAttendanceRequestConstraints
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.executor.RequestExecutor
import com.example.orgcore.internal.commun.model.RequestResult
import com.example.orgcore.internal.commun.model.ServerRequestErrorType
import com.example.orgdatabase.public.model.DBAttendance

internal class AttendanceRequestExecutor(
    private val cancelAttendanceRequestExecutor: (CancelAttendanceRequestConstraints)-> RequestExecutor<DBAttendance, CancelAttendanceRequestConstraints>
): RequestExecutor<Any, AttendanceRequestConstraints> {

    companion object {
        private const val CLASS_NAME = "AttendanceRequestExecutor"
        private const val TAG = "ATTENDANCE"
    }

    override suspend fun execute(constraint: AttendanceRequestConstraints): RequestResult<Any> {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|execute|requestType: ${constraint.requestType}, businessType: ${constraint.businessType}"
        )
        return when (constraint) {
            is CancelAttendanceRequestConstraints -> {
                LoggerHelper.logger.i(TAG, "$CLASS_NAME|execute|routing to cancel attendance request executor")
                cancelAttendanceRequestExecutor(constraint).execute(constraint)
            }
            else -> {
                LoggerHelper.logger.e(
                    TAG,
                    "$CLASS_NAME|execute|unknown or unsupported attendance request constraint type: ${constraint::class.java.name}"
                )
                RequestResult.ServerFailure(ServerRequestErrorType.UNKNOWN_ERROR)
            }
        }
    }
}
