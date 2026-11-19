package com.example.orgcore.internal.commun.model

import androidx.annotation.IntDef

@IntDef(
    ServerErrorCode.BAD_REQUEST,
    ServerErrorCode.UNAUTHORIZED,
    ServerErrorCode.FORBIDDEN,
    ServerErrorCode.NOT_FOUND,
    ServerErrorCode.REQUEST_TIMEOUT,
    ServerErrorCode.UNPROCESSABLE_ENTITY,
    ServerErrorCode.TOO_MANY_REQUESTS,
    ServerErrorCode.INTERNAL_SERVER_ERROR,
    ServerErrorCode.BAD_GATEWAY,
    ServerErrorCode.SERVICE_UNAVAILABLE,
    ServerErrorCode.GATEWAY_TIMEOUT
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
internal annotation class ServerErrorCode {
    companion object {
        const val BAD_REQUEST = 400
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val REQUEST_TIMEOUT = 408
        const val UNPROCESSABLE_ENTITY = 422
        const val TOO_MANY_REQUESTS = 429
        const val INTERNAL_SERVER_ERROR = 500
        const val BAD_GATEWAY = 502
        const val SERVICE_UNAVAILABLE = 503
        const val GATEWAY_TIMEOUT = 504
    }
}

internal object ServerErrorUtil {
    val PERMANENT_CLIENT_ERRORS = setOf(
        ServerErrorCode.BAD_REQUEST,
        ServerErrorCode.UNAUTHORIZED,
        ServerErrorCode.FORBIDDEN,
        ServerErrorCode.NOT_FOUND,
        ServerErrorCode.REQUEST_TIMEOUT,
        ServerErrorCode.UNPROCESSABLE_ENTITY
    )
}
