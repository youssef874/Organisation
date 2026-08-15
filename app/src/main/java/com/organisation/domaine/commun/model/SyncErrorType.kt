package com.organisation.domaine.commun.model

import androidx.annotation.StringDef


@Target(AnnotationTarget.TYPE)
@StringDef(
    SyncErrorType.OFFLINE,
    SyncErrorType.UNKNOWN
)

annotation class SyncErrorType{

    companion object{
        const val OFFLINE = "offline"
        const val UNKNOWN = "unknown"
    }
}
