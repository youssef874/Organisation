package com.example.orgcore.internal.commun.model

import androidx.annotation.IntDef

@IntDef(
    ServerErrorCode.UNAUTHORIZED,
    ServerErrorCode.FORBIDDEN,
    ServerErrorCode.REQUEST_TIMEOUT,
    ServerErrorCode.UNPROCESSABLE_ENTITY,
    ServerErrorCode.GATEWAY_TIMEOUT
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
internal annotation class ServerErrorCode {
    companion object {
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val REQUEST_TIMEOUT = 408
        const val UNPROCESSABLE_ENTITY = 422
        const val GATEWAY_TIMEOUT = 504
    }
}
