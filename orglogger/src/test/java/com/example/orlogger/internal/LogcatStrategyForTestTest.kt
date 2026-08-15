package com.youssefmkh.logger.internal

import com.youssefmkh.logger.Priority
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog

/**
 * Clas contain unit test for [LogcatStrategy] logic
 */
@RunWith(RobolectricTestRunner::class)
class LogcatStrategyForTestTest {

    @Test
    fun log_with_tag_in_DEBUG(){
        val logcatStrategy = LogcatStrategy()
        logcatStrategy.log(
            priority = Priority.DEBUG,
            tag = "tag",
            message = "message"
        )
        val logItems = ShadowLog.getLogs()

        assert(logItems[0].type == Priority.DEBUG)
        assert(logItems[0].tag == "tag")
        assert(logItems[0].msg == "message")
    }

    @Test
    fun log_with_no_tag_in_debug(){
        val logcatStrategy = LogcatStrategy()
        logcatStrategy.log(
            priority = Priority.DEBUG,
            tag = null,
            message = "message"
        )
        val logItems = ShadowLog.getLogs()
        assert(logItems[0].tag == LogcatStrategy.DEFAULT_TAG)
    }
}