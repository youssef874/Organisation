package com.organisation.domaine.account.model

import androidx.annotation.StringDef


@Target(AnnotationTarget.TYPE)
@StringDef(
    AccountType.BOSS,
    AccountType.WORKER,
    AccountType.UNKNOWN,
    AccountType.MANAGER
)
annotation class AccountType {

    companion object {
        const val BOSS = "boss"
        const val WORKER = "worker"

        const val MANAGER = "manager"

        const val UNKNOWN = "unknown"
    }
}
