package com.example.orgcore.internal.account.Authentication.model

import androidx.annotation.StringDef

@StringDef(
    AuthenticationBusinessType.LOGIN
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AuthenticationBusinessType {
    companion object {
        const val LOGIN = "login"
    }
}
