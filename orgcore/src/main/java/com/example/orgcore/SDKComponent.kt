package com.example.orgcore

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * This represents provider for some util components for the sdk
 */
internal class SDKComponent {

    /**
     * This represents the [Context] provider
     */
    class SDKContext private constructor(){

        private var isInitialized = false

        private lateinit var context: Context

        /**
         * Call this to initialize the context to be provided
         */
        fun initializeContext(context: Context){
            if (!isInitialized){
                this.context = context
                isInitialized = true
            }
        }

        companion object{

            @SuppressLint("StaticFieldLeak")
            private lateinit var sdkContext: SDKContext

            fun initialize(context: Context){
                sdkContext = SDKContext()
                sdkContext.initializeContext(context)
            }

            fun getContext(): Context{
                if (!sdkContext.isInitialized){
                    throw UninitializedPropertyAccessException("Could access this object is not initialized please call MPSDKEnvironment.initializeMPSDK(context)")
                }
                return sdkContext.context
            }
        }
    }

    /**
     * This represents the [CoroutineScope] provider
     */
    object CoroutineComponent{

        val sdkCoroutineScope = CoroutineScope(SupervisorJob() +Dispatchers.Default)
    }
}