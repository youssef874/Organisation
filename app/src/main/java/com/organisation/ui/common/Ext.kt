package com.organisation.ui.common

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.organisation.R
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.theme.AppAbsentColor
import com.organisation.ui.theme.AppLeaveColor
import com.organisation.ui.theme.AppNoneColor
import com.organisation.ui.theme.AppPartialLeaveColor
import com.organisation.ui.theme.AppPendingColor
import com.organisation.ui.theme.AppPresentColor
import com.organisation.ui.theme.AppWeekendColor

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffSetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xC6E6DFDF),
                Color(0xFFA09D9D),
                Color(0xDCE6DFDF)
            ),
            start = Offset(startOffSetX, 0f),
            end = Offset(startOffSetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}


fun AttendanceStatus.getColor(): Color {
    return when (this) {
        AttendanceStatus.Present -> AppPresentColor
        AttendanceStatus.Absent -> AppAbsentColor
        AttendanceStatus.Leave -> AppLeaveColor
        AttendanceStatus.PartialLeave -> AppPartialLeaveColor
        AttendanceStatus.Pending -> AppPendingColor
        AttendanceStatus.Holiday,
        AttendanceStatus.PersonalHoliday,
        AttendanceStatus.Weekend -> AppWeekendColor
        else -> AppNoneColor
    }
}

fun AttendanceStatus.getStringResId(): Int {
    return when (this) {
        AttendanceStatus.Present -> R.string.status_present
        AttendanceStatus.Absent -> R.string.status_absent
        AttendanceStatus.Leave -> R.string.status_leave
        AttendanceStatus.PartialLeave -> R.string.status_partial_leave
        AttendanceStatus.Pending -> R.string.status_pending
        AttendanceStatus.Holiday -> R.string.status_holiday
        AttendanceStatus.PersonalHoliday-> R.string.status_personal_holiday
        AttendanceStatus.Weekend -> R.string.status_weekend
        AttendanceStatus.None -> R.string.status_none
        AttendanceStatus.InProgress -> R.string.status_in_progress
    }
}
