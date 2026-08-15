package com.example.orgcore

import android.content.Context
import androidx.startup.AppInitializer

object OrgSDKEnvironment {

    fun initializeMPSDK(context: Context){
        AppInitializer.getInstance(context).initializeComponent(ContextInitializer::class.java)
    }
}