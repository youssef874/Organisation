package com.example.orgcore.internal.attendance.processor

import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.attendance.model.AttendanceRequestConstraints
import com.example.orgcore.internal.attendance.model.CancelAttendanceRequestConstraints
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class AttendancePayloadProcessor(
    private val cancelAttendancePayloadProcessor: () -> PayloadProcessor<CancelAttendanceRequestConstraints>
): PayloadProcessor<AttendanceRequestConstraints> {

    companion object {
        private const val CLASS_NAME = "AttendancePayloadProcessor"
        private const val TAG = "ATTENDANCE"
        const val KEY_BUSINESS_TYPE = "businessType"
    }

    override suspend fun decode(json: JSONObject): AttendanceRequestConstraints {
        return withContext(Dispatchers.IO) {
            LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|start decoding attendance payload")
            val businessType = json.optString(KEY_BUSINESS_TYPE)
            LoggerHelper.logger.d(TAG, "$CLASS_NAME|decode|businessType: $businessType")

            val constraint = when (businessType) {
                AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST -> {
                    LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|routing to cancel attendance payload processor")
                    cancelAttendancePayloadProcessor().decode(json)
                }
                else -> {
                    LoggerHelper.logger.e(TAG, "$CLASS_NAME|decode|unknown or unsupported attendance businessType: $businessType")
                    throw IllegalStateException("Unknown or unsupported attendance businessType: $businessType")
                }
            }

            LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|successfully decoded attendance request constraint for businessType: $businessType")
            constraint
        }
    }
}
