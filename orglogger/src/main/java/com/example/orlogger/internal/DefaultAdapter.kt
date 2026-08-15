package com.youssefmkh.logger.internal

import com.example.orlogger.internal.DefaultFormatter
import com.youssefmkh.logger.Formatter
import com.youssefmkh.logger.LogAdapter
import com.youssefmkh.logger.Priority

class DefaultAdapter(
    private val formatter: Formatter = DefaultFormatter.newBuilder().build(),
    private val inDebugMode: Boolean = true
): LogAdapter {
    override fun isLoggable(priority: Int): Boolean {
        return if (inDebugMode) true else priority >= Priority.INFO
    }

    override fun log(priority: Int, tag: String?, message: String) {
        formatter.log(priority,tag,message)
    }
}