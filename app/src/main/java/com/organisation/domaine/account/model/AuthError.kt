package com.organisation.domaine.account.model

import androidx.annotation.IntDef

@Target(AnnotationTarget.TYPE)
@IntDef(
    AuthError.USER_NOT_AUTHENTICATED,
    AuthError.INTERNAL_SERVER_ERROR,
    AuthError.NO_INTERNET_CONNECTION,
    AuthError.UNKNOWN_ERROR
)
annotation class AuthError {

    companion object{
        const val NO_INTERNET_CONNECTION = 0x0001
        const val INTERNAL_SERVER_ERROR = 0x0002
        const val USER_NOT_AUTHENTICATED = 0x0004
        const val UNKNOWN_ERROR = 0x0005
    }
}