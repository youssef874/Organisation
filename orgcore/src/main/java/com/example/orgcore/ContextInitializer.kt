package com.example.orgcore

import android.content.Context
import androidx.startup.Initializer

/**
 * This class responsible on initializing the application context during app startup
 * to provide it to the sdk
 */
internal class ContextInitializer: Initializer<Unit> {


    override fun create(context: Context) {
        SDKComponent.SDKContext.initialize(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}