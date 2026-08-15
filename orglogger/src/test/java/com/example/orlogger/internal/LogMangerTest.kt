package com.youssefmkh.logger.internal

import com.youssefmkh.logger.LogAdapter
import com.youssefmkh.logger.Priority
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.openMocks
import java.io.PrintWriter
import java.io.StringWriter

class LogMangerTest {

    private val manager = LogManger()
    @Mock
    private lateinit var adapter: LogAdapter

    @Before
    fun setup(){
        openMocks(this)
        `when`(adapter.isLoggable(any(Int::class.java))).thenReturn(true)
        manager.addAdapter(adapter)
    }

    @Test
    fun when_d_called_and_adapter_is_loggable_expect_adapter_log_to_be_called(){
        manager.d("tag","message")
        verify(adapter).log(Priority.DEBUG,"tag","message")
    }

    @Test
    fun when_error_called_without_throwable_expect_adapter_to_be_called_with_message_does_not_contain_throwable_message(){
        manager.e("tag","message")
        verify(adapter).log(Priority.ERROR,"tag","message")
    }

    @Test
    fun when_error_called_wit_throwable_expect_adapter_to_be_called_with_message_to_contain_throwable_message(){
        val throwable = Throwable("exception")
        val stringWrite = StringWriter()
        val printWriter = PrintWriter(stringWrite)
        throwable.printStackTrace(printWriter)
        printWriter.flush()
        manager.e(throwable,"tag","message")
        verify(adapter).log(Priority.ERROR,"tag","$stringWrite message")
    }

    @Test
    fun when_w_called_and_adapter_is_loggable_expect_adapter_log_to_be_called_with_warn_as_priority(){
        manager.w("tag","message")
        verify(adapter).log(Priority.WARN,"tag","message")
    }

    @Test
    fun when_i_called_and_adapter_is_loggable_expect_adapter_log_to_be_called_with_INFO_as_priority(){
        manager.i("tag","message")
        verify(adapter).log(Priority.INFO,"tag","message")
    }

    @Test
    fun when_wtf_called_and_adapter_is_loggable_expect_adapter_log_to_be_called_with_ASSERT_as_priority(){
        manager.wtf("tag","message")
        verify(adapter).log(Priority.ASSERT,"tag","message")
    }

    @Test
    fun when_v_called_and_adapter_is_loggable_expect_adapter_log_to_be_called_with_VERBOSE_as_priority(){
        manager.v("tag","message")
        verify(adapter).log(Priority.VERBOSE,"tag","message")
    }

    @Test
    fun when_adapter_cleared_expect_no_log_called(){
        manager.clear()
        manager.v("tag","message")
        verifyNoInteractions(adapter)
    }

    @Test
    fun when_add_multiple_adapter_expect_them_to_be_takenInto_consideration(){
        manager.clear()
        val adapter1 = mock(LogAdapter::class.java)
        val adapter2 = mock(LogAdapter::class.java)

        manager.addAdapter(adapter2)
        manager.addAdapter(adapter1)
        manager.d("tag","Message")
        verify(adapter2).isLoggable(Priority.DEBUG)
        verify(adapter2).isLoggable(Priority.DEBUG)
    }

    @Test
    fun when_adapter_is_not_loggable_expect_adapter_log_to_not_be_called(){
        manager.clear()
        val adapter1 = mock(LogAdapter::class.java)
        `when`(adapter1.isLoggable(any(Int::class.java))).thenReturn(false)
        manager.addAdapter(adapter1)
        manager.d("tag","message")
        verify(adapter1, never()).log(Priority.DEBUG,"tag","message")
    }
}