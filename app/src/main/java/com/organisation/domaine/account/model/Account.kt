package com.organisation.domaine.account.model

import android.net.Uri

data class Account(
    val id: Long,
    val name: String,
    val phoneNumber: String?,
    val email: String?,
    val image: Uri?,
    val type: @AccountType String
)
