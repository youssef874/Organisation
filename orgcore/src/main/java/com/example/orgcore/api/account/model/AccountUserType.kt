package com.example.orgcore.api.account.model

import androidx.annotation.IntDef

@Target(AnnotationTarget.TYPE)
@IntDef(
    AccountUserType.BOSS,
    AccountUserType.MANAGER,
    AccountUserType.WORKER
)

annotation class AccountUserType{
    companion object{
        const val BOSS = 0
        const val MANAGER = 1
        const val WORKER = 2
    }
}