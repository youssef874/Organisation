package com.example.orgcore.internal.commun.model

import androidx.annotation.StringDef

@StringDef(
    RequestType.ATTENDANCE,
    RequestType.AUTHENTICATION
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class RequestType {
    companion object {
        const val ATTENDANCE = "attendance"
        const val AUTHENTICATION = "authentication"
    }
}
