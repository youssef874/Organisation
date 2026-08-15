package com.youssefmkh.logger


/**
 * Used to determine how messages should be printed or saved.
 */
interface Formatter {

    fun log(priority: @Priority Int,tag: String?,message: String)
}