package com.example.orgcore.api.account.model

data class AuthenticatedAccount(
    val name: String,
    val accountUserType: @AccountUserType Int,
    val phoneNumber: String?
)
