package com.organisation.ui.attendance

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.organisation.R
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.attendance.model.AttendanceStat
import com.organisation.ui.attendance.model.CalenderDisplayMode
import com.organisation.ui.attendance.model.DateRange
import com.organisation.ui.theme.AppAbsentColor
import com.organisation.ui.theme.AppLeaveColor
import com.organisation.ui.theme.AppNoneColor
import com.organisation.ui.theme.AppPartialLeaveColor
import com.organisation.ui.theme.AppPendingColor
import com.organisation.ui.theme.AppPresentColor
import com.organisation.ui.theme.AppWeekendColor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


@Composable
fun AttendanceStat.getLabelAndColor(): AttendanceStat {
    var label: String
    var color: Color
    when (this.attendanceStatus) {
        AttendanceStatus.Present -> {
            color = AppPresentColor
            label = stringResource(R.string.status_present)
        }

        AttendanceStatus.Absent -> {
            color = AppAbsentColor
            label = stringResource(R.string.status_absent)
        }
        AttendanceStatus.Leave -> {
            color = AppLeaveColor
            label = stringResource(R.string.status_leave)
        }
        AttendanceStatus.InProgress -> {
            color = AppPendingColor
            label = stringResource(R.string.status_in_progress)
        }
        AttendanceStatus.Holiday -> {
            color = AppWeekendColor
            label = stringResource(R.string.status_holiday)
        }

        AttendanceStatus.PartialLeave -> {
            color = AppPartialLeaveColor
            label = stringResource(R.string.status_partial_leave)
        }
        AttendanceStatus.Pending -> {
            color = AppPendingColor
            label = stringResource(R.string.status_pending)
        }
        AttendanceStatus.PersonalHoliday -> {
            color = AppWeekendColor
            label = stringResource(R.string.status_personal_holiday)
        }
        AttendanceStatus.None -> {
            color = AppNoneColor
            label = ""
        }
        AttendanceStatus.Weekend -> {
            color = AppWeekendColor
            label = stringResource(R.string.status_weekend)
        }
    }
    return this.copy(label = label, valueColor = color)
}

internal fun LocalDate.startOfPeriod(mode: CalenderDisplayMode): LocalDate = when (mode) {
    CalenderDisplayMode.MONTH -> withDayOfMonth(1)
    CalenderDisplayMode.WEEK  -> weekStart()
}

/**
 * Week start. Defaults to Monday; override with the locale's first day if needed
 * via `WeekFields.of(locale).firstDayOfWeek`.
 */
internal fun LocalDate.weekStart(firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
    val diff = (dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    return minusDays(diff.toLong())
}

/** The full range covered by this date's period in the given mode. */
internal fun LocalDate.rangeFor(mode: CalenderDisplayMode): DateRange = when (mode) {
    CalenderDisplayMode.MONTH -> DateRange(this.withDayOfMonth(1), with(TemporalAdjusters.lastDayOfMonth()))
    CalenderDisplayMode.WEEK  -> {
        val start = weekStart()
        DateRange(start, start.plusDays(6))
    }
}

/** Move forward / backward by one period in the given mode. */
internal fun LocalDate.advance(units: Long, mode: CalenderDisplayMode): LocalDate = when (mode) {
    CalenderDisplayMode.MONTH -> plusMonths(units)
    CalenderDisplayMode.WEEK  -> plusWeeks(units)
}