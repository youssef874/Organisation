package com.example.orgcore.api.account.model

import android.net.Uri

data class OrgAccount (
    val id: Long,
    val name: String,
    val phoneNumber: String?,
    val email: String?,
    val image: Uri?,
    val type: @AccountUserType Int
)