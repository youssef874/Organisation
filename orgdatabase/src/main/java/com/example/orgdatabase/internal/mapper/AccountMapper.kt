package com.example.orgdatabase.internal.mapper

import com.example.orgdatabase.internal.entity.AccountEntity
import com.example.orgdatabase.internal.entity.AccountWithDetails
import com.example.orgdatabase.internal.entity.AttendanceEntity
import com.example.orgdatabase.internal.entity.EmailEntity
import com.example.orgdatabase.internal.entity.PhoneNumberEntity
import com.example.orgdatabase.public.model.DBAccount
import com.example.orgdatabase.public.model.DBAttendance

internal fun AccountWithDetails.toDBAccount(): DBAccount {
    return DBAccount(
        id = account.id,
        name = account.name,
        photoUri = account.photoUri,
        isUser = account.isUser,
        accountType = account.accountType,
        serverId = account.serverId,
        position = account.position,
        supervisorId = account.supervisorId,
        emails = emails.map { it.email },
        phoneNumbers = phoneNumbers.map { it.number }
    )
}

internal fun AccountEntity.toDBAccount(): DBAccount {
    return DBAccount(
        id = id,
        name = name,
        photoUri = photoUri,
        isUser = isUser,
        accountType = accountType,
        serverId = serverId,
        position = position,
        supervisorId = supervisorId,
        emails = emptyList(),
        phoneNumbers = emptyList()
    )
}

internal fun AttendanceEntity.toDBAttendance(): DBAttendance {
    return DBAttendance(
        id = id,
        accountId = accountId,
        serverId = serverId,
        checkIn = checkIn,
        checkOut = checkOut,
        date = date,
        attendanceStatus = attendanceStatus,
        requestReason = requestReason,
        isApproved = isApproved,
        isRequestInProgress = isRequestInProgress
    )
}

internal fun DBAttendance.toAttendanceEntity(): AttendanceEntity {
    return AttendanceEntity(
        id = id,
        accountId = accountId,
        serverId = serverId,
        checkIn = checkIn,
        checkOut = checkOut,
        date = date,
        attendanceStatus = attendanceStatus,
        requestReason = requestReason,
        isApproved = isApproved,
        isRequestInProgress = isRequestInProgress
    )
}

internal fun DBAccount.extractEmails(accountId: Long): List<EmailEntity> {
    return emails.map {
        EmailEntity(
            email = it,
            accountId = accountId
        )
    }
}

internal fun DBAccount.extractPhoneNumbers(accountId: Long): List<PhoneNumberEntity>{
    return phoneNumbers.map {
        PhoneNumberEntity(
            number = it,
            accountId = accountId
        )
    }
}

internal fun DBAccount.toAccountEntity(): AccountEntity{
    return AccountEntity(
        name = name,
        photoUri = photoUri,
        isUser = isUser,
        accountType = accountType,
        serverId = serverId,
        position = position
    )
}
