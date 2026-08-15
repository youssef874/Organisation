package com.youssefmkh.logger.internal

import com.youssefmkh.logger.LogAdapter
import com.youssefmkh.logger.Manger
import com.youssefmkh.logger.Priority
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.locks.ReentrantLock

internal class LogManger : Manger {

    private val adapters = mutableListOf<LogAdapter>()

    private val lock = ReentrantLock()

    override fun v(tag: String, message: String) {
        log(tag = tag, message = message, throwable = null, priority = Priority.VERBOSE)
    }

    override fun d(tag: String, message: String) {
        log(tag = tag, message = message, throwable = null, priority = Priority.DEBUG)
    }

    override fun e(tag: String, message: String) {
        log(tag = tag, message = message, throwable = null, priority = Priority.ERROR)
    }

    override fun e(throwable: Throwable, tag: String, message: String) {
        log(tag = tag, message = message, throwable = throwable, priority = Priority.ERROR)
    }

    override fun w(tag: String, message: String) {
        log(tag = tag, message = message, throwable = null, priority = Priority.WARN)
    }

    override fun i(tag: String, message: String) {
        log(tag = tag, message = message, throwable = null, priority = Priority.INFO)
    }

    override fun wtf(tag: String, message: String) {
        log(tag = tag, message = message, throwable = null, priority = Priority.ASSERT)
    }

    override fun addAdapter(logAdapter: LogAdapter) {
        adapters.add(logAdapter)
    }

    override fun clear() {
        adapters.clear()
    }

    private fun log(tag: String, message: String, throwable: Throwable?, priority: @Priority Int) {
        lock.lock()
        try {
            throwable?.let {
                val stringWrite = StringWriter()
                val printWriter = PrintWriter(stringWrite)
                it.printStackTrace(printWriter)
                printWriter.flush()
                adapters.forEach { logAdapter ->
                    if (logAdapter.isLoggable(priority))
                        logAdapter.log(priority, tag, "$stringWrite $message")
                }
            } ?: run {
                adapters.forEach { logAdapter ->
                    if (logAdapter.isLoggable(priority))
                        logAdapter.log(priority, tag, message)
                }
            }
        } finally {
            lock.unlock()
        }
    }
}