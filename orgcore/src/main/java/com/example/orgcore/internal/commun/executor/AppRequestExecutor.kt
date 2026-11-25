package com.example.orgcore.internal.commun.executor

import com.example.orgcore.internal.account.Authentication.model.AuthenticationRequestConstraint
import com.example.orgcore.internal.attendance.model.AttendanceRequestConstraint
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgcore.internal.commun.model.RequestResult
import com.example.orgcore.internal.commun.model.ServerRequestErrorType

internal class AppRequestExecutor(
    private val authentificationRequestExecutor: (AuthenticationRequestConstraint) -> RequestExecutor<Any, AuthenticationRequestConstraint>,
    private val attendanceRequestExecutor: (AttendanceRequestConstraint)-> RequestExecutor<Any, AttendanceRequestConstraint>
): RequestExecutor<Any, RequestConstraint> {

    companion object {
        private const val CLASS_NAME = "AppRequestExecutor"
        private const val TAG = "APPLICATION"
    }

    override suspend fun execute(constraint: RequestConstraint): RequestResult<Any> {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|execute|requestType: ${constraint.requestType}, businessType: ${constraint.businessType}"
        )
        return when (constraint) {
            is AuthenticationRequestConstraint -> {
                LoggerHelper.logger.i(TAG, "$CLASS_NAME|execute|routing to authentication executor")
                authentificationRequestExecutor(constraint).execute(constraint)
            }
            is AttendanceRequestConstraint -> {
                LoggerHelper.logger.i(TAG, "$CLASS_NAME|execute|routing to attendance executor")
                attendanceRequestExecutor(constraint).execute(constraint)
            }
            else -> {
                LoggerHelper.logger.e(
                    TAG,
                    "$CLASS_NAME|execute|unknown or unsupported request constraint type: ${constraint::class.java.name}"
                )
                RequestResult.ServerFailure(ServerRequestErrorType.UNKNOWN_ERROR)
            }
        }
    }
}
