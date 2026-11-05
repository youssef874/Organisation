package com.organisation.ui.attendance.model

import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus

sealed class AttendanceEvent {
    data class NewDaySelected(val record: AttendanceRecord) : AttendanceEvent()
    data class FilterRecordsBy(val status: AttendanceStatus) : AttendanceEvent()

    data object NextPeriod : AttendanceEvent()

    data object PreviousPeriod : AttendanceEvent()

    data class OnOrientationChanges(val newMode: CalenderDisplayMode) : AttendanceEvent()

    data object RequestOff : AttendanceEvent()
    data object CancelRequest : AttendanceEvent()
}
