package com.youssefmkh.logger


/**
 * This is abstract represent manager for [LogAdapter], [Formatter] and [Strategy],
 * and interact between api and internal
 */
interface Manger {

    fun v(tag: String, message: String)

    fun d(tag: String,message: String)

    fun e(tag: String,message: String)

    fun e(throwable: Throwable,tag: String,message: String)

    fun w(tag: String, message: String)

    fun i(tag: String,message: String)

    fun wtf(tag: String,message: String)

    fun addAdapter(logAdapter: LogAdapter)

    fun clear()
}