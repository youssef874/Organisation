package com.example.orgcore.internal.commun.processor

import com.example.orgcore.internal.attendance.model.CancelAttendanceRequestConstraints
import com.example.orgcore.internal.attendance.processor.AttendancePayloadProcessor
import com.example.orgcore.internal.attendance.processor.CancelAttendanceRequestPayloadProcessor
import com.example.orgcore.internal.commun.model.RequestConstraint

/**
 * Provider object for instantiating and wiring up the payload processor hierarchy (App, Domain, Leaf).
 */
internal object PayloadProcessorProvider {


    /**
     * Provides an instance of [CancelAttendanceRequestPayloadProcessor] for cancel attendance requests.
     */
    fun getCancelAttendanceRequestPayloadProcessor(): PayloadProcessor<CancelAttendanceRequestConstraints> {
        return CancelAttendanceRequestPayloadProcessor()
    }

    /**
     * Provides an instance of [AttendancePayloadProcessor] handling all attendance request constraints.
     */
    fun getAttendancePayloadProcessor(): PayloadProcessor<RequestConstraint> {
        return AttendancePayloadProcessor(
            cancelAttendancePayloadProcessor = {
                getCancelAttendanceRequestPayloadProcessor()
            }
        )
    }

    /**
     * Provides the root [AppPayloadProcessor] instance wired with all domain payload processors.
     */
    fun getAppPayloadProcessor(): PayloadProcessor<RequestConstraint> {
        return AppPayloadProcessor(
            attendancePayloadProcessor = {
                getAttendancePayloadProcessor()
            }
        )
    }
}
