package com.youssefmkh.logger

import com.youssefmkh.logger.internal.DefaultAdapter
import com.example.orlogger.internal.DefaultFormatter
import com.youssefmkh.logger.internal.LogManger

open class Logger {

    protected val manger: Manger = LogManger()

    fun i(tag: String, message: String){
        manger.i(tag, message)
    }

    fun d(tag: String, message: String){
        manger.d(tag,message)
    }

    fun w(tag: String, message: String){
        manger.w(tag, message)
    }

    fun wtf(tag: String, message: String){
        manger.wtf(tag, message)
    }

    fun v(tag: String, message: String){
        manger.v(tag, message)
    }

    fun e(throwable: Throwable, tag: String, message: String){
        manger.e(throwable, tag, message)
    }

    fun e(tag: String,message: String){
        manger.e(tag, message)
    }

//    fun addLogAdapter(logAdapter: LogAdapter){
//        manger.addAdapter(logAdapter)
//    }
//
//    fun clear(){
//        manger.clear()
//    }

    class Default(inDebug: Boolean = true, private val builder: Builder):Logger(){

        class Builder{
             private val _classesToIgnore = mutableListOf<String>()
            val classesToIgnore = _classesToIgnore.toList()

            fun addClassesToIgnore(classes: List<String>): Builder{
                _classesToIgnore.addAll(classes)
                return this
            }

            fun addClassToIgnore(className: String): Builder{
                _classesToIgnore.add(className)
                return this
            }

            fun build(): Default = Default(builder = this)
        }

        companion object{

            fun newBuild(): Builder = Builder()
        }

        private var logAdapter: LogAdapter =
            DefaultAdapter(
                inDebugMode = inDebug,
                formatter = DefaultFormatter.newBuilder()
                    .addClassToIgnore(Default::class.java.name)
                    .addClassToIgnore(Logger::class.java.name)
                    .addClassToIgnore(LogManger::class.java.name)
                    .addClassToIgnore(DefaultAdapter::class.java.name)
                    .addClassesToIgnore(builder.classesToIgnore)
                    .build()
            )

        var inDebugMode = inDebug
            set(value) {
                logAdapter = DefaultAdapter(
                    inDebugMode = value,
                    formatter = DefaultFormatter.newBuilder()
                        .addClassToIgnore(Default::class.java.name)
                        .addClassToIgnore(Logger::class.java.name)
                        .addClassToIgnore(LogManger::class.java.name)
                        .addClassToIgnore(DefaultAdapter::class.java.name)
                        .addClassesToIgnore(builder.classesToIgnore)
                        .build()
                )
                setAdapter()
                field = value
            }

        init {
            setAdapter()
        }

        private fun setAdapter() {
            manger.clear()
            manger.addAdapter(logAdapter)
        }

    }

    class FullCustom( adapters: List<LogAdapter>): Logger(){

        init {
            manger.clear()
            adapters.forEach {
                manger.addAdapter(logAdapter = it)
            }
        }

    }

    class CustomiseFormatter(
        private val formatter: Formatter
    ): Logger(){


        var isLoggable: Boolean = true

            set(value) {
                adapter = DefaultAdapter(formatter = formatter, inDebugMode = value)
                applyAdapter()
                field = value
            }

        private var adapter: LogAdapter = DefaultAdapter(formatter = formatter, inDebugMode = isLoggable)

        init {
            applyAdapter()
        }

        constructor(formatter: Formatter,isLoggable: Boolean):this(formatter){
            this.isLoggable = isLoggable

            applyAdapter(isLoggable)
        }

        private fun applyAdapter(isLoggable: Boolean = this.isLoggable) {
            adapter = DefaultAdapter(formatter = formatter, inDebugMode = isLoggable)
            manger.clear()
            manger.addAdapter(
                adapter
            )
        }
    }

    class CustomStrategy(private val builder: DefaultFormatter.Builder): Logger(){

        var isLoggable: Boolean = true
            set(value) {
                applyAdapter(value)
                field = value
            }

       private var logAdapter = DefaultAdapter(builder.build(),isLoggable)

        init {
            applyAdapter()
            setupBuilder()
        }

        private fun setupBuilder(){
            builder.addClassToIgnore(Default::class.java.name)
                .addClassToIgnore(Logger::class.java.name)
                .addClassToIgnore(LogManger::class.java.name)
                .addClassToIgnore(DefaultAdapter::class.java.name)
        }

        constructor(builder: DefaultFormatter.Builder,isLoggable: Boolean):this(builder){
            this.isLoggable = isLoggable
            applyAdapter()
            setupBuilder()
        }


        private fun applyAdapter(isLoggable: Boolean = this.isLoggable) {
            logAdapter = DefaultAdapter(formatter = builder.build(), inDebugMode = isLoggable)
            manger.clear()
            manger.addAdapter(
                logAdapter
            )
        }
    }
}