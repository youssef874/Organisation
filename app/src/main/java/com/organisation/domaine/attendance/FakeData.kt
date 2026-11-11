package com.organisation.domaine.attendance

import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.attendance.model.AttendanceStat
import com.organisation.ui.theme.AppAbsentColor
import com.organisation.ui.theme.AppLeaveColor
import com.organisation.ui.theme.AppPendingColor
import com.organisation.ui.theme.AppPresentColor
import java.time.LocalDate

val sampleStats = listOf(
    AttendanceStat("18", "PRESENT", AppPresentColor),
    AttendanceStat("2", "ABSENT", AppAbsentColor),
    AttendanceStat("3", "OFF DAYS", AppLeaveColor),
    AttendanceStat("1", "IN PROGRESS", AppPendingColor)
)

val dummyRecords = mapOf(
    LocalDate.of(2026, 9, 1) to
            AttendanceRecord(
                1L,
                1,
                "Saturday",
                null,
                null,
                AttendanceStatus.Holiday,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 2) to
            AttendanceRecord(
                2L,
                2,
                "Sunday",
                null,
                null,
                AttendanceStatus.Holiday,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 3) to
            AttendanceRecord(
                3L,
                3,
                "Monday",
                "09:00",
                "17:30",
                AttendanceStatus.Present,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 4) to
            AttendanceRecord(
                4L,
                4,
                "Tuesday",
                "08:55",
                "18:00",
                AttendanceStatus.Present,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 5) to
            AttendanceRecord(
                5L,
                5,
                "Wednesday",
                null,
                null,
                AttendanceStatus.Absent,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 6) to
            AttendanceRecord(
                6L,
                6,
                "Thursday",
                "09:10",
                null,
                AttendanceStatus.PartialLeave,
                "Doctor Appointment",
                monthOfYear = 9,
                year = 2026
    ),
    LocalDate.of(2026, 9, 7) to AttendanceRecord(
        7L,
        7,
        "Friday",
        null,
        null,
        AttendanceStatus.Pending,
        "Vacation Request",
        monthOfYear = 9,
        year = 2026
    ),
    LocalDate.of(2026, 9, 8) to
            AttendanceRecord(
                8L,
                8,
                "Saturday",
                null,
                null,
                AttendanceStatus.Holiday,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 9) to
            AttendanceRecord(
                9L,
                9,
                "Sunday",
                null,
                null,
                AttendanceStatus.Holiday,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 10) to
            AttendanceRecord(
                10L,
                10,
                "Monday",
                "08:45",
                "17:15",
                AttendanceStatus.Present,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 11) to
            AttendanceRecord(
                11L,
                11,
                "Tuesday",
                "09:00",
                "17:30",
                AttendanceStatus.Present,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026, 9, 24) to
            AttendanceRecord(
                id = 24L,
                dayOfMonth = 24,
                dayOfWeek = "Monday",
                checkIn = "09:00",
                checkOut = "17:30",
                status = AttendanceStatus.Present,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026,9,25) to
            AttendanceRecord(
                id = 25L,
                dayOfMonth = 25,
                dayOfWeek = "Tuesday",
                checkIn = "08:55",
                checkOut = "18:00",
                status = AttendanceStatus.Present,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026,9,26) to
            AttendanceRecord(
                id = 26L,
                dayOfMonth = 26,
                dayOfWeek = "Wednesday",
                checkIn = null,
                checkOut = null,
                status = AttendanceStatus.Absent,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026,9,27) to
            AttendanceRecord(
                id = 27L,
                dayOfMonth = 27,
                dayOfWeek = "Thursday",
                checkIn = "09:10",
                checkOut = null,
                status = AttendanceStatus.PartialLeave,
                requestReason = "Family Event",
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026,9,28) to
            AttendanceRecord(
                id = 28L,
                dayOfMonth = 28,
                dayOfWeek = "Friday",
                checkIn = null,
                checkOut = null,
                status = AttendanceStatus.Pending,
                requestReason = "Personal Leave",
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026,9,29) to
            AttendanceRecord(
                id = 29L,
                dayOfMonth = 29,
                dayOfWeek = "Saturday",
                checkIn = null,
                checkOut = null,
                status = AttendanceStatus.Holiday,
                monthOfYear = 9,
                year = 2026
            ),
    LocalDate.of(2026,9,30) to
            AttendanceRecord(
                id = 30L,
                dayOfMonth = 30,
                dayOfWeek = "Sunday",
                checkIn = null,
                checkOut = null,
                status = AttendanceStatus.Holiday,
                monthOfYear = 9,
                year = 2026
            )
)
