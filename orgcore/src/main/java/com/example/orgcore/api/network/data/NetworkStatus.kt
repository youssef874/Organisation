package com.example.orgcore.api.network.data

import androidx.annotation.IntDef


@Target(AnnotationTarget.TYPE)
@IntDef(
    NetworkStatus.NOT_CONNECTED,
    NetworkStatus.GOOD_CONNECTION,
    NetworkStatus.BAD_CONNECTION
)

annotation class NetworkStatus{

    companion object{
        const val NOT_CONNECTED = 0x0001
        const val GOOD_CONNECTION = 0x0002
        const val BAD_CONNECTION = 0x0003
    }
}
