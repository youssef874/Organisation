package com.example.orgcore.internal.attendance.processor

import com.example.orgcore.internal.attendance.model.CancelAttendanceRequestConstraints
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class CancelAttendanceRequestPayloadProcessor : PayloadProcessor<CancelAttendanceRequestConstraints> {

    companion object {
        private const val CLASS_NAME = "CancelAttendanceRequestPayloadProcessor"
        private const val TAG = "ATTENDANCE"
        const val KEY_ACCOUNT_ID = "accountId"
        const val KEY_DATE = "date"
    }

    override suspend fun decode(json: JSONObject): CancelAttendanceRequestConstraints {
        return withContext(Dispatchers.IO) {
            LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|start decoding cancel attendance request payload")

            val accountId = json.optString(KEY_ACCOUNT_ID, "")
            val date = json.optLong(KEY_DATE, 0L)

            require(accountId.isNotBlank()) { "Invalid or missing accountId in cancel attendance request payload" }
            require(date > 0L) { "Invalid or missing date in cancel attendance request payload: $date" }

            LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|successfully decoded cancel attendance request for accountId: $accountId, date: $date")

            CancelAttendanceRequestConstraints(
                accountId = accountId,
                date = date
            )
        }
    }
}
