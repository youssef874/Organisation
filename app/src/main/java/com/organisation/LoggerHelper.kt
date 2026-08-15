package com.organisation

import com.youssefmkh.logger.Logger


object LoggerHelper {

    val logger: Logger = Logger.Default.newBuild()
        .addClassToIgnore(LoggerHelper::class.java.name).build()


}