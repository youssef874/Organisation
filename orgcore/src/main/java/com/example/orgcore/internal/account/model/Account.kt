package com.example.orgcore.internal.account.model

import com.example.orgcore.api.account.model.AccountUserType

data class Account(
    val id: String,
    val name: String,
    val phoneNumber: String?,
    val email: String?,
    val image: String?,
    val type: @AccountUserType Int
)
