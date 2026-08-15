package com.youssefmkh.logger


/**
 * This abstract act as last destination for logger flow, can define disk strategy or logcat strategy.
 */
interface Strategy {

    /**
     * Each log request come to the api this will be invoked lastly.
     *
     * @param priority is the log level e.g. DEBUG, WARNING see [Priority]
     * @param tag is the given tag for the log message.
     * @param message is the given message for the log message.
     */
    fun log(priority: @Priority Int,tag: String?, message: String)
}