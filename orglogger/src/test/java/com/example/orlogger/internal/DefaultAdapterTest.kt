package com.youssefmkh.logger.internal

import com.youssefmkh.logger.Formatter
import com.youssefmkh.logger.Priority
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultAdapterTest {

    @After
    fun clean(){
        TestUtils.clearLog()
    }

    @Test
    fun isAlwaysLoggable(){
        val logAdapter = DefaultAdapter()
        assert(logAdapter.isLoggable(Priority.DEBUG))
        assert(logAdapter.isLoggable(Priority.INFO))
        assert(logAdapter.isLoggable(Priority.WARN))
        assert(logAdapter.isLoggable(Priority.VERBOSE))
        assert(logAdapter.isLoggable(Priority.ERROR))
        assert(logAdapter.isLoggable(Priority.ASSERT))
    }

    @Test
    fun log_with_custom_formatter(){
        val formatter = mock(Formatter::class.java)
        val logAdapter = DefaultAdapter(formatter)
        logAdapter.log(Priority.DEBUG, null, "message")
        verify(formatter).log(Priority.DEBUG,null,"message")
    }

    @Test
    fun log_with_default_formatter(){
        TestUtils.log = {priority,tag,msg->
            assert(priority == Priority.DEBUG)
            assert(tag == LogcatStrategy.DEFAULT_TAG)
            assert(msg.contains("message"))
            assert(msg.contains("${LogcatStrategy.DEFAULT_TAG}|message"))
        }
        val logAdapter = DefaultAdapter()
        logAdapter.log(Priority.DEBUG, null, "message")
    }
}