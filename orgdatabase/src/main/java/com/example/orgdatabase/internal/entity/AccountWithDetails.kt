package com.example.orgdatabase.internal.entity

import androidx.room.Embedded
import androidx.room.Relation

internal data class AccountWithDetails(
    @Embedded val account: AccountEntity,
    @Relation(
        parentColumn = AccountEntity.COLUMN_ID,
        entityColumn = EmailEntity.COLUMN_ACCOUNT_ID
    )
    val emails: List<EmailEntity>,
    @Relation(
        parentColumn = AccountEntity.COLUMN_ID,
        entityColumn = PhoneNumberEntity.COLUMN_ACCOUNT_ID
    )
    val phoneNumbers: List<PhoneNumberEntity>,
    @Relation(
        parentColumn = AccountEntity.COLUMN_ID,
        entityColumn = AttendanceEntity.COLUMN_ACCOUNT_ID
    )
    val attendanceRecords: List<AttendanceEntity>
)
