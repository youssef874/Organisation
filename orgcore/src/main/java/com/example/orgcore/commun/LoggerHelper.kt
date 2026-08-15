package com.example.orgcore.commun

import com.youssefmkh.logger.Logger

internal object LoggerHelper {

    val logger: Logger = Logger.Default.newBuild()
        .addClassToIgnore(LoggerHelper::class.java.name).build()
}