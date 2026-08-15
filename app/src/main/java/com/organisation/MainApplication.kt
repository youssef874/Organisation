package com.organisation

import android.app.Application
import com.example.orgcore.OrgSDKEnvironment
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        OrgSDKEnvironment.initializeMPSDK(this)
        super.onCreate()
    }
}