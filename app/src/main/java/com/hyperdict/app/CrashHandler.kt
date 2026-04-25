package com.hyperdict.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Global crash handler that catches uncaught exceptions and stores them
 * for display in the crash recovery screen.
 */
class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    companion object {
        private const val TAG = "CrashHandler"
        private const val PREFS_NAME = "crash_handler"
        private const val KEY_CRASH_TIME = "crash_time"
        private const val KEY_CRASH_MESSAGE = "crash_message"
        private const val KEY_CRASH_STACKTRACE = "crash_stacktrace"
        private const val KEY_CRASH_APP_VERSION = "crash_app_version"

        fun saveCrashInfo(context: Context, throwable: Throwable) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val stackTrace = getStackTrace(throwable)

            prefs.edit().apply {
                putString(KEY_CRASH_TIME, timestamp)
                putString(KEY_CRASH_MESSAGE, throwable.message ?: throwable.toString())
                putString(KEY_CRASH_STACKTRACE, stackTrace)
                putString(KEY_CRASH_APP_VERSION, BuildConfig.VERSION_NAME)
                apply()
            }
        }

        fun getCrashReport(context: Context): CrashReport? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val stackTrace = prefs.getString(KEY_CRASH_STACKTRACE, null) ?: return null
            val time = prefs.getString(KEY_CRASH_TIME, "Unknown") ?: "Unknown"
            val message = prefs.getString(KEY_CRASH_MESSAGE, "Unknown error") ?: "Unknown error"
            val appVersion = prefs.getString(KEY_CRASH_APP_VERSION, "Unknown") ?: "Unknown"

            return CrashReport(
                timestamp = time,
                message = message,
                stackTrace = stackTrace,
                appVersion = appVersion,
                deviceInfo = buildDeviceInfo()
            )
        }

        fun clearCrashReport(context: Context) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        }

        fun hasPendingCrash(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.contains(KEY_CRASH_STACKTRACE)
        }

        private fun getStackTrace(throwable: Throwable): String {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            return sw.toString()
        }

        private fun buildDeviceInfo(): String {
            return buildString {
                append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                append("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
                append("Brand: ${Build.BRAND}\n")
                append("Product: ${Build.PRODUCT}\n")
                append("CPU ABI: ${Build.SUPPORTED_ABIS.joinToString(", ")}\n")
            }
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Log.e(TAG, "Uncaught exception on thread ${thread.name}", throwable)
        saveCrashInfo(context, throwable)

        // Launch the crash recovery activity
        try {
            val intent = Intent(context, CrashRecoveryActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch crash recovery activity", e)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}

data class CrashReport(
    val timestamp: String,
    val message: String,
    val stackTrace: String,
    val appVersion: String,
    val deviceInfo: String
) {
    fun toFullString(): String {
        return buildString {
            append("=== Crash Report ===\n\n")
            append("Time: $timestamp\n")
            append("App Version: $appVersion\n\n")
            append("Device Info:\n$deviceInfo\n")
            append("Error Message:\n$message\n\n")
            append("Stack Trace:\n$stackTrace")
        }
    }
}
