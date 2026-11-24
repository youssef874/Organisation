package com.example.orgcore.internal.commun.processor

import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.processor.AttendancePayloadProcessor
import com.example.orgcore.internal.attendance.processor.AttendanceUpdatePayloadProcessor
import com.example.orgdatabase.public.api.AccountDataApi

/**
 * Provider object for instantiating and wiring up the payload processor hierarchy (App, Domain, Leaf).
 */
internal object PayloadProcessorProvider {

    /**
     * Provides an instance of [AttendanceUpdatePayloadProcessor] for attendance status/cancellation updates.
     */
    fun getAttendanceUpdatePayloadProcessor(accountDataApi: AccountDataApi): PayloadProcessor<Attendance> {
        return AttendanceUpdatePayloadProcessor(accountDataApi)
    }

    /**
     * Provides an instance of [AttendancePayloadProcessor] wired with attendance child processors.
     */
    fun getAttendancePayloadProcessor(accountDataApi: AccountDataApi): PayloadProcessor<*> {
        return AttendancePayloadProcessor(
            getAttendanceUpdatePayloadProcessor(accountDataApi)
        )
    }

    /**
     * Provides the root [AppPayloadProcessor] instance wired with all domain payload processors.
     */
    fun getAppPayloadProcessor(accountDataApi: AccountDataApi): PayloadProcessor<*> {
        return AppPayloadProcessor(
            getAttendancePayloadProcessor(accountDataApi)
        )
    }
}
