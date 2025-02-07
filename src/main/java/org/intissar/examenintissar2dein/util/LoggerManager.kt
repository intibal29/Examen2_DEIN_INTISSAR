package org.intissar.proyecto2.util

import java.io.IOException
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

object LoggerManager {
    private val logger: Logger = Logger.getLogger(LoggerManager::class.java.name)

    init {
        try {
            val fileHandler = FileHandler("app.log", true)
            fileHandler.formatter = SimpleFormatter()
            logger.addHandler(fileHandler)
            logger.level = Level.ALL
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun logInfo(message: String) {
        logger.info(message)
    }

    fun logError(message: String, e: Exception) {
        logger.log(Level.SEVERE, message, e)
    }
}
