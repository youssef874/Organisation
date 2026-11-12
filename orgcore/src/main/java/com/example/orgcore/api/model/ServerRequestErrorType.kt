package com.example.orgcore.api.model

import androidx.annotation.IntDef


@IntDef(
    ServerRequestErrorType.INTERNAL_SERVER_ERROR,
    ServerRequestErrorType.SERVER_CONNECTION_ERROR,
    ServerRequestErrorType.NO_NETWORK,
    ServerRequestErrorType.UNKNOWN_ERROR
)

@Target(AnnotationTarget.TYPE)
internal annotation class ServerRequestErrorType{

    companion object{

        const val NO_NETWORK = 0x0001
        const val SERVER_CONNECTION_ERROR = 0x0002
        const val INTERNAL_SERVER_ERROR = 0x0003
        const val UNKNOWN_ERROR = 0x0004
    }
}

open class ServerRequestError(val code: @ServerRequestErrorType Int, val msg: String)
