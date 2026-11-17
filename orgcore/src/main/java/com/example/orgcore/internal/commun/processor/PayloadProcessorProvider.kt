package com.example.orgcore.internal.commun.processor

import com.example.orgcore.internal.attendance.processor.AttendancePayloadProcessor
import com.example.orgcore.internal.attendance.processor.AttendanceUpdateStatePayloadProcessor
import com.example.orgdatabase.public.api.AccountDataApi

/**
 * Provider object for instantiating and wiring up the payload processor hierarchy (App, Domain, Leaf).
 */
internal object PayloadProcessorProvider {

    /**
     * Provides an instance of [AttendanceUpdateStatePayloadProcessor] for attendance status/cancellation updates.
     */
    fun getAttendanceUpdateStatePayloadProcessor(accountDataApi: AccountDataApi): PayloadProcessor<*> {
        return AttendanceUpdateStatePayloadProcessor(accountDataApi)
    }

    /**
     * Provides an instance of [AttendancePayloadProcessor] wired with attendance child processors.
     */
    fun getAttendancePayloadProcessor(accountDataApi: AccountDataApi): PayloadProcessor<*> {
        return AttendancePayloadProcessor(
            getAttendanceUpdateStatePayloadProcessor(accountDataApi)
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
