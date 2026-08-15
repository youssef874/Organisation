package com.example.orgdatabase.internal.mapper

import com.example.orgdatabase.internal.entity.AccountEntity
import com.example.orgdatabase.internal.entity.AccountWithDetails
import com.example.orgdatabase.internal.entity.EmailEntity
import com.example.orgdatabase.internal.entity.PhoneNumberEntity
import com.example.orgdatabase.public.model.DBAccount

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
