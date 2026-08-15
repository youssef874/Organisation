package com.example.orgcore.internal.account.Authentication.mapper

import com.example.orgcore.api.account.model.AuthenticatedAccount
import com.example.orgcore.internal.account.model.Account
import com.example.orgdatabase.public.model.DBAccount

internal fun Account.toApiAuthenticatedAccount(): AuthenticatedAccount {
    return AuthenticatedAccount(
        name = this.name,
        accountUserType = this.type,
        phoneNumber = this.phoneNumber
    )
}

internal fun Account.toDBAccount(isUser: Boolean): DBAccount = DBAccount(
    name = this.name,
    accountType = this.type,
    phoneNumbers = phoneNumber?.let { listOf(it) }?:listOf(),
    emails = email?.let { listOf(it) }?:listOf(),
    photoUri = image,
    isUser = isUser,
    serverId = id
)
