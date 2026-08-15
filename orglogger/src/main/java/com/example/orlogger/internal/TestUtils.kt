package com.youssefmkh.logger.internal

internal object TestUtils {

    var log: ((Int,String?,String)->Unit)? = null

    fun afterLog(priority: Int, tag: String?, message: String){
        log?.invoke(priority,tag,message)
    }

    fun clearLog(){
        log = null
    }
}