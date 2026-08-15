package com.youssefmkh.logger.internal

import android.util.Log
import com.youssefmkh.logger.Priority
import com.youssefmkh.logger.Strategy


/**
 * LogCat implementation for [Strategy]
 *
 * This simply prints out all logs to Logcat by using standard [Log] class.
 */
class LogcatStrategy: Strategy {

    companion object{
        const val DEFAULT_TAG = "NO_TAG"
    }

    override fun log(priority: Int, tag: String?, message: String) {
        writeMessageToLogcat(priority,tag?: DEFAULT_TAG,message)
        TestUtils.afterLog(priority, tag, message)
    }

    private fun writeMessageToLogcat(priority: Int, tag: String, message: String){
        when(priority){
            Priority.VERBOSE->Log.v(tag,message)
            Priority.DEBUG->Log.d(tag,message)
            Priority.INFO->Log.i(tag,message)
            Priority.WARN->Log.w(tag,message)
            Priority.ERROR->Log.e(tag,message)
            Priority.ASSERT->Log.wtf(tag,message)
        }
    }
}