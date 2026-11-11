package com.youssefmkh.logger.internal

import com.example.orlogger.internal.DefaultFormatter
import com.youssefmkh.logger.Priority
import com.youssefmkh.logger.Strategy
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class DefaultFormatterTest {


    @Test
    fun when_not_setting_only_the_Strategy_in_the_builder_expect_the_log_behave_as_default(){
        val strategy = object :Strategy{
            override fun log(priority: Int, tag: String?, message: String) {
                assert(priority == Priority.VERBOSE)
                assert(tag == "DefaultFormatter-tag")
                assert(message.contains("VERBOSE|DefaultFormatter-tag|message"))
            }

        }
        val defaultFormatter = DefaultFormatter.newBuilder()
            .addStrategy(strategy)
            .build()
        defaultFormatter.log(Priority.VERBOSE,"tag","message")
    }

    @Test
    fun when_logging_without_tag_expect_the_tag_written_default_only(){
        val strategy = object :Strategy{
            override fun log(priority: Int, tag: String?, message: String) {
                assert(tag == "DefaultFormatter")
            }

        }
        val defaultFormatter = DefaultFormatter.newBuilder()
            .addStrategy(strategy)
            .build()
        defaultFormatter.log(Priority.VERBOSE,null,"message")
    }

    @Test
    fun when_providing_custom_tag_expect_it_to_appear_as_part_of_the_over_all_tag(){
        val strategy = object :Strategy{
            override fun log(priority: Int, tag: String?, message: String) {
                assert(tag == "custom")
            }

        }
        val defaultFormatter = DefaultFormatter.newBuilder()
            .addStrategy(strategy)
            .setTag("custom")
            .build()
        defaultFormatter.log(Priority.VERBOSE,null,"message")
    }

    @Test
    fun when_setting_date_and_date_formatter_expect_to_appear_in_the_message(){
        val date = Date()
        date.time = 1000L
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SS", Locale.getDefault())
        val strategy = object :Strategy{
            override fun log(priority: Int, tag: String?, message: String) {
                assert(message.contains(simpleDateFormat.format(date)))
            }

        }

        val defaultFormatter = DefaultFormatter.newBuilder()
            .addStrategy(strategy)
            .setDate(date)
            .setSimpleDateFormat(simpleDateFormat)
            .build()
        defaultFormatter.log(Priority.VERBOSE,null,"message")
    }
}