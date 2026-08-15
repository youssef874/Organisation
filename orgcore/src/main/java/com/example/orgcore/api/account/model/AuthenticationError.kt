package com.example.orgcore.api.account.model

import androidx.annotation.IntDef
import com.youssefmkh.core.internal.commun.model.ServerRequestError
import com.youssefmkh.core.internal.commun.model.ServerRequestErrorType


@IntDef(
    AuthenticationErrorType.NO_NETWORK,
    AuthenticationErrorType.INTERNAL_SERVER_ERROR,
    AuthenticationErrorType.SERVER_CONNECTION_ERROR,
    AuthenticationErrorType.AUTHENTICATION_REQUEST_NOT_VALIDATED,
    AuthenticationErrorType.UNKNOWN_ERROR,
    AuthenticationErrorType.TIMEOUT,
    AuthenticationErrorType.ALREADY_AUTHENTICATED
)

@Target(AnnotationTarget.TYPE)
annotation class AuthenticationErrorType{

    companion object{

        const val NO_NETWORK = ServerRequestErrorType.NO_NETWORK
        const val SERVER_CONNECTION_ERROR = ServerRequestErrorType.SERVER_CONNECTION_ERROR
        const val INTERNAL_SERVER_ERROR = ServerRequestErrorType.INTERNAL_SERVER_ERROR
        const val UNKNOWN_ERROR = ServerRequestErrorType.UNKNOWN_ERROR

        const val AUTHENTICATION_REQUEST_NOT_VALIDATED = 0x0005

        const val ALREADY_AUTHENTICATED = 0x0006
        const val TIMEOUT = 0x0007
    }
}


data class AuthenticationError (val errorCode: @AuthenticationErrorType Int, val message: String): ServerRequestError(errorCode,message)