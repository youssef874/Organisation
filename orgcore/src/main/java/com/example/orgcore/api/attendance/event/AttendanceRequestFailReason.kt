package com.example.orgcore.api.attendance.event

import androidx.annotation.StringDef

/**
 * Defines the categorization of reasons why an attendance update or request operation failed.
 */
@StringDef(
    AttendanceRequestFailReason.NO_CONNECTION,
    AttendanceRequestFailReason.SUPERVISOR_REJECT,
    AttendanceRequestFailReason.SERVER_ISSUE,
    AttendanceRequestFailReason.TIMEOUT,
    AttendanceRequestFailReason.UNAUTHORIZED,
    AttendanceRequestFailReason.DATABASE_ERROR,
    AttendanceRequestFailReason.UNKNOWN
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AttendanceRequestFailReason {
    companion object {
        /** The request failed due to no internet connection or offline state. */
        const val NO_CONNECTION = "no_connection"
        /** The request was rejected by a supervisor. */
        const val SUPERVISOR_REJECT = "supervisor_reject"
        /** The request failed due to an internal server error or issue. */
        const val SERVER_ISSUE = "server_issue"
        /** The request timed out. */
        const val TIMEOUT = "timeout"
        /** The user is unauthorized or unauthenticated to perform the request. */
        const val UNAUTHORIZED = "unauthorized"
        /** The request failed while caching or persisting data to the local database. */
        const val DATABASE_ERROR = "database_error"
        /** The request failed due to an unknown error. */
        const val UNKNOWN = "unknown"
    }
}
