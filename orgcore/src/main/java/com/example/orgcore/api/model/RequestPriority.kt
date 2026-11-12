package com.example.orgcore.api.model

import androidx.annotation.IntDef


@IntDef(
    RequestPriority.PRIORITY_LOW,
    RequestPriority.PRIORITY_NORMAL,
    RequestPriority.PRIORITY_HIGH
)

@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
internal annotation class RequestPriority{
    companion object{
        const val PRIORITY_LOW = -5
        const val PRIORITY_NORMAL = 0
        const val PRIORITY_HIGH = 10
    }
}
