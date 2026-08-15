package com.example.orlogger.internal

import com.youssefmkh.logger.Formatter
import com.youssefmkh.logger.Priority
import com.youssefmkh.logger.Strategy
import com.youssefmkh.logger.internal.LogcatStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.get


class DefaultFormatter internal constructor(private val builder: Builder) : Formatter {

    companion object {
        private val NEW_LINE: String = System.lineSeparator()
        private const val SEPARATOR: String = "|"
        private const val MIN_STACK_OFFSET = 3

        fun newBuilder() = Builder()
    }

    private data class LogModel(
        val priority: @Priority Int,
        val tag: String?,
        val trace: Array<StackTraceElement>,
        val message: String
    )

    private val scope = CoroutineScope(Dispatchers.Default)
    private val channel = Channel<LogModel>()

    init {

        scope.launch {
            channel.consumeEach {
                val tag = it.tag?.let { it1 -> formatTag(it1) }
                builder.date?.time = System.currentTimeMillis()
                val stackOffset = getStackOffset(it.trace) +1
                val stringBuilder = StringBuilder()

                // machine-readable date/time
                stringBuilder.append(builder.date?.time?.toString())

                // human-readable date/time
                stringBuilder.append(SEPARATOR)
                stringBuilder.append(builder.date?.let { it1 -> builder.simpleDateFormat?.format(it1) })

                // level
                stringBuilder.append(SEPARATOR)
                stringBuilder.append(it.priority.toString())

                // tag
                stringBuilder.append(SEPARATOR)
                stringBuilder.append(tag)

                val element = it.trace[stackOffset]

                stringBuilder.append(SEPARATOR)
                stringBuilder.append(element.className)

                stringBuilder.append(SEPARATOR)
                stringBuilder.append(element.methodName)

                stringBuilder.append(SEPARATOR)
                stringBuilder.append(" (")
                stringBuilder.append(element.fileName)
                stringBuilder.append(":")
                stringBuilder.append(element.lineNumber)
                stringBuilder.append(") ")

                // message
                stringBuilder.append(SEPARATOR)
                stringBuilder.append(it.message)

                stringBuilder.append(NEW_LINE)
                builder.logStrategies.forEach { strategy ->
                    strategy.log(it.priority,tag,stringBuilder.toString())
                }
            }
        }
    }

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {

        var i = MIN_STACK_OFFSET
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (!isContainIgnored(name) && name != DefaultFormatter::class.java.name) {
                return --i
            }
            i++
        }
        return -1
    }

    private fun isContainIgnored(name: String): Boolean{
        if (name.isEmpty()){
            return false
        }
        if (builder.classesToIgnore.isEmpty()){
            return false
        }else{
            builder.classesToIgnore.forEach {
                if (it == name){
                    return true
                }
            }
        }
        return false
    }


    private fun formatTag( tag: String): String? {
        if (tag.isNotEmpty() && tag != builder.tag){
            return "${builder.tag}-$tag"
        }
        return builder.tag
    }

    override fun log(priority: Int, tag: String?, message: String) {
        val trace = Thread.currentThread().getStackTrace()
        scope.launch {
            channel.send(
                LogModel(priority, tag,trace ,message)
            )
        }
    }

    class Builder {

        var date: Date? = null
            private set

        var simpleDateFormat: SimpleDateFormat? = null
            private set

        private val _classesToIgnore = mutableListOf<String>()
        val classesToIgnore get() = _classesToIgnore.toList()

        private val _logStrategies = mutableListOf<Strategy>()
        val logStrategies: List<Strategy> get() = _logStrategies

        var tag: String? = "DefaultFormatter"

        fun setDate(date: Date): Builder{
            this.date = date
            return this
        }

        fun addClassToIgnore(className: String):  Builder{
            if (className.isEmpty()){
                return this
            }
            _classesToIgnore.add(className)
            return this
        }

        fun addClassesToIgnore(classesName: List<String>): Builder{
            if (classesName.isEmpty()){
                return this
            }
            _classesToIgnore.addAll(classesName)
            return this
        }

        fun setTag(tag: String?): Builder{
            this.tag = tag
            return this
        }

        fun setSimpleDateFormat(simpleDateFormat: SimpleDateFormat): Builder{
            this.simpleDateFormat = simpleDateFormat
            return this
        }

        fun addStrategy(strategy: Strategy):Builder{
            _logStrategies.add(strategy)
            return this
        }

        fun addListOfStrategies(strategies: List<Strategy>): Builder{
            _logStrategies.addAll(strategies)
            return this
        }

        fun build(): DefaultFormatter{
            if (date == null){
                date = Date()
            }
            if (simpleDateFormat == null){
                simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.getDefault())
            }
            if (logStrategies.isEmpty()){
                _logStrategies.add(
                    LogcatStrategy()
                )
            }
            return DefaultFormatter(this)
        }
    }
}