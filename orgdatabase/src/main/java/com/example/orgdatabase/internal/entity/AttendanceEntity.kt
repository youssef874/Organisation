package com.example.orgdatabase.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = AttendanceEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = [AccountEntity.COLUMN_ID],
            childColumns = [AttendanceEntity.COLUMN_ACCOUNT_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [AttendanceEntity.COLUMN_ACCOUNT_ID]),
        Index(value = [AttendanceEntity.COLUMN_DATE, AttendanceEntity.COLUMN_ATTENDANCE_STATUS])
    ]
)
internal data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Long = 0,
    @ColumnInfo(name = COLUMN_ACCOUNT_ID)
    val accountId: Long,
    @ColumnInfo(name = COLUMN_CHECK_IN)
    val checkIn: Long,
    @ColumnInfo(name = COLUMN_CHECK_OUT)
    val checkOut: Long,
    @ColumnInfo(name = COLUMN_DATE)
    val date: Long,
    @ColumnInfo(name = COLUMN_ATTENDANCE_STATUS)
    val attendanceStatus: String,
    @ColumnInfo(name = COLUMN_REQUEST_REASON)
    val requestReason: String? = null,
    @ColumnInfo(name = COLUMN_IS_APPROVED)
    val isApproved: Boolean = false,
    @ColumnInfo(name = COLUMN_IS_REQUEST_IN_PROGRESS)
    val isRequestInProgress: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "attendance"
        const val COLUMN_ID = "id"
        const val COLUMN_ACCOUNT_ID = "account_id"
        const val COLUMN_CHECK_IN = "check_in"
        const val COLUMN_CHECK_OUT = "check_out"
        const val COLUMN_DATE = "date"
        const val COLUMN_ATTENDANCE_STATUS = "attendance_status"
        const val COLUMN_REQUEST_REASON = "request_reason"
        const val COLUMN_IS_APPROVED = "is_approved"
        const val COLUMN_IS_REQUEST_IN_PROGRESS = "is_request_in_progress"

        // Status values
        const val STATUS_ABSENT = "absent"
        const val STATUS_PRESENT = "present"
        const val STATUS_IN_PROGRESS = "in_progress"
        const val STATUS_HOLIDAY = "holiday"
        const val STATUS_PERSONAL_HOLIDAY = "personal holiday"
        const val STATUS_WEEKEND = "weekend"
        const val STATUS_PAID_LEAVE = "paid leave"
        const val STATUS_UNPAID_LEAVE = "unpaid leave"
        const val STATUS_PAID_PARTIAL_LEAVE = "paid partial leave"
        const val STATUS_UNPAID_PARTIAL_LEAVE = "unpaid_partial_leave"
        const val STATUS_NONE = "none"
    }
}
