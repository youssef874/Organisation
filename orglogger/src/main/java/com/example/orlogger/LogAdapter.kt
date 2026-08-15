package com.youssefmkh.logger


/**
 * This abstract for log to be emitted through it
 */
interface LogAdapter {

    /**
     * To determine if the log message should be taken or not
     *
     * @param priority see [Priority]
     *
     * @return false if the log message will be ignored
     */
    fun isLoggable(priority: @Priority Int): Boolean

    /**
     * Each log request will go through this first is is considered as loggable
     */
    fun log(priority: @Priority Int, tag: String?, message: String)
}