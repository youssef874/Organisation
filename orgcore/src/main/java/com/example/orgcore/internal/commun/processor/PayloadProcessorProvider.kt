package com.example.orgcore.internal.commun.processor

import com.example.orgcore.internal.attendance.model.AttendanceRequestConstraint
import com.example.orgcore.internal.attendance.processor.AttendanceUpdatePayloadProcessor
import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgdatabase.public.api.AccountDataApi

/**
 * Provider object for instantiating and wiring up the payload processor hierarchy (App, Domain, Leaf).
 */
internal object PayloadProcessorProvider {

    /**
     * Provides an instance of [AttendanceUpdatePayloadProcessor] for attendance status/cancellation updates.
     */
    fun getAttendanceUpdatePayloadProcessor(accountDataApi: AccountDataApi): PayloadProcessor<AttendanceRequestConstraint> {
        return AttendanceUpdatePayloadProcessor(accountDataApi)
    }

    /**
     * Provides the root [AppPayloadProcessor] instance wired with all domain payload processors.
     */
    fun getAppPayloadProcessor(accountDataApi: AccountDataApi): PayloadProcessor<RequestConstraint> {
        return AppPayloadProcessor(
            getAttendanceUpdatePayloadProcessor(accountDataApi)
        )
    }
}
