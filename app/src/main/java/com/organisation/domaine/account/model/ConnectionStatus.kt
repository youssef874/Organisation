package com.organisation.domaine.account.model

import androidx.annotation.IntDef

@Target(AnnotationTarget.TYPE)
@IntDef(
    ConnectionStatus.NO_NETWORK,
    ConnectionStatus.WEAK_CONNECTION,
    ConnectionStatus.GOOD_CONNECTION
)

annotation class ConnectionStatus{

    companion object{
        const val NO_NETWORK = 0x0001
        const val WEAK_CONNECTION = 0x0003
        const val GOOD_CONNECTION = 0x0004
    }
}
